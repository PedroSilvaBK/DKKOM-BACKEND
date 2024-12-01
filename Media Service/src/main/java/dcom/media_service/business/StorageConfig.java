package dcom.media_service.business;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class StorageConfig {
    @Value("${prod}")
    private boolean production;
    @Bean
    public Storage googleCloudStorage() throws IOException {
        // Path to your credentials file
        String path;

        if (production) {
            path = "/var/secrets/service-principal-app.json";
        }
        else {
            path = new ClassPathResource("service-principal.json").getFile().getAbsolutePath();
        }

        // Load the credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(path));

        // Initialize the Storage client with the credentials
        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }
}
