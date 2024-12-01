package dcom.messaging_service.domain.responses;

import dcom.messaging_service.domain.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetMessagesResponse {
    private List<MessageDTO> messages;
    private String nextPageState;
}
