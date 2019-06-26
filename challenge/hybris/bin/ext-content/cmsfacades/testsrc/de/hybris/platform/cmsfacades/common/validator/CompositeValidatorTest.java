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
package de.hybris.platform.cmsfacades.common.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.cmsfacades.types.service.ComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.impl.DefaultComponentTypeStructure;
import de.hybris.platform.cmsfacades.types.service.validator.DependsOnAttributePostCreationValidator;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


@UnitTest
public class CompositeValidatorTest
{

	private CompositeValidator compositeValidator;

	@Before
	public void setup()
	{
		compositeValidator = new CompositeValidator();
	}

	@Test
	public void shouldSupportClass()
	{
		final ComponentTypeStructure data = new DefaultComponentTypeStructure();
		final Errors errors = new BeanPropertyBindingResult(data, data.getClass().getSimpleName());
		compositeValidator.setValidators(Arrays.asList(new DependsOnAttributePostCreationValidator()));
		compositeValidator.validate(data, errors);
		assertTrue(compositeValidator.supports(data.getClass()));
	}


	@Test
	public void shouldNotSupportSyncJobDataClass()
	{
		final SyncJobData data = new SyncJobData();
		//validator does not support SyncJobData
		final Validator baseComponentValidator = new DependsOnAttributePostCreationValidator();
		compositeValidator.setValidators(Arrays.asList(baseComponentValidator));
		assertFalse(compositeValidator.supports(data.getClass()));
	}
}
