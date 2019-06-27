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
package com.hybris.backoffice.excel.validators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.SelectedAttributeQualifier;
import com.hybris.backoffice.excel.template.header.ExcelHeaderService;
import com.hybris.backoffice.excel.template.sheet.ExcelSheetService;
import com.hybris.backoffice.excel.template.workbook.ExcelWorkbookService;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class WorkbookTypeCodeAndSelectedAttributeValidatorTest
{

	@Mock
	private ExcelWorkbookService excelWorkbookService;
	@Mock
	private ExcelSheetService excelSheetService;
	@Mock
	private ExcelHeaderService excelHeaderService;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@InjectMocks
	private WorkbookTypeCodeAndSelectedAttributeValidator validator;

	@Mock
	private Workbook workbook;

	@Mock
	private Sheet productSheet;

	@Mock
	private Sheet typeSystemSheet;


	@Before
	public void setup()
	{
		when(excelWorkbookService.getMetaInformationSheet(workbook)).thenReturn(typeSystemSheet);
		when(excelSheetService.getSheets(workbook)).thenReturn(Arrays.asList(productSheet));
		when(productSheet.getSheetName()).thenReturn(ProductModel._TYPECODE);
		when(excelSheetService.findTypeCodeForSheetName(any(), eq(ProductModel._TYPECODE))).thenReturn(ProductModel._TYPECODE);
	}

	@Test
	public void shouldNotReturnValidationErrorWhenTypeCodeAndSelectedAttributesExists()
	{
		// given
		final SelectedAttributeQualifier selectedAttributeQualifier = new SelectedAttributeQualifier(ProductModel.CODE,
				ProductModel.CODE);
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(true);
		when(permissionCRUDService.canChangeAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(true);
		when(excelHeaderService.getSelectedAttributesQualifiers(typeSystemSheet, productSheet))
				.thenReturn(Arrays.asList(selectedAttributeQualifier));

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).isEmpty();
	}

	@Test
	public void shouldReturnValidationErrorWhenTypeCodeDoesNotExist()
	{
		// given
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenThrow(UnknownIdentifierException.class);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey())
				.isEqualTo(WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER);
		verify(permissionCRUDService, never()).canReadAttribute(any(), any());
		verify(permissionCRUDService, never()).canChangeAttribute(any(), any());
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToReadType()
	{
		// given
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(false);
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(true);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey())
				.isEqualTo(WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER);
		verify(permissionCRUDService, never()).canReadAttribute(any(), any());
		verify(permissionCRUDService, never()).canChangeAttribute(any(), any());
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToChangeType()
	{
		// given
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(false);
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(true);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey())
				.isEqualTo(WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER);
		verify(permissionCRUDService, never()).canReadAttribute(any(), any());
		verify(permissionCRUDService, never()).canChangeAttribute(any(), any());
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToCreatingNewInstanceOfType()
	{
		// given
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(false);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey())
				.isEqualTo(WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER);
		verify(permissionCRUDService, never()).canReadAttribute(any(), any());
		verify(permissionCRUDService, never()).canChangeAttribute(any(), any());
	}

	@Test
	public void shouldReturnValidationErrorWhenColumnsAreDuplicated()
	{
		// given
		final SelectedAttributeQualifier firstSelectedColumn = new SelectedAttributeQualifier(ProductModel.CODE, ProductModel.CODE);
		final SelectedAttributeQualifier secondSelectedColumn = new SelectedAttributeQualifier(ProductModel.CODE,
				ProductModel.CODE);
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(true);
		when(permissionCRUDService.canChangeAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(true);
		when(excelHeaderService.getSelectedAttributesQualifiers(typeSystemSheet, productSheet))
				.thenReturn(Arrays.asList(firstSelectedColumn, secondSelectedColumn));

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey())
				.isEqualTo(WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_WORKBOOK_DUPLICATED_COLUMNS_HEADER);
		verify(permissionCRUDService).canReadType(anyString());
		verify(permissionCRUDService).canChangeType(anyString());
		verify(permissionCRUDService).canCreateTypeInstance(anyString());
	}

	@Test
	public void shouldReturnValidationErrorWhenColumnDoesNotExist()
	{
		// given
		final SelectedAttributeQualifier firstSelectedColumn = new SelectedAttributeQualifier(ProductModel.CODE, null);
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(true);
		when(permissionCRUDService.canChangeAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(true);
		when(excelHeaderService.getSelectedAttributesQualifiers(typeSystemSheet, productSheet))
				.thenReturn(Arrays.asList(firstSelectedColumn));

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey())
				.isEqualTo(WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_HEADER);
		verify(permissionCRUDService).canReadType(anyString());
		verify(permissionCRUDService).canChangeType(anyString());
		verify(permissionCRUDService).canCreateTypeInstance(anyString());
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToReadAttribute()
	{
		// given
		final SelectedAttributeQualifier firstSelectedColumn = new SelectedAttributeQualifier(ProductModel.CODE, ProductModel.CODE);
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(false);
		when(permissionCRUDService.canChangeAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(true);
		when(excelHeaderService.getSelectedAttributesQualifiers(typeSystemSheet, productSheet))
				.thenReturn(Arrays.asList(firstSelectedColumn));

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey())
				.isEqualTo(WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_HEADER);
		verify(permissionCRUDService).canReadType(anyString());
		verify(permissionCRUDService).canChangeType(anyString());
		verify(permissionCRUDService).canCreateTypeInstance(anyString());
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToChangeAttribute()
	{
		// given
		final SelectedAttributeQualifier firstSelectedColumn = new SelectedAttributeQualifier(ProductModel.CODE, ProductModel.CODE);
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(true);
		when(permissionCRUDService.canChangeAttribute(ProductModel._TYPECODE, ProductModel.CODE)).thenReturn(false);
		when(excelHeaderService.getSelectedAttributesQualifiers(typeSystemSheet, productSheet))
				.thenReturn(Arrays.asList(firstSelectedColumn));

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey())
				.isEqualTo(WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_HEADER);
		verify(permissionCRUDService).canReadType(anyString());
		verify(permissionCRUDService).canChangeType(anyString());
		verify(permissionCRUDService).canCreateTypeInstance(anyString());
	}
}
