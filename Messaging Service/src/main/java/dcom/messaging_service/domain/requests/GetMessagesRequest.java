package dcom.messaging_service.domain.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetMessagesRequest {
    private String userId;
    private String channelId;
    private String pagingState;
}
