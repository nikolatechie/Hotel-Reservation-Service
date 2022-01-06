package raf.edu.rs.reservationService.service;

import org.springframework.stereotype.Service;
import raf.edu.rs.reservationService.domain.Hotel;
import raf.edu.rs.reservationService.domain.Reservation;
import raf.edu.rs.reservationService.domain.Review;
import raf.edu.rs.reservationService.exceptions.NotFoundException;
import raf.edu.rs.reservationService.repository.HotelRepository;
import raf.edu.rs.reservationService.repository.ReservationRepository;
import raf.edu.rs.reservationService.repository.ReviewRepository;

import java.util.*;

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
        if (!reservationRepository.existsById(review.getReservationId()))
            throw new NotFoundException("Reservation with id " + review.getReservationId() + " not found!");

        reviewRepository.save(review);
        return review;
    }

    public void delete(Long id, Long clientId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No review found!"));

        if (!review.getUserId().equals(clientId))
            throw new NotFoundException("No review found with your ID");

        reviewRepository.deleteById(id);
    }

    public Review update(Long id, Review newReview, Long clientId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review with id " + id + " not found!"));

        if (!review.getUserId().equals(clientId))
            throw new NotFoundException("Can't find reservation on your name!");

        review.setRating(newReview.getRating());
        review.setComment(newReview.getComment());
        reviewRepository.save(review);
        return review;
    }

    public List<Review> listReviews(String hotelName, String city) {
        List<Review> reviews = new ArrayList<>();

        for (Review review: reviewRepository.findAll()) {
            Reservation reservation = reservationRepository.findById(review.getReservationId())
                    .orElseThrow(() -> new NotFoundException("Not valid reservation ID."));

            Hotel hotel = hotelRepository.findById(reservation.getHotelId())
                .orElseThrow(() -> new NotFoundException("Hotel with id " + reservation.getHotelId() + " not found!"));

            if ((hotelName == null || hotelName.equals(hotel.getName())) &&
                    (city == null || city.equals(hotel.getCity())))
                reviews.add(review);
        }

        return (reviews.isEmpty() ? null : reviews);
    }

    public List<Hotel> getTopRatedHotels() {
        List<Hotel> topHotels = hotelRepository.findAll();
        HashMap<Hotel, Integer> cnt = new HashMap<>();
        HashMap<Hotel, Double> rating = new HashMap<>();

        for (Review review: reviewRepository.findAll()) {
            Reservation reservation = reservationRepository.getById(review.getReservationId());
            Hotel hotel = hotelRepository.getById(reservation.getHotelId());

            Integer val = 0;
            Double sum = 0.0;
            if (cnt.containsKey(hotel)) val = cnt.get(hotel);
            if (rating.containsKey(hotel)) sum = rating.get(hotel);

            cnt.put(hotel, val + 1);
            rating.put(hotel, sum + review.getRating());

            if (!topHotels.contains(hotel))
                topHotels.add(hotel);
        }

        topHotels.sort(Comparator.comparingDouble(o -> (rating.get(o) / cnt.get(o))));
        Collections.reverse(topHotels);

        return topHotels.subList(0, Math.min(3, topHotels.size()));
    }
}