package dcom.messaging_service.business.get_messages.use_case_impl;

import dcom.messaging_service.business.cannotSeeMeSsagesException;
import dcom.messaging_service.business.clients.permissions_service_client.PermissionServiceClient;
import dcom.messaging_service.business.get_messages.use_case.GetMessagesUseCase;
import dcom.messaging_service.domain.AuthorDTO;
import dcom.messaging_service.domain.MessageDTO;
import dcom.messaging_service.domain.requests.GetMessagesRequest;
import dcom.messaging_service.domain.responses.GetMessagesResponse;
import dcom.messaging_service.persistence.entities.AuthorEntity;
import dcom.messaging_service.persistence.entities.MessageEntity;
import dcom.messaging_service.persistence.repositories.AuthorRepository;
import dcom.messaging_service.persistence.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMessagesUseCaseImpl implements GetMessagesUseCase {
    private final MessageRepository messageRepository;
    private final AuthorRepository authorRepository;
    private static final int PAGE_SIZE = 25;
    private final PermissionServiceClient permissionServiceClient;

    public GetMessagesResponse getMessages(GetMessagesRequest getMessagesRequest) {
        String userId = getMessagesRequest.getUserId();
        String channelId = getMessagesRequest.getChannelId();

        log.debug("message request for user {} with channel {}", userId, channelId);

        boolean canSeeChannel = permissionServiceClient.canSeeChannel(userId, channelId);

        if (!canSeeChannel) {
            log.debug("user - {} tried to see channel {}", userId, channelId);
            throw new cannotSeeMeSsagesException("not authorized to see messages");
        }

        Pageable pageRequest;
        if (getMessagesRequest.getPagingState().isEmpty()) {
            pageRequest = CassandraPageRequest.first(PAGE_SIZE);
        } else {
            ByteBuffer pagingState = fromHexString(getMessagesRequest.getPagingState().trim());
            pageRequest = CassandraPageRequest.of(PageRequest.of(1, PAGE_SIZE), pagingState);
        }

        Slice<MessageEntity> messageSlice = messageRepository.findLatestByChannelId(UUID.fromString(getMessagesRequest.getChannelId()), pageRequest);

        Set<UUID> authorIds = messageSlice.stream()
                .map(MessageEntity::getAuthorId)
                .collect(Collectors.toSet());

        List<AuthorEntity> authors = authorRepository.findAuthorsByIds(authorIds.stream().toList());

        Map<UUID, String> authorNameMap = authors.stream()
                .collect(Collectors.toMap(AuthorEntity::getId, AuthorEntity::getUsername));


        List<MessageDTO> mappedMessages = messageSlice.getContent().stream()
                .map(message -> MessageDTO.builder()
                        .channelId(message.getChannelId())
                        .author(AuthorDTO.builder()
                                .id(message.getAuthorId())
                                .username(authorNameMap.get(message.getAuthorId()))
                                .build())
                        .id(message.getId())
                        .content(message.getContent())
                        .timestamp(message.getTimestamp())
                        .editedTimeStamp(message.getEditedTimeStamp())
                        .attachments(message.getAttachments())
                        .build())
                .toList();

        log.debug("messages retrieved {}", mappedMessages.size());

        String nextPageState = null;
        if (messageSlice.hasNext()) {
            ByteBuffer pagingState = ((CassandraPageRequest) messageSlice.getPageable()).getPagingState();
            if (pagingState != null) {
                nextPageState = toHexString(pagingState);
            }
        }

        return GetMessagesResponse.builder()
                .messages(mappedMessages)
                .nextPageState(nextPageState)
                .build();
    }

    private String toHexString(ByteBuffer buffer) {
        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return DatatypeConverter.printHexBinary(byteArray);
    }

    private ByteBuffer fromHexString(String hex) {
        byte[] byteArray = DatatypeConverter.parseHexBinary(hex);
        return ByteBuffer.wrap(byteArray);
    }
}
