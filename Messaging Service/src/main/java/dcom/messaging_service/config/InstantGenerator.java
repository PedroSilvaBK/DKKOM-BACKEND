package dcom.messaging_service.config;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class InstantGenerator {
    public long getInstantEpochMilli() {
        return Instant.now().toEpochMilli();
    }
}
