package my_social_media_project_backend.demo.exception;

public class AccountNameExistedException extends RuntimeException {
    public AccountNameExistedException(String message) {
        super(message);
    }
}
