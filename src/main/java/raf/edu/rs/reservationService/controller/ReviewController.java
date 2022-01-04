package raf.edu.rs.reservationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.domain.Review;
import raf.edu.rs.reservationService.service.ReviewService;
import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    private ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Hotel>> getTopRatedHotels() {
        return new ResponseEntity<>(reviewService.getTopRatedHotels(), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Review> addNewReview(@RequestBody Review review) {
        return new ResponseEntity<>(reviewService.save(review), HttpStatus.CREATED);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Review>> getAllReviews(
            @RequestParam(value = "hotelName") String hotelName,
            @RequestParam(value = "city") String city) {
        // ?
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // TODO: i ostale operacije...
}