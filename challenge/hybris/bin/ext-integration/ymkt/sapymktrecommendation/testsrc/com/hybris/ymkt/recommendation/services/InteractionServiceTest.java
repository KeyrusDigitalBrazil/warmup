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
package com.hybris.ymkt.recommendation.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.ymkt.common.user.UserContextService;
import com.hybris.ymkt.recommendation.dao.InteractionContext;
import com.hybris.ymkt.recommendationbuffer.model.SAPRecoClickthroughModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class InteractionServiceTest
{
	@Mock
	private ModelService modelService;
	@Mock
	private UserContextService userContextService;
	@InjectMocks
	private InteractionService interactionService;
	private SAPRecoClickthroughModel clickthroughModel = new SAPRecoClickthroughModel();

	@Test
	public void testSaveClickthrough()
	{
		InteractionContext interactionContext = new InteractionContext();
		interactionContext.setProductId("2054947");
		interactionContext.setProductType("SAP_HYBRIS_PRODUCT");
		interactionContext.setScenarioId("SAP_TOP_SELLERS_EMAIL_CAMPAIGN");
		interactionContext.setSourceObjectId("BDD3131C3D7BF6050B8D34965FB65B93");
		Mockito.when(modelService.create(SAPRecoClickthroughModel.class)).thenReturn(clickthroughModel);
		Mockito.doNothing().when(modelService).save(clickthroughModel);
		interactionService.saveClickthrough(interactionContext);
	}

}
