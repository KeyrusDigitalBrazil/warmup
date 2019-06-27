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
package de.hybris.platform.odata2services.filter.impl

import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupStrategy
import org.apache.olingo.odata2.api.processor.ODataContext
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator
import org.junit.Test
import org.springframework.core.convert.converter.Converter
import spock.lang.Ignore
import spock.lang.Specification

@Ignore("Ignoring so platform doesn't run this test. The child classes will run the test cases.")
class BaseNavigationPropertyVisitingStrategyUnitTest extends Specification
{
	def itemLookupRequestFactory = Mock(ItemLookupRequestFactory)
	def itemLookupStrategy = Mock(ItemLookupStrategy)
	def context = Mock(ODataContext)
	def operatorConverter = Mock(Converter)
	def strategy

	@Test
	def "empty condition is returned if supported operator is not '='"()
	{
		given:
		operatorConverter.convert(_) >> "PROPERTY_ACCESS"

		expect:
		strategy.visit(null, BinaryOperator.PROPERTY_ACCESS, null, null).getConditions().isEmpty()
	}
}
