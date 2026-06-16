package com.raceon.api.global.upload;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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

    private final ImageRepository imageRepository;

    /**
     * 공통 이미지 업로드 + 리사이즈 (100KB 미만 보장) + DB 저장
     *
     * @param file       업로드 파일
     * @param uploadType 업로드 유형 (RECORD, PROFILE, GROUP)
     * @param ownerId    경로에 사용할 식별자 (userIdx, groupIdx 등)
     * @return 저장된 Image 엔티티
     */
    @Transactional
    public Image upload(MultipartFile file, UploadType uploadType, Long ownerId) {
        validateImageFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String storedExtension = extension.equals("webp") ? "jpg" : extension;
        String filename = UUID.randomUUID() + "." + storedExtension;
        String relativePath = uploadType.buildRelativePath(ownerId);

        File dir = new File(basePath + relativePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("업로드 디렉토리 생성에 실패했습니다.");
        }

        byte[] resized;
        long fileSize;
        try {
            byte[] originalBytes = file.getBytes();
            resized = compressUnderTarget(originalBytes, extension);
            fileSize = resized.length;
        } catch (IOException e) {
            throw new IllegalStateException("파일 처리에 실패했습니다.");
        }

        File dest = new File(dir, filename);
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(resized);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.");
        }

        String filePath = "/upload" + relativePath + filename;
        String contentType = "webp".equals(extension) ? "image/jpeg" : file.getContentType();

        Image image = Image.builder()
                .uploadType(uploadType)
                .ownerIdx(ownerId)
                .filePath(filePath)
                .originalFilename(originalFilename)
                .fileSize(fileSize)
                .contentType(contentType)
                .build();

        return imageRepository.save(image);
    }

    /** 기록증 이미지 업로드 */
    public Image uploadRecordImage(MultipartFile file, Long userIdx) {
        return upload(file, UploadType.RECORD, userIdx);
    }

    /** 사용자 프로필 이미지 업로드 */
    public Image uploadProfileImage(MultipartFile file, Long userIdx) {
        return upload(file, UploadType.PROFILE, userIdx);
    }

    /** 모임 프로필 이미지 업로드 */
    public Image uploadGroupImage(MultipartFile file, Long groupIdx) {
        return upload(file, UploadType.GROUP, groupIdx);
    }

    /**
     * 100KB 미만이 될 때까지 품질 → 해상도 순으로 단계적 압축.
     * 1단계: 품질을 0.8 → 0.1 까지 0.1씩 감소
     * 2단계: 품질 0.1 유지, 해상도를 0.9배씩 축소
     */
    private byte[] compressUnderTarget(byte[] originalBytes, String extension) throws IOException {
        for (double quality = 0.8; quality >= 0.1 - 1e-9; quality -= 0.1) {
            byte[] result = resize(originalBytes, MAX_WIDTH, MAX_HEIGHT, quality, extension);
            if (result.length < TARGET_SIZE_BYTES) {
                return result;
            }
        }

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
