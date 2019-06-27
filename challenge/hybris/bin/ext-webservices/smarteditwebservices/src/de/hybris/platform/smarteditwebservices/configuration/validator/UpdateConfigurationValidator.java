/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.smarteditwebservices.configuration.validator;

import de.hybris.platform.smarteditwebservices.constants.SmarteditwebservicesConstants;
import de.hybris.platform.smarteditwebservices.dto.UpdateConfigurationDto;

import java.util.Objects;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates the configuration Data bean
 */
public class UpdateConfigurationValidator implements Validator
{

	private static final String KEY = "key";
	private static final String VALUE = "value";

	@Override
	public boolean supports(final Class<?> clazz)
	{
		return UpdateConfigurationDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(final Object obj, final Errors errors)
	{
		final UpdateConfigurationDto target = (UpdateConfigurationDto) obj;

		// uid is mandatory, as it may be passed along with the uri path wheres the key isn't
		if (Objects.isNull(target.getUid()))
		{
			errors.rejectValue(KEY, SmarteditwebservicesConstants.FIELD_REQUIRED);
		}
		if (Objects.isNull(target.getValue()))
		{
			errors.rejectValue(VALUE, SmarteditwebservicesConstants.FIELD_REQUIRED);
		}
		if (!Objects.isNull(target.getKey()) && !Objects.isNull(target.getUid()) && !target.getUid().equals(target.getKey()))
		{
			errors.rejectValue(KEY, SmarteditwebservicesConstants.INVALID_CONFIGURATION_KEY);
		}
	}

}
