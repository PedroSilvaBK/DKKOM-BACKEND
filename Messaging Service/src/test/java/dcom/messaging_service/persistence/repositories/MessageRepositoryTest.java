package dcom.messaging_service.persistence.repositories;

import dcom.messaging_service.persistence.entities.MessageEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;

    @BeforeEach
    void setup() {
        messageRepository.deleteAll(); // Ensure a clean database for each test
    }

    @Test
    void saveMessage() {
        String messageId = "123e4567-e89b-42d3-a456-556642440000";
        String userId = "123e4563-e89b-42d3-a456-556642440000";
        String channelId = "143e4567-e89b-42d3-a456-556642440000";
        long messageTimeStamp = 1736608230L;


        MessageEntity messageEntity = MessageEntity.builder()
                .id(UUID.fromString(messageId))
                .authorId(UUID.fromString(userId))
                .attachments(null)
                .channelId(UUID.fromString(channelId))
                .timestamp(messageTimeStamp)
                .content("message content for test")
                .build();

        MessageEntity savedEntity = messageRepository.save(messageEntity);

        assertNotNull(savedEntity);
    }
  
}