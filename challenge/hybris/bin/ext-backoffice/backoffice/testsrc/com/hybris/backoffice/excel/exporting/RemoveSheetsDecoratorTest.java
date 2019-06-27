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


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;


@RunWith(MockitoJUnitRunner.class)
public class RemoveSheetsDecoratorTest
{

	private final RemoveSheetsDecorator removeSheetsDecorator = new RemoveSheetsDecorator();
	private Workbook workbook;

	@Before
	public void setup()
	{
		workbook = new XSSFWorkbook();
		workbook.createSheet(ExcelTemplateConstants.UtilitySheet.TYPE_SYSTEM.getSheetName());
		workbook.createSheet(ExcelTemplateConstants.UtilitySheet.PK.getSheetName());
		workbook.createSheet(ExcelTemplateConstants.UtilitySheet.CLASSIFICATION_TYPE_SYSTEM.getSheetName());
	}

	@Test
	public void shouldRemovePkSheet()
	{
		// given
		final List<ExcelTemplateConstants.UtilitySheet> sheetsToRemove = Lists.newArrayList(ExcelTemplateConstants.UtilitySheet.PK);
		removeSheetsDecorator.setSheetsToRemove(sheetsToRemove);

		// when
		removeSheetsDecorator.decorate(new ExcelExportResult(workbook, Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList()));

		// then
		assertThat(workbook.getNumberOfSheets()).isEqualTo(2);
		assertThat(workbook.getSheet(ExcelTemplateConstants.UtilitySheet.TYPE_SYSTEM.getSheetName())).isNotNull();
		assertThat(workbook.getSheet(ExcelTemplateConstants.UtilitySheet.CLASSIFICATION_TYPE_SYSTEM.getSheetName())).isNotNull();
		assertThat(workbook.getSheet(ExcelTemplateConstants.UtilitySheet.PK.getSheetName())).isNull();
	}
}
