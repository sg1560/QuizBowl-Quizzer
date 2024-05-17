package com.QuizBowl.admin;

import com.QuizBowl.users.ProfileImage;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long id;
    private String userName;
    private String password;
    public Admin(){}

    public Admin(long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return userName;
    }

    public void setName(String name) {
        this.userName = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId(){
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Admin)) return false;
        return id == ((Admin)o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
