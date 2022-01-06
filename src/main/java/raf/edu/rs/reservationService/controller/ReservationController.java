package raf.edu.rs.reservationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.edu.rs.reservationService.domain.Reservation;
import raf.edu.rs.reservationService.security.CheckSecurity;
import raf.edu.rs.reservationService.service.ReservationService;
import java.util.List;

@RestController
@RequestMapping(path = "/reservation")
public class ReservationController {
    private ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    @CheckSecurity(roles = {"ROLE_ADMIN"})
    public ResponseEntity<List<Reservation>> getAllReservations(@RequestHeader("Authorization") String authorization) {
        return new ResponseEntity<>(reservationService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    @CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<Reservation> addNewReservation(@RequestHeader("Authorization") String authorization,
                                                         @RequestBody Reservation reservation) {
        return new ResponseEntity<>(reservationService.save(reservation), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<Reservation> update(@RequestHeader("Authorization") String authorization,
                                              @PathVariable Long id, @RequestBody Reservation reservation) {
        return new ResponseEntity<>(reservationService.update(id, reservation), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER"})
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authorization, @PathVariable Long id) {
        reservationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}