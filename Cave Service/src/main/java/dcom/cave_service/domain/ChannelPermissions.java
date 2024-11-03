package dcom.cave_service.domain;

public class ChannelPermissions {
    public static final int SEE_CHANNEL = 1;
    public static final int SEND_MESSAGES = 1 << 2;
    public static final int CONNECT = 1 << 3;
    public static final int SPEAK = 1 << 4;
    public static final int VIDEO = 1 << 5;
    public static final int MOVE_MEMBERS = 1 << 6;
}
