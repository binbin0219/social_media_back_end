package my_social_media_project_backend.demo.exception;

public class CannotRejectFriendRequestException extends RuntimeException {
    public CannotRejectFriendRequestException(String message) {
        super(message);
    }
}
