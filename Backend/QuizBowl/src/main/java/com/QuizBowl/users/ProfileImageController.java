package com.QuizBowl.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ProfileImageController {
    @Autowired
    private ClientController clientController;

    @Autowired
    private ProfileImageRepository profileImageRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "image/getbyid/{cid}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getImageById(@PathVariable long cid) {
        Client client = userRepository.findById(cid).orElseThrow();
        ProfileImage image = client.getProfileImage();
        if (image == null) return null;
        return image.getImageData();
    }

    @PostMapping(value = "image/post/{cid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void handleFileUpload(@PathVariable long cid, @RequestParam("image") MultipartFile imageFile) throws IOException {
        handleFileDelete(cid);
        Client client = clientController.getUserById(cid);
        ProfileImage image = new ProfileImage(client, "ProfilePicture", imageFile.getBytes());
        client.setProfileImage(image);
        profileImageRepository.save(image);
        userRepository.save(client);
    }

    @DeleteMapping("image/delete/{cid}")
    public void handleFileDelete(@PathVariable long cid) {
        Client client = clientController.getUserById(cid);
        if (client.getProfileImage() == null) return;
        ProfileImage profileImage = profileImageRepository.findById(client.getProfileImage().getIid()).orElseThrow();
        client.setProfileImage(null);
        profileImageRepository.delete(profileImage);
        userRepository.save(client);
    }
}
