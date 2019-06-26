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

import de.hybris.platform.odata2services.filter.PropertyExpressionVisitor;

import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;

/**
 * Default implementation of the {@link PropertyExpressionVisitor}
 */
public class DefaultPropertyExpressionVisitor implements PropertyExpressionVisitor
{
	@Override
	public Object visit(final PropertyExpression expression, final String name, final EdmTyped type)
	{
		if (isNavigationProperty(type))
		{
			return type;
		}
		return name;
	}

	private boolean isNavigationProperty(final EdmTyped type)
	{
		return type instanceof EdmNavigationProperty;
	}
}
