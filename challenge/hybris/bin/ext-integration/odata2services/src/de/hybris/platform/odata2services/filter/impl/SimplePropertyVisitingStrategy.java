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
package de.hybris.platform.odata2services.filter.impl;

import de.hybris.platform.integrationservices.search.WhereClauseCondition;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;
import de.hybris.platform.odata2services.filter.BinaryExpressionVisitingStrategy;
import de.hybris.platform.odata2services.filter.IntegrationKeyFilteringNotSupported;

import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.springframework.core.convert.converter.Converter;

/**
 * This strategy creates a {@link WhereClauseCondition} from the property name and it's value.
 * For example if filtering by code eq 'MyProduct', this strategy creates a where clause condition
 * with code = 'MyProduct'.
 */
public class SimplePropertyVisitingStrategy implements BinaryExpressionVisitingStrategy
{
	private Converter<BinaryOperator, String> operatorConverter;

	@Override
	public boolean isApplicable(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		return leftResult instanceof String;
	}

	@Override
	public WhereClauseConditions visit(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		throwExceptionIfIntegrationKey((String) leftResult);
		return new WhereClauseCondition(String.format("{%s} %s '%s'", leftResult, getOperatorConverter().convert(operator), rightResult)).toWhereClauseConditions();
	}

	private void throwExceptionIfIntegrationKey(final String propertyName)
	{
		if ("integrationKey".equals(propertyName))
		{
			throw new IntegrationKeyFilteringNotSupported();
		}
	}

	protected Converter<BinaryOperator, String> getOperatorConverter()
	{
		return operatorConverter;
	}

	public void setOperatorConverter(final Converter<BinaryOperator, String> operatorConverter)
	{
		this.operatorConverter = operatorConverter;
	}
}
