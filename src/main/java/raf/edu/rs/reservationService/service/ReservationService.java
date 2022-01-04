package raf.edu.rs.reservationService.service;

import org.springframework.stereotype.Service;
import raf.edu.rs.reservationService.domain.Reservation;
import raf.edu.rs.reservationService.domain.Room;
import raf.edu.rs.reservationService.exceptions.InsertException;
import raf.edu.rs.reservationService.exceptions.NotFoundException;
import raf.edu.rs.reservationService.repository.HotelRepository;
import raf.edu.rs.reservationService.repository.ReservationRepository;
import raf.edu.rs.reservationService.repository.RoomRepository;
import java.util.List;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
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

        return reservationRepository.save(reservation);
    }

    public Reservation update(Long id, Reservation newReservation) {
        // ne koristi se?
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found!"));

        reservation.setHotelId(newReservation.getHotelId());
        reservation.setRoomId(newReservation.getRoomId());
        reservation.setStartDate(newReservation.getStartDate());
        reservation.setEndDate(newReservation.getEndDate());
        reservationRepository.save(reservation);
        return reservation;
    }

    public void delete(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found!"));

        reservationRepository.delete(reservation);
    }

    private boolean matchingIds(Reservation reservation) {
        List<Room> rooms = roomRepository.findAll();

        for (Room room: rooms) {
            if (room.getId().equals(reservation.getRoomId()) && room.getHotelId().equals(reservation.getHotelId()))
                return true;
        }

        return false;
    }
}