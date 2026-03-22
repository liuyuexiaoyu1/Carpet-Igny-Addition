package com.liuyue.igny.utils.deobfuscator.mapping;

import com.google.common.collect.Maps;
import net.fabricmc.mapping.reader.v2.MappingGetter;
import net.fabricmc.mapping.reader.v2.TinyMetadata;
import net.fabricmc.mapping.reader.v2.TinyV2Factory;
import net.fabricmc.mapping.reader.v2.TinyVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * Copy from Carpet-TIS-Addition
 * 此文件以LGPL-3.0协议开源
 */

public class TinyMappingV2Reader implements MappingReader {
    @Override
    public Map<String, String> readMapping(BufferedReader reader) throws IOException {
        Map<String, String> mappings = Maps.newHashMap();
        TinyV2Factory.visit(reader, new MappingVisitor(mappings));
        return mappings;
    }

    private static class MappingVisitor implements TinyVisitor {
        private final Map<String, String> mappings;
        private int intermediaryIndex;
        private int namedIndex;

        public MappingVisitor(Map<String, String> mappings) {
            this.mappings = mappings;
        }

        private void putMappings(MappingGetter name) {
            String intermediaryName = name.get(this.intermediaryIndex).replace('/', '.');
            String remappedName = name.get(this.namedIndex).replace('/', '.');
            this.mappings.put(intermediaryName, remappedName);
        }

        @Override
        public void start(TinyMetadata metadata) {
            this.intermediaryIndex = metadata.index("intermediary");
            this.namedIndex = metadata.index("named");
        }

        @Override
        public void pushClass(MappingGetter name) {
            this.putMappings(name);
        }

        @Override
        public void pushField(MappingGetter name, String descriptor) {
            this.putMappings(name);
        }

        @Override
        public void pushMethod(MappingGetter name, String descriptor) {
            this.putMappings(name);
        }
    }
}