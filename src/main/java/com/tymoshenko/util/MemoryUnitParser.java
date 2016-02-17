package com.tymoshenko.util;

import java.text.DecimalFormat;

/**
 * @author Yakiv Tymoshenko
 * @since 08.02.2016
 */
public class MemoryUnitParser {

    /**
     * Converts string representation to bytes.
     * FIXME : now supports only KB and simply removes all non-digit chars from the string
     * @param memoryWithUnit e.g. "41 808 KB"
     * @return bytes
     */
    public static long parse(final String memoryWithUnit) {
        String digitsOnly = removeNonDigitChars(memoryWithUnit);
        return Long.valueOf(digitsOnly);
    }

    public static String parse(final long bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("Negative memory used: " + bytes);
        }
        if (bytes == 0) {
            return "0 B";
        }
        String result = null;
        final long[] _dividers = MemoryUnit.dividers;
        final String[] _units = MemoryUnit.units;
        for (int i = 0; i < _dividers.length; i++) {
            final long divider = _dividers[i];
            if (bytes >= divider) {
                result = format(bytes, divider, _units[i]);
                break;
            }
        }
        return result;
    }

    public static String parse(final long value, MemoryUnit memoryUnit) {
        long product = value * memoryUnit.getDivider();
        return parse(product);
    }

    private static String format(final long value,
                                 final long divider,
                                 final String unit) {
        final double result =
                divider > 1 ? (double) value / (double) divider : (double) value;
        return new DecimalFormat("#,##0.#").format(result) + " " + unit;
    }

    private static String removeNonDigitChars(String number) {
        StringBuilder sb = new StringBuilder();
        char[] chars = number.toCharArray();
        for (char c : chars) {
            if (Character.isDigit(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
