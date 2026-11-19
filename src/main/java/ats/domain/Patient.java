package main.java.ats.domain;

import main.java.ats.util.TextUtil;
import java.time.LocalDate;

public class Patient {
    private static int nextId = 1;

    private final int id;
    private final String firstName;
    private final String lastName;
    private final String insuranceNum; // optional (RAMQ number)
    private final LocalDate dateOfBirth; // optional
    private final String phoneNum; // optional

    public Patient(String firstName, String lastName, String insuranceNum, LocalDate dateOfBirth, String phoneNum) {
        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("first and last name are required");
        }
        this.id = nextId++;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.insuranceNum = (insuranceNum == null || insuranceNum.trim().isEmpty()) ? null : insuranceNum.trim();
        this.dateOfBirth = dateOfBirth;
        this.phoneNum = (phoneNum == null || phoneNum.trim().isEmpty()) ? null : phoneNum.trim();
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getInsuranceNum() {
        return insuranceNum;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    /**
     * Every criterion that is supplied must match (blank/null criteria are
     * ignored).
     */
    public boolean matches(String insuranceQuery, LocalDate dob, String phoneQuery, String namePart) {
        if (!TextUtil.isBlank(insuranceQuery)) {
            if (insuranceNum == null
                    || !TextUtil.normalize(insuranceNum).equals(TextUtil.normalize(insuranceQuery))) {
                return false;
            }
        }
        if (dob != null && !dob.equals(dateOfBirth)) {
            return false;
        }
        if (!TextUtil.isBlank(phoneQuery)) {
            String q = TextUtil.digitsOnly(phoneQuery);
            if (q.isEmpty() || phoneNum == null || !TextUtil.digitsOnly(phoneNum).contains(q)) {
                return false;
            }
        }
        if (!TextUtil.isBlank(namePart) && !TextUtil.containsAllTokens(getFullName(), namePart)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getFullName() + (phoneNum == null ? "" : ", " + phoneNum)
                + (insuranceNum == null ? "" : ", " + insuranceNum)
                + (dateOfBirth == null ? "" : ", born " + dateOfBirth);
    }
}