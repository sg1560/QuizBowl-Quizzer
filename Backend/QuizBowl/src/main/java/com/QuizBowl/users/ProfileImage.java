package com.QuizBowl.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class ProfileImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long iid;

    @JsonIgnore
    @OneToOne
    private Client client;

    private String imageName;

    @Lob
    @Column(name = "imagedata", length = 1000)
    private byte[] imageData;

    public ProfileImage() {
    }

    public ProfileImage(Client client, String imageName, byte[] imageData) {
        this.client = client;
        this.imageName = imageName;
        this.imageData = imageData;
    }

    public long getIid() {
        return iid;
    }

    public void setIid(long iid) {
        this.iid = iid;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public byte[] getImageData() {
        return imageData;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProfileImage)) return false;
        return iid == ((ProfileImage)o).iid;
    }
}
