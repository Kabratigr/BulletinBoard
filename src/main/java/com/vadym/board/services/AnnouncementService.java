package com.vadym.board.services;

import com.vadym.board.models.Announcement;
import com.vadym.board.models.Image;
import com.vadym.board.models.User;
import com.vadym.board.repositories.AnnouncementRepository;
import com.vadym.board.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;

    private final UserRepository userRepository;

    public List<Announcement> filterByTitle(String title) {
        if (title != null && !title.isEmpty()) {
            List<String> searchByKeyWords = Arrays.asList(title.split("\\s+"));
            return searchByKeyWords.stream()
                    .map(keyword -> announcementRepository.findByTitleContainingIgnoreCase(title))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
        return announcementRepository.findAll();
    }

    public List<Announcement> filterOnCategoryPage(String title, String category) {
        if (!title.isEmpty() && !category.isEmpty()) {
            List<String> searchByKeyWords = Arrays.asList(title.split("\\s+"));
            return searchByKeyWords.stream()
                    .map(keyword -> announcementRepository.findByTitleContainingIgnoreCaseAndCategory(title, category))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        }
        return announcementRepository.findByCategory(category);
    }

    private double currencyConversion(int price, String currency) {
        return switch (currency) {
            case "EUR" -> price * 1.06;
            case "UAH" -> price * 0.025;
            default -> price;
        };
    }

    public List<Announcement> sortAnnouncements(List<Announcement> announcements, String sortOrder) {
        announcements.sort((announcement1, announcement2) -> {
            if (sortOrder.equals("priceAscending") || sortOrder.equals("priceDescending")) {
                double price1 = currencyConversion(announcement1.getPrice(), announcement1.getCurrency());
                double price2 = currencyConversion(announcement2.getPrice(), announcement2.getCurrency());
                return sortOrder.equals("priceAscending") ? Double.compare(price1, price2) : Double.compare(price2, price1);
            } else if (sortOrder.equals("dateAscending") || sortOrder.equals("dateDescending")) {
                LocalDateTime dateTime1 = announcement1.getCreationDate();
                LocalDateTime dateTime2 = announcement2.getCreationDate();
                return sortOrder.equals("dateAscending") ? dateTime1.compareTo(dateTime2) : dateTime2.compareTo(dateTime1);
            }
            return 0;
        });
        return announcements;
    }

    public void addAnnouncement(Principal principal,
                                Announcement announcement,
                                MultipartFile imageFile1,
                                MultipartFile imageFile2,
                                MultipartFile imageFile3) throws IOException {
        announcement.setUser(getUserByPrincipal(principal));
        Image image1;
        Image image2;
        Image image3;
        if (imageFile1.getSize() != 0) {
            image1 = imageConversion(imageFile1);
            image1.setPreviewImage(true);
            announcement.addAnnouncementImage(image1);
        }
        if (imageFile2.getSize() != 0) {
            image2 = imageConversion(imageFile2);
            announcement.addAnnouncementImage(image2);
        }
        if (imageFile3.getSize() != 0) {
            image3 = imageConversion(imageFile3);
            announcement.addAnnouncementImage(image3);
        }
        Announcement announcementFromDb = announcementRepository.save(announcement);
        if (!announcementFromDb.getImages().isEmpty()) {
            announcementFromDb.setPreviewImageId(announcementFromDb.getImages().get(0).getId());
        } else {
            announcementFromDb.setPreviewImageId(null);
        }
        announcementRepository.save(announcement);
    }

    public User getUserByPrincipal(Principal principal) {
        if (principal == null) {
            return new User();
        }
        return userRepository.findByEmail(principal.getName());
    }

    private Image imageConversion(MultipartFile imageFile) throws IOException {
        Image image = new Image();
        image.setImageSize(imageFile.getSize());
        image.setImageName(imageFile.getName());
        image.setOriginalImageName(imageFile.getOriginalFilename());
        image.setContentType(imageFile.getContentType());
        image.setBytes(imageFile.getBytes());
        return image;
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }

    public List<Announcement> getAnnouncementsByCategory(String category) {
        return announcementRepository.findByCategory(category);
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }
}
