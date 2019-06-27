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

import org.apache.commons.lang3.StringUtils;

/**
 * Represents the supported conjunctive operator
 */
public enum ConjunctiveOperator
{
	AND("AND"),
	OR("OR"),
	UNKNOWN("");

	private final String operator;

	ConjunctiveOperator(final String operator)
	{
		this.operator = operator;
	}

	/**
	 * Converts a String to a ConjunctiveOperator enum
	 *
	 * @param operator Operator to convert
	 * @return The String {@code operator} as a ConjunctiveOperator.
	 * If {@code operator} is not found, {@code UNKNOWN} is returned.
	 */
	public static ConjunctiveOperator fromString(final String operator)
	{
		if (StringUtils.isNotBlank(operator))
		{
			for (final ConjunctiveOperator op : ConjunctiveOperator.values())
			{
				if (op.operator.equals(operator))
				{
					return op;
				}
			}
		}
		return UNKNOWN;
	}

	@Override
	public String toString()
	{
		return operator;
	}
}
