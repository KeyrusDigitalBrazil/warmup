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

import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns.FULL_NAME;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants.ClassificationTypeSystemColumns;
import com.hybris.backoffice.excel.template.cell.DefaultExcelCellService;
import com.hybris.backoffice.excel.template.cell.ExcelCellService;


@RunWith(MockitoJUnitRunner.class)
public class ClassificationTypeSystemSheetPopulatorTest
{
	private static final String CLASSIFICATION_ATTRIBUTE_FULL = "classificationAttributeFull";

	@Mock
	ClassificationTypeSystemSheetCompressor mockedCompressor;
	private final ExcelCellService excelCellService = new DefaultExcelCellService();
	@InjectMocks
	ClassificationTypeSystemSheetPopulator populator;

	@Before
	public void setUp()
	{
		populator.setExcelCellService(excelCellService);
	}

	@Test
	public void shouldPopulateClassificationTypeSystemSheetWithAttributes() throws IOException
	{
		// given
		final Set<ExcelAttribute> classAttributeAssignmentModels = asSet(createClassificationAttributeMock());
		populator.setCellValuePopulators(
				new EnumMap<ClassificationTypeSystemColumns, ExcelClassificationCellPopulator>(ClassificationTypeSystemColumns.class)
				{
					{
						put(FULL_NAME, ignored -> CLASSIFICATION_ATTRIBUTE_FULL);
					}
				});
		final Collection<Map<ClassificationTypeSystemColumns, String>> rows = Collections
				.singletonList(new EnumMap<ClassificationTypeSystemColumns, String>(ClassificationTypeSystemColumns.class)
				{
					{
						put(FULL_NAME, CLASSIFICATION_ATTRIBUTE_FULL);
					}
				});
		given(mockedCompressor.compress(rows)).willReturn(rows);

		try (final Workbook workbook = new XSSFWorkbook())
		{
			// when
			populator.populate(createExcelExportResultWithAvailableAttributes(workbook, classAttributeAssignmentModels));

			// then
			final Sheet classificationTypeSystemSheet = workbook
					.getSheet(ExcelTemplateConstants.UtilitySheet.CLASSIFICATION_TYPE_SYSTEM.getSheetName());
			assertThat(classificationTypeSystemSheet).isNotNull();

			final Row firstRow = classificationTypeSystemSheet.getRow(0);
			assertThat(firstRow).isNull();

			final Row secondRow = classificationTypeSystemSheet.getRow(1);
			assertThat(getCellValue(secondRow, FULL_NAME)).isEqualTo(CLASSIFICATION_ATTRIBUTE_FULL);

			final Row thirdRow = classificationTypeSystemSheet.getRow(2);
			assertThat(thirdRow).isNull();
		}
	}

	private ExcelClassificationAttribute createClassificationAttributeMock()
	{
		final ClassificationSystemVersionModel classificationSystemVersionModel = mock(ClassificationSystemVersionModel.class);
		given(classificationSystemVersionModel.getVersion()).willReturn("classificationSystemVersion");
		given(classificationSystemVersionModel.getCategorySystemID()).willReturn("classificationSystemId");

		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		given(classificationClassModel.getName(Locale.ENGLISH)).willReturn("classificationClassEnglish");
		given(classificationClassModel.getName(Locale.GERMAN)).willReturn("classificationClassGerman");

		final ClassificationAttributeModel classificationAttributeModel = mock(ClassificationAttributeModel.class);
		given(classificationAttributeModel.getName(Locale.ENGLISH)).willReturn("classificationAttributeNameEnglish");
		given(classificationAttributeModel.getName(Locale.GERMAN)).willReturn("classificationAttributeNameGerman");
		given(classificationAttributeModel.getSystemVersion()).willReturn(classificationSystemVersionModel);

		final ClassAttributeAssignmentModel classAttributeAssignmentModel = mock(ClassAttributeAssignmentModel.class);
		given(classAttributeAssignmentModel.getClassificationAttribute()).willReturn(classificationAttributeModel);
		given(classAttributeAssignmentModel.getClassificationClass()).willReturn(classificationClassModel);
		given(classAttributeAssignmentModel.getSystemVersion()).willReturn(classificationSystemVersionModel);
		given(classAttributeAssignmentModel.getLocalized()).willReturn(false);
		given(classAttributeAssignmentModel.getMandatory()).willReturn(true);

		final ExcelClassificationAttribute attribute = new ExcelClassificationAttribute();
		attribute.setAttributeAssignment(classAttributeAssignmentModel);
		return attribute;
	}

	private ExcelExportResult createExcelExportResultWithAvailableAttributes(final Workbook workbook,
			final Collection<ExcelAttribute> attributes)
	{
		return new ExcelExportResult(workbook, null, null, null, attributes);
	}

	private String getCellValue(final Row row, final ClassificationTypeSystemColumns column)
	{
		return row.getCell(column.getIndex()).getStringCellValue();
	}

	@SafeVarargs
	private static <T> Set<T> asSet(final T... elements)
	{
		return new HashSet<>(Arrays.asList(elements));
	}
}
