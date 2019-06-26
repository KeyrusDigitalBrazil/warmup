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
package com.sap.hybris.sapomsreturnprocess.inbound.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.sap.sapmodel.model.SAPReturnRequestsModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.returnsexchange.constants.SapreturnsexchangeConstants;
import com.sap.hybris.sapomsreturnprocess.enums.SAPReturnRequestOrderStatus;


/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultDataHubOmsInboundOrderHelperTest
{
	@InjectMocks
	private final DefaultDataHubOmsInboundOrderHelper dataHubOmsInboundOrderHelper = new DefaultDataHubOmsInboundOrderHelper();

	ReturnRequestModel returnRequest;
	@Mock
	private FlexibleSearchService flexibleSearchService;

	SAPReturnRequestsModel sapReturnRequests;

	@Mock
	private ModelService modelService;
	@Mock
	private BusinessProcessService businessProcessService;

	private final String sapReturnRequestCode = "1234";
	private final String returnRequestCode = "5000";


	@Before
	public void setUp()
	{
		sapReturnRequests = new SAPReturnRequestsModel();
		returnRequest = new ReturnRequestModel();
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(
				"SELECT {o:pk} FROM {SAPReturnRequests AS o} WHERE  {o.code} = ?code");
		flexibleSearchQuery.addQueryParameter("code", sapReturnRequestCode);
		when(flexibleSearchService.searchUnique(flexibleSearchQuery)).thenReturn(sapReturnRequests);
		returnRequest.setCode(returnRequestCode);
		sapReturnRequests.setReturnRequest(returnRequest);
	}

	@Test
	public void testProcessCancelOrderConfirmationFromDataHub()
	{
		Mockito.doNothing().when(modelService).save(any(SAPReturnRequestsModel.class));
		dataHubOmsInboundOrderHelper.processCancelOrderConfirmationFromDataHub(sapReturnRequestCode);
		assertEquals(SAPReturnRequestOrderStatus.CANCELLED_FROM_BACKEND, sapReturnRequests.getSapReturnRequestOrderStatus());
		final String eventName = SapreturnsexchangeConstants.RETURNORDER_CANCELLATION_CONFIRMATION_EVENT + returnRequestCode;
		verify(businessProcessService).triggerEvent(eventName);
	}
}
