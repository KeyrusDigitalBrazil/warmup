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

/**
 * Throw this exception if filtering by more than one level of nesting (e.g. catalogVersion/catalog/id eq 'Default')
 */
public class NestedFilterNotSupportedException extends ODataRuntimeApplicationException
{
	private static final String MESSAGE = "Nested filter [%s] of more than one level is not supported";

	public NestedFilterNotSupportedException(final String filter)
	{
		super(String.format(MESSAGE, filter), Locale.ENGLISH, HttpStatusCodes.BAD_REQUEST, "filter_not_supported");
	}
}
