package com.vadym.board.services;

import com.vadym.board.models.Announcement;
import com.vadym.board.models.Image;
import com.vadym.board.repositories.AnnouncementRepository;
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

    private final UtilityService utilityService;

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

    public List<Announcement> sortAnnouncements(List<Announcement> announcements, String sortOrder) {
        announcements.sort((announcement1, announcement2) -> {
            if (sortOrder.equals("priceAscending") || sortOrder.equals("priceDescending")) {
                double price1 = utilityService.currencyConversion(announcement1.getPrice(), announcement1.getCurrency());
                double price2 = utilityService.currencyConversion(announcement2.getPrice(), announcement2.getCurrency());
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
        announcement.setUser(utilityService.getUserByPrincipal(principal));
        Image image1;
        Image image2;
        Image image3;
        if (imageFile1.getSize() != 0) {
            image1 = utilityService.imageConversion(imageFile1);
            image1.setPreviewImage(true);
            announcement.addAnnouncementImage(image1);
        }
        if (imageFile2.getSize() != 0) {
            image2 = utilityService.imageConversion(imageFile2);
            announcement.addAnnouncementImage(image2);
        }
        if (imageFile3.getSize() != 0) {
            image3 = utilityService.imageConversion(imageFile3);
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
