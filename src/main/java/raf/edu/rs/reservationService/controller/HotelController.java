package raf.edu.rs.reservationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.security.CheckSecurity;
import raf.edu.rs.reservationService.security.SecurityAspect;
import raf.edu.rs.reservationService.service.HotelService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {
    private HotelService hotelService;
    private SecurityAspect securityAspect;

    public HotelController(HotelService hotelService, SecurityAspect securityAspect) {
        this.hotelService = hotelService;
        this.securityAspect = securityAspect;
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return new ResponseEntity<>(hotelService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<Hotel> addNewHotel(@RequestHeader("Authorization") String authorization,
                                             @RequestBody Hotel hotel) {
        hotel.setManagerId(securityAspect.getUserId(authorization));
        return new ResponseEntity<>(hotelService.save(hotel), HttpStatus.CREATED);
    }

    @PutMapping
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<Hotel> updateHotel(@RequestHeader("Authorization") String authorization,
                                             @RequestBody Hotel newHotel) {
        Long managerId = securityAspect.getUserId(authorization);
        return new ResponseEntity<>(hotelService.update(managerId, newHotel), HttpStatus.OK);
    }

    /*@DeleteMapping(path = "/{id}")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> deleteHotel(@RequestHeader("Authorization") String authorization, @PathVariable Long id) {
        hotelService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }*/
}