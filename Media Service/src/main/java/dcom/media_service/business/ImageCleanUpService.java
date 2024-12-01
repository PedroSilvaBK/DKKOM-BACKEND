package dcom.media_service.business;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageCleanUpService {
    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupUnusedImages() {
        Page<Blob> blobs = storage.list(bucketName);

        for (Blob blob : blobs.iterateAll()) {
            Map<String, String> metadata = blob.getMetadata();
            if (metadata != null) {
                String isUsed = metadata.get("isUsed");
                long uploadedAt = Long.parseLong(metadata.get("uploadedAt"));
                long now = System.currentTimeMillis();

                if ("false".equals(isUsed) && now - uploadedAt > 3600000) {
                    storage.delete(blob.getBlobId());
                }
            }
        }
    }
}
