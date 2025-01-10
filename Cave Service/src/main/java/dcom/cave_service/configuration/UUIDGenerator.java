package dcom.cave_service.configuration;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator {
    public UUID generateUUID() {
        return java.util.UUID.randomUUID();
    }
}