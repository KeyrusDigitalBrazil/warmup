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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.workbook.ExcelWorkbookService;


@RunWith(MockitoJUnitRunner.class)
public class IsoCodeDecoratorTest
{
	@Mock
	private ExcelWorkbookService excelWorkbookService;
	@Mock
	private CommonI18NService commonI18NService;

	private final IsoCodeDecorator isoCodeDecorator = new IsoCodeDecorator();

	@Before
	public void setUp()
	{
		isoCodeDecorator.setCommonI18NService(commonI18NService);
		isoCodeDecorator.setExcelWorkbookService(excelWorkbookService);
	}

	@Test
	public void shouldPropertyBeAddedToTheWorbook()
	{
		// given
		final Workbook workbook = mock(Workbook.class);
		final String key = "isoCode";
		final String isoCode = "en";
		final ExcelExportResult result = mock(ExcelExportResult.class);
		given(result.getWorkbook()).willReturn(workbook);
		given(commonI18NService.getCurrentLanguage()).willReturn(mock(LanguageModel.class, (Answer) answer -> isoCode));

		// when
		isoCodeDecorator.decorate(result);

		// then
		verify(excelWorkbookService).addProperty(workbook, key, isoCode);
	}
}
