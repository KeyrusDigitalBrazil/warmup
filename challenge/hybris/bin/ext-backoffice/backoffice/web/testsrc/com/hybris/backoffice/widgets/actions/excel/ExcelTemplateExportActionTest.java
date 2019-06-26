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
package com.hybris.backoffice.widgets.actions.excel;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.hybris.backoffice.excel.exporting.ExcelExportService;
import com.hybris.backoffice.excel.exporting.ExcelExportWorkbookPostProcessor;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.testing.AbstractActionUnitTest;


public class ExcelTemplateExportActionTest extends AbstractActionUnitTest<ExcelTemplateExportAction>
{
	@Mock
	private ExcelExportService excelExportService;
	@Mock
	private TypeService typeService;
	@Mock
	private PermissionFacade permissionFacade;
	@Mock
	private ExcelExportWorkbookPostProcessor excelExportWorkbookPostProcessor;

	@InjectMocks
	@Spy
	private ExcelTemplateExportAction action;

	@Override
	public ExcelTemplateExportAction getActionInstance()
	{
		return action;
	}

	@Before
	public void setUp()
	{
		doNothing().when(action).saveFile(any(), any());
	}

	@Test
	public void shouldNotOpenWizardWhenTypeCodeIsNotAssignedFromProduct()
	{
		// given
		final String typeCode = CatalogVersionModel._TYPECODE;
		final ActionContext<String> ctx = new ActionContext<>(typeCode, null, null, null);
		given(typeService.isAssignableFrom(ProductModel._TYPECODE, typeCode)).willReturn(false);
		given(excelExportService.exportTemplate(typeCode)).willReturn(mock(Workbook.class));

		// when
		action.perform(ctx);

		// then
		verify(excelExportService).exportTemplate(typeCode);
		verify(action).saveFile(any(), any());
		verify(action, never()).sendOutput(any(), any());
	}

	@Test
	public void shouldOpenWizardwhenTypeCodeIsAssignedFromProduct()
	{
		// given
		final String typeCode = ProductModel._TYPECODE;
		final ActionContext<String> ctx = new ActionContext<>(typeCode, null, null, null);
		given(typeService.isAssignableFrom(ProductModel._TYPECODE, typeCode)).willReturn(true);

		// when
		action.perform(ctx);

		// then
		verify(action).sendOutput(any(), any());
		verify(excelExportService, never()).exportTemplate(typeCode);
		verify(action, never()).saveFile(any(), any());
	}

}
