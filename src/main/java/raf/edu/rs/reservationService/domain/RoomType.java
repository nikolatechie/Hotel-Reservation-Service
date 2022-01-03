package raf.edu.rs.reservationService.domain;

import javax.persistence.*;

@Entity
@Table
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    private String type;
    private Double pricePerDay;
}