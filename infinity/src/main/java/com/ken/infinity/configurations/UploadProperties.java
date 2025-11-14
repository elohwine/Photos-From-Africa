package com.ken.infinity.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UploadProperties {
    // Prefer environment variable UPLOAD_BASE_DIR, then application property upload.base-dir
    @Value("${upload.base-dir:}")
    private String uploadBaseDirProp;

    public String getBaseDir() {
        String env = System.getenv("UPLOAD_BASE_DIR");
        if (env != null && !env.isBlank()) return env;
        if (uploadBaseDirProp != null && !uploadBaseDirProp.isBlank()) return uploadBaseDirProp;
        // default for local development (serves files from Spring Boot static resources)
        return "src/main/resources/static";
    }
}
