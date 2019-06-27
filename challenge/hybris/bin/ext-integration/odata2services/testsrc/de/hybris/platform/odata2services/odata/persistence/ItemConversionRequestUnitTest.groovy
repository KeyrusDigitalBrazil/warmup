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

package de.hybris.platform.odata2services.odata.persistence

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import org.apache.olingo.odata2.api.edm.*
import org.apache.olingo.odata2.api.uri.NavigationSegment
import org.junit.Test
import spock.lang.Specification

import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder
import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder

@UnitTest
class ItemConversionRequestUnitTest extends Specification {
    def entitySet = Stub(EdmEntitySet) {
        getEntityType() >> Stub(EdmEntityType) {
            getProperty("referenced") >> Stub(EdmProperty) {
                getType() >> Stub(EdmType) {
                    getName() >> 'ReferencedItem'
                }
            }
        }
        getEntityContainer() >> Stub(EdmEntityContainer) {
            getEntitySet('ReferencedItems') >> Stub(EdmEntitySet) {
                getName() >> 'ReferenceItems'
            }
        }
    }

    @Test
    def "creates sub-request for navigation property conversion"() {
        given:
        def item = Stub(ItemModel)
        def request = requestBuilder()
                .withOptions(conversionOptionsBuilder().withNavigationSegments([Stub(NavigationSegment)]).build())
                .build()

        when:
        def subrequest = request.propertyConversionRequest('referenced', item)

        then:
        subrequest?.acceptLocale == request.acceptLocale
        subrequest.integrationObjectCode == request.integrationObjectCode
        subrequest.entitySet.name == 'ReferenceItems'
        subrequest.itemModel == item
        subrequest.options.navigationSegments == []
    }

    @Test
    def "conversionLevel reflects how deep the property is nested from the original request"() {
        given:
        def parent = requestBuilder().build()

        when:
        def child1 = parent.propertyConversionRequest('son', Stub(ItemModel))
        def child2 = parent.propertyConversionRequest('daughter', Stub(ItemModel))
        def grandchild = child1.propertyConversionRequest('child', Stub(ItemModel))

        then:
        parent.conversionLevel == 0
        child1.conversionLevel == 1
        child2.conversionLevel == 1
        grandchild.conversionLevel == 2
    }

    def requestBuilder() {
        itemConversionRequestBuilder()
                .withEntitySet(entitySet)
                .withItemModel(Stub(ItemModel))
                .withIntegrationObject("SomeObject")
                .withAcceptLocale(Locale.ENGLISH)
                .withOptions(conversionOptionsBuilder().build())
    }
}
