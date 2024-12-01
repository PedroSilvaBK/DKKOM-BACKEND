package dcom.cave_service.domain.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaveRoleCreated {
    private UUID id;
    private int position;
    private String name;
    private UUID caveId;
}
