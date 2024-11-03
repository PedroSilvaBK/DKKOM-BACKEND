package dcom.websocketgateway;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ConcurrentHashMap<String, WebSocketSession> sessionCache = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> channelSubscriptions = new ConcurrentHashMap<>();

    public void subscribeToChannel(String channelId, WebSocketSession session) {
        channelSubscriptions.computeIfAbsent(channelId, k -> new HashSet<>()).add(session.getId());
    }

    public Set<WebSocketSession> getSessionsForChannel(String channelId) {
        Set<String> sessionIds = channelSubscriptions.getOrDefault(channelId, new HashSet<>());
        Set<WebSocketSession> sessions = new HashSet<>();
        for (String sessionId : sessionIds) {
            WebSocketSession session = sessionCache.get(sessionId);
            if (session != null && session.isOpen()) {
                sessions.add(session);
            }
        }
        return sessions;
    }

    public void saveSession(String userId, WebSocketSession session) {
        redisTemplate.opsForHash().put("sessions", userId, session.getId());
        sessionCache.put(session.getId(), session);
    }

    public WebSocketSession getSession(String userId) {
        String sessionId = (String) redisTemplate.opsForHash().get("sessions", userId);
        return sessionId != null ? sessionCache.get(sessionId) : null;
    }

    public void unsubscribeSessionFromChannel(String userId, String channelId) {
        String sessionId = (String) redisTemplate.opsForHash().get("sessions", userId);
        if (sessionId != null) {
            // Remove the session from the channel
            Set<String> sessions = channelSubscriptions.getOrDefault(channelId, new HashSet<>());
            sessions.remove(sessionId);

            // Remove from cache if this is the last channel for this session
            if (sessions.isEmpty()) {
                sessionCache.remove(sessionId);
            }
        }
    }

    public void deleteSession(String userId, String channelId) {
        unsubscribeSessionFromChannel(userId, channelId);
        redisTemplate.opsForHash().delete("sessions", userId);
    }

    public void deleteSession(String userId) {
        String sessionId = (String) redisTemplate.opsForHash().get("sessions", userId);
        if (sessionId != null) {
            sessionCache.remove(sessionId);
            redisTemplate.opsForHash().delete("sessions", userId);
        }
    }
}
