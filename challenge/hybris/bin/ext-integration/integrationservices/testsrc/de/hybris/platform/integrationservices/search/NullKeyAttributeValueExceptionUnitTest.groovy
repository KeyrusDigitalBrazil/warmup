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

package de.hybris.platform.integrationservices.search

import de.hybris.bootstrap.annotations.UnitTest
import org.junit.Test
import spock.lang.Specification

import static de.hybris.platform.integrationservices.model.BaseMockItemAttributeModelBuilder.simpleAttributeBuilder

@UnitTest
class NullKeyAttributeValueExceptionUnitTest extends Specification {
    @Test
    def "reads item type back"()
    {
        given:
        def item = simpleAttributeBuilder().withIntegrationObjectItemCode("MyType").build()

        expect:
        new NullKeyAttributeValueException(item, Collections.emptyMap()).itemType == 'MyType'
    }

    @Test
    def "reads attribute name back"()
    {
        given:
        def item = simpleAttributeBuilder().withName("MyAttribute").build()

        expect:
        new NullKeyAttributeValueException(item, Collections.emptyMap()).attributeName == 'MyAttribute'
    }
}
