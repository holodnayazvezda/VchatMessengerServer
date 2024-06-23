package com.example.vchatmessengerserver.channel;

import com.example.vchatmessengerserver.auth.Auth;
import com.example.vchatmessengerserver.files.avatar.AvatarDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/channel")
public class ChannelController {
    @Autowired
    ChannelService channelService;

    @PutMapping(value = "/add_member", name = "Add member to channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> addMember(Authentication authentication, Long channelId) {
        return ResponseEntity.ok(
                channelService.addMember(Auth.getUser(authentication), channelId)
        );
    }

    @PutMapping(value = "/remove_member", name = "Remove member from channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> removeMember(Authentication authentication, Long channelId) {
        return ResponseEntity.ok(
                channelService.removeMember(Auth.getUser(authentication), channelId)
        );
    }

    @PutMapping(value = "/edit_name", name = "Edit name of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editName(Authentication authentication, Long channelId, String newName) {
        return ResponseEntity.ok(
                channelService.editName(Auth.getUser(authentication), channelId, newName)
        );
    }

    @PutMapping(value = "/edit_nickname", name = "Edit nickname of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editNickname(Authentication authentication, Long channelId, String newNickname) {
        return ResponseEntity.ok(
                channelService.editNickname(Auth.getUser(authentication), channelId, newNickname)
        );
    }

    @PutMapping(value = "/add_message", name = "Add message with transferred id to channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> addMessage(Authentication authentication, Long channelId, Long messageId) {
        return ResponseEntity.ok(
                channelService.addMessage(Auth.getUser(authentication), channelId, messageId)
        );
    }

    @PutMapping(value = "/remove_message", name = "Remove message with transferred id from channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> removeMessage(Authentication authentication, Long channelId, Long messageId) {
        return ResponseEntity.ok(
                channelService.removeMessage(Auth.getUser(authentication), channelId, messageId)
        );
    }

    @PostMapping(value = "/create", name = "Create a new channel")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> create(Authentication authentication, @RequestBody CreateChannelDto dto) {
        Channel channel = channelService.create(Auth.getUser(authentication), dto);
        return ResponseEntity.ok(channel);
    }

    @PostMapping(value = "/edit_avatar", name = "Edit avatar of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editAvatar(
            Authentication authentication,
            Long channelId,
            @RequestBody AvatarDTO newAvatarDTO
    ) {
        return ResponseEntity.ok(
                channelService.editAvatar(Auth.getUser(authentication), channelId, newAvatarDTO)
        );
    }

    @PostMapping(value = "/edit_all", name = "Edit all the params of the group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editAll(
            Authentication authentication,
            Long channelId,
            String newName,
            String newNickname,
            @RequestBody AvatarDTO newAvatarDTO
    ) {
        return ResponseEntity.ok(
                channelService.editAll(Auth.getUser(authentication), channelId, newName, newNickname, newAvatarDTO)
        );
    }

    @GetMapping(value = "/get_by_id", name = "Gte channel by transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> getById(Long channelId) {
        return ResponseEntity.ok(
                channelService.getById(channelId)
        );
    }

    @DeleteMapping(value = "/delete", name = "Delete channel by transferred id")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication, Long channelId) {
        channelService.delete(Auth.getUser(authentication), channelId);
    }
}
