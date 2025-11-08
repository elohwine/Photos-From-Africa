package com.ken.infinity.models;

import java.sql.Timestamp;
import javax.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;
    private int price;
    private String address;
    private String status;
    private Timestamp ordered_at;

    // Stripe or other provider identifiers
    private String paymentProvider; // e.g., "stripe"
    private String externalPaymentId; // e.g., Stripe PaymentIntent ID
    private String paymentStatus; // e.g., PENDING, SUCCEEDED, FAILED

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;

    public Order() {}

    public Order(int id, String email, int price, String address, String status, Timestamp ordered_at, User user, Photo photo) {
        this.id = id;
        this.email = email;
        this.price = price;
        this.address = address;
        this.status = status;
        this.ordered_at = ordered_at;
        this.user = user;
        this.photo = photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getOrdered_at() {
        return ordered_at;
    }

    public void setOrdered_at(Timestamp ordered_at) {
        this.ordered_at = ordered_at;
    }

    public String getPaymentProvider() {
        return paymentProvider;
    }

    public void setPaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public String getExternalPaymentId() {
        return externalPaymentId;
    }

    public void setExternalPaymentId(String externalPaymentId) {
        this.externalPaymentId = externalPaymentId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public int getUser_id() {
        return user != null ? user.getId() : 0;
    }

    public void setUser_id(int user_id) {
        if (this.user == null) {
            this.user = new User();
        }
        this.user.setId(user_id);
    }

    public int getPhoto_id() {
        return photo != null ? photo.getId() : 0;
    }

    public void setPhoto_id(int photo_id) {
        if (this.photo == null) {
            this.photo = new Photo();
        }
        this.photo.setId(photo_id);
    }
}
