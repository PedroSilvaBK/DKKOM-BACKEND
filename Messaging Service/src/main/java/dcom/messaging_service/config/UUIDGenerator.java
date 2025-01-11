package dcom.messaging_service.config;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator {
    public UUID generateUUID() {
        return UUID.randomUUID();
    }
}
