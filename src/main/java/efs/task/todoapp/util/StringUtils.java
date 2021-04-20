package efs.task.todoapp.util;

public final class StringUtils {
    private StringUtils() {}

    public static boolean isBlank(String word) {
        return word == null || word.isBlank();
    }
}
