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
package de.hybris.platform.smarteditwebservices.configuration.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.smarteditwebservices.configuration.SmarteditConfigurationModelFactory;
import de.hybris.platform.smarteditwebservices.configuration.dao.SmarteditConfigurationDao;
import de.hybris.platform.smarteditwebservices.model.SmarteditConfigurationModel;

import java.util.Collection;

import javax.annotation.Resource;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultSmarteditConfigurationDaoIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String VALUE_THREE = "VALUE_THREE";
	private static final String KEY_THREE = "KEY_THREE";
	private static final String VALUE_TWO = "VALUE_TWO";
	private static final String KEY_TWO = "KEY_TWO";
	private static final String VALUE_ONE = "VALUE_ONE";
	private static final String KEY_ONE = "KEY_ONE";

	@Resource
	private SmarteditConfigurationDao smarteditConfigurationDao;


	@Resource
	private ModelService modelService;

	private final SmarteditConfigurationModel configuration1 = SmarteditConfigurationModelFactory.modelBuilder(KEY_ONE, VALUE_ONE);
	private final SmarteditConfigurationModel configuration2 = SmarteditConfigurationModelFactory.modelBuilder(KEY_TWO, VALUE_TWO);
	private final SmarteditConfigurationModel configuration3 = SmarteditConfigurationModelFactory.modelBuilder(KEY_THREE,
			VALUE_THREE);
	private final SmarteditConfigurationModel ConfigurationDataList[] =
	{ configuration1, configuration2 };

	@Before
	public void setup() throws Exception
	{
		modelService.save(configuration1);
	}

	@Test
	public void save_Will_Save_A_Configuration() throws Exception
	{
		final Collection<SmarteditConfigurationModel> defaultConfigurations = smarteditConfigurationDao.loadAll();
		Assert.assertThat(defaultConfigurations, Matchers.contains(configuration1));
	}

	@Test
	public void loadAll_Will_Load_All_Configurations() throws Exception
	{
		modelService.save(configuration2);
		final Collection<SmarteditConfigurationModel> defaultConfigurations = smarteditConfigurationDao.loadAll();
		Assert.assertThat(defaultConfigurations, Matchers.contains(ConfigurationDataList));
	}

	@Test
	public void loadByKey_Will_Load_A_Particular_Configuration() throws Exception
	{
		modelService.save(configuration3);
		final SmarteditConfigurationModel configuration = smarteditConfigurationDao
				.findByKey(configuration3.getKey());
		Assert.assertThat(configuration, Matchers.equalTo(configuration3));
	}

	@Test
	public void loadById_Will_Load_A_Particular_Configuration() throws Exception
	{
		modelService.save(configuration3);
		final SmarteditConfigurationModel configuration = smarteditConfigurationDao
				.findByKey(configuration3.getKey());
		Assert.assertThat(configuration, Matchers.equalTo(configuration3));
	}

	@Test
	public void remove_Will_Delete_An_Existing_Configuration() throws Exception
	{
		modelService.save(configuration2);
		modelService.remove(configuration1);
		final Collection<SmarteditConfigurationModel> defaultConfigurations = smarteditConfigurationDao.loadAll();
		Assert.assertThat(defaultConfigurations, Matchers.contains(configuration2));
	}

	public static String toStringHelper(final Object target)
	{
		return ToStringBuilder.reflectionToString(target, ToStringStyle.MULTI_LINE_STYLE);
	}
}
