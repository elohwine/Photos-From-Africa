package com.ken.infinity.models;

import javax.persistence.*;

@Entity
@Table(name = "workshop_register")
public class WorkshopRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String confirm;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    public WorkshopRegister() {}

    public WorkshopRegister(int id, String confirm, User user, Workshop workshop) {
        this.id = id;
        this.confirm = confirm;
        this.user = user;
        this.workshop = workshop;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
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

    public int getWorkshop_id() {
        return workshop != null ? workshop.getId() : 0;
    }

    public void setWorkshop_id(int workshop_id) {
        if (this.workshop == null) {
            this.workshop = new Workshop();
        }
        this.workshop.setId(workshop_id);
    }
}
