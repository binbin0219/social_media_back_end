package my_social_media_project_backend.demo.exception;

public class CannotUnsendFriendRequestException extends RuntimeException {
    public CannotUnsendFriendRequestException(String message) {
        super(message);
    }
}
