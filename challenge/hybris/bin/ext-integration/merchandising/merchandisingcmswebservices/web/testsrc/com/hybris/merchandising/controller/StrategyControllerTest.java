/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.controller;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.hybris.merchandising.dto.DropdownElement;
import com.hybris.merchandising.service.StrategyService;
import com.hybris.merchandising.service.impl.StrategyTest;


/**
 * StrategyControllerTest is a test class for the {@link StrategyController}.
 */
public class StrategyControllerTest extends StrategyTest
{

	StrategyController controller;
	StrategyService mockStrategyService;

	@Before
	public void setUp()
	{
		controller = new StrategyController();
		mockStrategyService = Mockito.mock(StrategyService.class);
		Mockito.when(mockStrategyService.getStrategies(Integer.valueOf(1), Integer.valueOf(10))).thenReturn(getMockStrategies(10));
		Mockito.when(mockStrategyService.getStrategies(Integer.valueOf(1), Integer.valueOf(16))).thenReturn(getMockStrategies(16));
		controller.strategyService = mockStrategyService;
	}

	@Test
	public void testGetStrategies()
	{
		final Map<String, List<DropdownElement>> allStrategies = controller.getStrategies(null, null);
		for (int i = 0; i < 9; i++)
		{
			verifyDropDown(i, allStrategies.get("options").get(i));
		}
		Mockito.verify(mockStrategyService, Mockito.times(1)).getStrategies(1, 10);

		final Map<String, List<DropdownElement>> pagedStrategies = controller.getStrategies(Integer.valueOf(0), Integer.valueOf(16));
		for (int i = 0; i < 15; i++)
		{
			verifyDropDown(i, pagedStrategies.get("options").get(i));
		}
		Mockito.verify(mockStrategyService, Mockito.times(1)).getStrategies(Integer.valueOf(1), Integer.valueOf(16));
	}
}
