package dcom.messaging_service.persistence.entities;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Table("messages")
@Data
@Builder
public class MessageEntity {
    private UUID id;
    @NotNull
    @PrimaryKeyColumn(name = "channelid", type = PrimaryKeyType.PARTITIONED)
    private UUID channelId;
    @Size(max = 500)
    private String content;
    @NotNull
    private UUID authorId;
    @NotNull
    @PrimaryKeyColumn(name = "timestamp", type = PrimaryKeyType.CLUSTERED)
    private long timestamp;
    private long editedTimeStamp;

    @CassandraType(type = CassandraType.Name.LIST, typeArguments = CassandraType.Name.TEXT)
    private List<String> attachments;
}
