package dcom.cave_service.domain;

public class Permissions {
    public static final int SEE_CHANNELS = 1;
    public static final int MANAGE_CHANNELS = 1 << 2;
    public static final int CREATE_INVITES = 1 << 3;
    public static final int KICK_MEMBERS = 1 << 4;
    public static final int BAN_MEMBERS = 1 << 5;
    public static final int SEND_MESSAGES = 1 << 6;
    public static final int CONNECT_VOICE_CHANNELS = 1 << 7;
    public static final int SPEAK = 1 << 8;
    public static final int VIDEO = 1 << 9;
    public static final int MOVE_MEMBERS = 1 << 10;
}
