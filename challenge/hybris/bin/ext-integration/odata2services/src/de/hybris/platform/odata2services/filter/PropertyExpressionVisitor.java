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

import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;

/**
 * Acts as a delegate to visiting the {@link PropertyExpression}. The intended usage
 * of this interface is to call the visit method from the
 * {@link org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor#visitProperty(PropertyExpression, String, EdmTyped)}
 */
public interface PropertyExpressionVisitor
{
	/**
	 * Visit the {@link PropertyExpression}
	 *
	 * @param expression Property expression
	 * @param name Property name
	 * @param type Property type
	 * @return The result from this visit method
	 */
	Object visit(PropertyExpression expression, String name, EdmTyped type);
}
