package dcom.cave_service.business.create_channel.use_case;

import dcom.cave_service.domain.requests.CreateChannelRequest;
import dcom.cave_service.domain.responses.CreateChannelResponse;

public interface CreateChannelUseCase {
    CreateChannelResponse createChannel(CreateChannelRequest createChannelRequest);
}
