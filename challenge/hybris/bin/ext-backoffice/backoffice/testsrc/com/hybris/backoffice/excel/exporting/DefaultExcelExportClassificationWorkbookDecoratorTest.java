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
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.ItemModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ExcelExportResult;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelExportClassificationWorkbookDecoratorTest
{
	@Spy
	DefaultExcelExportClassificationWorkbookDecorator decorator;

	@Test
	public void shouldExtractClassificationAttributes()
	{
		// given
		final Workbook workbook = mock(Workbook.class);
		final ExcelExportResult excelExportResult = mock(ExcelExportResult.class);
		final ExcelClassificationAttribute excelClassificationAttribute = mock(ExcelClassificationAttribute.class);
		final ExcelAttributeDescriptorAttribute excelAttributeDescriptorAttribute = mock(ExcelAttributeDescriptorAttribute.class);
		final List<ItemModel> itemModels = Collections.singletonList(mock(ItemModel.class));

		given(excelExportResult.getWorkbook()).willReturn(workbook);
		given(excelExportResult.getSelectedAdditionalAttributes())
				.willReturn(Arrays.asList(excelClassificationAttribute, excelAttributeDescriptorAttribute));
		given(excelExportResult.getSelectedItems()).willReturn(itemModels);
		doNothing().when(decorator).decorate(any(), any(), any());

		// when
		decorator.decorate(excelExportResult);

		// then
		then(decorator).should().decorate(workbook, Collections.singletonList(excelClassificationAttribute), itemModels);
	}
}
