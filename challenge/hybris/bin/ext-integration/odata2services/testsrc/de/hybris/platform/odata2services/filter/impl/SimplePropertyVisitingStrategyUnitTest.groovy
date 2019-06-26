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
import de.hybris.platform.integrationservices.search.WhereClauseCondition
import de.hybris.platform.odata2services.filter.BinaryOperatorToSqlOperatorConverter
import de.hybris.platform.odata2services.filter.IntegrationKeyFilteringNotSupported
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator
import org.junit.Test
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class SimplePropertyVisitingStrategyUnitTest extends Specification
{
    def simplePropertyVisitingStrategy = new SimplePropertyVisitingStrategy();

    @Test
    @Unroll
    def "isApplicable for leftResult #left is #expected"()
    {
        when:
        def isApplicable = simplePropertyVisitingStrategy.isApplicable(null, null, left, null)

        then:
        isApplicable == expected

        where:
        left            | expected
        new Object()    | false
        "String"        | true
    }

    @Test
    @Unroll
    def "filter with '#left' left operand, '#right' right operand, and '#operator' operator produces where clause: #expected"()
    {
        given:
        simplePropertyVisitingStrategy.setOperatorConverter(Mock(BinaryOperatorToSqlOperatorConverter) {
            convert(BinaryOperator.EQ) >> "="
            convert(null) >> null
        })

        when:
        def whereClauseConditions = simplePropertyVisitingStrategy.visit(null, operator, left, right)

        then:
        expected == whereClauseConditions

        where:
        left        | operator          | right   | expected
        null        | null              | null    | new WhereClauseCondition("{null} null 'null'").toWhereClauseConditions()
        "left"      | BinaryOperator.EQ | "right" | new WhereClauseCondition("{left} = 'right'").toWhereClauseConditions()
    }

    @Test
    def "integrationKey is not supported"()
    {
        when:
        simplePropertyVisitingStrategy.visit(null, null, "integrationKey", null)

        then:
        thrown IntegrationKeyFilteringNotSupported
    }
}
