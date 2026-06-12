package com.raceon.api.global.upload;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final int MAX_WIDTH = 1920;
    private static final int MAX_HEIGHT = 1920;
    private static final double OUTPUT_QUALITY = 0.8;

    @Value("${upload.base-path}")
    private String basePath;

    /**
     * 기록증 이미지 업로드 + 리사이즈
     * @return URL 경로 (e.g. /upload/recode/1/uuid.jpg)
     */
    public String uploadRecordImage(MultipartFile file, Long userIdx) {
        validateImageFile(file);

        String extension = extractExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + extension;
        String relativePath = "/recode/" + userIdx + "/";

        File dir = new File(basePath + relativePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("업로드 디렉토리 생성에 실패했습니다.");
        }

        File dest = new File(dir, filename);
        try {
            Thumbnails.of(file.getInputStream())
                    .size(MAX_WIDTH, MAX_HEIGHT)
                    .keepAspectRatio(true)
                    .outputQuality(OUTPUT_QUALITY)
                    .toFile(dest);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.");
        }

        return "/upload" + relativePath + filename;
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (jpg, png, webp만 허용)");
        }
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "jpg";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }
}
