package com.raceon.api.global.upload;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp"
    );
    private static final int MAX_WIDTH = 1080;
    private static final int MAX_HEIGHT = 1920;
    private static final long TARGET_SIZE_BYTES = 100 * 1024L; // 100KB

    @Value("${upload.base-path}")
    private String basePath;

    /**
     * 기록증 이미지 업로드 + 리사이즈 (100KB 미만 보장)
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
            byte[] originalBytes = file.getBytes();
            byte[] resized = compressUnderTarget(originalBytes, extension);
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(resized);
            }
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.");
        }

        return "/upload" + relativePath + filename;
    }

    /**
     * 100KB 미만이 될 때까지 품질 → 해상도 순으로 단계적 압축.
     * 1단계: 품질을 0.8 → 0.1 까지 0.1씩 감소
     * 2단계: 품질 0.1 유지, 해상도를 0.9배씩 축소
     */
    private byte[] compressUnderTarget(byte[] originalBytes, String extension) throws IOException {
        // 1단계: 품질 단계적 감소
        for (double quality = 0.8; quality >= 0.1 - 1e-9; quality -= 0.1) {
            byte[] result = resize(originalBytes, MAX_WIDTH, MAX_HEIGHT, quality, extension);
            if (result.length < TARGET_SIZE_BYTES) {
                return result;
            }
        }

        // 2단계: 해상도 축소 (품질은 0.1 고정)
        int width = MAX_WIDTH;
        int height = MAX_HEIGHT;
        while (width > 100 && height > 100) {
            width  = (int) (width  * 0.9);
            height = (int) (height * 0.9);
            byte[] result = resize(originalBytes, width, height, 0.1, extension);
            if (result.length < TARGET_SIZE_BYTES) {
                return result;
            }
        }

        // 최소 해상도까지 줄여도 초과 시 그대로 반환
        return resize(originalBytes, width, height, 0.1, extension);
    }

    private byte[] resize(byte[] src, int width, int height, double quality, String extension)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(src))
                .size(width, height)
                .keepAspectRatio(true)
                .outputQuality(quality)
                .outputFormat(toOutputFormat(extension))
                .toOutputStream(baos);
        return baos.toByteArray();
    }

    /** webp는 Thumbnailator 출력 포맷으로 지원 안 되므로 jpeg로 변환 */
    private String toOutputFormat(String extension) {
        return "webp".equals(extension) ? "jpeg" : extension;
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 확장자입니다. (jpg, jpeg, png, webp만 허용)");
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
