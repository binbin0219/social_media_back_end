package my_social_media_project_backend.demo;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserSessionRegistry {

    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    public void onConnect(Long userId) {
        userSessions.putIfAbsent(userId, new UserSession());
    }

    public void onDisconnect(Long userId) {
        userSessions.remove(userId);
    }

    public void setChatOpen(Long userId, boolean isOpen) {
        userSessions.computeIfPresent(userId, (k,v) -> {
            v.setChatOpen(isOpen);
            return v;
        });
    }

    public void setNotificationOpen(Long userId, boolean isOpen) {
        userSessions.computeIfPresent(userId, (k,v) -> {
            v.setNotificationOpen(isOpen);
            return v;
        });
    }

    public void setActiveChatRoomId(Long userId, String chatRoomId) {
        userSessions.computeIfPresent(userId, (k, v) -> {
            v.setActiveChatRoomId(chatRoomId);
            return v;
        });
    }

    public void setViewingNotification(Long userId, boolean viewing) {
        userSessions.computeIfPresent(userId, (k, v) -> {
            v.setViewingNotification(viewing);
            return v;
        });
    }

    public String getActiveChatRoomId(Long userId) {
        UserSession session = userSessions.get(userId);
        if(session != null) {
            return session.getActiveChatRoomId();
        } else {
            return null;
        }
    }

    public boolean isConnected(Long userId) {
        return userSessions.containsKey(userId);
    }

    public boolean isChatOpen(Long userId) {
        UserSession session = userSessions.get(userId);
        return session != null && Objects.equals(session.isChatOpen, true);
    }

    public boolean isNotificationOpen(Long userId) {
        UserSession session = userSessions.get(userId);
        return session != null && Objects.equals(session.isNotificationOpen, true);
    }

    public boolean isAnyChatRoomActive(Long userId) {
        UserSession session = userSessions.get(userId);
        return session != null && !Objects.equals(session.getActiveChatRoomId(), null);
    }

    public boolean isViewingChat(Long userId, String chatRoomId) {
        UserSession session = userSessions.get(userId);
        return session != null && Objects.equals(session.getActiveChatRoomId(), chatRoomId);
    }

    public boolean isViewingNotification(Long userId) {
        UserSession session = userSessions.get(userId);
        return session != null && session.isViewingNotification();
    }

    public static class UserSession {
        private boolean connected;
        private boolean isChatOpen;
        private boolean isNotificationOpen;
        private String activeChatRoomId;
        private boolean viewingNotification;

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public boolean isChatOpen() {
            return isChatOpen;
        }

        public void setChatOpen(boolean chatOpen) {
            isChatOpen = chatOpen;
        }

        public boolean isNotificationOpen() {
            return isNotificationOpen;
        }

        public void setNotificationOpen(boolean notificationOpen) {
            isNotificationOpen = notificationOpen;
        }

        public String getActiveChatRoomId() {
            return activeChatRoomId;
        }

        public void setActiveChatRoomId(String activeChatRoomId) {
            this.activeChatRoomId = activeChatRoomId;
        }

        public boolean isViewingNotification() {
            return viewingNotification;
        }

        public void setViewingNotification(boolean viewingNotification) {
            this.viewingNotification = viewingNotification;
        }
    }
}

