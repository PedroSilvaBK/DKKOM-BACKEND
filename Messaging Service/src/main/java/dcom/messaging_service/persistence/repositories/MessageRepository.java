package dcom.messaging_service.persistence.repositories;

import dcom.messaging_service.domain.MessageIdDTO;
import dcom.messaging_service.persistence.entities.MessageEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MessageRepository extends CassandraRepository<MessageEntity, UUID> {
//    Slice<MessageEntity> findAllByChannelId(UUID channelId, Pageable pageable);

    @Query("SELECT * FROM messages WHERE channelid = ?0 ORDER BY timestamp DESC")
    Slice<MessageEntity> findLatestByChannelId(UUID channelId, Pageable pageable);


    @Query("SELECT id FROM messages WHERE authorId = ?0 ALLOW FILTERING")
    List<MessageIdDTO> findByAuthorId(UUID authorId);

    @Query("UPDATE messages SET authorName = :authorName WHERE id IN :ids")
    void updateAuthorNameByIds(@Param("authorName") String authorName, @Param("ids") List<UUID> ids);
}
