
package mypackage.dto;

public class WorkerProfileUpdateDTO {

    private String fullName;
    private String phoneNumber;
    private String address;

    // Default Constructor
    public WorkerProfileUpdateDTO() {
    }

    // Parameterized Constructor
    public WorkerProfileUpdateDTO(
            String fullName,
            String phoneNumber,
            String address) {

        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters and Setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
