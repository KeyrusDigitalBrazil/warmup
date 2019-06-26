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
class WhereClauseConditionsUnitTest extends Specification
{
	def static final CONDITION_1 = new WhereClauseCondition("condition1")
	def static final CONDITION_2 = new WhereClauseCondition("condition2")

	@Test
	@Unroll
	def "create a WhereClauseConditions from an array of conditions #conditions"()
	{
		given:
		def whereClauseConditions = new WhereClauseConditions((WhereClauseCondition[])conditions)

		expect:
		whereClauseConditions.getConditions().size() == count
		whereClauseConditions.getConditions() == resultList

		where:
		conditions 					| count | resultList
		[CONDITION_1, CONDITION_2]	| 2 	| [CONDITION_1, CONDITION_2]
		[null, CONDITION_1] 		| 1 	| [CONDITION_1]
		null 						| 0		| []
		[]							| 0		| []
	}


	@Test
	@Unroll
	def "create a WhereClauseConditions from a list of conditions #conditions"()
	{
		given:
		def whereClauseConditions = new WhereClauseConditions(conditions)

		expect:
		whereClauseConditions.getConditions().size() == count
		whereClauseConditions.getConditions() == resultList

		where:
		conditions 					| count | resultList
		[CONDITION_1, CONDITION_2]	| 2 	| [CONDITION_1, CONDITION_2]
		[null, CONDITION_1] 		| 1 	| [CONDITION_1]
		null 						| 0		| []
		[]							| 0		| []
	}

	@Test
	@Unroll
	def "two WhereClauseConditions are equal == #equal"()
	{
		given:
		def whereClauseConditions1 = new WhereClauseConditions(condition1)
		def whereClauseConditions2 = new WhereClauseConditions(condition2)

		expect:
		(whereClauseConditions1 == whereClauseConditions2) == equal
		(whereClauseConditions1.hashCode() == whereClauseConditions2.hashCode()) == equal

		where:
		condition1 	| condition2 	| equal
		CONDITION_1 | CONDITION_1	| true
		CONDITION_1	| CONDITION_2	| false
	}

	@Test
	def "conditions are in the same order as the original list"()
	{
		given:
		def whereClauseConditions = new WhereClauseConditions((WhereClauseCondition[])[CONDITION_2, null, CONDITION_1, CONDITION_2])

		expect:
		whereClauseConditions.getConditions() == [CONDITION_2, CONDITION_1, CONDITION_2]
	}

	@Test
	@Unroll
	def "join conditions1 #conditions1 and conditions2 #conditions2 with the conjunctive operator #operator"()
	{
		when:
		def whereClauseConditions = conditions1.join((WhereClauseConditions)conditions2, operator)

		then:
		whereClauseConditions.getConditions().size() == count
		whereClauseConditions.getConditions() == resultList

		where:
		conditions1 							| conditions2 								| operator 						| count | resultList
		new WhereClauseConditions(CONDITION_1)	| new WhereClauseConditions(CONDITION_2)	| ConjunctiveOperator.AND		| 2 	| [new WhereClauseCondition(CONDITION_1.getCondition(), ConjunctiveOperator.AND), CONDITION_2]
		new WhereClauseConditions(CONDITION_1)	| new WhereClauseConditions([])				| ConjunctiveOperator.OR 		| 1 	| [new WhereClauseCondition(CONDITION_1.getCondition(), ConjunctiveOperator.OR)]
		new WhereClauseConditions([]) 			| new WhereClauseConditions(CONDITION_2)	| ConjunctiveOperator.AND		| 1		| [CONDITION_2]
		new WhereClauseConditions(CONDITION_1)	| null										| ConjunctiveOperator.OR		| 1		| [CONDITION_1]
		new WhereClauseConditions(CONDITION_1)	| new WhereClauseConditions(CONDITION_2)	| ConjunctiveOperator.UNKNOWN	| 1		| [CONDITION_1]
		new WhereClauseConditions(CONDITION_1)	| new WhereClauseConditions(CONDITION_2)	| null							| 1		| [CONDITION_1]
	}
}


