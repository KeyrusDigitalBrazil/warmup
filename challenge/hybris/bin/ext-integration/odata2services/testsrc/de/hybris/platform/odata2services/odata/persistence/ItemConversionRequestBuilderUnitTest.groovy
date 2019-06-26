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
import org.apache.olingo.odata2.api.edm.EdmEntitySet
import org.apache.olingo.odata2.api.edm.EdmEntityType
import org.junit.Test
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static de.hybris.platform.odata2services.odata.persistence.ConversionOptions.conversionOptionsBuilder
import static de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest.itemConversionRequestBuilder

@UnitTest
class ItemConversionRequestBuilderUnitTest extends Specification {
    private static final Locale locale = Locale.ENGLISH
    private static String code = "IntegrationObjectCode"

    @Shared
    def itemModel = Stub(ItemModel)
    @Shared
    def entityType = Stub(EdmEntityType)
    @Shared
    def entitySet = Stub(EdmEntitySet) {
        getEntityType() >> entityType
    }

    @Test
    @Unroll
    def "build() throws IllegalArgumentException when #attr is null"() {
        when:
        itemConversionRequestBuilder().withEntitySet(set)
                .withAcceptLocale(loc)
                .withItemModel(item)
                .withOptions(options)
                .withIntegrationObject(objCode)
                .build()

        then:
        thrown(IllegalArgumentException)

        where:
        attr                      | set       | loc    | item      | options                 | objCode
        "item model"              | entitySet | locale | null      | Stub(ConversionOptions) | code
        "entity set"              | null      | locale | itemModel | Stub(ConversionOptions) | code
        "locale"                  | entitySet | null   | itemModel | Stub(ConversionOptions) | code
        "options"                 | entitySet | locale | itemModel | null                    | code
        "integration object code" | entitySet | locale | itemModel | Stub(ConversionOptions) | null
    }

    @Test
    def "build() throws IllegalArgumentException when entity set has null entity type"() {
        given:
        def entitySet = Stub(EdmEntitySet)

        when:
        itemConversionRequestBuilder().withEntitySet(entitySet)
                .withAcceptLocale(locale)
                .withItemModel(itemModel)
                .build()

        then:
        thrown(IllegalArgumentException)
    }

    @Test
    def "build() creates request with the specified properties"() {
        setup:
        def options = conversionOptionsBuilder().build()

        when:
        def request = itemConversionRequestBuilder().withEntitySet(entitySet)
                .withAcceptLocale(locale)
                .withItemModel(itemModel)
                .withIntegrationObject(code)
                .withOptions(options)
                .build()

        then:
        request.entitySet.is entitySet
        request.entityType.is entityType
        request.acceptLocale.is locale
        request.itemModel.is itemModel
        request.integrationObjectCode.is code
        request.options.is options
    }

    @Test
    def "build() creates a copy of another request"() {
        given:
        def options = conversionOptionsBuilder().build()
        def preRequest = itemConversionRequestBuilder().withEntitySet(entitySet)
                .withAcceptLocale(locale)
                .withItemModel(itemModel)
                .withIntegrationObject(code)
                .withOptions(options)
                .build()

        when:
        def request = itemConversionRequestBuilder().from(preRequest).build()

        then:
        !preRequest.is(request)
        request?.entitySet?.is entitySet
        request?.entityType?.is entityType
        request?.acceptLocale?.is locale
        request?.itemModel?.is itemModel
        request?.integrationObjectCode?.is code
        request?.options?.is options
    }
}