package com.skillsup.service;

import com.skillsup.model.Human;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Converter {
    public static final String ANNOTATION_JSON_VALUE = "JsonValue";
    public static final String ANNOTATION_CUSTOM_DATE_FORMAT = "CustomDateFormat";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String IS_NULL = null;
    public static final String PREFIX_GET_METHOD = "get";
    public static final String PREFIX_SET_METHOD = "set";
    private static final Logger LOG = LoggerFactory.getLogger(Converter.class);

    public String toJson(Object o) {

        Class<?> oClass = o.getClass();
        Field[] fields = oClass.getDeclaredFields();

        StringBuilder jsonString = new StringBuilder();
        jsonString.append('{');

        for (Field field : fields) {

            String fieldName = field.getName();
            String fieldType = field.getType().getTypeName()
                    .substring(field.getType().getTypeName().lastIndexOf('.') + 1);
            Method getMethod = null;
            try {
                getMethod = oClass.getMethod(String.format("%s%s%s", PREFIX_GET_METHOD,
                        fieldName.toUpperCase().charAt(0), fieldName.substring(1)));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            try {
                if (getMethod == null) {
                    throw new AssertionError();
                }
                if (getMethod.invoke(o) != IS_NULL || field.getType().isPrimitive()) {

                    List<String> annotationList = getAnnotationList(field);
                    if (annotationList.stream().filter(a -> a.contains(ANNOTATION_JSON_VALUE)).count() == 1L) {
                           fieldName = annotationList.get(0)
                                   .substring(annotationList.get(0).lastIndexOf('=') + 1,
                                           annotationList.get(0).lastIndexOf(')'));
                    }
                    jsonString.append('\"').append(fieldName).append('\"').append(':');

                    String dateFormat = "";
                    if (annotationList.stream().filter(a -> a.contains(ANNOTATION_CUSTOM_DATE_FORMAT)).count() == 1L) {

                        dateFormat = annotationList.get(0)
                                .substring(annotationList.get(0).lastIndexOf('=') + 1,
                                        annotationList.get(0).lastIndexOf(')'));
                    }

                    switch (fieldType) {
                        case "String":
                            jsonString.append('\"').append(getMethod.invoke(o)).append('\"');
                            break;
                        case "LocalDate":
                            String localDateString;
                            if (!dateFormat.isEmpty()) {
                                LocalDate localDate = (LocalDate) getMethod.invoke(o);
                                localDateString = localDate.format(DateTimeFormatter.ofPattern(dateFormat));
                            } else {
                                localDateString = getMethod.invoke(o).toString();
                            }
                            jsonString.append('\"').append(localDateString).append('\"');
                            break;
                        default:
                            jsonString.append(getMethod.invoke(o).toString());
                            break;
                    }
                    jsonString.append(',');
                }
            } catch (NullPointerException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (jsonString.length() > 1) {
            jsonString.deleteCharAt(jsonString.length() - 1);
        }
        jsonString.append('}');
        return jsonString.toString();
    }

    private static String getMethodName(String name) {
        return String.format("%s%s%s",
                PREFIX_SET_METHOD,
                name.toUpperCase().charAt(0),
                name.substring(1));
    }

    public Human fromJson(String json, Class<Human> clazz) {

        Human human = null;
        try {
            human = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        String newJson = json.replace("{","")
                .replace("}","")
                .replace("\"","");
        if ( newJson.isEmpty() ) {
                return human;
        }

        Map< String, String > jsonMap = Arrays
                .stream(newJson.split(","))
                .map(s -> s.split(":"))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));

        Field[] fields = new Field[0];
        if (human != null) {
            fields = human.getClass().getDeclaredFields();
        }

        LOG.info("amount of fields: [{}]",
                fields.length);

        for (Field field : fields) {
            String fieldName = field.getName();
            String key = fieldName;
            List<String> annotationList = getAnnotationList(field);
            if (annotationList.stream()
                    .filter(a -> a.contains(ANNOTATION_JSON_VALUE)).count() == 1L) {
                key = annotationList.get(0)
                        .substring(annotationList.get(0).lastIndexOf('=') + 1,
                                annotationList.get(0).lastIndexOf(')'));
            }

            if (!jsonMap.containsKey(key)) continue;
            String value = jsonMap.get(key);

            String dateFormat = DEFAULT_DATE_FORMAT;
            if (annotationList.stream().filter(a -> a.contains(ANNOTATION_CUSTOM_DATE_FORMAT)).count() == 1L) {
                dateFormat = annotationList.get(0)
                        .substring(annotationList.get(0).lastIndexOf('=') + 1,
                                annotationList.get(0).lastIndexOf(')'));
            }

            try {
                Method setMethod =
                        clazz.getMethod(getMethodName(fieldName) , field.getType());
                runMethod(human, setMethod, value, field.getType().getTypeName(), dateFormat);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return human;
    }

    private static List<String> getAnnotationList(Field field) {
        return Arrays.stream(field.getAnnotations())
                .map(Annotation::toString)
                .collect(Collectors.toList());
    }

    private static void runMethod(Human human, Method setMethod, String value, String fieldType, String dateFormat )  {

        String shortFieldType = fieldType.substring(fieldType.lastIndexOf('.') + 1);

        try {
            switch (shortFieldType) {
                case "Integer":
                    setMethod.invoke(human, Integer.valueOf(value));
                    break;
                case "LocalDate":
                    setMethod.invoke(human, LocalDate.parse(value, DateTimeFormatter.ofPattern(dateFormat)));
                    break;
                default:
                    setMethod.invoke(human, value);
                    break;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
