package dcom.websocketgateway.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final ConcurrentHashMap<String, WebSocketSession> sessionCache = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> channelSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sessionSubscriptions = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Set<String>> sessionsByCave = new ConcurrentHashMap<>();

    public void addCaveSession(String caveId, WebSocketSession session) {
        sessionsByCave.computeIfAbsent(caveId, k -> new CopyOnWriteArraySet<>()).add(session.getId());
    }

    public WebSocketSession getSession(String sessionId) {
        return sessionCache.get(sessionId);
    }

    public Set<WebSocketSession> getSessions(List<String> sessionIds) {
        return sessionIds.stream().map(sessionCache::get).collect(Collectors.toSet());
    }

    public Set<WebSocketSession>  getSessionsByCave(String caveId) {
        Set<String> sessionIds = sessionsByCave.getOrDefault(caveId, new HashSet<>());
        return extractSessions(sessionIds);
    }

    public Set<WebSocketSession>  getSessionsByCaveIds(List<String> caveIds) {
        Set<String> sessionIds = new HashSet<>();
        caveIds.forEach(caveId -> {
            sessionIds.addAll(
                    sessionsByCave.getOrDefault(caveId, new HashSet<>())
            );
        });

        return extractSessions(sessionIds);
    }

    public void subscribeToChannel(String channelId, WebSocketSession session) {
        Set<String> currentSubscriptions = sessionSubscriptions.get(session.getId());
        if (currentSubscriptions != null && !currentSubscriptions.isEmpty()) {
            String previousChannelId = currentSubscriptions.iterator().next();
            unsubscribeSessionFromChannel(session.getId(), previousChannelId);
        }

        channelSubscriptions.computeIfAbsent(channelId, k -> new HashSet<>()).add(session.getId());
        sessionSubscriptions.computeIfAbsent(session.getId(), k -> new HashSet<>()).add(channelId);
    }

    public Set<WebSocketSession> getSessionsForChannel(String channelId) {
        Set<String> sessionIds = channelSubscriptions.getOrDefault(channelId, new HashSet<>());
        return extractSessions(sessionIds);
    }

    public Set<WebSocketSession> getSessionsForChannels(List<String> channelId) {
        Set<String> sessionIds = new HashSet<>();
        for (String sessionId : channelId) {
            sessionIds.addAll(
                    channelSubscriptions.getOrDefault(sessionId, new HashSet<>())
            );
        }
        return extractSessions(sessionIds);
    }

    private Set<WebSocketSession> extractSessions(Set<String> sessionIds) {
        Set<WebSocketSession> sessions = new HashSet<>();
        for (String sessionId : sessionIds) {
            WebSocketSession session = sessionCache.get(sessionId);
            if (session != null && session.isOpen()) {
                sessions.add(session);
            }
        }
        return sessions;
    }

    public void saveSession(String sessionId, WebSocketSession session) {
        sessionCache.put(sessionId, session);
    }

    public void unsubscribeSessionFromChannel(String sessionId, String channelId) {
        Set<String> sessions = channelSubscriptions.getOrDefault(channelId, new HashSet<>());
        sessions.remove(sessionId);

        if (sessions.isEmpty()) {
            channelSubscriptions.remove(channelId);
        }

        Set<String> channels = sessionSubscriptions.getOrDefault(sessionId, new HashSet<>());
        channels.remove(channelId);

        if (channels.isEmpty()) {
            sessionSubscriptions.remove(sessionId);
        }
    }

    public void deleteSession(String sessionId) {
        Set<String> channels = sessionSubscriptions.getOrDefault(sessionId, new HashSet<>());
        for (String channelId : channels) {
            unsubscribeSessionFromChannel(sessionId, channelId);
        }

        for (Map.Entry<String, Set<String>> entry : sessionsByCave.entrySet()) {
            String caveId = entry.getKey();
            Set<String> sessions = entry.getValue();
            if (sessions.contains(sessionId)) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    sessionsByCave.remove(caveId);
                }
            }
        }

        sessionCache.remove(sessionId);
        sessionSubscriptions.remove(sessionId);
    }
}
