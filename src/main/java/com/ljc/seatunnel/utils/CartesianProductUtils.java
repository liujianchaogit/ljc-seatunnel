package com.ljc.seatunnel.utils;

import java.util.ArrayList;
import java.util.List;

public class CartesianProductUtils {

    public static List<List> cartesianProduct(List... lists) {
        List first = transformation(lists[0]);
        for (int i = 0; i < lists.length - 1; i++) {
            first = match(first, lists[i + 1]);
        }
        return first;
    }

    private static List<List> match(List<List> lists, List container1) {
        List<List> r = new ArrayList<>();
        for (List list : lists) {
            for (Object o : container1) {
                List list1 = new ArrayList();
                list1.addAll(list);
                list1.add(o);
                r.add(list1);
            }
        }
        return r;
    }

    private static List<List> transformation(List container) {
        List<List> r = new ArrayList<>();
        container.forEach(
                c -> {
                    List list = new ArrayList();
                    list.add(c);
                    r.add(list);
                });
        return r;
    }

    public static String maskPassword(String password) {
        int length = password.length();
        if (length == 0) {
            return "";
        } else if (length == 1) {
            return "*";
        } else if (length == 2) {
            return "**";
        }
        int maskLength = Math.max(length / 2, 1);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < maskLength; i++) {
            sb.append("*");
        }
        if (length % 2 == 1) {
            sb.append(password.charAt(maskLength));
            maskLength++;
        }
        for (int i = maskLength; i < length; i++) {
            sb.append("*");
        }
        return sb.toString();
    }
}
