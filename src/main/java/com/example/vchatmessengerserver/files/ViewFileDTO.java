package com.example.vchatmessengerserver.files;
import org.springframework.core.io.ByteArrayResource;

public record ViewFileDTO(
        ByteArrayResource fileResource,
        String contentType,
        String contentDisposition
) {}
