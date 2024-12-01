package dcom.messaging_service.domain.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateMessageRequest {
    private String channelId;
    @Length(min = 1, max = 255)
    private String content;
    private List<String> attachments;
}
