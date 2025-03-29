package my_social_media_project_backend.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserSignupDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 6, max = 20, message = "Username must be between 6 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9]{6,20}$",
            message = "Username can only contain letters and numbers (no spaces or symbols)"
    )
    private String username;

    @NotBlank(message = "Account name is required")
    @Size(min = 6, max = 20, message = "Account name must be between 6 and 20 characters")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,20}$",
            message = "Account name must contain at least one letter and one number, and cannot have spaces or symbols"
    )
    private String accountName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be at least 6 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\W)(?!.*\\s).{6,20}$",
            message = "Password must contain at least one uppercase letter, one special character, and no spaces"
    )
    private String password;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "male|female", message = "Gender must be either 'male' or 'female'")
    private String gender;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "First name must contain only letters and spaces (no symbols or numbers)"
    )
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Last name must contain only letters and spaces (no symbols or numbers)"
    )
    private String lastName;

    public String getUsername() {
        return username;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
