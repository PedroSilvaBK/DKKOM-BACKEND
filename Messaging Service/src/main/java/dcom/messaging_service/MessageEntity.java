package dcom.messaging_service;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Table("messages")
@Data
@Builder
public class MessageEntity {

    @PrimaryKey
    private UUID id;
    @NotNull
    private UUID channelId;
    @Size(max = 500)
    private String content;
    @NotNull
    private UUID authorId;
    @NotNull
    private long timestamp;
    private long editedTimeStamp;
}
