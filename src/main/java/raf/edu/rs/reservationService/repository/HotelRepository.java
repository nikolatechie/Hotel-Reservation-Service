package raf.edu.rs.reservationService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raf.edu.rs.reservationService.domain.Hotel;

public interface HotelRepository extends JpaRepository<Hotel, Long> {}