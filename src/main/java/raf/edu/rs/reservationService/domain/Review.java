package raf.edu.rs.reservationService.domain;

import javax.persistence.*;

@Entity
@Table
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;
    private Integer rating;
    private String comment;

    public Review(Long reservationId, Integer rating, String comment) {
        this.reservationId = reservationId;
        this.rating = rating;
        this.comment = comment;
    }

    public Review() {}

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}