package com.example.vchatmessengerserver.message;

import com.example.vchatmessengerserver.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM VCHAT_TEXT_MESSAGE m WHERE m.messageChat.id = :groupId AND m.content LIKE %:content% ORDER BY m.creationDate DESC LIMIT :limit OFFSET :offset")
    List<Message> findMessagesIds(Long groupId, String content, int limit, int offset);

    @Query("SELECT m FROM VCHAT_TEXT_MESSAGE m WHERE m.messageChat.id = :groupId ORDER BY m.creationDate DESC LIMIT :limit OFFSET :offset")
    List<Message> getMessagesWithOffset(Long groupId, int limit, int offset);
}
