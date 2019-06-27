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
package com.hybris.backoffice.excel.template.populator;

import static org.fest.assertions.Assertions.assertThat;

import java.util.function.Function;

import org.junit.Test;


public class ExcelClassificationCellPopulatorTest
{

	@Test
	public void shouldBeAFunctionalInterface()
	{
		assertThat((ExcelClassificationCellPopulator) context -> null).isInstanceOf(Function.class);
	}
}
