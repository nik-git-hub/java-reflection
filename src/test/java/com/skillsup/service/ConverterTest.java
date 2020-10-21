package com.skillsup.service;

import com.skillsup.model.Human;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConverterTest {

    private static final Logger LOG = LoggerFactory.getLogger(ConverterTest.class);
    private static Converter converter;

    @BeforeClass
    public static void setUp() {
        converter = new Converter();
    }

    @Ignore
    @Test
    public void shouldBeObjectConvertedToJson() {
        //GIVEN
        Human human = new Human.Builder()
                .withFirstName("John")
                .withLastName("Lennon")
                .withHobby("Guitar")
                .withBirthDate(LocalDate.of(1940, 10, 9))
                .withAge(40)
                .build();
        String json = "{\"firstName\":\"John\",\"lastName\":\"Lennon\"," +
                "\"fun\":\"Guitar\",\"birthDate\":\"09-10-1940\",\"age\":40}";
        //WHEN
        String actualJson = converter.toJson(human);
        //THEN
        assertEquals(json, actualJson);
    }

    @Test
    public void shouldBeEmptyObjectConvertedToJson() {
        //GIVEN
        Human human = new Human();
        String json = "{}";
        //WHEN
        String actualJson = converter.toJson(human);
        //THEN
        assertEquals(json, actualJson);
    }

    @Test
    public void shouldBeNotNullFieldsConvertedToJson() {
        //GIVEN
        Human human = new Human.Builder()
                .withLastName("Lennon")
                .withAge(40)
                .build();
        String json = "{\"lastName\":\"Lennon\",\"age\":40}";
        //WHEN
        String actualJson = converter.toJson(human);
        //THEN
        assertEquals(json, actualJson);
    }

    @Ignore
    @Test
    public void shouldBeFieldBirthDateConvertedToFormatDDMMYYYY() {
        //GIVEN
        Human human = new Human.Builder().withBirthDate(LocalDate.of(1940, 10, 9)).build();
        String json = "{\"birthDate\":\"09-10-1940\"}";
        //WHEN
        String actualJson = converter.toJson(human);
        //THEN
        assertEquals(json, actualJson);
    }

    @Ignore
    @Test
    public void shouldBeFieldHobbyConvertedTo_fun() {
        //GIVEN
        Human human = new Human.Builder()
                .withHobby("Guitar")
                .build();
        String json = "{\"fun\":\"Guitar\"}";
        //WHEN
        String actualJson = converter.toJson(human);
        //THEN
        assertEquals(json, actualJson);
    }

    @Test
    public void shouldBeNullField_firstName() {
        //GIVEN
//        String json = "{\"lastName\":\"Lennon\",\"fun\":\"Guitar\",\"birthDate\":\"09-10-1940\",\"age\":40}";
        String json = "{\"lastName\":\"Lennon\"}";
        //WHEN
        Human human = converter.fromJson(json, Human.class);
        //THEN
        assertTrue(Objects.isNull(human.getFirstName()));
    }

    @Ignore
    @Test
    public void shouldBeDefaultDateFormat_YYYYMMDD() {
        //GIVEN
        String json = "{\"birthDate\":\"09-10-1940\"}";
        String birthDate = "1940-10-09";
        //WHEN
        Human human = converter.fromJson(json, Human.class);
        //THEN
        String actualBirthDate = human.getBirthDate().toString();
        assertEquals(birthDate, actualBirthDate);
    }

    @Test
    public void shouldHaveValueFieldHobby() {

        LOG.info("start: shouldHaveValueFieldHobby");

        //GIVEN
        String json = "{\"fun\":\"Guitar\"}";
        String hobby = "Guitar";
        //WHEN
        Human human = converter.fromJson(json, Human.class);
        //THEN
        String actualValue = human.getHobby();
        assertEquals(hobby, actualValue);
        LOG.info("end: shouldHaveValueFieldHobby: [{}]", actualValue);

    }
}
