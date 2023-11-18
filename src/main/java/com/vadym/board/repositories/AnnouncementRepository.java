package com.vadym.board.repositories;

import com.vadym.board.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByTitleContainingIgnoreCaseAndCategory(String title, String category);
    List<Announcement> findByTitleContainingIgnoreCase(String title);
    List<Announcement> findByCategory(String category);
}
