package raf.edu.rs.reservationService.service;

import org.springframework.stereotype.Service;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.exceptions.InsertException;
import raf.edu.rs.reservationService.exceptions.NotFoundException;
import raf.edu.rs.reservationService.repository.HotelRepository;
import java.util.List;

@Service
public class HotelService {
    private HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<Hotel> findAll() {
        return hotelRepository.findAll();
    }

    public Hotel save(Hotel hotel) {
        if (findByManagerId(hotel.getManagerId()) == null)
            return hotelRepository.save(hotel);

        throw new InsertException("You already work at one hotel!");
    }

    public Hotel findByManagerId(Long managerId) {
        for (Hotel hotel: findAll()) {
            if (hotel.getManagerId().equals(managerId))
                return hotel;
        }

        return null;
    }

    public Hotel update(Long managerId, Hotel newHotel) {
        Hotel hotel = findByManagerId(managerId);

        if (hotel == null)
            throw new NotFoundException("Manager has no hotel saved!");

        hotel.setCity(newHotel.getCity());
        hotel.setName(newHotel.getName());
        hotel.setDescription(newHotel.getDescription());
        hotel.setNumberOfRooms(newHotel.getNumberOfRooms());
        hotelRepository.save(hotel);
        return hotel;
    }

    public void delete(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel with id " + id + " not found!"));

        hotelRepository.delete(hotel);
    }

    public Hotel getById(Long id) {
        return hotelRepository.getById(id);
    }

    public boolean existsById(Long id) {
        return hotelRepository.existsById(id);
    }

    public Hotel getByManagerId(Long managerId) {
        for (Hotel hotel: findAll()) {
            if (hotel.getManagerId().equals(managerId))
                return hotel;
        }

        return null;
    }
}