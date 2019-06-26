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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.smarteditwebservices.data.ConfigurationData;

import static org.junit.Assert.*;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@UnitTest
public class BaseConfigurationValidatorTest
{

	private BaseConfigurationValidator validator = new BaseConfigurationValidator();


	@Test
	public void testValidConfigurationData()
	{
		final ConfigurationData data = new ConfigurationData();
		data.setKey("key");
		data.setValue("value");
		final Errors errors = new BeanPropertyBindingResult(data, data.getClass().getSimpleName());
		validator.validate(data, errors);
		assertEquals(0, errors.getErrorCount());
	}


	@Test
	public void testInvalidConfigurationData()
	{
		final ConfigurationData data = new ConfigurationData();
		final Errors errors = new BeanPropertyBindingResult(data, data.getClass().getSimpleName());
		validator.validate(data, errors);
		assertEquals(2, errors.getErrorCount());
	}

}
