package raf.edu.rs.reservationService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raf.edu.rs.reservationService.repository.ReservationRepository;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public String printMessage() {
        return "Reservation webpage";
    }
}