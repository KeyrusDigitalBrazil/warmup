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
package com.hybris.backoffice.excel.importing;

import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.UtilitySheet.CLASSIFICATION_TYPE_SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.importing.data.ClassificationTypeSystemRow;
import com.hybris.backoffice.excel.template.CollectionFormatter;
import com.hybris.backoffice.excel.template.cell.ExcelCellService;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelClassificationTypeSystemServiceTest
{
	@Mock
	private Workbook workbook;

	@Mock
	private Sheet classificationTypeSystemSheet;

	@Mock
	private ExcelCellService cellService;

	@Mock
	private CollectionFormatter collectionFormatter;

	@InjectMocks
	private ExcelClassificationTypeSystemService service;

	@Before
	public void setUp()
	{
		given(workbook.getSheet(CLASSIFICATION_TYPE_SYSTEM.getSheetName())).willReturn(classificationTypeSystemSheet);
		given(classificationTypeSystemSheet.getLastRowNum()).willReturn(1);
	}

	@Test
	public void shouldCreateClassificationTypeSystemRow()
	{
		// given
		final String dimensions = "Electronics.Dimensions - SampleClassification/1.0";
		mockClassificationTypeSystemRow(dimensions, "dimensions", "false", "");

		// when
		final ExcelClassificationTypeSystemService.ExcelClassificationTypeSystem excelClassificationTypeSystem = service
				.loadTypeSystem(workbook);

		// then
		assertClassificationTypeSystemRow(excelClassificationTypeSystem, dimensions, "dimensions", false, "");
	}

	@Test
	public void shouldCreateClassificationTypeSystemRowForEachLanguageOfLocalizedAttribute()
	{
		// given
		final String weightEn = "Electronics.Weight[en] - SampleClassification/1.0";
		final String weightDe = "Electronics.Weight[de] - SampleClassification/1.0";
		final String fullName = "{Electronics.Weight[en] - SampleClassification/1.0},{Electronics.Weight[de] - SampleClassification/1.0}";

		given(collectionFormatter.formatToCollection(fullName)).willReturn(Sets.newHashSet(weightEn, weightDe));
		mockClassificationTypeSystemRow(fullName, "weight", "true", "{en},{de}");

		// when
		final ExcelClassificationTypeSystemService.ExcelClassificationTypeSystem classificationTypeSystem = service
				.loadTypeSystem(workbook);

		// then
		assertClassificationTypeSystemRow(classificationTypeSystem, weightEn, "weight", true, "en");
		assertClassificationTypeSystemRow(classificationTypeSystem, weightDe, "weight", true, "de");
	}

	protected void assertClassificationTypeSystemRow(
			final ExcelClassificationTypeSystemService.ExcelClassificationTypeSystem classificationTypeSystem, final String fullName,
			final String attribute, final boolean isLocalized, final String isoCode)
	{
		final Optional<ClassificationTypeSystemRow> optionalRow = classificationTypeSystem.findRow(fullName);
		assertThat(optionalRow).isPresent();
		final ClassificationTypeSystemRow row = optionalRow.get();
		assertThat(row.getFullName()).isEqualTo(fullName);
		assertThat(row.getClassificationSystem()).isEqualTo("SampleClassification");
		assertThat(row.getClassificationVersion()).isEqualTo("1.0");
		assertThat(row.getClassificationClass()).isEqualTo("electronics");
		assertThat(row.getClassificationAttribute()).isEqualTo(attribute);
		assertThat(row.isLocalized()).isEqualTo(isLocalized);
		assertThat(row.getIsoCode()).isEqualTo(isoCode);
		assertThat(row.isMandatory()).isFalse();
	}

	protected void mockClassificationTypeSystemRow(final String fullName, final String classificationAttribute,
			final String attrLocalized, final String attrLocLang)
	{
		final Row row = mock(Row.class);
		given(classificationTypeSystemSheet.getRow(1)).willReturn(row);
		mockCell(row, 0, fullName);
		mockCell(row, 1, "SampleClassification");
		mockCell(row, 2, "1.0");
		mockCell(row, 3, "electronics");
		mockCell(row, 4, classificationAttribute);
		mockCell(row, 5, attrLocalized);
		mockCell(row, 6, attrLocLang);
		mockCell(row, 7, "false");
	}

	private void mockCell(final Row row, final int index, final String cellValue)
	{
		final Cell cell = mock(Cell.class);
		given(row.getCell(index)).willReturn(cell);
		given(cellService.getCellValue(cell)).willReturn(cellValue);
	}

}
