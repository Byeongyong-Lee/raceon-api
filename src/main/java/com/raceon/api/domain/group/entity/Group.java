package com.raceon.api.domain.group.entity;

import com.raceon.api.global.upload.Image;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_idx")
    private Long groupIdx;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "group_members")
    private Integer groupMembers;

    @Column(name = "manager_members")
    private Integer managerMembers;

    @Column(name = "area_code", length = 20)
    private String areaCode;

    @Column(name = "tag1", length = 50)
    private String tag1;

    @Column(name = "tag2", length = 50)
    private String tag2;

    @Column(name = "tag3", length = 50)
    private String tag3;

    @Column(name = "tag4", length = 50)
    private String tag4;

    @Column(name = "tag5", length = 50)
    private String tag5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_idx")
    private Image profileImage;

    @Column(name = "owner_idx", nullable = false)
    private Long ownerIdx;

    @Column(name = "del_at", nullable = false, length = 1)
    @Builder.Default
    private String delAt = "N";

    @CreationTimestamp
    @Column(name = "create_dt", nullable = false, updatable = false)
    private LocalDateTime createDt;

    @UpdateTimestamp
    @Column(name = "update_dt", nullable = false)
    private LocalDateTime updateDt;

    public void update(String name, String description, Image profileImage,
                       Integer groupMembers, Integer managerMembers, String areaCode,
                       String tag1, String tag2, String tag3, String tag4, String tag5) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (profileImage != null) this.profileImage = profileImage;
        if (groupMembers != null) this.groupMembers = groupMembers;
        if (managerMembers != null) this.managerMembers = managerMembers;
        if (areaCode != null) this.areaCode = areaCode;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.tag4 = tag4;
        this.tag5 = tag5;
    }

    public void updateProfileImage(Image profileImage) {
        this.profileImage = profileImage;
    }

    public void delete() {
        this.delAt = "Y";
    }
}
