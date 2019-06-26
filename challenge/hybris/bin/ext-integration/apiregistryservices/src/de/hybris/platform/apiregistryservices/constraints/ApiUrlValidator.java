/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.apiregistryservices.constraints;

import de.hybris.platform.apiregistryservices.utils.EventExportUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates that the given string value is a valid url.
 * It also checks the protocol of the url matches one of the protocols which are specified in project.properties.
 */
public class ApiUrlValidator implements ConstraintValidator<ApiUrlValid, String>
{

	@Override
	public void initialize(final ApiUrlValid urlValid)
	{
		// empty
	}

	@Override
	public boolean isValid(final String url, final ConstraintValidatorContext constraintValidatorContext)
	{
		return EventExportUtils.isUrlValid(url);
	}
}
