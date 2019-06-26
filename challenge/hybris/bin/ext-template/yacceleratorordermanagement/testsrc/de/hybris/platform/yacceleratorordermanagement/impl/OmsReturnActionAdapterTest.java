/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.yacceleratorordermanagement.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OmsReturnActionAdapterTest
{
	private static final String RETURN_PROCESS_CODE = "return-process";

	@InjectMocks
	private final OmsReturnActionAdapter omsReturnActionAdapter = new OmsReturnActionAdapter();
	@Mock
	private BusinessProcessService businessProcessService;
	@Mock
	private ReturnRequestModel returnRequest;
	@Mock
	private ReturnProcessModel returnProcess;
	@Mock
	private ProcessTaskModel currentTask;
	@Mock
	private OrderModel order;
	@Mock
	private BaseStoreModel baseStore;


	@Before
	public void setUp() throws Exception
	{
		when(returnRequest.getOrder()).thenReturn(order);
		when(order.getStore()).thenReturn(baseStore);
		when(baseStore.getCreateReturnProcessCode()).thenReturn(RETURN_PROCESS_CODE);
		when(returnRequest.getReturnProcess()).thenReturn(Collections.singletonList(returnProcess));
		when(returnProcess.getCode()).thenReturn(RETURN_PROCESS_CODE);
		when(returnProcess.getCurrentTasks()).thenReturn(Collections.singletonList(currentTask));
	}

	@Test
	public void shouldApproveReturnRequest()
	{
		//When
		omsReturnActionAdapter.requestReturnApproval(returnRequest);

		//Then
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

	@Test
	public void shouldAcceptGoodsReturnRequest()
	{
		//When
		omsReturnActionAdapter.requestReturnReception(returnRequest);

		//Then
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

	@Test
	public void shouldPaymentReverseReturnRequest()
	{
		//When
		omsReturnActionAdapter.requestManualPaymentReversalForReturnRequest(returnRequest);

		//Then
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

	@Test
	public void shouldTaxReverseReturnRequest()
	{
		//When
		omsReturnActionAdapter.requestManualTaxReversalForReturnRequest(returnRequest);

		//Then
		verify(businessProcessService).triggerEvent(anyString());
	}

	@Test
	public void shouldCancelReturnRequest()
	{
		//Given
		when(currentTask.getAction()).thenReturn(OmsReturnActionAdapter.WAIT_FOR_CONFIRM_OR_CANCEL_REFUND_ACTION);

		//When
		omsReturnActionAdapter.requestReturnCancellation(returnRequest);

		//Then
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCancelNullReturnRequest()
	{
		//When
		omsReturnActionAdapter.requestReturnCancellation(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCancelReturnRequestNoProcess()
	{
		//Given
		when(returnRequest.getReturnProcess()).thenReturn(Collections.emptyList());
		//When
		omsReturnActionAdapter.requestReturnCancellation(returnRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCancelReturnRequestNoOrder()
	{
		//Given
		when(returnRequest.getOrder()).thenReturn(null);
		//When
		omsReturnActionAdapter.requestReturnCancellation(returnRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCancelReturnRequestNoBaseStore()
	{
		//Given
		when(order.getStore()).thenReturn(null);
		//When
		omsReturnActionAdapter.requestReturnCancellation(returnRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCancelReturnRequestNoTasksAvailableToProcess()
	{
		//Given
		when(returnProcess.getCurrentTasks()).thenReturn(Collections.emptyList());

		//When
		omsReturnActionAdapter.requestReturnCancellation(returnRequest);
	}

}
