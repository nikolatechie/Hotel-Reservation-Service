package raf.edu.rs.reservationService.email;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.domain.Reservation;
import raf.edu.rs.reservationService.dto.MessageDto;
import raf.edu.rs.reservationService.dto.UserDto;
import raf.edu.rs.reservationService.messageHelper.MessageHelper;
import raf.edu.rs.reservationService.service.HotelService;
import raf.edu.rs.reservationService.service.ReservationService;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
@EnableScheduling
public class EmailScheduler {
    private ReservationService reservationService;
    private JmsTemplate jmsTemplate;
    private MessageHelper messageHelper;
    private RestTemplate userServiceRestTemplate;
    private HotelService hotelService;

    public EmailScheduler(ReservationService reservationService, JmsTemplate jmsTemplate, MessageHelper messageHelper,
                          RestTemplate userServiceRestTemplate, HotelService hotelService) {
        this.reservationService = reservationService;
        this.jmsTemplate = jmsTemplate;
        this.messageHelper = messageHelper;
        this.userServiceRestTemplate = userServiceRestTemplate;
        this.hotelService = hotelService;
    }

    @Scheduled(cron = "0 */10 * ? * *") // za slanje svakih 10 minuta
    //@Scheduled(fixedDelay = 10000) // svakih 10 sekundi
    public void sendReminderEmails() {
        List<Reservation> reservations = reservationService.findAll();

        for (Reservation reservation: reservations) {
            if (reservation.getSentReminder()) continue;

            if (Period.between(LocalDate.now(), reservation.getStartDate()).getDays() <= 2) {
                reservation.setSentReminder(true);
                reservationService.getReservationRepository().save(reservation);
                Hotel hotel = null;

                // ne radi preko getById?
                for (Hotel savedHotel: hotelService.findAll()) {
                    if (savedHotel.getId().equals(reservation.getHotelId())) {
                        hotel = savedHotel;
                        break;
                    }
                }

                ResponseEntity<UserDto> userDto = userServiceRestTemplate.exchange(
                        "http://localhost:8081/api/user/get/" + reservation.getUserId(), HttpMethod.GET,
                        null, UserDto.class);

                MessageDto messageDto = new MessageDto("reservation_reminder", userDto.getBody().getFirstName(),
                        userDto.getBody().getLastName(), userDto.getBody().getEmail(), hotel.getName());

                String str = messageHelper.createTextMessage(messageDto);
                jmsTemplate.convertAndSend("send_mail_destination", str);
            }
        }
    }
}