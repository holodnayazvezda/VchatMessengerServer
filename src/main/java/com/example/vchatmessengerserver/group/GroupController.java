package com.example.vchatmessengerserver.group;

import com.example.vchatmessengerserver.auth.Auth;
import com.example.vchatmessengerserver.message.Message;
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
    public ResponseEntity<Group> addMember(Authentication authentication, Group group) {
        return ResponseEntity.ok(
                groupService.addMember(Auth.getUser(authentication), group)
        );
    }

    @PutMapping(value = "/remove_member", name = "Remove member from group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> removeMember(Authentication authentication, Group group) {
        return ResponseEntity.ok(
                groupService.removeMember(Auth.getUser(authentication), group)
        );
    }

    @PutMapping(value = "/v1.0/group/edit_name", name = "Edit name of the group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editName(Authentication authentication, Group group, String newName) {
        return ResponseEntity.ok(
                groupService.editName(Auth.getUser(authentication), group, newName)
        );
    }

    @PutMapping(value = "/v1.0/group/edit_type_of_image", name = "Edit type of image of the group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editTypeOfImage(Authentication authentication, Group group, Integer newTypeOfImage) {
        return ResponseEntity.ok(
                groupService.editTypeOfImage(Auth.getUser(authentication), group, newTypeOfImage)
        );
    }

    @PutMapping(value = "/v1.0/group/edit_image", name = "Edit image of the group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editImage(Authentication authentication, Group group, String imageData) {
        return ResponseEntity.ok(
                groupService.editImage(Auth.getUser(authentication), group, imageData)
        );
    }

    @PutMapping(value = "/v1.0/group/edit_all", name = "Edit all the params of the group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> editAll(Authentication authentication, Group group, String newName, Integer newTypeOfImage, String newImageData) {
        return ResponseEntity.ok(
                groupService.editAll(Auth.getUser(authentication), group, newName, newTypeOfImage, newImageData)
        );
    }

    @PutMapping(value = "/v1.0/group/add_message", name = "Add message with transferred id to group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> addMessage(Authentication authentication, Group group, Message message) {
        return ResponseEntity.ok(
                groupService.addMessage(Auth.getUser(authentication), group, message)
        );
    }

    @PutMapping(value = "/v1.0/group/remove_message", name = "Remove message with transferred id from group with transferred id")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> removeMessage(Authentication authentication, Group group, Message message) {
        return ResponseEntity.ok(
                groupService.removeMessage(Auth.getUser(authentication), group, message)
        );
    }

    @PutMapping(value = "/get_fot_user", name = "Get chat for user")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> getForUser(Authentication authentication, @RequestBody Group chat) {
        return ResponseEntity.ok(
                groupService.getChatForUser(Auth.getUser(authentication), chat)
        );
    }

    @PostMapping(value = "/create", name = "Create a new group")
    @SecurityRequirement(name = "basicAuth")
    public ResponseEntity<Group> create(Authentication authentication, @RequestBody CreateGroupDto dto) {
        return ResponseEntity.ok(
                groupService.create(Auth.getUser(authentication), dto)
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
    public void delete(Authentication authentication, @RequestBody Group group) {
        groupService.delete(Auth.getUser(authentication), group);
    }
}
