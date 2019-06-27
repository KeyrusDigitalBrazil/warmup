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
package de.hybris.platform.odata2services.filter

import de.hybris.bootstrap.annotations.UnitTest
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class BinaryOperatorToSqlOperatorConverterUnitTest extends Specification
{
	def converter = new BinaryOperatorToSqlOperatorConverter()

	@Test
	@Unroll
	def "converting #binaryOp to #sqlOp"()
	{
		expect:
		sqlOp.equals(converter.convert(binaryOp))

		where:
		binaryOp           | sqlOp
		BinaryOperator.AND | "AND"
		BinaryOperator.OR  | "OR"
		BinaryOperator.EQ  | "="
	}

	@Test
	def "unsupported operator throws exception"()
	{
		when:
		converter.convert(BinaryOperator.PROPERTY_ACCESS)

		then:
		thrown OperatorNotSupportedException
	}
}
