package com.vadym.board.services;

import com.vadym.board.model.Announcement;
import com.vadym.board.repositories.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;

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

    public void addAnnouncement(Announcement announcement) {
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
}
