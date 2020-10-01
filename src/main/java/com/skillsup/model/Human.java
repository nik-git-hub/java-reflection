package com.skillsup.model;

import com.skillsup.annotation.CustomDateFormat;
import com.skillsup.annotation.JsonValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Human {

    private String firstName;
    private String lastName;
    @JsonValue(name = "fun")
    private String hobby;
    @CustomDateFormat(format = "dd-MM-yyyy")
    private LocalDate birthDate;
    private Integer age;


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

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Human{" +
                "firstName=" + firstName +
                ", lastName=" + lastName +
                ", hobby=" + hobby +
                ", birthDate=" + birthDate + ", " +
                "age=" + age + '}';
    }


    public static class Builder {
        private Human newHuman;

        public Builder() {
            newHuman = new Human();
        }

        public Builder withFirstName (String firstName) {
            newHuman.firstName = firstName;
            return this;
        }

        public Builder withLastName (String lastName) {
            newHuman.lastName = lastName;
            return this;
        }

        public Builder withHobby (String hobby) {
            newHuman.hobby = hobby;
            return this;
        }

        public Builder withBirthDate (LocalDate birthDate) {
            newHuman.birthDate = birthDate;
            return this;
        }

        public Builder withAge (int age) {
            newHuman.age = age;
            return this;
        }

        public Human build(){
            return newHuman;
        }

    }
}
