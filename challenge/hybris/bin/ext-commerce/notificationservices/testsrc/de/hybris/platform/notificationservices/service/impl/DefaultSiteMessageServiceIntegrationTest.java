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
package de.hybris.platform.notificationservices.service.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link DefaultSiteMessageService}
 */
@IntegrationTest
public class DefaultSiteMessageServiceIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String MSG_TITLE = "test message title";

	private static final String MSG_LINK = "/test/message/link";

	private static final String MSG_CONTENT = "test message content";

	@Resource
	private DefaultSiteMessageService siteMessageService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private I18NService i18nService;

	@Resource
	private UserService userService;

	private SiteMessageType type;
	private Locale locale;
	private NotificationType notificationType;

	@Before
	public void setup() throws ImpExException
	{
		type = SiteMessageType.SYSTEM;
		locale = Locale.ENGLISH;
		notificationType = NotificationType.NOTIFICATION;
		i18nService.setCurrentLocale(locale);

		importCsv("/notificationservices/test/DefaultSiteMessageServiceIntegrationTest.impex", "UTF-8");
	}

	@Test
	public void testGetPaginatedMesages()
	{

		final CustomerModel customer = new CustomerModel();
		customer.setUid("customer1@hybris.com");
		final CustomerModel customerModel = flexibleSearchService.getModelByExample(customer);

		final SearchPageData searchPageData = prepareSearchPageData();

		final SearchPageData<SiteMessageForCustomerModel> siteMessageData = siteMessageService.getPaginatedMessagesForCustomer(customerModel,
				searchPageData);
		Assert.assertEquals(2, siteMessageData.getResults().size());
		Assert.assertEquals("customer1@hybris.com", siteMessageData.getResults().get(0).getCustomer().getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPaginatedMessagesWithoutSearchParameter()
	{
		final CustomerModel customer = new CustomerModel();
		customer.setUid("customer1@hybris.com");
		final CustomerModel customerModel = flexibleSearchService.getModelByExample(customer);
		final SearchPageData<SiteMessageForCustomerModel> siteMessageData = siteMessageService.getPaginatedMessagesForCustomer(customerModel,
				null);
	}

	@Test
	public void testGetPaginatedMesagesForType()
	{

		final CustomerModel customer = new CustomerModel();
		customer.setUid("customer2@hybris.com");
		final CustomerModel customerModel = flexibleSearchService.getModelByExample(customer);

		final SearchPageData searchPageData = prepareSearchPageData();

		final SearchPageData<SiteMessageForCustomerModel> siteMessageData = siteMessageService
				.getPaginatedMessagesForType(customerModel,
				SiteMessageType.SYSTEM, searchPageData);

		Assert.assertEquals(1, siteMessageData.getResults().size());
		Assert.assertEquals("customer2@hybris.com", siteMessageData.getResults().get(0).getCustomer().getUid());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPaginatedMesagesForTypeWithNullType()
	{

		final CustomerModel customer = new CustomerModel();
		customer.setUid("customer2@hybris.com");
		final CustomerModel customerModel = flexibleSearchService.getModelByExample(customer);

		final SearchPageData searchPageData = prepareSearchPageData();

		final SearchPageData<SiteMessageForCustomerModel> siteMessageData = siteMessageService
				.getPaginatedMessagesForType(customerModel, null, searchPageData);

	}

	protected SearchPageData prepareSearchPageData()
	{
		final SearchPageData searchPageData = new SearchPageData();
		final PaginationData pagination = new PaginationData();
		pagination.setCurrentPage(0);
		pagination.setPageSize(10);
		pagination.setNeedsTotal(true);

		searchPageData.setPagination(pagination);
		searchPageData.setSorts(Collections.EMPTY_LIST);

		return searchPageData;
	}

	@Test
	public void testCreateMessage_localNull()
	{
		final SiteMessageModel message = siteMessageService.createMessage(MSG_TITLE, MSG_CONTENT, type, null, notificationType,
				null);

		Assert.assertEquals(MSG_TITLE, message.getTitle(locale));
		Assert.assertEquals(MSG_CONTENT, message.getContent(locale));
		Assert.assertEquals(type, message.getType());
		Assert.assertEquals(notificationType, message.getNotificationType());
	}

	@Test
	public void testCreateMessage()
	{
		final SiteMessageModel message = siteMessageService.createMessage(MSG_TITLE, MSG_CONTENT, type, null, notificationType,
				locale);
		Assert.assertEquals(MSG_TITLE, message.getTitle(locale));
		Assert.assertEquals(MSG_CONTENT, message.getContent(locale));
		Assert.assertEquals(type, message.getType());
		Assert.assertEquals(notificationType, message.getNotificationType());
	}

	@Test
	public void testGetSiteMessagesForCustomer_customerWithMessage()
	{
		final CustomerModel customer = (CustomerModel) userService.getUserForUID("customer1@hybris.com");
		final List<SiteMessageForCustomerModel> result = siteMessageService.getSiteMessagesForCustomer(customer);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public void testGetSiteMessagesForCustomer_customerWithoutMessage()
	{
		final CustomerModel customer = (CustomerModel) userService.getUserForUID("customer3@hybris.com");
		final List<SiteMessageForCustomerModel> result = siteMessageService.getSiteMessagesForCustomer(customer);
		Assert.assertEquals(0, result.size());
	}


}
