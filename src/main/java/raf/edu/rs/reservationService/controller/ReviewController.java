package raf.edu.rs.reservationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.domain.Review;
import raf.edu.rs.reservationService.security.CheckSecurity;
import raf.edu.rs.reservationService.security.SecurityAspect;
import raf.edu.rs.reservationService.service.ReviewService;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {
    private ReviewService reviewService;
    private SecurityAspect securityAspect;

    public ReviewController(ReviewService reviewService, SecurityAspect securityAspect) {
        this.reviewService = reviewService;
        this.securityAspect = securityAspect;
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getTopRatedHotels() {
        return new ResponseEntity<>(reviewService.getTopRatedHotels(), HttpStatus.OK);
    }

    @PostMapping
    @CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<Review> addNewReview(@RequestHeader("Authorization") String authorization,
                                               @RequestBody Review review) {
        review.setUserId(securityAspect.getUserId(authorization));
        return new ResponseEntity<>(reviewService.save(review), HttpStatus.CREATED);
    }

    @PutMapping
    @CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<Review> updateReview(@RequestHeader("Authorization") String authorization,
                                               @RequestBody @NotNull Long id, @RequestBody @NotNull Review review) {
        Long clientId = securityAspect.getUserId(authorization);
        return new ResponseEntity<>(reviewService.update(id, review, clientId), HttpStatus.OK);
    }

    @DeleteMapping
    @CheckSecurity(roles = {"ROLE_CLIENT"})
    public ResponseEntity<Review> deleteReview(@RequestHeader("Authorization") String authorization,
                                               @RequestBody Long id) {
        reviewService.delete(id, securityAspect.getUserId(authorization));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Review>> getAllReviews(
            @RequestParam(required = false, value = "hotelName") String hotelName,
            @RequestParam(required = false, value = "city") String city) {
        return new ResponseEntity<>(reviewService.listReviews(hotelName, city), HttpStatus.OK);
    }
}