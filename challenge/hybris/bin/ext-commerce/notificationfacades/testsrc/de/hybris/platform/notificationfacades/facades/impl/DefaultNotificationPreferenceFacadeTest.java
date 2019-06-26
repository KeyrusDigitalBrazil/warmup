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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationfacades.data.NotificationPreferenceData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import junit.framework.Assert;


/**
 * @deprecated since 6.7. use {@link DefaultNotificationPreferenceFacadeIntegrationTest}
 */

@UnitTest
@Deprecated
public class DefaultNotificationPreferenceFacadeTest
{
	private static final String TEST_CUSTOMER_UID = "testcustomer@gmail.com";
	private DefaultNotificationPreferenceFacade defaultNotificationPreferenceFacade;
	@Mock
	private ModelService modelService;
	@Mock
	private UserService userService;
	@Mock
	private Converter<CustomerModel, NotificationPreferenceData> notificationPreferenceConverter;

	CustomerModel customerModel;

	NotificationPreferenceData notificationPreferenceData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultNotificationPreferenceFacade = new DefaultNotificationPreferenceFacade();
		defaultNotificationPreferenceFacade.setModelService(modelService);
		defaultNotificationPreferenceFacade.setNotificationPreferenceConverter(notificationPreferenceConverter);
		defaultNotificationPreferenceFacade.setUserService(userService);
	}

	@Test
	public void testUpdateNotificationPreference()
	{
		customerModel = new CustomerModel();
		notificationPreferenceData = new NotificationPreferenceData();
		customerModel.setUid(TEST_CUSTOMER_UID);
		notificationPreferenceData.setEmailEnabled(true);
		notificationPreferenceData.setSmsEnabled(false);

		given(userService.getCurrentUser()).willReturn(customerModel);

		defaultNotificationPreferenceFacade.updateNotificationPreference(notificationPreferenceData);
		verify(modelService, times(1)).save(customerModel);
	}

	@Test
	public void testGetNotificationPreference()
	{
		customerModel = new CustomerModel();
		notificationPreferenceData = new NotificationPreferenceData();
		customerModel.setUid(TEST_CUSTOMER_UID);
		notificationPreferenceData.setEmailAddress(TEST_CUSTOMER_UID);

		given(notificationPreferenceConverter.convert(customerModel)).willReturn(notificationPreferenceData);
		given(userService.getCurrentUser()).willReturn(customerModel);
		defaultNotificationPreferenceFacade.getNotificationPreference();

		Assert.assertEquals(TEST_CUSTOMER_UID, notificationPreferenceData.getEmailAddress());
	}

}
