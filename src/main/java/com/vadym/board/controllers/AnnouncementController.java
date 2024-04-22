package com.vadym.board.controllers;

import com.vadym.board.models.Announcement;
import com.vadym.board.services.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/")
    public String announcements(@RequestParam(name = "title", required = false) String title,
                                @RequestParam(name = "sort", required = false) String sort,
                                Model model) {
        if (sort != null && !sort.isEmpty()) {
            model.addAttribute("announcements", announcementService.sortAnnouncements(
                    announcementService.getAllAnnouncements(), sort));
        } else {
            model.addAttribute("announcements", announcementService.filterByTitle(title));
        }
        return "main-page";
    }

    @GetMapping("/details/{id}")
    public String announcementDetails(@PathVariable Long id, Model model) {
        model.addAttribute("announcement", announcementService.getAnnouncementById(id));
        model.addAttribute("images", announcementService.getAnnouncementById(id).getImages());
        return "announcement-details";
    }

    @GetMapping("/filter/{category}")
    public String announcementCategories(@PathVariable String category,
                                         @RequestParam(name = "sort", required = false) String sort,
                                         Model model) {
        if (sort != null && !sort.isEmpty()) {
            model.addAttribute("announcements", announcementService.sortAnnouncements(
                    announcementService.getAnnouncementsByCategory(category), sort));
        } else {
            model.addAttribute("announcements", announcementService.getAnnouncementsByCategory(category));
        }
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
    public String addAnnouncement(@RequestParam(name = "imageFile1") MultipartFile imageFile1,
                                  @RequestParam(name = "imageFile2") MultipartFile imageFile2,
                                  @RequestParam(name = "imageFile3") MultipartFile imageFile3,
                                  Announcement announcement) throws IOException {
        announcementService.addAnnouncement(announcement, imageFile1, imageFile2, imageFile3);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/";
    }
}
