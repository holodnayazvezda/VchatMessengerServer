package com.example.vchatmessengerserver.files;

import com.example.vchatmessengerserver.exceptions.DataNotFoundException;
import com.example.vchatmessengerserver.files.avatar.AvatarService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

@Service
public class FilesService {

    @Autowired
    AvatarService avatarService;

    public static String getUploadDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return System.getProperty("user.dir") + "/uploads/";
        } else {
            return "/root/VchatMessengerServer/uploads/";
        }
    }

    private String getContentTypeByFileExtension(Path path) throws IOException {
        String contentType = Files.probeContentType(path);
        if(contentType == null) {
            String extension = FilenameUtils.getExtension(path.getFileName().toString()).toLowerCase(Locale.ROOT);

            return switch (extension) {
                case "png" -> "image/png";
                case "apng" -> "image/apng";
                case "avif" -> "image/avif";
                case "webp" -> "image/webp";
                case "bmp" -> "image/bmp";
                case "ico", "cur" -> "image/x-icon";
                case "gif" -> "image/gif";
                case "svg" -> "image/svg+xml";

                case "audio" -> "audio/aac";
                case "mp3" -> "audio/mpeg";
                case "oga", "opus" -> "audio/ogg";
                case "wav" -> "audio/wav";
                case "weba" -> "audio/webm";

                case "avi" -> "video/x-msvideo";
                case "mp4" -> "video/mp4";
                case "mpeg" -> "video/mpeg";
                case "ogv" -> "video/ogg";
                case "webm" -> "video/webm";

                default -> "image/jpeg";
            };
        }
        return contentType;
    }

    public void createDirectoryIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public boolean checkIfFileExists(String filename) {
        File file = download(filename);
        return file.exists();
    }

    public void upload(MultipartFile file, String fileName) throws IOException {
        Path path = Paths.get(getUploadDirectory()).toAbsolutePath().normalize();
        createDirectoryIfNotExists(path.toString());

        String imagePath = path + "/" + fileName;
        Files.copy(
                file.getInputStream(),
                Paths.get(imagePath)
        );
    }

    public void upload(MultipartFile file) throws IOException {
        upload(file, Objects.requireNonNull(file.getOriginalFilename()));
    }

    public String uploadAvatar(MultipartFile file) throws IOException {
        String avatarFilename = avatarService.getAvatarFileName(file);
        upload(file, avatarFilename);
        return avatarFilename;
    }

    public File download(String filename) {
        Path path = Paths.get(getUploadDirectory()).toAbsolutePath().normalize();
        createDirectoryIfNotExists(path.toString());
        return new File(path + "/" + filename);
    }

    public ViewFileDTO view(String filename) throws IOException {
        File file = download(filename);

        if (file.exists()) {
            Path path = Paths.get(file.getAbsolutePath());

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            String contentType = getContentTypeByFileExtension(path);
            String contentDisposition = "inline;filename=\"" + file.getName() + "\"";

            return new ViewFileDTO(resource, contentType, contentDisposition);
        }

        throw new DataNotFoundException();
    }
}
