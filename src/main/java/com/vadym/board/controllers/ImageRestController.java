package com.vadym.board.controllers;

import com.vadym.board.models.Announcement;
import com.vadym.board.models.Image;
import com.vadym.board.repositories.AnnouncementRepository;
import com.vadym.board.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ImageRestController {

    private final ImageRepository imageRepository;

    private final AnnouncementRepository announcementRepository;

    @GetMapping("/images/{id}")
    private ResponseEntity<?> getImageById(@PathVariable Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        return ResponseEntity.ok()
                .header("fileName", image.getOriginalImageName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getImageSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getBytes())));
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            Announcement announcement = image.getAnnouncement();
            announcement.getImages().remove(image);
            imageRepository.delete(image);
            if (image.isPreviewImage()) {
                announcement.setPreviewImageId(null);
            }
            announcementRepository.save(announcement);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
