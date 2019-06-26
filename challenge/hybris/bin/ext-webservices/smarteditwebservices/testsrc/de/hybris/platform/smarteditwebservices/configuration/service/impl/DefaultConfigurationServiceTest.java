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
package de.hybris.platform.smarteditwebservices.configuration.service.impl;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.smarteditwebservices.configuration.SmarteditConfigurationModelFactory;
import de.hybris.platform.smarteditwebservices.configuration.dao.SmarteditConfigurationDao;
import de.hybris.platform.smarteditwebservices.model.SmarteditConfigurationModel;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationServiceTest
{

	private static final String VALUE_THREE = "VALUE_THREE";
	private static final String KEY_THREE = "KEY_THREE";
	private static final String VALUE_TWO = "VALUE_TWO";
	private static final String KEY_TWO = "KEY_TWO";
	private static final String VALUE_ONE = "VALUE_ONE";
	private static final String KEY_ONE = "KEY_ONE";

	private static final SmarteditConfigurationModel CONFIGURATION_MODEL_1 = SmarteditConfigurationModelFactory.modelBuilder("1",
			KEY_ONE, VALUE_ONE);
	private static final SmarteditConfigurationModel CONFIGURATION_MODEL_2 = SmarteditConfigurationModelFactory.modelBuilder("2",
			KEY_TWO, VALUE_TWO);
	private static final SmarteditConfigurationModel CONFIGURATION_MODEL_3 = SmarteditConfigurationModelFactory.modelBuilder("3",
			KEY_THREE, VALUE_THREE);
	private static final SmarteditConfigurationModel CONFIGURATION_MODEL_NOT_IN_DB = SmarteditConfigurationModelFactory
			.modelBuilder("4", "KEY_FOUR", "VALUE_FOUR");
	@Mock
	private SmarteditConfigurationDao smarteditConfigurationDao;

	@Mock
	private ModelService modelService;

	@InjectMocks
	private DefaultSmarteditConfigurationService configurationService;

	@Before
	public void setup()
	{
		when(smarteditConfigurationDao.loadAll()).thenReturn(Arrays.asList(CONFIGURATION_MODEL_1, CONFIGURATION_MODEL_2,
				CONFIGURATION_MODEL_3));
		when(smarteditConfigurationDao.findByKey(CONFIGURATION_MODEL_1.getKey())).thenReturn(CONFIGURATION_MODEL_1);
		when(smarteditConfigurationDao.findByKey(CONFIGURATION_MODEL_2.getKey())).thenReturn(CONFIGURATION_MODEL_2);
		when(smarteditConfigurationDao.findByKey(CONFIGURATION_MODEL_3.getKey())).thenReturn(CONFIGURATION_MODEL_3);
		when(smarteditConfigurationDao.findByKey(KEY_ONE)).thenReturn(CONFIGURATION_MODEL_1);
		when(smarteditConfigurationDao.findByKey(KEY_TWO)).thenReturn(CONFIGURATION_MODEL_2);
		when(smarteditConfigurationDao.findByKey(KEY_THREE)).thenReturn(CONFIGURATION_MODEL_3);
	}

	@Test
	public void testLoadAll() throws InvocationTargetException, IllegalAccessException
	{
		final List<SmarteditConfigurationModel> models = configurationService.findAll();
		assertNotNull(models);
		assertThat(3, is(models.size()));
		assertThat(CONFIGURATION_MODEL_1, is(models.get(0)));
	}

	@Test
	public void testDelete() throws InvocationTargetException, IllegalAccessException
	{
		configurationService.delete(CONFIGURATION_MODEL_1.getKey());
	}

	@Test(expected = UnknownIdentifierException.class)
	public void testDeleteNotFound() throws InvocationTargetException, IllegalAccessException
	{
		configurationService.delete("id-not-found");
	}

	@Test
	public void testCreate() throws InvocationTargetException, IllegalAccessException
	{
		final SmarteditConfigurationModel configurationModel = configurationService.create(
				CONFIGURATION_MODEL_NOT_IN_DB);
		assertThat(CONFIGURATION_MODEL_NOT_IN_DB.getKey(), is(configurationModel.getKey()));
		assertThat(CONFIGURATION_MODEL_NOT_IN_DB.getValue(), is(configurationModel.getValue()));
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void testCreateDuplicate() throws InvocationTargetException, IllegalAccessException
	{
		configurationService.create(CONFIGURATION_MODEL_1);
	}

	@Test
	public void testUpdate() throws InvocationTargetException, IllegalAccessException
	{
		final String valuedModified = "value-modified";
		CONFIGURATION_MODEL_1.setValue(valuedModified);
		final SmarteditConfigurationModel updatedModel = configurationService.update(
				CONFIGURATION_MODEL_1.getKey(), CONFIGURATION_MODEL_1);

		assertNotNull(updatedModel);
		assertThat(valuedModified, is(updatedModel.getValue()));
		assertThat(CONFIGURATION_MODEL_1.getKey(), is(updatedModel.getKey()));
	}


	@Test(expected = UnknownIdentifierException.class)
	public void testUpdateNotFound() throws InvocationTargetException, IllegalAccessException
	{
		configurationService.update("id-not-present", CONFIGURATION_MODEL_NOT_IN_DB);
	}
}
