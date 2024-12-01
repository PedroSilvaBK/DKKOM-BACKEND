package dcom.userpresenceservice.business.get_users_presence_use_case;

import dcom.userpresenceservice.domain.UserPresence;

import java.util.List;

public interface GetUsersPresenceUseCase {
    List<UserPresence> getUsersPresence(List<String> userIds);
}
