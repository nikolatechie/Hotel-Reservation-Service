package raf.edu.rs.reservationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.edu.rs.reservationService.domain.Room;
import raf.edu.rs.reservationService.security.CheckSecurity;
import raf.edu.rs.reservationService.service.RoomService;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/room")
public class RoomController {
    private RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return new ResponseEntity<>(roomService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<Room> save(@RequestBody Room newRoom) {
        return new ResponseEntity<>(roomService.save(newRoom), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{id}")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<Room> update(@PathVariable Long id, @RequestBody Room newRoom) {
        return new ResponseEntity<>(roomService.update(id, newRoom), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> delete(@PathVariable Long id) {
        roomService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/list")
    public ResponseEntity<List<Room>> listAvailableRooms(
            @RequestParam(required = false, value = "city") String city,
            @RequestParam(required = false, value = "hotelName") String hotelName,
            @RequestParam(required = false, value = "startDate") String date1,
            @RequestParam(required = false, value = "endDate") String date2,
            @RequestParam(required = false, value = "sort") String sort) {

        LocalDate startDate = LocalDate.now().plusYears(1), endDate = LocalDate.now().plusYears(1);

        if (date1 != null)
            startDate = LocalDate.of(
                Integer.parseInt(date1.substring(0, 4)),
                Integer.parseInt(date1.substring(5, 7)),
                Integer.parseInt(date1.substring(8))
            );

        if (date2 != null)
            endDate = LocalDate.of(
                Integer.parseInt(date2.substring(0, 4)),
                Integer.parseInt(date2.substring(5, 7)),
                Integer.parseInt(date2.substring(8))
            );

        return new ResponseEntity<>(roomService.findAllAvailable(city, hotelName, startDate, endDate, sort),
                HttpStatus.OK);
    }
}