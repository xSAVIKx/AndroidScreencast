package com.github.xsavikx.androidscreencast.api.event

import spock.lang.Specification
import spock.lang.Unroll


class EventType2CodeMapperTest extends Specification {
    @Unroll
    def "should return UNKNOWN for not yet mapped type #type"() {
        given:
        def mapper = new EventType2CodeMapper()
        when:
        def result = mapper.forType(type, 0)
        then:
        result == EventCode.UNKNOWN
        where:
        type << Type.values() - [Type.EV_KEY, Type.EV_SYN, Type.EV_ABS]
    }

    @Unroll
    def "should map EV_ABS to AbsoluteAxes.#enumInstance by its code"() {
        given:
        def mapper = new EventType2CodeMapper()
        when:
        def result = mapper.forType(Type.EV_ABS, enumInstance.getCode())
        then:
        result != null
        result.code == enumInstance.code
        where:
        enumInstance << AbsoluteAxes.values()
    }

    @Unroll
    def "should map EV_SYN to SynchronizationReport.#enumInstance by its code"() {
        given:
        def mapper = new EventType2CodeMapper()
        when:
        def result = mapper.forType(Type.EV_SYN, enumInstance.code)
        then:
        result != null
        result.code == enumInstance.code
        where:
        enumInstance << SynchronizationReport.values()
    }

    @Unroll
    def "should map EV_KEY to Button.#enumInstance by its code"() {
        given:
        def mapper = new EventType2CodeMapper()
        when:
        def result = mapper.forType(Type.EV_KEY, enumInstance.code)
        then:
        result != null
        result.code == enumInstance.code
        where:
        enumInstance << Button.values()
    }
}