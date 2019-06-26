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

import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndAttributesValidator.CLASSIFICATION_SYSTEM_ERRORS_HEADER;
import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndAttributesValidator.INSUFFICIENT_PERMISSIONS_TO_TYPE;
import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndAttributesValidator.UNKNOWN_CLASSIFICATION_SYSTEM_VERSION;
import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_DESCRIPTION;
import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER;
import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_WORKBOOK_DUPLICATED_COLUMNS_DESCRIPTION;
import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_WORKBOOK_DUPLICATED_COLUMNS_HEADER;
import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_DESCRIPTION;
import static com.hybris.backoffice.excel.validators.WorkbookTypeCodeAndSelectedAttributeValidator.EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.importing.ExcelAttributeTypeSystemService;
import com.hybris.backoffice.excel.importing.ExcelClassificationTypeSystemService;
import com.hybris.backoffice.excel.importing.ExcelTypeSystemService;
import com.hybris.backoffice.excel.importing.data.ClassificationTypeSystemRow;
import com.hybris.backoffice.excel.template.header.ExcelHeaderService;
import com.hybris.backoffice.excel.template.populator.typesheet.TypeSystemRow;
import com.hybris.backoffice.excel.template.sheet.ExcelSheetService;
import com.hybris.backoffice.excel.template.workbook.ExcelWorkbookService;
import com.hybris.backoffice.excel.validators.data.ExcelValidationResult;


@RunWith(MockitoJUnitRunner.class)
public class WorkbookTypeCodeAndAttributesValidatorTest
{

	public static final String CLASSIFICATION_SYSTEM = "SampleClassification";
	public static final String CLASSIFICATION_VERSION = "1.0";
	@Mock
	private ExcelWorkbookService excelWorkbookService;
	@Mock
	private ExcelSheetService excelSheetService;
	@Mock
	private ExcelHeaderService excelHeaderService;
	@Mock
	private PermissionCRUDService permissionCRUDService;
	@Mock
	private Workbook workbook;
	@Mock
	private Sheet productSheet;
	@Mock
	private Sheet typeSystemSheet;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private UserService userService;
	@Mock
	private ExcelTypeSystemService excelTypeSystemService;
	@Mock
	private ExcelClassificationTypeSystemService excelClassificationTypeSystemService;

	@Spy
	@InjectMocks
	private WorkbookTypeCodeAndAttributesValidator validator;

	@Mock
	private CatalogVersionModel classificationSystem;
	@Mock
	private UserModel userModel;

	private final List<String> attributeNames = new ArrayList<>();
	private final ExcelAttributeTypeSystemService.ExcelTypeSystem excelTypeSystem = mock(
			ExcelAttributeTypeSystemService.ExcelTypeSystem.class);
	private final ExcelClassificationTypeSystemService.ExcelClassificationTypeSystem excelClassificationTypeSystem = mock(
			ExcelClassificationTypeSystemService.ExcelClassificationTypeSystem.class);

	@Before
	public void setup()
	{
		when(productSheet.getWorkbook()).thenReturn(workbook);
		when(excelWorkbookService.getMetaInformationSheet(workbook)).thenReturn(typeSystemSheet);
		when(excelSheetService.getSheets(workbook)).thenReturn(Arrays.asList(productSheet));
		when(productSheet.getSheetName()).thenReturn(ProductModel._TYPECODE);
		when(excelSheetService.findTypeCodeForSheetName(any(), eq(ProductModel._TYPECODE))).thenReturn(ProductModel._TYPECODE);
		when(excelHeaderService.getHeaderDisplayNames((productSheet))).thenReturn(attributeNames);

		when(excelTypeSystemService.loadTypeSystem(workbook)).thenReturn(excelTypeSystem);
		when(excelClassificationTypeSystemService.loadTypeSystem(workbook)).thenReturn(excelClassificationTypeSystem);
		when(userService.getCurrentUser()).thenReturn(userModel);
		when(catalogVersionService.getCatalogVersion(CLASSIFICATION_SYSTEM, CLASSIFICATION_VERSION))
				.thenReturn(classificationSystem);
		when(catalogVersionService.canRead(classificationSystem, userModel)).thenReturn(true);
		when(catalogVersionService.canWrite(classificationSystem, userModel)).thenReturn(true);

		when(permissionCRUDService.canReadType(anyString())).thenReturn(true);
		when(permissionCRUDService.canChangeType(anyString())).thenReturn(true);
		when(permissionCRUDService.canCreateTypeInstance(anyString())).thenReturn(true);
		when(permissionCRUDService.canReadAttribute(anyString(), anyString())).thenReturn(true);
		when(permissionCRUDService.canChangeAttribute(anyString(), anyString())).thenReturn(true);

		doNothing().when(excelTypeSystem).putRow(any(), any());
		doNothing().when(excelClassificationTypeSystem).putRow(any(), any());
		when(excelTypeSystem.findRow(any())).thenReturn(Optional.empty());
		when(excelClassificationTypeSystem.findRow(any())).thenReturn(Optional.empty());
	}

