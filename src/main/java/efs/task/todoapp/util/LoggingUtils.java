package efs.task.todoapp.util;

import efs.task.todoapp.ToDoApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public final class LoggingUtils {
    private LoggingUtils() {}

    public static void loadProperties() {
        InputStream stream = ToDoApplication.class.getClassLoader().getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
