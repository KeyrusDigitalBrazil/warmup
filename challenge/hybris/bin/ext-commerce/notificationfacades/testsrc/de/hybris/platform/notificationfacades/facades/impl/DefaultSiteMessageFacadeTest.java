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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.notificationfacades.data.SiteMessageData;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.service.SiteMessageService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSiteMessageFacadeTest
{
	private DefaultSiteMessageFacade defaultSiteMessageFacade;
	@Mock
	private SiteMessageService siteMessageService;
	@Mock
	private UserService userService;
	@Mock
	private Converter<SiteMessageForCustomerModel, SiteMessageData> siteMessageConverter;

	@Mock
	private Converter<SearchPageData<SiteMessageForCustomerModel>, SearchPageData<SiteMessageData>> SiteMessageSearchPageDataConverter;

	private CustomerModel customerModel;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultSiteMessageFacade = new DefaultSiteMessageFacade();
		defaultSiteMessageFacade.setUserService(userService);
		defaultSiteMessageFacade.setSiteMessageService(siteMessageService);
		defaultSiteMessageFacade.setSiteMessageSearchPageDataConverter(SiteMessageSearchPageDataConverter);

	}

	@Test
	public void testGetPaginatedSiteMessages()
	{
		customerModel = new CustomerModel();
		customerModel.setUid("customer@hybris.com");
		final SearchPageData searchPageData = new SearchPageData();

		final SearchPageData<SiteMessageForCustomerModel> searchResult = new SearchPageData();

		final SearchPageData<SiteMessageData> convertResult = new SearchPageData();

		given(userService.getCurrentUser()).willReturn(customerModel);
		given(siteMessageService.getPaginatedMessagesForCustomer(customerModel, searchPageData)).willReturn(searchResult);
		given(SiteMessageSearchPageDataConverter.convert(searchResult)).willReturn(convertResult);


		final SearchPageData<SiteMessageData> data = defaultSiteMessageFacade.getPaginatedSiteMessages(searchPageData);
		Assert.assertEquals(convertResult, data);
	}

	@Test
	public void testGetPaginatedSiteMessagesForType()
	{
		customerModel = new CustomerModel();
		customerModel.setUid("customer@hybris.com");
		final SearchPageData searchPageData = new SearchPageData();

		final SearchPageData<SiteMessageForCustomerModel> searchResult = new SearchPageData();

		final SearchPageData<SiteMessageData> convertResult = new SearchPageData();

		given(userService.getCurrentUser()).willReturn(customerModel);
		given(siteMessageService.getPaginatedMessagesForType(customerModel, SiteMessageType.SYSTEM, searchPageData))
				.willReturn(searchResult);
		given(SiteMessageSearchPageDataConverter.convert(searchResult)).willReturn(convertResult);


		final SearchPageData<SiteMessageData> data = defaultSiteMessageFacade
				.getPaginatedSiteMessagesForType("SYSTEM", searchPageData);
		Assert.assertEquals(convertResult, data);

	}

}
