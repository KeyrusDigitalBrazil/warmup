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

import static de.hybris.platform.odata2services.filter.impl.WhereClauseConditionUtil.EMPTY_CONDITIONS;

import de.hybris.platform.integrationservices.search.WhereClauseConditions;
import de.hybris.platform.odata2services.filter.BinaryExpressionVisitingStrategy;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupStrategy;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.springframework.core.convert.converter.Converter;

/**
 * Provides common functionality to NavigationPropertyVisitingStrategies
 */
public abstract class AbstractNavigationPropertyVisitingStrategy implements BinaryExpressionVisitingStrategy
{
	private ItemLookupRequestFactory itemLookupRequestFactory;
	private ItemLookupStrategy itemLookupStrategy;
	private ODataContext context;
	private Converter<BinaryOperator, String> operatorConverter;

	@Override
	public WhereClauseConditions visit(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		final String sqlOperator = getOperatorConverter().convert(operator);
		if ("=".equals(sqlOperator))
		{
			return createWhereClauseConditionForEqual(expression, operator, leftResult, rightResult);
		}
		return EMPTY_CONDITIONS;
	}

	protected abstract WhereClauseConditions createWhereClauseConditionForEqual(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult);

	protected String getLeftOperandPropertyName(final BinaryExpression expression)
	{
		return ((MemberExpression) expression.getLeftOperand()).getProperty().getUriLiteral();
	}
	
	protected String getLeftOperandNavPropertyName(final BinaryExpression expression)
	{
		return ((MemberExpression) expression.getLeftOperand()).getPath().getUriLiteral();
	}

	protected ItemLookupRequestFactory getItemLookupRequestFactory()
	{
		return itemLookupRequestFactory;
	}

	public void setItemLookupRequestFactory(final ItemLookupRequestFactory itemLookupRequestFactory)
	{
		this.itemLookupRequestFactory = itemLookupRequestFactory;
	}

	protected ItemLookupStrategy getItemLookupStrategy()
	{
		return itemLookupStrategy;
	}

	public void setItemLookupStrategy(final ItemLookupStrategy itemLookupStrategy)
	{
		this.itemLookupStrategy = itemLookupStrategy;
	}

	protected ODataContext getContext()
	{
		return context;
	}

	public void setContext(final ODataContext context)
	{
		this.context = context;
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
