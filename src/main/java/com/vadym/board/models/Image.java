package com.vadym.board.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "image_Size")
    private Long imageSize;
    @Column(name = "image_Name")
    private String imageName;
    @Column(name = "original_Image_Name")
    private String originalImageName;
    @Column(name = "content_Type")
    private String contentType;
    @Column(name = "is_Preview_Image")
    private boolean isPreviewImage;
    @Column(name = "bytes", columnDefinition = "LONGBLOB")
    private byte[] bytes;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Announcement announcement;
}
