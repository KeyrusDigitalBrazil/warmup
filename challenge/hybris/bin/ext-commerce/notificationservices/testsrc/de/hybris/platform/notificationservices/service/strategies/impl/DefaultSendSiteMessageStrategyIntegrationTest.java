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
package de.hybris.platform.notificationservices.service.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.notificationservices.model.SiteMessageForCustomerModel;
import de.hybris.platform.notificationservices.model.SiteMessageModel;
import de.hybris.platform.notificationservices.strategies.impl.DefaultSendSiteMessageStrategy;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link DefaultSendSiteMessageStrategy}
 */
@IntegrationTest
public class DefaultSendSiteMessageStrategyIntegrationTest extends ServicelayerTransactionalTest
{

	private static final String CUSTOMER_UID = "testcustomer";
	private static final String MSG_UID = "0000001";

	@Resource
	private DefaultSendSiteMessageStrategy sendSiteMessageStrategy;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	private CustomerModel customer;
	private SiteMessageModel message;


	@Before
	public void setup() throws ImpExException
	{
		importCsv("/notificationservices/test/DefaultSendSiteMessageStrategyTest.impex", "UTF-8");

		final CustomerModel customerExample = new CustomerModel();
		customerExample.setUid(CUSTOMER_UID);
		customer = flexibleSearchService.getModelByExample(customerExample);

		final SiteMessageModel messageExample = new SiteMessageModel();
		messageExample.setUid(MSG_UID);
		message = flexibleSearchService.getModelByExample(messageExample);
	}

	@Test
	public void testSendMessage()
	{
		sendSiteMessageStrategy.sendMessage(customer, message);

		final SiteMessageForCustomerModel example = new SiteMessageForCustomerModel();
		example.setCustomer(customer);
		example.setMessage(message);
		final SiteMessageForCustomerModel result = flexibleSearchService.getModelByExample(example);

		Assert.assertEquals(CUSTOMER_UID, result.getCustomer().getUid());
		Assert.assertEquals(MSG_UID, result.getMessage().getUid());
	}

}
