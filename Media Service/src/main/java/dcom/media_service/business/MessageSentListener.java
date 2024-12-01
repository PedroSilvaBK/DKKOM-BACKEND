package dcom.media_service.business;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSentListener {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final GCSUploadService gcsUploadService;

    @KafkaListener(topics = "message-sent", groupId = "media-message-sent-listener")
    public void onMessage(ConsumerRecord<String, String> record) {
        try {
            List<String> imageIds = objectMapper.readValue(record.value(), new TypeReference<>() {
            });

            for (String imageId : imageIds) {
                gcsUploadService.markImageAsUsed(imageId);
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
