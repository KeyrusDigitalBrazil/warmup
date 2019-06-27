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

import org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor;

/**
 * Defines the interface to creating an {@link ExpressionVisitor}
 */
public interface ExpressionVisitorFactory
{
	/**
	 * Creates an {@link ExpressionVisitor}
	 * @param parameters parameters used to create the ExpressionVisitor
	 * @return ExpressionVisitor instance
	 */
	ExpressionVisitor create(ExpressionVisitorParameters parameters);
}
