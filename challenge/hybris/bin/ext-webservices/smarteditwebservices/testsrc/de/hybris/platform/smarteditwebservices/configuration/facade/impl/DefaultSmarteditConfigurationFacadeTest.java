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
package de.hybris.platform.smarteditwebservices.configuration.facade.impl;

import static de.hybris.platform.smarteditwebservices.configuration.facade.DefaultConfigurationKey.DEFAULT_LANGUAGE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.validator.impl.DefaultFacadeValidationService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.smarteditwebservices.configuration.SmarteditConfigurationDuplicateKeyException;
import de.hybris.platform.smarteditwebservices.configuration.SmarteditConfigurationModelFactory;
import de.hybris.platform.smarteditwebservices.configuration.SmarteditConfigurationNotFoundException;
import de.hybris.platform.smarteditwebservices.configuration.service.SmarteditConfigurationService;
import de.hybris.platform.smarteditwebservices.configuration.validator.BaseConfigurationValidator;
import de.hybris.platform.smarteditwebservices.configuration.validator.UpdateConfigurationValidator;
import de.hybris.platform.smarteditwebservices.data.ConfigurationData;
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
public class DefaultSmarteditConfigurationFacadeTest
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
	private SmarteditConfigurationService configurationService;

	@Mock
	private AbstractPopulatingConverter<SmarteditConfigurationModel, ConfigurationData> configurationModelToDataConverter;

	@Mock
	private AbstractPopulatingConverter<ConfigurationData, SmarteditConfigurationModel> configurationDataToModelConverter;


	@Mock
	private DefaultFacadeValidationService facadeValidationService;

	@Mock
	private BaseConfigurationValidator baseConfigurationValidator;

	@Mock
	private UpdateConfigurationValidator updateConfigurationValidator;

	@InjectMocks
	private final DefaultSmarteditConfigurationFacade configurationFacade = new DefaultSmarteditConfigurationFacade();

	@Before
	public void setup() throws InvocationTargetException, IllegalAccessException
	{

		when(configurationService.findAll())
				.thenReturn(Arrays.asList(CONFIGURATION_MODEL_1, CONFIGURATION_MODEL_2, CONFIGURATION_MODEL_3));
		when(configurationService.update(CONFIGURATION_MODEL_1.getKey(), CONFIGURATION_MODEL_1)).thenReturn(CONFIGURATION_MODEL_1);
		when(configurationService.update(CONFIGURATION_MODEL_2.getKey(), CONFIGURATION_MODEL_2)).thenReturn(CONFIGURATION_MODEL_2);
		when(configurationService.update(CONFIGURATION_MODEL_3.getKey(), CONFIGURATION_MODEL_3)).thenReturn(CONFIGURATION_MODEL_3);
		when(configurationService.create(CONFIGURATION_MODEL_1)).thenReturn(CONFIGURATION_MODEL_1);
		when(configurationService.create(CONFIGURATION_MODEL_2)).thenReturn(CONFIGURATION_MODEL_2);
		when(configurationService.create(CONFIGURATION_MODEL_3)).thenReturn(CONFIGURATION_MODEL_3);

		configurationFacade.setFacadeValidationService(facadeValidationService);
		configurationFacade.setCreateConfigurationValidator(baseConfigurationValidator);
		configurationFacade.setUpdateConfigurationValidator(updateConfigurationValidator);

	}

	@Test
	public void testLoadAll()
	{
		final ConfigurationData data1 = buildConfiguraitonData(CONFIGURATION_MODEL_1);
		final ConfigurationData data2 = buildConfiguraitonData(CONFIGURATION_MODEL_2);
		final ConfigurationData data3 = buildConfiguraitonData(CONFIGURATION_MODEL_3);

		when(configurationModelToDataConverter.convert(CONFIGURATION_MODEL_1)).thenReturn(data1);
		when(configurationModelToDataConverter.convert(CONFIGURATION_MODEL_2)).thenReturn(data2);
		when(configurationModelToDataConverter.convert(CONFIGURATION_MODEL_3)).thenReturn(data3);

		final List<ConfigurationData> configurationDatas = configurationFacade.findAll();
		assertNotNull(configurationDatas);

		assertThat(CONFIGURATION_MODEL_1.getKey(), is(configurationDatas.get(0).getKey()));
		assertThat(CONFIGURATION_MODEL_2.getKey(), is(configurationDatas.get(1).getKey()));
		assertThat(CONFIGURATION_MODEL_3.getKey(), is(configurationDatas.get(2).getKey()));
	}

	protected ConfigurationData buildConfiguraitonData(final SmarteditConfigurationModel configurationModel)
	{
		final ConfigurationData data = new ConfigurationData();
		data.setKey(configurationModel.getKey());
		data.setValue(configurationModel.getValue());
		return data;
	}

	@Test
	public void testCreate() throws InvocationTargetException, IllegalAccessException
	{
		when(configurationService.create(any())).thenReturn(CONFIGURATION_MODEL_NOT_IN_DB);
		final ConfigurationData configurationData = new ConfigurationData();
		configurationData.setKey(CONFIGURATION_MODEL_NOT_IN_DB.getKey());
		configurationData.setValue(CONFIGURATION_MODEL_NOT_IN_DB.getValue());

		when(configurationModelToDataConverter.convert(CONFIGURATION_MODEL_NOT_IN_DB)).thenReturn(configurationData);
		when(configurationDataToModelConverter.convert(configurationData)).thenReturn(CONFIGURATION_MODEL_NOT_IN_DB);

		final ConfigurationData configurationDataCreated = configurationFacade.create(configurationData);

		assertThat(configurationData.getKey(), is(configurationDataCreated.getKey()));
		assertThat(configurationData.getValue(), is(configurationDataCreated.getValue()));
	}

	@Test(expected = SmarteditConfigurationDuplicateKeyException.class)
	public void testCreateDuplicate() throws InvocationTargetException, IllegalAccessException
	{
		when(configurationService.create(any())).thenThrow(new AmbiguousIdentifierException("duplicate key"));
		final ConfigurationData configurationData = new ConfigurationData();
		configurationData.setKey(CONFIGURATION_MODEL_NOT_IN_DB.getKey());
		configurationData.setValue(CONFIGURATION_MODEL_NOT_IN_DB.getValue());
		configurationFacade.create(configurationData);
	}

	@Test
	public void testUpdate() throws InvocationTargetException, IllegalAccessException
	{
		final ConfigurationData configurationData = buildConfiguraitonData(CONFIGURATION_MODEL_1);

		when(configurationService.update(any(), any())).thenReturn(CONFIGURATION_MODEL_1);
		when(configurationModelToDataConverter.convert(CONFIGURATION_MODEL_1)).thenReturn(configurationData);

		final ConfigurationData configurationDataCreated = configurationFacade.update(configurationData.getKey(),
				configurationData);

		assertThat(configurationData.getKey(), is(configurationDataCreated.getKey()));
		assertThat(configurationData.getValue(), is(configurationDataCreated.getValue()));
	}

	@Test(expected = SmarteditConfigurationNotFoundException.class)
	public void testUpdateNotFound() throws InvocationTargetException, IllegalAccessException
	{
		when(configurationService.update(any(), any())).thenThrow(new UnknownIdentifierException("not found"));
		final ConfigurationData configurationData = new ConfigurationData();
		configurationData.setKey(CONFIGURATION_MODEL_1.getKey());
		configurationData.setValue(CONFIGURATION_MODEL_1.getValue());
		configurationFacade.update(configurationData.getKey(), configurationData);
	}

	@Test
	public void testDelete()
	{
		configurationFacade.delete(CONFIGURATION_MODEL_1.getKey());
	}

	@Test(expected = SmarteditConfigurationNotFoundException.class)
	public void testDeleteNotFound() throws InvocationTargetException, IllegalAccessException
	{
		doThrow(new UnknownIdentifierException("not found")).when(configurationService).delete(any());
		configurationFacade.delete(CONFIGURATION_MODEL_1.getKey());
	}

	@Test
	public void findByDefaultConfigurationKey_will_return_configuration_data() throws Exception
	{
		final ConfigurationData configurationData = buildConfiguraitonData(CONFIGURATION_MODEL_1);

		when(configurationService.findByKey(DEFAULT_LANGUAGE.getKey())).thenReturn(CONFIGURATION_MODEL_1);
		when(configurationModelToDataConverter.convert(CONFIGURATION_MODEL_1)).thenReturn(configurationData);

		final ConfigurationData config = configurationFacade.findByDefaultConfigurationKey(DEFAULT_LANGUAGE);

		assertThat(config, is(configurationData));

	}
}
