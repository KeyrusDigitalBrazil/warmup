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
package de.hybris.platform.warehousing.atp.formula.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.warehousing.atp.formula.dao.AtpFormulaDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAtpFormulaServiceTest
{

	@InjectMocks
	private DefaultAtpFormulaService atpFormulaService;

	@Mock
	private AtpFormulaDao atpFormulaDao;

	@Test
	public void shouldGetAllAtpFormula()
	{
		//When
		atpFormulaService.getAllAtpFormula();

		//Then
		verify(atpFormulaDao).getAllAtpFormula();
	}

}
