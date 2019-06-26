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
package de.hybris.platform.integrationservices.search;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a where clause condition (e.g. code = 'abc'). It doesn't include the keyword 'WHERE' in the condition.
 * The WhereClauseCondition can also store the conjunctive operator (AND, OR, etc.) to the next WhereClauseCondition.
 */
public class WhereClauseCondition
{
	private final String condition;
	private final ConjunctiveOperator operator;

	/**
	 * Stores the where clause condition without the conjunctive operator (AND, OR, etc) to the next WhereClauseCondition
	 * @param condition Where clause condition (e.g. code = 'abc')
	 */
	public WhereClauseCondition(final String condition)
	{
		this.condition = condition;
		this.operator = ConjunctiveOperator.UNKNOWN;
	}

	/**
	 * Stores the where clause condition  with the conjunctive operator (AND, OR, etc) to the next WhereClauseCondition
	 * @param condition Where clause condition
	 */
	public WhereClauseCondition(final String condition, final ConjunctiveOperator operator)
	{
		this.condition = condition;
		this.operator = operator != null? operator : ConjunctiveOperator.UNKNOWN;
	}

	public String getCondition()
	{
		return condition;
	}

	public ConjunctiveOperator getConjunctiveOperator()
	{
		return operator;
	}

	public String getConjunctiveOperatorString()
	{
		return operator.toString();
	}

	public WhereClauseConditions toWhereClauseConditions()
	{
		return new WhereClauseConditions(this);
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}

		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final WhereClauseCondition that = (WhereClauseCondition) o;

		return new EqualsBuilder()
				.append(condition, that.condition)
				.append(operator, that.operator)
				.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder(17, 37)
				.append(condition)
				.append(operator)
				.toHashCode();
	}
	
	@Override
	public String toString()
	{
		return "WhereClauseCondition{" +
				"condition='" + condition + '\'' +
				", conjunctiveOperator='" + operator + '\'' +
				'}';
	}
}
