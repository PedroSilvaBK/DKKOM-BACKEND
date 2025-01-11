package dcom.messaging_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties = "spring.profiles.active=test")
class MessagingServiceApplicationTest {
    @Test
    void contextLoads() {
        System.out.println("context loaded");
    }
}