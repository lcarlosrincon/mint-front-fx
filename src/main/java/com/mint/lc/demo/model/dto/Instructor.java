package com.mint.lc.demo.model.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Instructor {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthday;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getInfo() {
        StringBuilder info = new StringBuilder(this.firstName);
        info.append(" ").append(this.lastName);
        if (this.birthday != null) {
            info.append(" (").append(DateTimeFormatter.ofPattern("MMM dd").format(this.birthday)).append(")");
        }
        return info.toString();
    }
}
