package com.raceon.api.global.upload;

import lombok.Getter;

@Getter
public class ImageResponse {
    private final Long imageIdx;
    private final String filePath;

    public ImageResponse(Image image) {
        this.imageIdx = image.getImageIdx();
        this.filePath = image.getFilePath();
    }
}
