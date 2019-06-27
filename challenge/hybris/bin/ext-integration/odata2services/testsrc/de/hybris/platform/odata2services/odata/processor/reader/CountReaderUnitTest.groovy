/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.processor.reader

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest
import de.hybris.platform.odata2services.odata.persistence.ModelEntityService
import de.hybris.platform.odata2services.odata.processor.reader.CountReader
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.commons.InlineCount
import org.apache.olingo.odata2.api.uri.UriInfo
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class CountReaderUnitTest extends Specification {
    def entityService = Mock(ModelEntityService)
    def countReader = new CountReader()

    def setup() {
        countReader.setEntityService(entityService)
    }

    @Test
    @Unroll
    def "#result when #condition"() {
        expect:
        countReader.isApplicable(uriInfo) == expected

        where:
        uriInfo                              | result           | condition                  | expected
        inlineCountInfo(InlineCount.NONE)    | 'not applicable' | '$inlineCount is NONE'     | false
        inlineCountInfo(InlineCount.ALLPAGES)| 'not applicable' | '$inlineCount is ALLPAGES' | false
        inlineCountInfo(null)                | 'not applicable' | '$inlineCount is absent'   | false
        countInfo(false)                     | 'not applicable' | '$count is not present'    | false
        countInfo(true)                      | 'applicable'     | '$count is present'        | true
    }

    @Test
    def "reads count returned by the persistence service"() {
        given:
        def request = Mock(ItemLookupRequest)
        entityService.count(request) >> 99

        when:
        def response = countReader.read(request)

        then:
        with(response) {
            getEntity() == 99
            status == HttpStatusCodes.OK
            getHeader('Content-Type') == "text/plain"
        }
    }

    def countInfo(final boolean value) {
        Mock(UriInfo) {
            isCount() >> value
        }
    }

    UriInfo inlineCountInfo(final InlineCount count) {
        Mock(UriInfo) {
            getInlineCount() >> count
        }
    }
}
