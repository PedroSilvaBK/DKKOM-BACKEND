package dcom.messaging_service;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface MessageRepository extends CassandraRepository<MessageEntity, UUID> {
}
