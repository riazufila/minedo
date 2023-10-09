package net.minedo.mc.repositories;

import org.jetbrains.annotations.NotNull;

/**
 * Database helper.
 */
public final class DatabaseUtils {

    /**
     * Build SQL statements with IN conditions.
     *
     * @param count number of conditions
     * @return return concatenated value placeholder
     */
    public static @NotNull String buildWhereInClause(int count) {
        if (count == 0) {
            throw new RuntimeException("Count must be more than zero.");
        }

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
