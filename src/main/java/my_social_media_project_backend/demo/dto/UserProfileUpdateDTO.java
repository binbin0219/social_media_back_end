package my_social_media_project_backend.demo.dto;

public class UserProfileUpdateDTO {

    private String avatar;
    private String username;
    private String firstName;
    private String lastName;
    private String description;
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

    public String getDescription() { return description; }

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

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setRelationshipStatus(String relationshipStatus) {
        this.relationshipStatus = relationshipStatus;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setPhoneNumber(PhoneNumberDTO phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
