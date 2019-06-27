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

package de.hybris.platform.yacceleratorordermanagement.actions.returns;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ApproveReturnActionTest
{
	@InjectMocks
	private ApproveReturnAction action;

	@Mock
	private ReturnProcessModel returnProcessModel;
	@Mock
	private ModelService modelService;
	@Mock
	private ReturnRequestModel instoreReturn;
	@Mock
	private ReturnRequestModel onlineReturn;


	@Test
	public void shouldApproveOnlineReturn() throws Exception
	{
		//Given
		when(returnProcessModel.getReturnRequest()).thenReturn(onlineReturn);

		//When
		action.execute(returnProcessModel);

		//Then
		verify(onlineReturn).setStatus(ReturnStatus.WAIT);
	}

	@Test
	public void shouldApproveInstoreReturn() throws Exception
	{
		//Given
		when(returnProcessModel.getReturnRequest()).thenReturn(instoreReturn);

		//When
		action.execute(returnProcessModel);

		//Then
		verify(instoreReturn).setStatus(ReturnStatus.WAIT);
	}

}