	@Test
	public void shouldPassWhenTypeAndAttributesExist()
	{
		// given
		addAttribute("Approval", "approval");
		addClassificationAttribute("electronics", "weight");

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
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER,
				EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_DESCRIPTION);

	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToReadType()
	{
		// given
		when(permissionCRUDService.canReadType(ProductModel._TYPECODE)).thenReturn(false);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER,
				EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_DESCRIPTION);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToChangeType()
	{
		// given
		when(permissionCRUDService.canChangeType(ProductModel._TYPECODE)).thenReturn(false);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER,
				EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_DESCRIPTION);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToCreatingNewInstanceOfType()
	{
		// given
		when(permissionCRUDService.canCreateTypeInstance(ProductModel._TYPECODE)).thenReturn(false);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_HEADER,
				EXCEL_IMPORT_VALIDATION_METADATA_UNKNOWN_TYPE_DESCRIPTION);
	}

	@Test
	public void shouldReturnValidationErrorWhenColumnsAreDuplicated()
	{
		// given
		addAttribute("Approval", "approval");
		addAttribute("Identifier", "identifier");
		addAttribute("Approval", "approval");

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_WORKBOOK_DUPLICATED_COLUMNS_HEADER,
				EXCEL_IMPORT_VALIDATION_WORKBOOK_DUPLICATED_COLUMNS_DESCRIPTION);
	}

	@Test
	public void shouldReturnValidationErrorWhenColumnDoesNotExist()
	{
		// given
		addAttribute("Approval", "approval");
		when(permissionCRUDService.canReadAttribute(ProductModel._TYPECODE, "approval"))
				.thenThrow(UnknownIdentifierException.class);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_HEADER,
				EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_DESCRIPTION);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToReadAttribute()
	{
		// given
		addAttribute("Approval", "approval");
		when(permissionCRUDService.canReadAttribute(ProductModel._TYPECODE, "approval")).thenReturn(false);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_HEADER,
				EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_DESCRIPTION);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionToChangeAttribute()
	{
		// given
		addAttribute("Approval", "approval");
		when(permissionCRUDService.canChangeAttribute(ProductModel._TYPECODE, "approval")).thenReturn(false);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_HEADER,
				EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_DESCRIPTION);
	}

	@Test
	public void shouldReturnValidationErrorWhenUnknownColumnExists()
	{
		// given

		attributeNames.add("Unknown column");

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_HEADER,
				EXCEL_IMPORT_VALIDATION_WORKBOOK_UNKNOWN_ATTRIBUTE_DESCRIPTION);
	}

	@Test
	public void shouldReturnValidationErrorWhenClassificationSystemDoesNotExist()
	{
		// given
		addClassificationAttribute("electronics", "weight");
		when(catalogVersionService.getCatalogVersion("SampleClassification", "1.0")).thenThrow(UnknownIdentifierException.class);
		when(excelClassificationTypeSystem.exists()).thenReturn(true);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, CLASSIFICATION_SYSTEM_ERRORS_HEADER, UNKNOWN_CLASSIFICATION_SYSTEM_VERSION);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionsToReadFromClassificationSystem()
	{
		// given
		addClassificationAttribute("electronics", "weight");
		when(catalogVersionService.canRead(classificationSystem, userModel)).thenReturn(false);
		when(excelClassificationTypeSystem.exists()).thenReturn(true);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, CLASSIFICATION_SYSTEM_ERRORS_HEADER, UNKNOWN_CLASSIFICATION_SYSTEM_VERSION);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHavePermissionsToWriteToClassificationSystem()
	{
		// given
		addClassificationAttribute("electronics", "weight");
		when(catalogVersionService.canWrite(classificationSystem, userModel)).thenReturn(false);
		when(excelClassificationTypeSystem.exists()).thenReturn(true);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, CLASSIFICATION_SYSTEM_ERRORS_HEADER, UNKNOWN_CLASSIFICATION_SYSTEM_VERSION);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHaveReadPermissionsToClassificationTypes()
	{
		// given
		when(permissionCRUDService.canReadType(ClassificationAttributeModel._TYPECODE)).thenReturn(false);
		when(excelClassificationTypeSystem.exists()).thenReturn(true);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, CLASSIFICATION_SYSTEM_ERRORS_HEADER, INSUFFICIENT_PERMISSIONS_TO_TYPE);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHaveWritePermissionsToClassificationTypes()
	{
		// given
		when(permissionCRUDService.canChangeType(ClassificationAttributeModel._TYPECODE)).thenReturn(false);
		when(excelClassificationTypeSystem.exists()).thenReturn(true);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, CLASSIFICATION_SYSTEM_ERRORS_HEADER, INSUFFICIENT_PERMISSIONS_TO_TYPE);
	}

	@Test
	public void shouldReturnValidationErrorWhenUserDoesNotHaveCreatePermissionsToClassificationTypes()
	{
		// given
		when(permissionCRUDService.canCreateTypeInstance(ClassificationAttributeModel._TYPECODE)).thenReturn(false);
		when(excelClassificationTypeSystem.exists()).thenReturn(true);

		// when
		final List<ExcelValidationResult> validationResults = validator.validate(workbook);

		// then
		assertValidationResult(validationResults, CLASSIFICATION_SYSTEM_ERRORS_HEADER, INSUFFICIENT_PERMISSIONS_TO_TYPE);
	}

	@Test
	public void shouldValidationOfClassificationNotBeLaunchedWhenThereIsNoClassificationInExcelSheet()
	{
		// given
		given(excelClassificationTypeSystem.exists()).willReturn(false);

		// when
		validator.validate(workbook);

		// then
		then(validator).should(never()).validateClassificationAttributes(any(), any());
	}

	private void addAttribute(final String name, final String qualifier)
	{
		attributeNames.add(name);
		final TypeSystemRow row = createTypeSystemRow(name, name, qualifier);
		given(excelTypeSystem.findRow(name)).willReturn(Optional.of(row));
	}

	private void addClassificationAttribute(final String clazz, final String attribute)
	{
		final String fullName = clazz + "." + attribute + " - " + CLASSIFICATION_SYSTEM + "/" + CLASSIFICATION_VERSION;
		attributeNames.add(fullName);
		final ClassificationTypeSystemRow row = createClassificationTypeSystemRow(fullName, clazz, attribute);
		given(excelClassificationTypeSystem.findRow(row.getFullName())).willReturn(Optional.of(row));
	}

	private ClassificationTypeSystemRow createClassificationTypeSystemRow(final String fullName, final String clazz,
			final String attribute)
	{
		final ClassificationTypeSystemRow row = new ClassificationTypeSystemRow();
		row.setFullName(fullName);
		row.setClassificationSystem(CLASSIFICATION_SYSTEM);
		row.setClassificationVersion(CLASSIFICATION_VERSION);
		row.setClassificationClass(clazz);
		row.setClassificationAttribute(attribute);
		return row;
	}

	private TypeSystemRow createTypeSystemRow(final String attrDisplayName, final String attrName, final String attrQualifier)
	{
		final TypeSystemRow typeSystemRow = new TypeSystemRow();
		typeSystemRow.setAttrName(attrName);
		typeSystemRow.setAttrQualifier(attrQualifier);
		typeSystemRow.setAttrDisplayName(attrDisplayName);
		return typeSystemRow;
	}

	protected void assertValidationResult(final List<ExcelValidationResult> validationResults, final String headerMessageKey,
			final String descriptionMessageKey)
	{
		assertThat(validationResults).hasSize(1);
		assertThat(validationResults.get(0).getHeader().getMessageKey()).isEqualTo(headerMessageKey);
		assertThat(validationResults.get(0).getValidationErrors()).hasSize(1);
		assertThat(validationResults.get(0).getValidationErrors().get(0).getMessageKey()).isEqualTo(descriptionMessageKey);
	}
}
