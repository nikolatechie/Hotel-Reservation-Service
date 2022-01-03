package raf.edu.rs.reservationService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raf.edu.rs.reservationService.domain.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {}