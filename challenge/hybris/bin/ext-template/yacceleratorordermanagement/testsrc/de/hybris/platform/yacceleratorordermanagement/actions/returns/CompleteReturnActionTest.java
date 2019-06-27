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
package de.hybris.platform.yacceleratorordermanagement.actions.returns;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.OrderReturnRecordHandler;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CompleteReturnActionTest
{
	@InjectMocks
	private CompleteReturnAction action;

	@Mock
	private ModelService modelService;
	@Mock
	private OrderReturnRecordHandler orderReturnRecordsHandler;
	@Mock
	private ReturnProcessModel returnProcessModel;
	@Mock
	private ReturnRequestModel returnRequest;
	@Mock
	private ReturnEntryModel returnEntry;


	@Before
	public void setup()
	{
		when(returnProcessModel.getReturnRequest()).thenReturn(returnRequest);
		when(returnRequest.getReturnEntries()).thenReturn(Collections.singletonList(returnEntry));

		doNothing().when(modelService).save(returnRequest);
	}

	@Test
	public void shouldCompleteReturnRequestAndEntry() throws Exception
	{
		//When
		action.execute(returnProcessModel);

		//Then
		verify(orderReturnRecordsHandler).finalizeOrderReturnRecordForReturnRequest(returnRequest);
		verify(returnRequest).setStatus(ReturnStatus.COMPLETED);
		verify(returnEntry).setStatus(ReturnStatus.COMPLETED);
	}

}
