package com.raceon.api.global.upload;

public enum UploadType {
    RECORD("recode"),
    PROFILE("profile"),
    GROUP("group");

    private final String directory;

    UploadType(String directory) {
        this.directory = directory;
    }

    public String buildRelativePath(Long id) {
        return "/" + directory + "/" + id + "/";
    }
}
