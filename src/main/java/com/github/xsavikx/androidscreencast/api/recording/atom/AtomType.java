/*
 * Copyright 2020 Yurii Serhiichuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.xsavikx.androidscreencast.api.recording.atom;

/**
 * Represents all possible Atom types regarding
 * <a href="https://developer.apple.com/library/content/documentation/QuickTime/QTFF/QTFFChap2/qtff2.html#//apple_ref/doc/uid/TP40000939-CH204-SW1">Movie Atoms Apple docs</a>
 */
public enum AtomType {
    MEDIA_DATA("mdat"),
    MOVIE("moov"),
    MOVIE_HEADER("mvhd"),
    TRACK("trak"),
    TRACK_HEADER("tkhd"),
    MEDIA("mdia"),
    MEDIA_HEADER("mdhd"),
    HANDLER("hdlr"),
    MEDIA_HANDLER("mhlr"),
    VIDEO("vide"),
    MEDIA_INFORMATION("minf"),
    VIDEO_MEDIA_INFORMATION("vmhd"),
    FILE_ALIAS("alis"),
    DATA_INFORMATION("dinf"),
    DATA_REFERENCE("dref"),
    SAMPLE_TABLE("stbl"),
    SAMPLE_DESCRIPTION("stsd"),
    RAW("raw "),
    JAVA("java"),
    JPEG("jpeg"),
    PNG("png "),
    TIME_TO_SAMPLE_MAPPING("stts"),
    SAMPLE_TO_CHUNK_MAPPING("stsc"),
    SAMPLE_SIZE("stsz"),
    STANDARD_CHUNK_OFFSET_TABLE("stco"),
    WIDE_CHUNK_OFFSET_TABLE("co64"),
    FILE_TYPE("ftyp"),
    QUICK_TIME("qt  "),
    WIDE("wide"),
    DATA_HANDLER("dhlr");
    /**
     * String representation of AtomType that must have exactly 4 characters
     */
    private final String stringRepresentation;

    AtomType(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public String toStringRepresentation() {
        return stringRepresentation;
    }
}
