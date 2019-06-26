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
 package de.hybris.platform.notificationfacades.facades.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceDataList;
import de.hybris.platform.notificationfacades.facades.NotificationPreferenceFacade;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.notificationservices.service.NotificationService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test cases for {@link DefaultNotificationPreferenceFacade}
 */
@IntegrationTest
public class DefaultNotificationPreferenceFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	@Resource(name = "notificationPreferenceFacade")
	private NotificationPreferenceFacade notificationFacade;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "notificationService")
	private NotificationService notificationService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "notificationPreferenceEntryConverter")
	private Converter<NotificationChannel, NotificationPreferenceData> notificationEntryConverter;

	@Resource(name = "notificationPreferencesConverter")
	private Converter<List<NotificationPreferenceData>, NotificationPreferenceDataList> notificationConverter;

	private static final String CUSTOMER_ID = "test@hybris.com";
	private CustomerModel customer;
	private List<NotificationPreferenceData> notificationPreferenceDataList;

	@Before
	public void prepare()
	{
		customer = modelService.create(CustomerModel.class);
		customer.setUid(CUSTOMER_ID);
		modelService.save(customer);
		userService.setCurrentUser(customer);

		notificationPreferenceDataList = new ArrayList();

	}

	@Test
	public void testUpdateNotificationPreference()
	{
		final NotificationPreferenceData data = notificationEntryConverter.convert(NotificationChannel.EMAIL);
		data.setEnabled(true);
		notificationPreferenceDataList.add(data);
		notificationFacade.updateNotificationPreference(notificationPreferenceDataList);

		Assert.assertTrue(customer.getNotificationChannels().contains(NotificationChannel.EMAIL));
	}

	@Test
	public void testGetNotificationPreferences()
	{
		final List<NotificationPreferenceData> preferenceList = notificationFacade.getNotificationPreferences();
		Assert.assertEquals(NotificationChannel.values().length, preferenceList.size());

	}

	@Test
	public void testGetNotificationPreferencesByEnabledChannels()
	{
		final Set<NotificationChannel> enabledChannels = customer.getNotificationChannels();
		final List<NotificationPreferenceData> preferenceList = notificationFacade.getNotificationPreferences(enabledChannels);
		preferenceList.forEach(p -> {
			if (enabledChannels.contains(p.getChannel()))
			{
				Assert.assertTrue(p.isEnabled());
			}
			else
			{
				Assert.assertFalse(p.isEnabled());
			}
		});

	}

	@Test
	public void testGetValidNotificationPreferences()
	{
		final List<NotificationPreferenceData> preferenceList = notificationFacade.getValidNotificationPreferences();
		preferenceList.forEach(p -> {
			if (NotificationChannel.SMS.equals(p.getChannel()))
			{
				Assert.assertTrue(StringUtils.isNotBlank(p.getValue()));
			}
			else if (NotificationChannel.EMAIL.equals(p.getChannel()))
			{
				Assert.assertTrue(StringUtils.isNotBlank(p.getValue()));
			}
		});
	}

	@Test
	public void testGetValidNotificationPreferencesByEnabledChannels()
	{
		final Set<NotificationChannel> enabledChannels = customer.getNotificationChannels();
		final List<NotificationPreferenceData> preferenceList = notificationFacade.getValidNotificationPreferences(enabledChannels);
		preferenceList.forEach(p -> {
			if (enabledChannels.contains(p.getChannel()))
			{
				Assert.assertTrue(p.isEnabled());
			}
			else
			{
				Assert.assertFalse(p.isEnabled());
			}
		});
	}

}
