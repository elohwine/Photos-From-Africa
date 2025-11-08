package com.ken.infinity.models;

import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "workshop")
public class Workshop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private String description;
    private String mode;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    private Timestamp datetime;
    private String venue;
    private int total_seats;
    private int registered_seats;
    private String imgUrl;
    private String status;

    public Workshop() {}

    public Workshop(int id, String title, String description, String mode, User organizer, Timestamp datetime, String venue, int total_seats, int registered_seats, String imgUrl, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.mode = mode;
        this.organizer = organizer;
        this.datetime = datetime;
        this.venue = venue;
        this.total_seats = total_seats;
        this.registered_seats = registered_seats;
        this.imgUrl = imgUrl;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public int getOrganizer_id() {
        return organizer != null ? organizer.getId() : 0;
    }

    public void setOrganizer_id(int organizer_id) {
        if (this.organizer == null) {
            this.organizer = new User();
        }
        this.organizer.setId(organizer_id);
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getTotal_seats() {
        return total_seats;
    }

    public void setTotal_seats(int total_seats) {
        this.total_seats = total_seats;
    }

    public int getRegistered_seats() {
        return registered_seats;
    }

    public void setRegistered_seats(int registered_seats) {
        this.registered_seats = registered_seats;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
