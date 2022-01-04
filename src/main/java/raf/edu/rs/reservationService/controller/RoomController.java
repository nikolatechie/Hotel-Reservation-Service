package raf.edu.rs.reservationService.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raf.edu.rs.reservationService.domain.Room;
import raf.edu.rs.reservationService.service.RoomService;
import java.util.List;

@RestController
@RequestMapping(path = "/api/room")
public class RoomController {
    private RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
}