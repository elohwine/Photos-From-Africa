package com.ken.infinity.models;

import javax.persistence.*;

@Entity
@Table(name = "photoshoot_register")
public class PhotoshootRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String confirm;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "photoshoot_id")
    private Photoshoot photoshoot;

    public PhotoshootRegister() {}

    public PhotoshootRegister(int id, String confirm, User user, Photoshoot photoshoot) {
        this.id = id;
        this.confirm = confirm;
        this.user = user;
        this.photoshoot = photoshoot;
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

    public Photoshoot getPhotoshoot() {
        return photoshoot;
    }

    public void setPhotoshoot(Photoshoot photoshoot) {
        this.photoshoot = photoshoot;
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

    public int getPhotoshoot_id() {
        return photoshoot != null ? photoshoot.getId() : 0;
    }

    public void setPhotoshoot_id(int photoshoot_id) {
        if (this.photoshoot == null) {
            this.photoshoot = new Photoshoot();
        }
        this.photoshoot.setId(photoshoot_id);
    }
}
