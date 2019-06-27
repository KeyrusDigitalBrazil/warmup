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
package com.hybris.backoffice.excel.template.sheet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.template.workbook.DefaultExcelWorkbookService;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelWorkbookServiceTest
{

	private final DefaultExcelWorkbookService workbookService = new DefaultExcelWorkbookService();

	@Test
	public void shouldMetaInformationSheetBeReturned()
	{
		// given
		final ExcelTemplateConstants.UtilitySheet metaInformationSheet = ExcelTemplateConstants.UtilitySheet.TYPE_SYSTEM;
		final Sheet sheet = mock(Sheet.class);
		final Workbook workbook = mock(Workbook.class);
		given(workbook.getSheet(metaInformationSheet.getSheetName())).willReturn(sheet);

		// when - then
		assertThat(sheet).isEqualTo(workbookService.getMetaInformationSheet(workbook));
	}
}
