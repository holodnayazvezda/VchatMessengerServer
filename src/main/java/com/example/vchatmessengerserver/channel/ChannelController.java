package com.example.vchatmessengerserver.channel;

import com.example.vchatmessengerserver.auth.Auth;
import com.example.vchatmessengerserver.message.Message;
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
    public ResponseEntity<Channel> addMember(Authentication authentication, Channel channel) {
        return ResponseEntity.ok(
                channelService.addMember(Auth.getUser(authentication), channel)
        );
    }

    @PutMapping(value = "/remove_member", name = "Remove member from channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> removeMember(Authentication authentication, Channel channel) {
        return ResponseEntity.ok(
                channelService.removeMember(Auth.getUser(authentication), channel)
        );
    }

    @PutMapping(value = "/edit_name", name = "Edit name of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editName(Authentication authentication, Channel channel, String newName) {
        return ResponseEntity.ok(
                channelService.editName(Auth.getUser(authentication), channel, newName)
        );
    }

    @PutMapping(value = "/edit_nickname", name = "Edit nickname of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editNickname(Authentication authentication, Channel channel, String newName) {
        return ResponseEntity.ok(
                channelService.editNickname(Auth.getUser(authentication), channel, newName)
        );
    }

    @PutMapping(value = "/edit_type_of_image", name = "Edit type of image of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editTypeOfImage(Authentication authentication, Channel channel, Integer newTypeOfImage) {
        return ResponseEntity.ok(
                channelService.editTypeOfImage(Auth.getUser(authentication), channel, newTypeOfImage)
        );
    }

    @PutMapping(value = "/edit_image", name = "Edit image of the channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editImage(Authentication authentication, Channel channel, String imageData) {
        return ResponseEntity.ok(
                channelService.editImage(Auth.getUser(authentication), channel, imageData)
        );
    }

    @PutMapping(value = "/edit_all", name = "Edit all the params of the group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> editAll(Authentication authentication, Channel channel, String newName, String newNickname, Integer newTypeOfImage, String newImageData) {
        return ResponseEntity.ok(
                channelService.editAll(Auth.getUser(authentication), channel, newName, newNickname, newTypeOfImage, newImageData)
        );
    }

    @PutMapping(value = "/add_message", name = "Add message with transferred id to channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> addMessage(Authentication authentication, Channel channel, Message message) {
        return ResponseEntity.ok(
                channelService.addMessage(Auth.getUser(authentication), channel, message)
        );
    }

    @PutMapping(value = "/remove_message", name = "Remove message with transferred id from channel with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> removeMessage(Authentication authentication, Channel channel, Message message) {
        return ResponseEntity.ok(
                channelService.removeMessage(Auth.getUser(authentication), channel, message)
        );
    }

    @PostMapping(value = "/create", name = "Create a new channel")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Channel> create(Authentication authentication, @RequestBody CreateChannelDto dto) {
        Channel channel = channelService.create(Auth.getUser(authentication), dto);
        return ResponseEntity.ok(channel);
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
    public void delete(Authentication authentication, Channel channel) {
        channelService.delete(Auth.getUser(authentication), channel);
    }
}
