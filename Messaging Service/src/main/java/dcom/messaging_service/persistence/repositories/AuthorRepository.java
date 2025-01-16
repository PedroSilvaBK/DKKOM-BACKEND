package dcom.messaging_service.persistence.repositories;

import dcom.messaging_service.persistence.entities.AuthorEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorRepository extends CassandraRepository<AuthorEntity, UUID> {
    @Query("SELECT * FROM authors WHERE id IN ?0")
    List<AuthorEntity> findAuthorsByIds(List<UUID> authorIds);

    @Query("UPDATE authors SET username = :username WHERE id = :id")
    void updateAuthorNameByIds(@Param("username") String username, @Param("id") UUID id);
}
