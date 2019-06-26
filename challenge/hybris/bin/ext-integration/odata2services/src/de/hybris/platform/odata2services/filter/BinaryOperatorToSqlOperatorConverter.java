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
package de.hybris.platform.odata2services.filter;

import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.springframework.core.convert.converter.Converter;

/**
 * Converts a {@link BinaryOperator} to a SQL operator.
 * Not all SQL operators are supported currently. An {@link OperatorNotSupportedException}
 * exception will be thrown for the unsupported operators
 */
public class BinaryOperatorToSqlOperatorConverter implements Converter<BinaryOperator, String>
{
	@Override
	public String convert(final BinaryOperator operator)
	{
		switch (operator)
		{
			case EQ:
				return "=";
			case AND:
				return "AND";
			case OR:
				return "OR";
			default:
				throw new OperatorNotSupportedException(operator);
		}
	}
}
