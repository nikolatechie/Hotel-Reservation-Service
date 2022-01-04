package raf.edu.rs.reservationService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raf.edu.rs.reservationService.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {}