package com.tymoshenko.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Yakiv
 * @since 17.02.2016
 */
public class MemoryUnitParserTest {

    @Test
    public void shouldParseLong_Bytes() throws Exception {
        assertEquals("Wrong value parse zero", "0 B", MemoryUnitParser.parse(0));
        assertEquals("Wrong value parse < 1024 B", "999 B", MemoryUnitParser.parse(999));
//        assertEquals("Wrong value parse < 1024 B", "1 000 B", MemoryUnitParser.parse(1000));
    }

    @Test
    public void shouldParseLong_KyloBytes() throws Exception {
        assertEquals("Wrong value parse 1024", "1 KB", MemoryUnitParser.parse(1024));
        assertEquals("Wrong value parse > 1 MB. Truncation", "1 KB", MemoryUnitParser.parse(1025));
        assertEquals("Wrong value parse > 1 MB. Fraction", "1,9 KB", MemoryUnitParser.parse(1925));
    }

    @Test
    public void shouldParseLong_MegaBytes() throws Exception {
        assertEquals("Wrong value parse 1024", "1 MB", MemoryUnitParser.parse(1048576));
        assertEquals("Wrong value parse > 1 GB. Truncation", "1 MB", MemoryUnitParser.parse(1068576));
        assertEquals("Wrong value parse > 1 GB. Fraction", "1,9 MB", MemoryUnitParser.parse(1948576));
    }

    @Test
    public void shouldParseLong_GygaBytes() throws Exception {
        assertEquals("Wrong value parse 1024", "1 GB", MemoryUnitParser.parse(1073741824));
        assertEquals("Wrong value parse > 1 GB. Truncation", "1 GB", MemoryUnitParser.parse(1079741824));
        assertEquals("Wrong value parse > 1 GB. Fraction", "1,8 GB", MemoryUnitParser.parse(1973741824));

        assertEquals("Wrong value parse > 1 GB. Fraction", "10,2 GB", MemoryUnitParser.parse(10973741824L));
    }
}