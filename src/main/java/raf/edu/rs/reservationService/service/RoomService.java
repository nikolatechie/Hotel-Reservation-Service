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
    private HotelService hotelService;
    private ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, HotelService hotelService,
                       ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.hotelService = hotelService;
        this.reservationRepository = reservationRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public List<Room> findAllAvailable(String city, String hotelName, String roomType, LocalDate startDate,
                                       LocalDate endDate, String sort) {
        List<Room> availableRooms = new ArrayList<>();
        boolean isCityNull = (city == null), isHotelNameNull = (hotelName == null), isTypeNull = (roomType == null);

        for (Room room: findAll()) {
            Hotel hotel = hotelService.getById(room.getHotelId());
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
        Hotel hotel = hotelService.getByManagerId(managerId);
        Long hotelId = hotel.getId();
        newRoom.setHotelId(hotelId);

        if (!hotelService.existsById(hotelId) || !hotelService.getById(hotelId).getManagerId().equals(managerId) ||
            newRoom.getRoomNumber() > hotel.getNumberOfRooms())
            throw new NotFoundException("Invalid parameters for hotel with id " + hotelId);

        InsertException insertException = isRoomAdded(newRoom);

        if (insertException != null)
            throw insertException;

        return roomRepository.save(newRoom);
    }

    public Room update(Integer roomNumber, Room newRoom, Long managerId) {
        Room room = findByRoomNumber(roomNumber)
                .orElseThrow(() -> new NotFoundException("Room with roomNumber " + roomNumber + " not found!"));

        if (!hotelService.getById(room.getHotelId()).getManagerId().equals(managerId))
            throw new NotFoundException("Room with roomNumber " + roomNumber + " doesn't belong to your hotel!");

        room.setRoomNumber(newRoom.getRoomNumber());
        room.setType(newRoom.getType());
        room.setPricePerDay(newRoom.getPricePerDay());
        roomRepository.save(room);
        return room;
    }

    public void rangeUpdate(Long managerId, Double startPrice, Double endPrice, String type) {
        for (Room room: findAll()) {
            Hotel hotel = hotelService.getById(room.getHotelId());

            if (hotel.getManagerId().equals(managerId) && room.getPricePerDay() >= startPrice &&
                    room.getPricePerDay() <= endPrice) {
                room.setType(type);
                roomRepository.save(room);
            }
        }
    }

    public void delete(Long id, Long managerId) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room with id " + id + " not found!"));

        if (hotelService.getById(room.getHotelId()).getManagerId().equals(managerId))
            roomRepository.delete(room);
        else
            throw new NotFoundException("Can't delete this room because you're not managing it!");
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

    public Room getById(Long id) {
        return roomRepository.getById(id);
    }

    public Optional<Room> findByRoomNumber(Integer roomNumber) {
        for (Room room: findAll()) {
            if (room.getRoomNumber().equals(roomNumber))
                return Optional.of(room);
        }

        return Optional.empty();
    }
}