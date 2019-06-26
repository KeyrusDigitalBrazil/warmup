/*
 * [y] hybris Platform
 *
 * Copyright (c) 2019 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.persistence

import de.hybris.bootstrap.annotations.UnitTest
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty
import org.apache.olingo.odata2.api.uri.NavigationPropertySegment
import org.apache.olingo.odata2.api.uri.NavigationSegment
import org.junit.Test
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder

@UnitTest
class ConversionOptionsUnitTest extends Specification
{
    @Shared
    def NAVIGATION_SEGMENT = Stub(NavigationSegment)
    @Shared
    def EXPAND_SEGMENT = Stub(NavigationPropertySegment)

    @Test
    def "initialized with default values"() {
        given:
        def options = conversionOptionsBuilder().build()

        expect:
        options.includeCollections
        options.navigationSegments?.empty
        options.expand?.empty
    }

    @Test
    @Unroll
    def "includeCollections is #result when it was set to #value"() {
        given:
        def options = conversionOptionsBuilder().withIncludeCollections(value).build()

        expect:
        options.includeCollections == result

        where:
        value | result
        true  | true
        false | false
    }

    @Test
    @Unroll
    def "navigation segments is #expected when it was set to #value"() {
        given:
        def options = conversionOptionsBuilder().withNavigationSegments(value).build()

        expect:
        options.navigationSegments == expected

        where:
        value                | expected
        [NAVIGATION_SEGMENT] | [NAVIGATION_SEGMENT]
        null                 | []
    }

    @Test
    @Unroll
    def "expand segments is #expected when it was set to #value"() {

        given:
        def options = conversionOptionsBuilder().withExpand(value).build()

        expect:
        options.expand == expected

        where:
        value              | expected
        [[EXPAND_SEGMENT]] | [[EXPAND_SEGMENT]]
        null               | []
    }

    @Test
    def "can be copied from another conversion options"() {
        given:
        def original = conversionOptionsBuilder()
                .withIncludeCollections(false)
                .withNavigationSegments([NAVIGATION_SEGMENT])
                .withExpand([[EXPAND_SEGMENT]])
                .build()

        when:
        def copy = conversionOptionsBuilder().from(original).build()

        then:
        copy.includeCollections == original.includeCollections
        copy.navigationSegments == original.navigationSegments
        copy.expand == original.expand
    }

    @Test
    @Unroll
    def "isNavigationSegmentPresent() is #res when navigation segments set to #segments"() {
        given:
        def options = conversionOptionsBuilder().withNavigationSegments(segments).build()

        expect:
        options.navigationSegmentPresent == res

        where:
        segments             | res
        [NAVIGATION_SEGMENT] | true
        []                   | false
        null                 | false
    }

    @Test
    @Unroll
    def "isExpandPresent() is #res when expand set to #expand"() {
        given:
        def options = conversionOptionsBuilder().withExpand(expand).build()

        expect:
        options.expandPresent == res

        where:
        expand             | res
        [[EXPAND_SEGMENT]] | true
        [[]]               | false
        []                 | false
        null               | false
    }

    @Test
    @Unroll
    def "navigate() when original segments are #original and property is #property"() {
        given:
        def options = conversionOptionsBuilder()
                .withExpand([[EXPAND_SEGMENT]])
                .withNavigationSegments(original)
                .withIncludeCollections(true)
                .build()

        when:
        def consumed = options.navigate(property)

        then:
        consumed?.navigationSegments == result
        consumed.expand == options.expand
        consumed.includeCollections == options.includeCollections

        where:
        original                                 | property | result
        [NAVIGATION_SEGMENT]                     | null     | [NAVIGATION_SEGMENT]
        [NAVIGATION_SEGMENT]                     | ''       | [NAVIGATION_SEGMENT]
        [NAVIGATION_SEGMENT]                     | '   '    | [NAVIGATION_SEGMENT]
        [NAVIGATION_SEGMENT, NAVIGATION_SEGMENT] | 'valid'  | [NAVIGATION_SEGMENT]
        [NAVIGATION_SEGMENT]                     | 'valid'  | []
        []                                       | 'valid'  | []
    }

    @Test
    @Unroll
    def "isNextNavigationSegment() is #result when #condition"() {
        given:
        def options = conversionOptionsBuilder().withNavigationSegments(segments).build()

        expect:
        options.isNextNavigationSegment('otherItem') == result

        where:
        condition                                             | segments                                            | result
        'next navigation segment matches the property'        | [navigation('otherItem')]                           | true
        'next navigation segment does not match the property' | [navigation('nestedItem'), navigation('otherItem')] | false
        'there are no navigation segments'                    | []                                                  | false
    }

    @Test
    @Unroll
    def "isNextExpandSegment() is #result when #condition"() {
        given:
        def options = conversionOptionsBuilder().withExpand(segments).build()

        expect:
        options.isNextExpandSegment('otherItem') == result

        where:
        condition                                         | segments                                      | result
        'next expand segment matches the property'        | [[expand('otherItem')]]                       | true
        'next expand segment does not match the property' | [[expand('nestedItem'), expand('otherItem')]] | false
        'there are no expend segments'                    | []                                            | false
        'next expand segment is empty'                    | [[]]                                          | false
    }

    def navigation(String name) {
        Stub(NavigationSegment) {
            getNavigationProperty() >> Stub(EdmNavigationProperty) {
                getName() >> name
            }
        }
    }

    def expand(String name) {
        Stub(NavigationPropertySegment) {
            getNavigationProperty() >> Stub(EdmNavigationProperty) {
                getName() >> name
            }
        }
    }
}
