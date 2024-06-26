package com.example.vchatmessengerserver.group;

import com.example.vchatmessengerserver.auth.Auth;
import com.example.vchatmessengerserver.files.avatar.AvatarDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {
    @Autowired
    GroupService groupService;

    @PutMapping(value = "/add_member", name = "Add member to group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> addMember(Authentication authentication, Long groupId) {
        return ResponseEntity.ok(
                groupService.addMember(Auth.getUser(authentication), groupId)
        );
    }

    @PutMapping(value = "/remove_member", name = "Remove member from group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> removeMember(Authentication authentication, Long groupId) {
        return ResponseEntity.ok(
                groupService.removeMember(Auth.getUser(authentication), groupId)
        );
    }

    @PutMapping(value = "/edit_name", name = "Edit name of the group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editName(Authentication authentication, Long groupId, String newName) {
        return ResponseEntity.ok(
                groupService.editName(Auth.getUser(authentication), groupId, newName)
        );
    }

    @PutMapping(value = "/add_message", name = "Add message with transferred id to group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> addMessage(Authentication authentication, Long groupId, Long messageId) {
        return ResponseEntity.ok(
                groupService.addMessage(Auth.getUser(authentication), groupId, messageId)
        );
    }

    @PutMapping(value = "/remove_message", name = "Remove message with transferred id from group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> removeMessage(Authentication authentication, Long groupId, Long messageId) {
        return ResponseEntity.ok(
                groupService.removeMessage(Auth.getUser(authentication), groupId, messageId)
        );
    }

    @PutMapping(value = "/get_for_user", name = "Get chat for user")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> getForUser(Authentication authentication, Long chatId) {
        return ResponseEntity.ok(
                groupService.getChatForUser(Auth.getUser(authentication), chatId)
        );
    }

    @PostMapping(value = "/create", name = "Create a new group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> create(Authentication authentication, @RequestBody CreateGroupDto dto) {
        return ResponseEntity.ok(
                groupService.create(Auth.getUser(authentication), dto)
        );
    }

    @PostMapping(value = "/edit_avatar", name = "Edit avatar of the group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editAvatar(
            Authentication authentication,
            Long groupId,
            @RequestBody AvatarDTO newAvatarDTO
    ) {
        return ResponseEntity.ok(
                groupService.changeAvatar(Auth.getUser(authentication), groupId, newAvatarDTO)
        );
    }

    @PostMapping(value = "/edit_all", name = "Edit all the params of the group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editAll(
            Authentication authentication,
            Long groupId,
            String newName,
            @RequestBody AvatarDTO newAvatarDTO
    ) {
        return ResponseEntity.ok(
                groupService.editAll(Auth.getUser(authentication), groupId, newName, newAvatarDTO)
        );
    }

    @GetMapping(value = "/get", name = "Get chat by id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> getChat(Long chatId) {
        return ResponseEntity.ok(
                groupService.getChatById(chatId)
        );
    }

    @GetMapping(value = "/search_with_offset", name = "Search chats with offset")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<List<Group>> searchWithOffset(String chatName, int limit, int offset) {
        return ResponseEntity.ok(
                groupService.searchWithOffset(chatName, limit, offset)
        );
    }

    @DeleteMapping(value = "/delete", name = "Delete group")
    @SecurityRequirement(name = "basicAuth")
    public void delete(Authentication authentication, Long groupId) {
        groupService.delete(Auth.getUser(authentication), groupId);
    }
}
