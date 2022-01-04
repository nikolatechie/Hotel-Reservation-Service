package raf.edu.rs.reservationService.service;

import org.springframework.stereotype.Service;
import raf.edu.rs.reservationService.domain.Room;
import raf.edu.rs.reservationService.exceptions.InsertException;
import raf.edu.rs.reservationService.exceptions.NotFoundException;
import raf.edu.rs.reservationService.repository.HotelRepository;
import raf.edu.rs.reservationService.repository.RoomRepository;
import java.util.List;
import java.util.Objects;

@Service
public class RoomService {
    private RoomRepository roomRepository;
    private HotelRepository hotelRepository;

    public RoomService(RoomRepository roomRepository, HotelRepository hotelRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
    }

    public List<Room> findAll() {
        return roomRepository.findAll();
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
            if (room.getHotelId().equals(newRoom.getHotelId()) &&
                    Objects.equals(room.getRoomNumber(), newRoom.getRoomNumber())) {
                return new InsertException("Room with number " + room.getRoomNumber() +
                        " within the hotel with id " + room.getHotelId() + " already exists!");
            }
        }

        return null;
    }
}