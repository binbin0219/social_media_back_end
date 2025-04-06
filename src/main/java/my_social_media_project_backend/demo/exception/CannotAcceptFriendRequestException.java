package my_social_media_project_backend.demo.exception;

public class CannotAcceptFriendRequestException extends RuntimeException {
    public CannotAcceptFriendRequestException(String message) {
        super(message);
    }
}
