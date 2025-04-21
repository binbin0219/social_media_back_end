package my_social_media_project_backend.demo.dto;

public class UserProfileUpdateDTO {

    private String avatar;
    private String username;
    private String firstName;
    private String lastName;
    private String gender;
    private String country;
    private String region;
    private String relationshipStatus;
    private String occupation;
    private PhoneNumberDTO phoneNumber;

    public String getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getRelationshipStatus() {
        return relationshipStatus;
    }

    public String getOccupation() {
        return occupation;
    }

    public PhoneNumberDTO getPhoneNumber() {
        return phoneNumber;
    }
}
