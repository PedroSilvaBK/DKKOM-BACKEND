package dcom.cave_service.domain;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@Data
public class Channel {
    private UUID id;
    private String name;
}
