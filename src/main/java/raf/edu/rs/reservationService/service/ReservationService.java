package raf.edu.rs.reservationService.service;

import io.github.resilience4j.retry.Retry;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.domain.Reservation;
import raf.edu.rs.reservationService.domain.Room;
import raf.edu.rs.reservationService.dto.MessageDto;
import raf.edu.rs.reservationService.dto.UserDto;
import raf.edu.rs.reservationService.exceptions.InsertException;
import raf.edu.rs.reservationService.exceptions.NotFoundException;
import raf.edu.rs.reservationService.messageHelper.MessageHelper;
import raf.edu.rs.reservationService.repository.HotelRepository;
import raf.edu.rs.reservationService.repository.ReservationRepository;
import raf.edu.rs.reservationService.repository.RoomRepository;
import java.time.Period;
import java.util.List;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private RoomService roomService;
    private HotelRepository hotelRepository;
    private JmsTemplate jmsTemplate;
    private MessageHelper messageHelper;
    private RestTemplate userServiceRestTemplate;
    private Retry userServiceRetry;

    public ReservationService(ReservationRepository reservationRepository, RoomService roomService,
                              HotelRepository hotelRepository, JmsTemplate jmsTemplate, MessageHelper messageHelper,
                              RestTemplate userServiceRestTemplate, Retry userServiceRetry) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
        this.hotelRepository = hotelRepository;
        this.jmsTemplate = jmsTemplate;
        this.messageHelper = messageHelper;
        this.userServiceRestTemplate = userServiceRestTemplate;
        this.userServiceRetry = userServiceRetry;
    }

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public Reservation save(Reservation reservation) {
        if (!matchingIds(reservation))
            throw new NotFoundException("The IDs for reservation don't match!");

        for (Reservation savedReservation: findAll()) {
            if (!reservation.getHotelId().equals(savedReservation.getHotelId())) break;
            if (!reservation.getRoomId().equals(savedReservation.getRoomId())) break;

            if (!(reservation.getStartDate().isAfter(savedReservation.getEndDate()) ||
                    reservation.getEndDate().isBefore(savedReservation.getStartDate()))) {
                throw new InsertException("The room is already reserved in that period!");
            }
        }

        // retry pattern
        ResponseEntity<Integer> discountResponseEntity =
                Retry.decorateSupplier(userServiceRetry, () -> getDiscount(reservation.getUserId())).get();

        // calculate price
        Room room = roomService.getById(reservation.getRoomId());
        Double price = room.getPricePerDay() *
                (Period.between(reservation.getStartDate(), reservation.getEndDate()).getDays() + 1);
        price -= price * discountResponseEntity.getBody().doubleValue() / 100;
        reservation.setTotalPrice(price);

        // inform user service to increase number of reservations
        userServiceRestTemplate.exchange("http://localhost:8081/api/user/incrementRez/" + reservation.getUserId(),
                HttpMethod.POST, null, ResponseEntity.class);

        sendMail("reservation_successful", reservation);
        reservation.setSentReminder(false);
        return reservationRepository.save(reservation);
    }

    private ResponseEntity<Integer> getDiscount(Long userId) {
        try {
            return userServiceRestTemplate.exchange("http://localhost:8081/api/user/discount/" + userId,
                    HttpMethod.GET, null, Integer.class);
        }
        catch (HttpClientErrorException e) {
            throw new NotFoundException("Discount not found!");
        }
        catch (Exception e) {
            throw new RuntimeException("Internal server error");
        }
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    public Reservation update(Long id, Reservation newReservation, Long userId) {
        // ne koristi se?
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found!"));

        Hotel hotel = hotelRepository.getById(reservation.getHotelId());

        if (!reservation.getUserId().equals(userId) && !hotel.getManagerId().equals(userId))
            throw new NotFoundException("No manager nor user found with your ID!");

        reservation.setHotelId(newReservation.getHotelId());
        reservation.setRoomId(newReservation.getRoomId());
        reservation.setStartDate(newReservation.getStartDate());
        reservation.setEndDate(newReservation.getEndDate());
        reservation.setSentReminder(false); // ako je promijenjen datum, onda treba ponovo da se salje email
        reservationRepository.save(reservation);
        return reservation;
    }

    public void delete(Long id, Long userId) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found!"));

        Hotel hotel = hotelRepository.getById(reservation.getHotelId());

        if (!reservation.getUserId().equals(userId) && !hotel.getManagerId().equals(userId))
            throw new NotFoundException("This reservation doesn't match your ID!");

        // inform user service to decrease number of reservations
        userServiceRestTemplate.exchange("http://localhost:8081/api/user/decrementRez/" + reservation.getUserId(),
                HttpMethod.POST, null, ResponseEntity.class);

        sendMail("reservation_cancellation", reservation);
        reservationRepository.delete(reservation);
    }

    private boolean matchingIds(Reservation reservation) {
        List<Room> rooms = roomService.findAll();

        for (Room room: rooms) {
            if (room.getId().equals(reservation.getRoomId()) && room.getHotelId().equals(reservation.getHotelId()))
                return true;
        }

        return false;
    }

    /** reservation confirmation sent on email */
    private void sendMail(String emailType, Reservation reservation) {
        Hotel hotel = hotelRepository.getById(reservation.getHotelId());

        ResponseEntity<UserDto> userDto = userServiceRestTemplate.exchange("http://localhost:8081/api/user/get/" +
                        reservation.getUserId(), HttpMethod.GET, null, UserDto.class);

        MessageDto msg = new MessageDto(emailType, userDto.getBody().getFirstName(), userDto.getBody().getLastName(),
                userDto.getBody().getEmail(), hotel.getName());

        String str = messageHelper.createTextMessage(msg);
        jmsTemplate.convertAndSend("send_mail_destination", str);
    }
}