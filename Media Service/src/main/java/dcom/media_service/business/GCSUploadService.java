package dcom.media_service.business;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GCSUploadService {
    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    private final Storage storage;

    public String uploadImage(MultipartFile file) throws IOException {
        String uniqueId = UUID.randomUUID().toString();
        String fileName = uniqueId + "_" + file.getOriginalFilename();

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .setMetadata(Map.of("isUsed", "false", "uploadedAt", String.valueOf(System.currentTimeMillis())))
                .build();

        storage.create(blobInfo, file.getBytes());

        log.debug("image stored: {}", file.getOriginalFilename());
        return uniqueId;
    }

    public void markImageAsUsed(String imageId) {
        Page<Blob> blobs = storage.list(bucketName);
        // Find the blob with the matching ID
        for (Blob blob : blobs.iterateAll()) {
            if (blob.getName().startsWith(imageId + "_")) {
                Map<String, String> updatedMetadata = new HashMap<>(Objects.requireNonNull(blob.getMetadata()));
                updatedMetadata.put("isUsed", "true");
                blob.toBuilder().setMetadata(updatedMetadata).build().update();

                log.debug("image marked as used: {}", imageId);
            }
        }
    }


    public boolean deleteImage(String uniqueId) {
        BlobId blobId = BlobId.of(bucketName, uniqueId);

        log.debug("image deleted: {}", uniqueId);
        return storage.delete(blobId);
    }

    public byte[] getImage(String id) throws IOException {
        log.debug("bucket for image image retrieved: {}", bucketName);
        Page<Blob> blobs = storage.list(bucketName);

        // Find the blob with the matching ID
        for (Blob blob : blobs.iterateAll()) {
            if (blob.getName().startsWith(id + "_")) {
                return blob.getContent();
            }
        }
        throw new IOException("Image not found");
    }
}
