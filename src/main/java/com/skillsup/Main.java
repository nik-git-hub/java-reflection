package com.skillsup;

import com.skillsup.model.Human;
import com.skillsup.service.Converter;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Converter converter = new Converter();

        Human human = new Human.Builder()
                .withFirstName("John")
                .withLastName("Lennon")
                .withHobby("Guitar")
                .withBirthDate(LocalDate.of(1940, 10, 9))
                .withAge(40)
                .build();

        String json = converter.toJson(human);
        System.out.println("json " + json);

        Human human3 = converter.fromJson(json, Human.class);
        System.out.println("object human " + human3);

    }
}
