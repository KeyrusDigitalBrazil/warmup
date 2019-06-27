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

import de.hybris.bootstrap.annotations.UnitTest
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression
import org.apache.olingo.odata2.core.edm.provider.EdmSimplePropertyImplProv
import org.junit.Test
import spock.lang.Specification

@UnitTest
class DefaultPropertyExpressionVisitorUnitTest extends Specification
{
	def visitor = new DefaultPropertyExpressionVisitor()

	@Test
	def "visiting simple type"()
	{
		given:
		def propertyName = "myProp"

		expect:
		visitor.visit(Mock(PropertyExpression), propertyName, Mock(EdmSimplePropertyImplProv)) == propertyName
	}

	@Test
	def "visiting navigation property type"()
	{
		def type = Mock(EdmNavigationProperty)

		expect:
		visitor.visit(Mock(PropertyExpression), "property", type) == type
	}
}
