package com.vadym.board.controllers;

import com.vadym.board.models.Announcement;
import com.vadym.board.services.AnnouncementService;
import com.vadym.board.services.UtilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    private final UtilityService utilityService;

    @GetMapping("/")
    public String announcements(@RequestParam(name = "title", required = false) String title,
                                @RequestParam(name = "sort", required = false) String sort,
                                Model model, Principal principal) {
        model.addAttribute("user", utilityService.getUserByPrincipal(principal));
        if (sort != null && !sort.isEmpty()) {
            model.addAttribute("announcements", announcementService.sortAnnouncements(
                    announcementService.getAllAnnouncements(), sort));
        } else {
            model.addAttribute("announcements", announcementService.filterByTitle(title));
        }
        return "main-page";
    }

    @GetMapping("/details/{id}")
    public String announcementDetails(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("announcement", announcementService.getAnnouncementById(id));
        model.addAttribute("images", announcementService.getAnnouncementById(id).getImages());
        model.addAttribute("user", utilityService.getUserByPrincipal(principal));
        return "announcement-details";
    }

    @PreAuthorize("#announcement.user.email.equals(authentication.principal.email)")
    @GetMapping("/{announcement}/edit")
    public String editAnnouncementForm(@PathVariable(name = "announcement") Announcement announcement, Model model) {
        model.addAttribute("announcement", announcement);
        return "announcement-edit";
    }

    @PreAuthorize("#announcement.user.email.equals(authentication.principal.email)")
    @PostMapping("/{announcement}/edit")
    public String editAnnouncement(@RequestParam(name = "title", required = false) String title,
                                 @RequestParam(name = "category", required = false) String category,
                                 @RequestParam(name = "price", required = false) int price,
                                 @RequestParam(name = "currency", required = false) String currency,
                                 @RequestParam(name = "city", required = false) String city,
                                 @RequestParam(name = "description", required = false) String description,
                                 @RequestParam(name = "imageFile1") MultipartFile imageFile1,
                                 @RequestParam(name = "imageFile2") MultipartFile imageFile2,
                                 @RequestParam(name = "imageFile3") MultipartFile imageFile3,
                                 @PathVariable(name = "announcement") Announcement announcement) throws IOException {
        announcementService.updateAnnouncement(announcement, title, category, price, currency, city, description, imageFile1, imageFile2, imageFile3);
        return "redirect:/";
    }

    @GetMapping("/filter/{category}")
    public String announcementCategories(@PathVariable String category,
                                         @RequestParam(name = "sort", required = false) String sort,
                                         Model model, Principal principal) {
        model.addAttribute("user", utilityService.getUserByPrincipal(principal));
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
                                  Announcement announcement, Principal principal) throws IOException {
        announcementService.addAnnouncement(principal, announcement, imageFile1, imageFile2, imageFile3);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return "redirect:/";
    }
}
