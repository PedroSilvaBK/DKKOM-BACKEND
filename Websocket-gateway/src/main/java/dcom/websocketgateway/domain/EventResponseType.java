package dcom.websocketgateway.domain;

public enum EventResponseType {
    CHAT_MESSAGE("chat-message"),
    UPDATE_USER_PERMISSION("update-user-permissions"),
    UPDATE_USER_PRESENCE("update-user-presence"),
    CAVE_ROLE_CREATED("cave-role-created"),
    UPDATE_CHANNEL_LIST("update-channel-list"),
    ROLE_ASSIGNED_TO_MEMBER("role-assigned-to-member"),
    USER_JOINED_CAVE("user-joined-cave"),
    USER_JOINED_VOICE_CHANNEL("user-joined-voice-channel"),
    USER_DISCONNECT_VOICE_CHANNEL("user-disconnect-voice-channel"),
    WEB_RTC_ANSWER("webrtc-answer"),
    WEB_RTC_SERVER_CANDIDATE("webrtc-server-candidate"),
    WEB_RTC_OFFER("webrtc-offer");

    private final String label;

    EventResponseType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
