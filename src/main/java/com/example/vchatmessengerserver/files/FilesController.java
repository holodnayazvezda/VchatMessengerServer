package com.example.vchatmessengerserver.files;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FilesController {
    @Autowired
    FilesService filesService;

    @Operation(description = "Upload File")
    @SecurityRequirement(name = "basicAuth")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(
            Authentication authentication,
            @RequestParam MultipartFile file
    ) throws IOException {
        // TODO: implement authentication logic
        filesService.upload(file);
    }

    @Operation(description = "Upload File")
    @PostMapping(value = "/upload_avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(
            @RequestParam MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(
                    filesService.uploadAvatar(file)
        );
    }

    @GetMapping("/download")
    @SecurityRequirement(name = "basicAuth")
    public void download(
            Authentication authentication,
            @RequestParam String filename,
            HttpServletResponse response
    ) throws IOException {
        // TODO: implement authentication logic
        File file = filesService.download(filename);
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        Files.copy(file.toPath(), response.getOutputStream());
    }

    @GetMapping(value = "/view", produces = MediaType.ALL_VALUE)
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Resource> view(
            Authentication authentication,
            @RequestParam String filename
    ) throws IOException {
        ViewFileDTO viewFileDTO = filesService.view(filename);
        // TODO: implement authentication logic
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(viewFileDTO.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, viewFileDTO.contentDisposition())
                .body(viewFileDTO.fileResource());
    }
}
