package raf.edu.rs.reservationService.service;

import org.springframework.stereotype.Service;
import raf.edu.rs.reservationService.domain.Hotel;
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
        return hotelRepository.save(hotel);
    }

    public Hotel update(Long id, Hotel newHotel) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel with id " + id + " not found!"));

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
}