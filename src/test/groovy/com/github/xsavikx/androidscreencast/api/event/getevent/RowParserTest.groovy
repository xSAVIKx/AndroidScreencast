package com.github.xsavikx.androidscreencast.api.event.getevent

import com.github.xsavikx.androidscreencast.api.event.*
import spock.lang.Specification

class RowParserTest extends Specification {
    private final EventType2CodeMapper mapper = new EventType2CodeMapper();
    private final RowParser parser = new RowParser(mapper)

    def "should parse valid getevent row"() {
        given:
        def row = "0003 0039 000019ba"
        when:
        def result = parser.parse(row)
        then:
        result != null
        result.type == Type.EV_ABS.type
        result.code == AbsoluteAxes.ABS_MT_TRACKING_ID.code
        result.value == 0x19ba
        result.value == 6586
    }

    def "should parse getevent row with unknown values into event with UNKNOWN values set"() {
        given:
        def row = "0999 9939 0fffffff"
        when:
        def result = parser.parse(row)
        then:
        result != null
        result.type == EventType.UNKNOWN.type
        result.code == EventCode.UNKNOWN.code
        result.value == 0x0fffffff
    }

    def "should throw exception if given row has less than 3 whitespace-separated chunks"() {
        given:
        def row = "0003 0039"
        when:
        parser.parse(row)
        then:
        thrown(IllegalStateException)
    }

    def "should throw exception if given row contain non HEX number values"() {
        given:
        def row = "0003 invalid value"
        when:
        parser.parse(row)
        then:
        thrown(NumberFormatException)
    }
}