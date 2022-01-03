package raf.edu.rs.reservationService.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Hotel hotel;

    @ManyToOne
    private RoomType roomType;

    private LocalDate startDate;
    private LocalDate endDate;
}