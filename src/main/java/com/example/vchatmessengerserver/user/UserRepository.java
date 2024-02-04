package com.example.vchatmessengerserver.user;

import com.example.vchatmessengerserver.group.Group;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);

    @Query(value = "SELECT g FROM VCHAT_GROUP g JOIN g.members m WHERE " +
            "m.id = :userId ORDER BY COALESCE((SELECT MAX(m.creationDate)" +
            " FROM VCHAT_TEXT_MESSAGE m WHERE m.messageChat.id = g.id), g.creationDate) " +
            "DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Group> getChatsWithOffset(Long userId, int limit, int offset);

    @Query(value = "SELECT COUNT(*) FROM VCHAT_USER_CHATS WHERE VCHAT_USER_ID = ?1", nativeQuery = true)
    int getAmountOfChats(Long userId);

    @Query(value = "SELECT g FROM VCHAT_USER u JOIN u.chats g WHERE" +
            " u.id = :userId AND (g.name LIKE %:nameOfChat% OR " +
            "(TYPE(g) = VCHAT_CHANNEL AND ((g.nickname LIKE %:nameOfChat%) ))) " +
            "ORDER BY COALESCE((SELECT MAX(m.creationDate) FROM " +
            "VCHAT_TEXT_MESSAGE m WHERE m.messageChat.id = g.id), g.creationDate) " +
            "DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Group> searchChatsWithOffset(Long userId, String nameOfChat, int limit, int offset);
}
