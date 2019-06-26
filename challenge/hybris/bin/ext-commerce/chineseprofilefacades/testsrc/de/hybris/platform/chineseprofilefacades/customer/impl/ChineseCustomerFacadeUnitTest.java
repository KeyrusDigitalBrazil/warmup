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
package de.hybris.platform.chineseprofilefacades.customer.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chineseprofileservices.customer.ChineseCustomerAccountService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.notificationservices.enums.NotificationChannel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class ChineseCustomerFacadeUnitTest
{
	private static final String ISO_CODE_EN = "en";

	private static final String ISO_CODE_ZH = "zh";

	@Mock
	private UserService userService;

	@Mock
	private ChineseCustomerAccountService chineseCustomerAccountService;

	private CustomerModel customer;

	private DefaultChineseCustomerFacade customerFacade;

	private Set<NotificationChannel> channels;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		customer = new CustomerModel();
		customer.setEmailLanguage(ISO_CODE_ZH);

		channels = new HashSet<>();

		customerFacade = new DefaultChineseCustomerFacade();
		customerFacade.setUserService(userService);
		customerFacade.setChineseCustomerAccountService(chineseCustomerAccountService);
	}

	@Test
	public void testSaveEmailLanguageForNullUser()
	{
		customerFacade.saveEmailLanguageForCurrentUser(ISO_CODE_EN);
		verify(userService, times(0)).isAnonymousUser(customer);
	}

	@Test
	public void testSaveEmailLanguageForCurrentUser()
	{
		Mockito.doReturn(customer).when(userService).getCurrentUser();
		Mockito.doReturn(false).when(userService).isAnonymousUser(Mockito.any());
		customerFacade.saveEmailLanguageForCurrentUser(ISO_CODE_EN);
		Assert.assertEquals(customer.getEmailLanguage(), ISO_CODE_EN);
	}

	@Test
	public void testSaveEmailLanguageForAnonymousUser()
	{
		Mockito.doReturn(customer).when(userService).getCurrentUser();
		Mockito.doReturn(true).when(userService).isAnonymousUser(Mockito.any());
		customerFacade.saveEmailLanguageForCurrentUser(ISO_CODE_EN);
		Assert.assertEquals(customer.getEmailLanguage(), ISO_CODE_ZH);
	}


	@Test
	public void testUnbindMobileNumber()
	{
		customer.setMobileNumber("18111111111");
		channels.add(NotificationChannel.SMS);
		customer.setNotificationChannels(channels);
		Mockito.doReturn(customer).when(userService).getCurrentUser();
		doNothing().when(chineseCustomerAccountService).updateMobileNumber(Mockito.anyObject());

		customerFacade.unbindMobileNumber();

		Assert.assertEquals(StringUtils.EMPTY, customer.getMobileNumber());
		Assert.assertEquals(SetUtils.EMPTY_SET, customer.getNotificationChannels());
	}

	@Test
	public void testUnbindMobileNumber_NoSms()
	{
		customer.setMobileNumber("18111111111");
		customer.setNotificationChannels(channels);
		Mockito.doReturn(customer).when(userService).getCurrentUser();
		doNothing().when(chineseCustomerAccountService).updateMobileNumber(Mockito.anyObject());

		customerFacade.unbindMobileNumber();

		Assert.assertEquals(StringUtils.EMPTY, customer.getMobileNumber());
		Assert.assertEquals(SetUtils.EMPTY_SET, customer.getNotificationChannels());
	}

	@Test
	public void testUnbindMobileNumber_MutilChannels()
	{
		customer.setMobileNumber("18111111111");
		channels.add(NotificationChannel.SMS);
		channels.add(NotificationChannel.EMAIL);
		customer.setNotificationChannels(channels);
		Mockito.doReturn(customer).when(userService).getCurrentUser();
		doNothing().when(chineseCustomerAccountService).updateMobileNumber(Mockito.anyObject());

		customerFacade.unbindMobileNumber();

		Assert.assertEquals(StringUtils.EMPTY, customer.getMobileNumber());
		Assert.assertEquals(1, customer.getNotificationChannels().size());
	}
}
