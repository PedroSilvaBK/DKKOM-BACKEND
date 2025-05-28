package dcom.cave_service.domain.responses;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCaveResponse {
    private UUID id;
}
