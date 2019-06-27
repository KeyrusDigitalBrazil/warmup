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
package com.hybris.backoffice.excel.exporting;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetVisibility;
import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;


@RunWith(MockitoJUnitRunner.class)
public class HideUtilitySheetsDecoratorTest
{

	private final Collection<ExcelTemplateConstants.UtilitySheet> sheetsToHide = Lists
			.newArrayList(ExcelTemplateConstants.UtilitySheet.HEADER_PROMPT);

	@Spy
	private final HideUtilitySheetsDecorator hideUtilitySheetsDecorator = new HideUtilitySheetsDecorator();

	@Before
	public void setUp()
	{
		hideUtilitySheetsDecorator.setUtilitySheets(sheetsToHide);
	}

	@Test
	public void shouldUtilitySheetBeHidden()
	{
		// given
		final Workbook workbook = mock(Workbook.class);
		final int index = 1;
		final SheetVisibility hiddenLevel = SheetVisibility.VERY_HIDDEN;
		given(workbook.getSheetIndex(ExcelTemplateConstants.UtilitySheet.HEADER_PROMPT.getSheetName())).willReturn(index);
		given(workbook.isSheetHidden(index)).willReturn(false);
		given(workbook.getSheetAt(index)).willReturn(mock(Sheet.class));

		doNothing().when(hideUtilitySheetsDecorator).activateFirstNonUtilitySheet(workbook);
		doReturn(hiddenLevel).when(hideUtilitySheetsDecorator).getUtilitySheetHiddenLevel();

		// when
		hideUtilitySheetsDecorator.decorate(new ExcelExportResult(workbook));

		// then
		verify(workbook).setSheetVisibility(index, hiddenLevel);
	}

}
