package dcom.messaging_service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDTO implements Serializable {
    private UUID id;
    private UUID channelId;
    private String content;
    private AuthorDTO author;
    private long timestamp;
    private long editedTimeStamp;
}
