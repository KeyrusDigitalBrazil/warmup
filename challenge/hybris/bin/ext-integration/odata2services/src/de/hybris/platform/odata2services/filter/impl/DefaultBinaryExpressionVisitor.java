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

import de.hybris.platform.odata2services.filter.BinaryExpressionVisitingStrategy;
import de.hybris.platform.odata2services.filter.BinaryExpressionVisitor;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link BinaryExpressionVisitor}
 */
public class DefaultBinaryExpressionVisitor implements BinaryExpressionVisitor
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultBinaryExpressionVisitor.class);

	private List<BinaryExpressionVisitingStrategy> strategies;

	@Override
	public Object visit(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		final Optional<BinaryExpressionVisitingStrategy> strategyOptional = getSingleApplicableVisitingStrategy(expression, operator, leftResult, rightResult);

		if(strategyOptional.isPresent())
		{
			return strategyOptional.get().visit(expression, operator, leftResult, rightResult);
		}
		LOG.debug("Did not find an applicable BinaryExpressionVisitingStrategy, returning EMPTY_CONDITION");
		return EMPTY_CONDITIONS;
	}

	private Optional<BinaryExpressionVisitingStrategy> getSingleApplicableVisitingStrategy(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		final List<BinaryExpressionVisitingStrategy> applicableStrategies = getStrategies().stream()
				.filter(s -> s.isApplicable(expression, operator, leftResult, rightResult)).collect(Collectors.toList());

		if (applicableStrategies.size() > 1)
		{
			throw new InternalProcessingException("Found more than 1 applicable BinaryExpressionVisitingStrategy");
		}
		return applicableStrategies.stream().findFirst();
	}

	protected List<BinaryExpressionVisitingStrategy> getStrategies()
	{
		return strategies;
	}

	public void setStrategies(final List<BinaryExpressionVisitingStrategy> strategies)
	{
		this.strategies = strategies;
	}
}
