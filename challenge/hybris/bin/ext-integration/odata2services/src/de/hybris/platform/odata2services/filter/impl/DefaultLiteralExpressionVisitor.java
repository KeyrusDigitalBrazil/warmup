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

import de.hybris.platform.odata2services.filter.LiteralExpressionVisitor;

import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.uri.expression.LiteralExpression;

/**
 * Default implementation of the {@link LiteralExpressionVisitor}
 */
public class DefaultLiteralExpressionVisitor implements LiteralExpressionVisitor
{
	@Override
	public Object visit(final LiteralExpression expression, final EdmLiteral literal)
	{
		return literal.getLiteral();
	}
}
