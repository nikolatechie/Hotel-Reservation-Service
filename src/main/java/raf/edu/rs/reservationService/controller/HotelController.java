package raf.edu.rs.reservationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.security.CheckSecurity;
import raf.edu.rs.reservationService.service.HotelService;
import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {
    private HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return new ResponseEntity<>(hotelService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<Hotel> addNewHotel(@RequestBody Hotel hotel) {
        return new ResponseEntity<>(hotelService.save(hotel), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody Hotel newHotel) {
        return new ResponseEntity<>(hotelService.update(id, newHotel), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> deleteHotel(@PathVariable Long id) {
        hotelService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}