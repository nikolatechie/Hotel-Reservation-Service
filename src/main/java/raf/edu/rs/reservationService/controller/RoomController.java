package raf.edu.rs.reservationService.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import raf.edu.rs.reservationService.domain.Room;
import raf.edu.rs.reservationService.security.CheckSecurity;
import raf.edu.rs.reservationService.security.SecurityAspect;
import raf.edu.rs.reservationService.service.RoomService;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/room")
public class RoomController {
    private RoomService roomService;
    private SecurityAspect securityAspect;

    public RoomController(RoomService roomService, SecurityAspect securityAspect) {
        this.roomService = roomService;
        this.securityAspect = securityAspect;
    }

    @GetMapping
    @CheckSecurity(roles = {"ROLE_CLIENT", "ROLE_MANAGER", "ROLE_ADMIN"})
    public ResponseEntity<List<Room>> getAllRooms(@RequestHeader("Authorization") String authorization) {
        return new ResponseEntity<>(roomService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<Room> save(@RequestHeader("Authorization") String authorization, @RequestBody Room newRoom) {
        Long managerId = securityAspect.getUserId(authorization);
        return new ResponseEntity<>(roomService.save(newRoom, managerId), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{roomNumber}")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<Room> update(@RequestHeader("Authorization") String authorization,
                                       @PathVariable Integer roomNumber, @RequestBody Room newRoom) {
        Long managerId = securityAspect.getUserId(authorization);
        return new ResponseEntity<>(roomService.update(roomNumber, newRoom, managerId), HttpStatus.OK);
    }

    @GetMapping(path = "/rangeUpdate")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> rangeUpdate(@RequestHeader("Authorization") String authorization,
                                         @RequestParam("startPrice") Double startPrice,
                                         @RequestParam("endPrice") Double endPrice,
                                         @RequestParam("type") String type) {
        System.out.println("TU SAM " + startPrice + " " + endPrice + " " + type);
        Long managerId = securityAspect.getUserId(authorization);
        roomService.rangeUpdate(managerId, startPrice, endPrice, type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*@DeleteMapping(path = "/{id}")
    @CheckSecurity(roles = {"ROLE_MANAGER"})
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authorization, @PathVariable Long id) {
        roomService.delete(id, securityAspect.getUserId(authorization));
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    @GetMapping(path = "/list")
    public ResponseEntity<List<Room>> listAvailableRooms(
            @RequestParam(required = false, value = "city") String city,
            @RequestParam(required = false, value = "hotelName") String hotelName,
            @RequestParam(required = false, value = "roomType") String roomType,
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

        return new ResponseEntity<>(roomService.findAllAvailable(city, hotelName, roomType, startDate, endDate, sort),
                HttpStatus.OK);
    }
}