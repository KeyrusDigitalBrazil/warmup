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
package com.hybris.backoffice.excel.data;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;


public class ExcelExportParamsTest
{
	@Test
	public void shouldAcceptEmptyListsAsParameters()
	{
		// when
		final ExcelExportParams excelExportParams = new ExcelExportParams(emptyList(), emptyList(), emptyList());

		// then
		assertThat(excelExportParams).isNotNull();
	}

	@Test
	public void shouldNotAcceptNullItemsToExport()
	{
		// given
		NullPointerException caughtException = null;

		// when
		try
		{
			new ExcelExportParams(null, emptyList(), emptyList());
		}
		catch (final NullPointerException e)
		{
			caughtException = e;
		}
		// then
		assertThat(caughtException).isNotNull().hasMessage("ItemsToExport collection cannot be null");
	}

	@Test
	public void shouldNotAcceptNullSelectedAttributes()
	{
		// given
		NullPointerException caughtException = null;

		// when
		try
		{
			new ExcelExportParams(emptyList(), null, emptyList());
		}
		catch (final NullPointerException e)
		{
			caughtException = e;
		}
		// then
		assertThat(caughtException).isNotNull().hasMessage("SelectedAttributes collection cannot be null");
	}

	@Test
	public void shouldNotAcceptNullAdditionalAttributes()
	{
		// given
		NullPointerException caughtException = null;

		// when
		try
		{
			new ExcelExportParams(emptyList(), emptyList(), null);
		}
		catch (final NullPointerException e)
		{
			caughtException = e;
		}
		// then
		assertThat(caughtException).isNotNull().hasMessage("AdditionalAttributes collection cannot be null");
	}
}
