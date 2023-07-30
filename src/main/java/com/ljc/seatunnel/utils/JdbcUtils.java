package com.ljc.seatunnel.utils;

import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public class JdbcUtils {

    /**
     * Get the host from the jdbc url.
     *
     * @param url jdbc:clickhouse://localhost:8123
     * @return localhost:8123
     */
    public static String getAddressFromUrl(String url) {
        checkNotNull(url, "url can not be null");
        Pattern pattern = Pattern.compile("jdbc:clickhouse:\\/\\/(.*):([0-9]+)(.*)");
        return pattern.matcher(url).replaceAll("$1:$2");
    }

    public static String replaceDatabase(String jdbcUrl, String databaseName) {
        if (databaseName == null) {
            return jdbcUrl;
        }
        String[] split = jdbcUrl.split("\\?");
        if (split.length == 1) {
            return replaceDatabaseWithoutParameter(jdbcUrl, databaseName);
        }
        return replaceDatabaseWithoutParameter(split[0], databaseName) + "?" + split[1];
    }

    private static String replaceDatabaseWithoutParameter(String jdbcUrl, String databaseName) {
        int lastIndex = jdbcUrl.lastIndexOf(':');
        char[] chars = jdbcUrl.toCharArray();
        for (int i = lastIndex + 1; i < chars.length; i++) {
            if (chars[i] == '/') {
                return jdbcUrl.substring(0, i + 1) + databaseName;
            }
        }
        return jdbcUrl + "/" + databaseName;
    }
}
