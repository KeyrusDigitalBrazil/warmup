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
package de.hybris.platform.returns.executors;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.returns.OrderReturnException;
import de.hybris.platform.returns.ReturnActionAdapter;
import de.hybris.platform.returns.ReturnActionRequest;
import de.hybris.platform.returns.impl.executors.DefaultReturnActionRequestExecutor;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultReturnActionRequestExecutorTest
{
	private ReturnRequestModel returnRequest;
	private ReturnActionRequest returnActionRequest;

	@Mock
	private ModelService modelService;

	@Mock
	private ReturnActionAdapter returnActionAdapter;

	@InjectMocks
	private final DefaultReturnActionRequestExecutor returnActionRequestExecutor = new DefaultReturnActionRequestExecutor();

	@Before
	public void setup()
	{
		returnRequest = new ReturnRequestModel();
		returnRequest.setRMA("RETURN_REQUEST1");

		final RefundEntryModel returnEntryModel1 = spy(new RefundEntryModel());
		returnEntryModel1.setExpectedQuantity(Long.valueOf(2L));
		when(returnEntryModel1.getPk()).thenReturn(PK.fromLong(1L));
		when(returnEntryModel1.getReturnRequest()).thenReturn(returnRequest);

		returnRequest.setReturnEntries(Arrays.asList(returnEntryModel1));

		doNothing().when(modelService).save(any());
		returnActionRequest = new ReturnActionRequest(returnRequest);
	}

	@Test
	public void shouldProcessManualPaymentReversalForReturnRequest() throws OrderReturnException
	{
		returnActionRequestExecutor.processManualPaymentReversalForReturnRequest(returnActionRequest);
		verify(returnActionAdapter, times(1)).requestManualPaymentReversalForReturnRequest(returnRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotProcessManualPaymentReversalForNullReturnActionRequest() throws OrderReturnException
	{
		returnActionRequestExecutor.processManualPaymentReversalForReturnRequest(null);
	}

	@Test
	public void shouldProcessManualTaxReversalForReturnRequest() throws OrderReturnException
	{
		returnActionRequestExecutor.processManualTaxReversalForReturnRequest(returnActionRequest);
		verify(returnActionAdapter, times(1)).requestManualTaxReversalForReturnRequest(returnRequest);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotProcessManualTaxReversalForNullReturnActionRequest() throws OrderReturnException
	{
		returnActionRequestExecutor.processManualPaymentReversalForReturnRequest(null);
	}
}
