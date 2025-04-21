package my_social_media_project_backend.demo.validator;

import my_social_media_project_backend.demo.exception.ValidationException;
import my_social_media_project_backend.demo.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator {
    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("username", "Username is required");
        }

        if (username.length() < 6 || username.length() > 20) {
            throw new ValidationException("username", "Username must be between 6 and 20 characters");
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException("username", "Username can only contain letters, numbers, and underscores");
        }
    }

    public void validateFirstName(String firstName) throws ValidationException {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new ValidationException("firstName", "First name is required");
        }

        if (firstName.length() < 2 || firstName.length() > 30) {
            throw new ValidationException("firstName", "First name must be between 2 and 30 characters");
        }

        if (!firstName.matches("^[a-zA-Z ]+$")) {
            throw new ValidationException("firstName", "First name must contain only letters and spaces");
        }
    }

    public void validateLastName(String lastName) throws ValidationException {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new ValidationException("lastName", "Last name is required");
        }

        if (lastName.length() < 2 || lastName.length() > 30) {
            throw new ValidationException("lastName", "Last name must be between 2 and 30 characters");
        }

        if (!lastName.matches("^[a-zA-Z ]+$")) {
            throw new ValidationException("lastName", "Last name must contain only letters and spaces");
        }
    }

    public void validateGender(String gender) throws ValidationException {
        if (gender == null || gender.trim().isEmpty()) {
            throw new ValidationException("gender", "Gender is required");
        }

        String normalized = gender.trim().toLowerCase();
        if (!normalized.equals("male") && !normalized.equals("female")) {
            throw new ValidationException("gender", "Gender must be either 'male' or 'female'");
        }
    }

    public void validateCountry(String country) throws ValidationException {
        if(country == null) return;

        if (!country.trim().matches("^[a-zA-Z\\s'-]+$")) {
            throw new ValidationException("country", "Country must contain only letters, spaces, apostrophes, or hyphens");
        }
    }

    public void validateRegion(String region) throws ValidationException {
        if(region == null) return;

        if (!region.trim().matches("^[\\p{L}\\s'-]+$")) {
            throw new ValidationException("region", "Region must contain only letters, spaces, apostrophes, or hyphens");
        }
    }

    public void validateRelationshipStatus(String status) throws ValidationException {
        if(status == null) return;

        Set<String> allowedStatuses = Set.of(
                "Single",
                "Married",
                "Divorced",
                "Widowed",
                "Separated",
                "Looking For Relationship",
                "It's Complicated",
                "Forever Alone"
        );

        if (!allowedStatuses.contains(status.trim())) {
            throw new ValidationException("relationshipStatus", "Invalid relationship status selected");
        }
    }

    public void validateOccupation(String occupation) throws ValidationException {
        if(occupation == null) return;

        if (!occupation.trim().matches("^[a-zA-Z\\s'-]+$")) {
            throw new ValidationException("occupation", "Occupation must contain only letters, spaces, apostrophes, or hyphens");
        }
    }

    public void validatePhoneNumber(String phoneNumber) throws ValidationException {
        if(phoneNumber == null) return;

        if (phoneNumber.trim().isEmpty()) {
            throw new ValidationException("phoneNumber", "Phone number is required");
        }

        String trimmed = phoneNumber.trim();

        if (!trimmed.matches("^\\+[0-9]{8,15}$")) {
            throw new ValidationException("phoneNumber", "Phone number must start with '+' and contain 8 to 15 digits");
        }
    }

    public void validateAvatar(String avatarBase64) throws ValidationException {
        if (avatarBase64 == null || avatarBase64.trim().isEmpty()) {
            throw new ValidationException("avatar", "Avatar is required");
        }

        // Ensure that the Base64 string has the correct pattern for a PNG image
        String base64Pattern = "^data:image/png;base64,([A-Za-z0-9+/=]+)$";
        Pattern pattern = Pattern.compile(base64Pattern);
        Matcher matcher = pattern.matcher(avatarBase64.trim());

        if (!matcher.matches()) {
            throw new ValidationException("avatar", "Avatar must be a valid PNG image in Base64 format");
        }

        // Extract Base64 data without the prefix (data:image/png;base64,)
        String base64Data = avatarBase64.trim().substring("data:image/png;base64,".length());

        // Decode Base64 data and check if it is a valid PNG
        try {
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Data);

            // Check the first 8 bytes for PNG signature
            if (decodedBytes.length < 8) {
                throw new ValidationException("avatar", "Avatar data is corrupted or not a valid PNG");
            }

            // PNG signature bytes
            byte[] pngSignature = new byte[] {(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A};

            for (int i = 0; i < pngSignature.length; i++) {
                if (decodedBytes[i] != pngSignature[i]) {
                    throw new ValidationException("avatar", "Avatar is not a valid PNG image");
                }
            }

        } catch (IllegalArgumentException e) {
            // Base64 decoding failed
            throw new ValidationException("avatar", "Avatar data is not a valid Base64 encoded string");
        }
    }

}
