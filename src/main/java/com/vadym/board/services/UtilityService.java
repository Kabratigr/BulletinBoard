package com.vadym.board.services;

import com.vadym.board.models.Announcement;
import com.vadym.board.models.Image;
import com.vadym.board.models.User;
import com.vadym.board.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UtilityService {

    private final UserRepository userRepository;

    public double currencyConversion(int price, String currency) {
        return switch (currency) {
            case "EUR" -> price * 1.06;
            case "UAH" -> price * 0.025;
            default -> price;
        };
    }

    public void handleImageFiles(Announcement announcement,
                                 MultipartFile imageFile1,
                                 MultipartFile imageFile2,
                                 MultipartFile imageFile3) throws IOException {
        if (imageFile1.getSize() != 0) {
            Image image1 = imageConversion(imageFile1);
            image1.setPreviewImage(true);
            announcement.addAnnouncementImage(image1);
        }
        if (imageFile2.getSize() != 0) {
            Image image2 = imageConversion(imageFile2);
            announcement.addAnnouncementImage(image2);
        }
        if (imageFile3.getSize() != 0) {
            Image image3 = imageConversion(imageFile3);
            announcement.addAnnouncementImage(image3);
        }
    }

    public Image imageConversion(MultipartFile imageFile) throws IOException {
        Image image = new Image();
        image.setImageSize(imageFile.getSize());
        image.setImageName(imageFile.getName());
        image.setOriginalImageName(imageFile.getOriginalFilename());
        image.setContentType(imageFile.getContentType());
        image.setBytes(imageFile.getBytes());
        return image;
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return new User();
        }
        return userRepository.findByEmail(principal.getName());
    }
}
