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
package com.sap.hybris.sapomsreturnprocess.adapter.impl;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.processengine.BusinessProcessEvent;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapOmsReturnActionAdapterTest
{
	private static final String RETURNREQUEST_CODE = "1234";
	private static final String RETURN_PROCESS_CODE = "oms-return-process";

	@InjectMocks
	private final SapOmsReturnActionAdapter omsReturnActionAdapter = new SapOmsReturnActionAdapter();
	@Mock
	private ReturnRequestModel returnRequest;
	@Mock
	private ReturnProcessModel returnProcess;
	@Mock
	private BusinessProcessService businessProcessService;

	private ProcessTaskModel currentTask;

	@Before
	public void setUp()
	{
		currentTask = new ProcessTaskModel();
		currentTask.setAction(SapOmsReturnActionAdapter.WAIT_FOR_NOTIFICATION_FROM_BACKEND);
		when(returnProcess.getCurrentTasks()).thenReturn(Collections.singletonList(currentTask));
		when(returnRequest.getCode()).thenReturn(RETURNREQUEST_CODE);
		when(returnProcess.getCode()).thenReturn(RETURN_PROCESS_CODE);
	}

	@Test
	public void testCancelReturnRequest()
	{
		omsReturnActionAdapter.cancelReturnRequest(returnRequest, returnProcess);
		verify(businessProcessService).triggerEvent(any(BusinessProcessEvent.class));
	}

}
