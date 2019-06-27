/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.sap.hybris.c4c.customer.action;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction.Transition;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.testframework.Assert;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.service.SapC4cCustomerPublicationService;
import com.sap.hybris.c4c.customer.util.SapC4cCustomerUtils;


/**
 *
 */
@UnitTest
public class SapC4cCustomerPublishActionTest
{
	@InjectMocks
	private final SapC4cCustomerPublishAction customerPublishAction = new SapC4cCustomerPublishAction();

	@Mock
	private SapC4cCustomerPublicationService c4cCustomerPublicationService;
	@Mock
	private SapC4cCustomerUtils customerUtil;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteAction() throws RetryLaterException, IOException
	{
		final StoreFrontCustomerProcessModel customerProcess = Mockito.mock(StoreFrontCustomerProcessModel.class);
		final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
		final C4CCustomerData customerData = Mockito.mock(C4CCustomerData.class);

		when(customerProcess.getCustomer()).thenReturn(customerModel);
		when(customerModel.getAddresses()).thenReturn(null);

		when(customerUtil.getCustomerDataForCustomer(Mockito.any(CustomerModel.class), Mockito.anyList())).thenReturn(customerData);
		doNothing().when(c4cCustomerPublicationService).publishCustomerToCloudPlatformIntegration(customerData);

		final Transition result = customerPublishAction.executeAction(customerProcess);

		Assert.assertEquals(Transition.OK, result);
	}
}
