package dcom.media_service.controller;

import dcom.media_service.business.GCSUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final GCSUploadService gcsUploadService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(gcsUploadService.uploadImage(file));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteImage(@PathVariable String id) {
        return ResponseEntity.ok(gcsUploadService.deleteImage(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
        try {
            byte[] image = gcsUploadService.getImage(id);

            // Set the Content-Type header dynamically based on the image format (e.g., JPEG, PNG)
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .header("Cache-Control", "max-age=3600") // Cache for 1 hour
                    .body(image);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
