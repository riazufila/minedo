package net.minedo.mc.repositories;

public class DatabaseUtils {

    public static String buildWhereInClause(int count) {
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                placeholders.append(", ");
            }

            placeholders.append("?");
        }

        return placeholders.toString();
    }

}
