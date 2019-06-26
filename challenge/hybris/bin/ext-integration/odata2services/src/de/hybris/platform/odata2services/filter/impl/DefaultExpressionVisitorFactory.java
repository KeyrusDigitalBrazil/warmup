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

import de.hybris.platform.odata2services.filter.BinaryExpressionVisitingStrategy;
import de.hybris.platform.odata2services.filter.BinaryExpressionVisitor;
import de.hybris.platform.odata2services.filter.ExpressionVisitorFactory;
import de.hybris.platform.odata2services.filter.ExpressionVisitorParameters;
import de.hybris.platform.odata2services.filter.FilterExpressionVisitor;
import de.hybris.platform.odata2services.filter.LiteralExpressionVisitor;
import de.hybris.platform.odata2services.filter.MemberExpressionVisitor;
import de.hybris.platform.odata2services.filter.PropertyExpressionVisitor;
import de.hybris.platform.odata2services.odata.integrationkey.IntegrationKeyToODataEntryGenerator;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupStrategy;
import de.hybris.platform.odata2services.odata.schema.entity.EntitySetNameGenerator;

import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;

import com.google.common.collect.Lists;

/**
 * The default implementation of the {@link ExpressionVisitorFactory}
 */
public class DefaultExpressionVisitorFactory implements ExpressionVisitorFactory
{
	private IntegrationKeyToODataEntryGenerator integrationKeyConverter;
	private ItemLookupRequestFactory itemLookupRequestFactory;
	private ItemLookupStrategy itemLookupStrategy;
	private Converter<BinaryOperator, String> operatorConverter;
	private EntitySetNameGenerator entitySetNameGenerator;

	@Override
	public ExpressionVisitor create(final ExpressionVisitorParameters parameters)
	{
		final DefaultExpressionVisitor visitor = new DefaultExpressionVisitor();
		visitor.setFilterExpressionVisitor(createFilterExpressionVisitor());
		visitor.setBinaryExpressionVisitor(createBinaryExpressionVisitor(parameters));
		visitor.setMemberExpressionVisitor(createMemberExpressionVisitor(parameters));
		visitor.setPropertyExpressionVisitor(createPropertyExpressionVisitor());
		visitor.setLiteralExpressionVisitor(createLiteralExpressionVisitor());
		return visitor;
	}

	protected FilterExpressionVisitor createFilterExpressionVisitor()
	{
		return new DefaultFilterExpressionVisitor();
	}

	protected BinaryExpressionVisitor createBinaryExpressionVisitor(final ExpressionVisitorParameters parameters)
	{
		final DefaultBinaryExpressionVisitor visitor = new DefaultBinaryExpressionVisitor();
		visitor.setStrategies(Lists.newArrayList(
				createSimplePropertyVisitingStrategy(),
				createNavigationPropertyVisitingStrategy(parameters),
				createNavigationPropertyWithIntegrationKeyVisitingStrategy(parameters),
				createCombineWhereClauseConditionsVisitingStrategy()
		));
		return visitor;
	}

	protected MemberExpressionVisitor createMemberExpressionVisitor(final ExpressionVisitorParameters parameters)
	{
		 final DefaultMemberExpressionVisitor visitor = new DefaultMemberExpressionVisitor();
		 visitor.setEntitySetNameGenerator(getEntitySetNameGenerator());
		 visitor.setUriInfo(parameters.getUriInfo());
		 return visitor;
	}

	protected PropertyExpressionVisitor createPropertyExpressionVisitor()
	{
		return new DefaultPropertyExpressionVisitor();
	}

	protected LiteralExpressionVisitor createLiteralExpressionVisitor()
	{
		return new DefaultLiteralExpressionVisitor();
	}

	protected BinaryExpressionVisitingStrategy createSimplePropertyVisitingStrategy()
	{
		final SimplePropertyVisitingStrategy strategy = new SimplePropertyVisitingStrategy();
		strategy.setOperatorConverter(getOperatorConverter());
		return strategy;
	}

	protected BinaryExpressionVisitingStrategy createNavigationPropertyVisitingStrategy(final ExpressionVisitorParameters parameters)
	{
		return setCommonFields(new NavigationPropertyVisitingStrategy(), parameters);
	}

	protected BinaryExpressionVisitingStrategy createNavigationPropertyWithIntegrationKeyVisitingStrategy(final ExpressionVisitorParameters parameters)
	{
		final NavigationPropertyWithIntegrationKeyVisitingStrategy strategy = new NavigationPropertyWithIntegrationKeyVisitingStrategy();
		strategy.setIntegrationKeyConverter(getIntegrationKeyConverter());
		return setCommonFields(strategy, parameters);
	}

	private AbstractNavigationPropertyVisitingStrategy setCommonFields(final AbstractNavigationPropertyVisitingStrategy strategy, final ExpressionVisitorParameters parameters)
	{
		strategy.setContext(parameters.getContext());
		strategy.setItemLookupRequestFactory(getItemLookupRequestFactory());
		strategy.setItemLookupStrategy(getItemLookupStrategy());
		strategy.setOperatorConverter(getOperatorConverter());
		return strategy;
	}

	protected BinaryExpressionVisitingStrategy createCombineWhereClauseConditionsVisitingStrategy()
	{
		final CombineWhereClauseConditionVisitingStrategy strategy = new CombineWhereClauseConditionVisitingStrategy();
		strategy.setOperatorConverter(getOperatorConverter());
		return strategy;
	}

	protected IntegrationKeyToODataEntryGenerator getIntegrationKeyConverter()
	{
		return integrationKeyConverter;
	}

	@Required
	public void setIntegrationKeyConverter(final IntegrationKeyToODataEntryGenerator integrationKeyConverter)
	{
		this.integrationKeyConverter = integrationKeyConverter;
	}

	protected ItemLookupRequestFactory getItemLookupRequestFactory()
	{
		return itemLookupRequestFactory;
	}

	@Required
	public void setItemLookupRequestFactory(final ItemLookupRequestFactory itemLookupRequestFactory)
	{
		this.itemLookupRequestFactory = itemLookupRequestFactory;
	}

	protected ItemLookupStrategy getItemLookupStrategy()
	{
		return itemLookupStrategy;
	}

	@Required
	public void setItemLookupStrategy(final ItemLookupStrategy itemLookupStrategy)
	{
		this.itemLookupStrategy = itemLookupStrategy;
	}

	protected Converter<BinaryOperator, String> getOperatorConverter()
	{
		return operatorConverter;
	}

	@Required
	public void setOperatorConverter(final Converter<BinaryOperator, String> operatorConverter)
	{
		this.operatorConverter = operatorConverter;
	}

	protected EntitySetNameGenerator getEntitySetNameGenerator()
	{
		return entitySetNameGenerator;
	}

	@Required
	public void setEntitySetNameGenerator(final EntitySetNameGenerator entitySetNameGenerator)
	{
		this.entitySetNameGenerator = entitySetNameGenerator;
	}
}
