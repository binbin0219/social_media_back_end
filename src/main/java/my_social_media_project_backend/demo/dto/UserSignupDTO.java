package my_social_media_project_backend.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserSignupDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 6, max = 20, message = "Username must be between 6 and 20 characters")
    @Pattern(
            regexp = "^[\\p{L}0-9]{6,20}$",
            message = "Username can only contain letters (any language) and numbers, 6–20 characters"
    )
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 50, message = "Email must not exceed 50 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be at least 6 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d\\W]{6,20}$",
            message = "Password must be 6–20 characters, contain at least one letter and one number, and have no spaces"
    )
    private String password;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "male|female", message = "Gender must be either 'male' or 'female'")
    private String gender;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getGender() {
        return gender;
    }
}
