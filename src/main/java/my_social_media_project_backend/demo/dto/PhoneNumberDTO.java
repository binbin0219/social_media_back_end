package my_social_media_project_backend.demo.dto;

public class PhoneNumberDTO {
    private String dialCode;
    private String phoneNumberBody;
    private String fullNumber;
    private String countryISO2;
    private String countryName;

    public String getDialCode() {
        return dialCode;
    }

    public void setDialCode(String dialCode) {
        this.dialCode = dialCode;
    }

    public String getPhoneNumberBody() {
        return phoneNumberBody;
    }

    public void setPhoneNumberBody(String phoneNumberBody) {
        this.phoneNumberBody = phoneNumberBody;
    }

    public String getFullNumber() {
        return fullNumber;
    }

    public void setFullNumber(String fullNumber) {
        this.fullNumber = fullNumber;
    }

    public String getCountryISO2() {
        return countryISO2;
    }

    public void setCountryISO2(String countryISO2) {
        this.countryISO2 = countryISO2;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
