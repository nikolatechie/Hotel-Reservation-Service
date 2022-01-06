package raf.edu.rs.reservationService.email;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import raf.edu.rs.reservationService.domain.Reservation;
import raf.edu.rs.reservationService.dto.MessageDto;
import raf.edu.rs.reservationService.messageHelper.MessageHelper;
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

    public EmailScheduler(ReservationService reservationService, JmsTemplate jmsTemplate, MessageHelper messageHelper) {
        this.reservationService = reservationService;
        this.jmsTemplate = jmsTemplate;
        this.messageHelper = messageHelper;
    }

    //@Scheduled(cron = "0 */10 * ? * *") // za slanje svakih 10 minuta
    @Scheduled(fixedDelay = 4000)
    public void sendReminderEmails() {
        List<Reservation> reservations = reservationService.findAll();

        for (Reservation reservation: reservations) {
            if (reservation.getSentReminder()) continue;

            if (Period.between(LocalDate.now(), reservation.getStartDate()).getDays() <= 2) {
                reservation.setSentReminder(true);
                reservationService.save(reservation);

                MessageDto messageDto = new MessageDto("reminder", "IME", "PREZIME",
                        "ngrujic2419rn@raf.rs", "hotelId", "link", reservation.getId());

                String str = messageHelper.createTextMessage(messageDto);
                jmsTemplate.convertAndSend("ngrujic2419rn@raf.rs", str); // za test
            }
        }
    }
}