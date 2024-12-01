package dcom.messaging_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.cql.CqlTemplate;

@Configuration
public class CassandraConfig {
    @Bean
    public CqlTemplate cqlTemplate(com.datastax.oss.driver.api.core.CqlSession session) {
        return new CqlTemplate(session);
    }
}
