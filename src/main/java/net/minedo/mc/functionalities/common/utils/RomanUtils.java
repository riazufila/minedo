package net.minedo.mc.functionalities.common.utils;

import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

/**
 * Roman utils.
 */
public class RomanUtils {

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    /**
     * Convert integer to roman.
     *
     * @param number number
     * @return number in roman
     */
    public static @NotNull String toRoman(int number) {
        int flooredKey = map.floorKey(number);
        if (number == flooredKey) {
            return map.get(number);
        }
        return map.get(flooredKey) + toRoman(number - flooredKey);
    }

}
