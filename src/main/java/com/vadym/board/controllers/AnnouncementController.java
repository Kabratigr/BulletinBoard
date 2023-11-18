package com.vadym.board.controllers;

import com.vadym.board.model.Announcement;
import com.vadym.board.services.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/")
    public String announcements(@RequestParam(name = "title", required = false) String title, Model model) {
        model.addAttribute("announcements", announcementService.filterByTitle(title));
        return "main-page";
    }

    @GetMapping("/details/{id}")
    public String announcementDetails(@PathVariable Long id, Model model) {
        model.addAttribute("announcement", announcementService.getAnnouncementById(id));
        return "announcement-details";
    }

    @GetMapping("/filter/{category}")
    public String announcementCategories(@PathVariable String category, Model model) {
        model.addAttribute("announcements", announcementService.getAnnouncementsByCategory(category));
        return "announcement-category";
    }

    @PostMapping("/filter")
    public String announcementsSearchOnCategoriesPage(@RequestParam(name = "title", required = false) String title,
                                                      @RequestParam(name = "category", required = false) String category,
                                                      Model model) {
        model.addAttribute("announcements", announcementService.filterOnCategoryPage(title, category));
        model.addAttribute("category", category);
        return "announcement-category";
    }

    @GetMapping("/add")
    public String addAnnouncementPage() {
        return "add-announcement";
    }

    @PostMapping("/add")
    public String addAnnouncement(Announcement announcement) {
        announcementService.addAnnouncement(announcement);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/";
    }
}
