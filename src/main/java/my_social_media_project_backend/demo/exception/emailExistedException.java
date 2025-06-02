package my_social_media_project_backend.demo.exception;

public class emailExistedException extends RuntimeException {
    public emailExistedException(String message) {
        super(message);
    }
}
