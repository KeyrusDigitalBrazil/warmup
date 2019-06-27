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

import java.util.Locale;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.exception.ODataRuntimeApplicationException;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;

/**
 * Throws this exception when the {@link BinaryOperator} is not supported
 */
public class OperatorNotSupportedException extends ODataRuntimeApplicationException
{
	private static final String MESSAGE = "Operator [%s] is not supported";

	public OperatorNotSupportedException(final BinaryOperator operator)
	{
		super(String.format(MESSAGE, operator.toUriLiteral()), Locale.ENGLISH, HttpStatusCodes.BAD_REQUEST, "operator_not_supported");
	}
}
