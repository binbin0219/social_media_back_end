package my_social_media_project_backend.demo.exception;

public class CannotSendFriendRequestException extends RuntimeException {
    public CannotSendFriendRequestException(String message) {
        super(message);
    }
}
