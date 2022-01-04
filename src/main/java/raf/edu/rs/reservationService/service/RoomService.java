package raf.edu.rs.reservationService.service;

import org.springframework.stereotype.Service;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.domain.Reservation;
import raf.edu.rs.reservationService.domain.Room;
import raf.edu.rs.reservationService.exceptions.InsertException;
import raf.edu.rs.reservationService.exceptions.NotFoundException;
import raf.edu.rs.reservationService.repository.HotelRepository;
import raf.edu.rs.reservationService.repository.ReservationRepository;
import raf.edu.rs.reservationService.repository.RoomRepository;
import java.time.LocalDate;
import java.util.*;

@Service
public class RoomService {
    private RoomRepository roomRepository;
    private HotelRepository hotelRepository;
    private ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository,
                       ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findAllAvailable(String city, String hotelName, LocalDate startDate, LocalDate endDate,
                                       String sort) {
        List<Room> availableRooms = new ArrayList<>();
        boolean isCityNull = city == null, isHotelNameNull = hotelName == null;

        for (Room room: findAll()) {
            Hotel hotel = hotelRepository.getById(room.getHotelId());
            if (isCityNull) city = hotel.getCity();
            if (isHotelNameNull) hotelName = hotel.getName();

            if (hotel.getCity().equals(city) &&
                    hotel.getName().equals(hotelName) &&
                    !isReserved(room, startDate, endDate))
                availableRooms.add(room);
        }

        if (availableRooms.isEmpty())
            return null;

        if ("asc".equals(sort))
            availableRooms.sort(Comparator.comparing(Room::getPricePerDay));
        else if ("desc".equals(sort))
            availableRooms.sort(Comparator.comparing(Room::getPricePerDay).reversed());

        return availableRooms;
    }

    private boolean isReserved(Room room, LocalDate startDate, LocalDate endDate) {
        for (Reservation reservation: reservationRepository.findAll()) {
            if (!room.getId().equals(reservation.getRoomId())) continue;

            if (!(startDate.isAfter(reservation.getEndDate()) || endDate.isBefore(reservation.getStartDate())))
                return true;
        }

        return false;
    }

    public Room save(Room newRoom) {
        Long hotelId = newRoom.getHotelId();

        if (!hotelRepository.existsById(hotelId))
            throw new NotFoundException("Hotel with id " + hotelId + " not found!");

        InsertException insertException = isRoomAdded(newRoom);

        if (insertException != null)
            throw insertException;

        return roomRepository.save(newRoom);
    }

    public Room update(Long id, Room newRoom) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room with id " + id + " not found!"));

        InsertException insertException = isRoomAdded(newRoom);

        if (insertException != null)
            throw insertException;

        room.setHotelId(newRoom.getHotelId());
        room.setRoomNumber(newRoom.getRoomNumber());
        room.setType(newRoom.getType());
        room.setPricePerDay(newRoom.getPricePerDay());
        roomRepository.save(room);
        return room;
    }

    public void delete(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room with id " + id + " not found!"));

        roomRepository.delete(room);
    }

    private InsertException isRoomAdded(Room newRoom) {
        List<Room> rooms = roomRepository.findAll();

        for (Room room: rooms) {
            if (!room.equals(newRoom) && room.getHotelId().equals(newRoom.getHotelId()) &&
                    room.getRoomNumber().equals(newRoom.getRoomNumber())) {
                return new InsertException("Room with number " + room.getRoomNumber() +
                        " within the hotel with id " + room.getHotelId() + " already exists!");
            }
        }

        return null;
    }
}