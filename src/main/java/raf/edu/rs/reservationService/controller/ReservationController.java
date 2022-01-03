package raf.edu.rs.reservationService.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.domain.RoomType;
import raf.edu.rs.reservationService.repository.ReservationRepository;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    private ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public String getMessage() {
        return "<h1>Welcome message</h1>";
    }
}