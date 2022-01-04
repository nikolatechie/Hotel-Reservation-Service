package raf.edu.rs.reservationService.service;

import org.springframework.stereotype.Service;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.domain.Review;
import raf.edu.rs.reservationService.repository.HotelRepository;
import raf.edu.rs.reservationService.repository.ReservationRepository;
import raf.edu.rs.reservationService.repository.ReviewRepository;
import java.util.List;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private HotelRepository hotelRepository;
    private ReservationRepository reservationRepository;

    public ReviewService(ReviewRepository reviewRepository, HotelRepository hotelRepository,
                         ReservationRepository reservationRepository) {
        this.reviewRepository = reviewRepository;
        this.hotelRepository = hotelRepository;
        this.reservationRepository = reservationRepository;
    }

    public Review save(Review review) {
        reviewRepository.save(review);
        return review;
    }

    public List<Hotel> getTopRatedHotels() {
        List<Hotel> topHotels = hotelRepository.findAll();


        return topHotels;
    }
}