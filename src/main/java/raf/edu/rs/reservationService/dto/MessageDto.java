package raf.edu.rs.reservationService.dto;

public class MessageDto {
    private String type;
    private String name;
    private String lastname;
    private String hotelName;
    private Long reservationId;
    private String link;
    private String email;

    public MessageDto(String type, String name, String lastname, String email, String hotelName, String link,
                      Long reservationId) {
        this.type = type;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.hotelName = hotelName;
        this.link = link;
        this.reservationId = reservationId;
    }

    public MessageDto() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
}