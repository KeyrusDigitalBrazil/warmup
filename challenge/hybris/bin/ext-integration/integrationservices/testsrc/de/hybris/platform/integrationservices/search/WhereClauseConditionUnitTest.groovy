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
import spock.lang.Unroll

@UnitTest
class WhereClauseConditionUnitTest extends Specification
{
	@Test
	@Unroll
	def "condition1 #condition1 == condition2 #condition2 is #equal"()
	{
		expect:
		(condition1 == condition2) == equal
		(condition1.hashCode() == condition2.hashCode()) == equal

		where:
		condition1 															| condition2 															| equal
		new WhereClauseCondition("abc") 							| new WhereClauseCondition("abc") 								| true
		new WhereClauseCondition("abc", ConjunctiveOperator.AND)	| new WhereClauseCondition("abc", ConjunctiveOperator.AND) 		| true
		new WhereClauseCondition("abc")								| new WhereClauseCondition("abc", ConjunctiveOperator.UNKNOWN) 	| true
		new WhereClauseCondition("abc")								| new WhereClauseCondition("abc", null) 				| true
		new WhereClauseCondition("abc") 							| new WhereClauseCondition("abc", ConjunctiveOperator.AND) 		| false
		new WhereClauseCondition("abc") 							| new WhereClauseCondition("def") 								| false

	}

	@Test
	def "getting the condition from the where clause condition"()
	{
		given:
		def whereClauseCondition = new WhereClauseCondition("some condition")

		expect:
		"some condition" == whereClauseCondition.getCondition()
	}

	@Test
	def "getting the conjunctive operator from the where clause condition"()
	{
		given:
		def whereClauseCondition = new WhereClauseCondition("some condition", ConjunctiveOperator.AND)

		expect:
		ConjunctiveOperator.AND == whereClauseCondition.getConjunctiveOperator()
	}
}
