package com.example.vchatmessengerserver.files.avatar;

import com.example.vchatmessengerserver.exceptions.DataNotFoundException;
import com.example.vchatmessengerserver.exceptions.IncorrectDataException;
import com.example.vchatmessengerserver.files.FilesService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class AvatarService {

    @Autowired
    AvatarRepository avatarRepository;

    @Autowired
    @Lazy
    FilesService filesService;

    private static final List<String> availableFileExtensionsForAvatar = Arrays.asList(
            "jpg", "jpeg", "png", "apng", "bmp", "webp"
    );

    public Avatar createAvatar(AvatarDTO avatarDto) {
        if (filesService.checkIfFileExists(avatarDto.getAvatarFileName())) {
            Avatar avatar = new Avatar();
            avatar.setAvatarFileName(avatarDto.getAvatarFileName());
            avatar.setAvatarType(avatarDto.getAvatarType());
            avatar.setAvatarBackgroundColor(avatarDto.getAvatarBackgroundColor());
            return avatarRepository.saveAndFlush(avatar);
        }
        throw new DataNotFoundException();
    }

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
