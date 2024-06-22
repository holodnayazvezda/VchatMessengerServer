package com.example.vchatmessengerserver.files.avatar;

import com.example.vchatmessengerserver.exceptions.IncorrectDataException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class AvatarService {

    private static final List<String> availableFileExtensionsForAvatar = Arrays.asList(
            "jpg", "jpeg", "png", "apng", "bmp", "webp"
    );

    public void checkIfFileIsAnImage(String extension) {
        if (!availableFileExtensionsForAvatar.contains(extension)) {
            throw new IncorrectDataException();
        }
    }

    public String getAvatarFileName(MultipartFile file) {
        String extension = Objects.requireNonNull(
                FilenameUtils.getExtension(file.getOriginalFilename())
        );
        checkIfFileIsAnImage(extension);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return  "avatar_" + timestamp + Strings.concat(".", extension);
    }
}
