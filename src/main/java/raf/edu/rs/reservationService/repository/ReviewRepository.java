package raf.edu.rs.reservationService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raf.edu.rs.reservationService.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {}