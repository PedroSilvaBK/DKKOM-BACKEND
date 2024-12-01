package dcom.websocketgateway.business.clients.cave_service_client;

import java.util.List;

public interface CaveServiceClient {
    List<String> getUserCaveIds(String userId);
}
