package raf.edu.rs.reservationService.domain;

import javax.persistence.*;

@Entity
@Table
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String name;
    private String description;
    private Integer numberOfRooms;

    public Hotel(String city, String name, String description, Integer numberOfRooms) {
        this.city = city;
        this.name = name;
        this.description = description;
        this.numberOfRooms = numberOfRooms;
    }

    public Hotel() {}

    public Long getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }
}