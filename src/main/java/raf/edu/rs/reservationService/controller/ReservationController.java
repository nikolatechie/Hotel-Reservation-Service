package raf.edu.rs.reservationService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    @GetMapping
    public String printMessage() {
        return "Reservation webpage";
    }
}