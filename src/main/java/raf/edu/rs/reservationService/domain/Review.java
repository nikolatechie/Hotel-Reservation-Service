package raf.edu.rs.reservationService.domain;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long reservationId;

    @Min(1)
    @Max(5)
    @NotNull
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