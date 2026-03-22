package com.liuyue.igny.utils.deobfuscator.mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * Copy from Carpet-TIS-Addition
 * 此文件以LGPL-3.0协议开源
 */

public interface MappingReader {
    Map<String, String> readMapping(BufferedReader mappingReader) throws IOException;
}