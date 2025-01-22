package dcom.websocketgateway.business.user_status;

import dcom.websocketgateway.domain.Status;

public interface UserStatusService {
    void updateUserStatus(String userId, int status);
}
