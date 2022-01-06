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

    public List<Room> findAllAvailable(String city, String hotelName, String roomType, LocalDate startDate,
                                       LocalDate endDate, String sort) {
        List<Room> availableRooms = new ArrayList<>();
        boolean isCityNull = city == null, isHotelNameNull = hotelName == null, isTypeNull = roomType == null;

        for (Room room: findAll()) {
            Hotel hotel = hotelRepository.getById(room.getHotelId());
            if (isCityNull) city = hotel.getCity();
            if (isHotelNameNull) hotelName = hotel.getName();
            if (isTypeNull) roomType = room.getType();

            if (hotel.getCity().equals(city) &&
                    hotel.getName().equals(hotelName) &&
                    room.getType().equals(roomType) &&
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

    public Room save(Room newRoom, Long managerId) {
        Long hotelId = newRoom.getHotelId();

        if (!hotelRepository.existsById(hotelId) || !hotelRepository.getById(hotelId).getManagerId().equals(managerId))
            throw new NotFoundException("Invalid parameters for hotel with id " + hotelId);

        InsertException insertException = isRoomAdded(newRoom);

        if (insertException != null)
            throw insertException;

        return roomRepository.save(newRoom);
    }

    public Room update(Long id, Room newRoom, Long managerId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room with id " + id + " not found!"));

        InsertException insertException = isRoomAdded(newRoom);

        if (insertException != null)
            throw insertException;

        if (!hotelRepository.getById(room.getHotelId()).getManagerId().equals(managerId))
            throw new NotFoundException("Room with id " + id + " doesn't belong to your hotel!");

        room.setHotelId(newRoom.getHotelId());
        room.setRoomNumber(newRoom.getRoomNumber());
        room.setType(newRoom.getType());
        room.setPricePerDay(newRoom.getPricePerDay());
        roomRepository.save(room);
        return room;
    }

    public void delete(Long id, Long managerId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room with id " + id + " not found!"));

        if (hotelRepository.getById(room.getHotelId()).getManagerId().equals(managerId))
            roomRepository.delete(room);
        else
            throw  new NotFoundException("Can't delete this room because you're not managing it!");
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