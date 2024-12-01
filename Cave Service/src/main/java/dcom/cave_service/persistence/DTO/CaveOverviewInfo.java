package dcom.cave_service.persistence.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaveOverviewInfo {
    private UUID caveId;
    private String caveName;
    private UUID owner;
}
