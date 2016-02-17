package com.tymoshenko.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Yakiv Tymoshenko
 * @since 08.02.2016
 */
public enum MemoryUnit {
    B(1),
    KB(1024),
    MB(KB.getDivider() * KB.getDivider()),
    GB(KB.getDivider() * MB.getDivider());

    private static final Logger LOG = LoggerFactory.getLogger(MemoryUnit.class);
    public static final long[] dividers = new long[]{GB.getDivider(), MB.getDivider(), KB.getDivider(), B.getDivider()};
    public static final String[] units = new String[]{GB.name(), MB.name(), KB.name(), B.name()};

    /**
     * @param s memory units e.g. "123,456.789 MB"
     * @return memory unit
     */
    public static MemoryUnit getValueOf(String s) {
        MemoryUnit memoryUnit;
        // "1"
        // "1 B"
        // "1 KB"
        int length = s.length();
        if (length < 4) {
            memoryUnit = B;
        } else {
            String unit = s.substring(length - 2, length).toUpperCase();
            try {
                memoryUnit = MemoryUnit.valueOf(unit);
            } catch (IllegalArgumentException e) {
                LOG.warn(String.format("Troubles determining memory unit. Assuming Bytes by default. String=\"%s\"", s));
                memoryUnit = B;
            }
        }
        return memoryUnit;
    }

    private long divider;

    MemoryUnit(long divider) {
        this.divider = divider;
    }

    public long getDivider() {
        return divider;
    }
}
