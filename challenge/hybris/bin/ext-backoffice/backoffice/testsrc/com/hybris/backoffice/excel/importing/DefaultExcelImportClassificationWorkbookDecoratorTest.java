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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.classification.ExcelClassificationAttributeFactory;
import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelClassificationAttribute;
import com.hybris.backoffice.excel.importing.data.ClassificationTypeSystemRow;
import com.hybris.backoffice.excel.template.CollectionFormatter;
import com.hybris.backoffice.excel.template.cell.ExcelCellService;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelImportClassificationWorkbookDecoratorTest
{
	private static final String DIMENSIONS_FULLNAME = "Electronical Goods.Dimensions - SampleClassification/1.0";
	private static final String WEIGHT_FULL_NAME = "Electronical Goods.Weight[en] - SampleClassification/1.0";

	@Mock
	private Workbook workbook;

	@Mock
	private Sheet productSheet;

	@Mock
	private ExcelCellService excelCellService;

	@Mock
	private ClassificationSystemService classificationSystemService;

	@Mock
	private ExcelClassificationTypeSystemService excelClassificationTypeSystemService;

	@Mock
	private ExcelClassificationAttributeFactory excelClassificationAttributeFactory;

	@Mock
	private CollectionFormatter collectionFormatter;

	@InjectMocks
	private DefaultExcelImportClassificationWorkbookDecorator defaultExcelImportClassificationWorkbookDecorator;

	@Mock
	private ExcelClassificationTypeSystemService.ExcelClassificationTypeSystem classificationTypeSystem;

	@Before
	public void setup()
	{
		given(productSheet.getWorkbook()).willReturn(workbook);
		given(excelClassificationTypeSystemService.loadTypeSystem(workbook)).willReturn(classificationTypeSystem);

		final ClassificationTypeSystemRow firstTypeSystemRow = new ClassificationTypeSystemRow();
		firstTypeSystemRow.setFullName("Electronical Goods.Dimensions - SampleClassification/1.0");
		firstTypeSystemRow.setClassificationSystem("SampleClassification");
		firstTypeSystemRow.setClassificationVersion("1.0");
		firstTypeSystemRow.setClassificationClass("electronics");
		firstTypeSystemRow.setClassificationAttribute("dimensions");
		firstTypeSystemRow.setLocalized(false);
		firstTypeSystemRow.setIsoCode("");
		firstTypeSystemRow.setMandatory(true);

		given(classificationTypeSystem.findRow(firstTypeSystemRow.getFullName())).willReturn(Optional.of(firstTypeSystemRow));

		final ClassificationTypeSystemRow secondTypeSystemRow = new ClassificationTypeSystemRow();
		secondTypeSystemRow.setFullName("Electronical Goods.Weight[en] - SampleClassification/1.0");
		secondTypeSystemRow.setClassificationSystem("SampleClassification");
		secondTypeSystemRow.setClassificationVersion("1.0");
		secondTypeSystemRow.setClassificationClass("electronics");
		secondTypeSystemRow.setClassificationAttribute("weight");
		secondTypeSystemRow.setLocalized(true);
		secondTypeSystemRow.setIsoCode("en");
		secondTypeSystemRow.setMandatory(false);
		given(classificationTypeSystem.findRow(secondTypeSystemRow.getFullName())).willReturn(Optional.of(secondTypeSystemRow));


		final Matcher<String> matcher = Matchers.not(Matchers.anyOf(Matchers.equalTo(secondTypeSystemRow.getFullName()),
				Matchers.equalTo(firstTypeSystemRow.getFullName())));

		given(classificationTypeSystem.findRow(org.mockito.Matchers.argThat(matcher))).willReturn(Optional.empty());
	}

	private void mockCell(final Row row, final int index, final String cellValue)
	{
		final Cell cell = mock(Cell.class);
		given(row.getCell(index)).willReturn(cell);
		given(excelCellService.getCellValue(cell)).willReturn(cellValue);
	}

	@Test
	public void shouldFindClassificationColumnsInSheet()
	{
		// given
		final Row headerRow = mock(Row.class);
		given(productSheet.getRow(0)).willReturn(headerRow);
		given(headerRow.getFirstCellNum()).willReturn((short) 0);
		given(headerRow.getLastCellNum()).willReturn((short) 3);
		mockCell(headerRow, 0, "Article Number*^");
		mockCell(headerRow, 1, "Catalog version*^");
		mockCell(headerRow, 2, DIMENSIONS_FULLNAME);
		mockCell(headerRow, 3, WEIGHT_FULL_NAME);

		final ClassificationSystemVersionModel systemVersionModel = mock(ClassificationSystemVersionModel.class);
		given(classificationSystemService.getSystemVersion("SampleClassification", "1.0")).willReturn(systemVersionModel);
		final ClassificationClassModel classificationClassModel = mock(ClassificationClassModel.class);
		given(classificationSystemService.getClassForCode(systemVersionModel, "electronics")).willReturn(classificationClassModel);

		final ClassAttributeAssignmentModel dimensionsAssignment = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeModel dimensionsAttribute = mock(ClassificationAttributeModel.class);
		final ClassAttributeAssignmentModel weightAssignment = mock(ClassAttributeAssignmentModel.class);
		final ClassificationAttributeModel weightAttribute = mock(ClassificationAttributeModel.class);
		given(classificationClassModel.getDeclaredClassificationAttributeAssignments())
				.willReturn(Arrays.asList(dimensionsAssignment, weightAssignment));
		given(dimensionsAssignment.getClassificationAttribute()).willReturn(dimensionsAttribute);
		given(weightAssignment.getClassificationAttribute()).willReturn(weightAttribute);

		given(dimensionsAttribute.getCode()).willReturn("dimensions");
		given(weightAttribute.getCode()).willReturn("weight");

		given(excelClassificationAttributeFactory.create(dimensionsAssignment, ""))
				.willReturn(createExcelAttributeWithAssignment(dimensionsAssignment));
		given(excelClassificationAttributeFactory.create(weightAssignment, "en"))
				.willReturn(createExcelAttributeWithAssignment(weightAssignment));

		given(excelClassificationTypeSystemService.loadTypeSystem(workbook)).willReturn(classificationTypeSystem);

		// when
		final Collection<ExcelAttribute> classificationAttributes = defaultExcelImportClassificationWorkbookDecorator
				.getExcelAttributes(productSheet);

		// then
		assertThat(classificationAttributes).hasSize(2);
		assertThat(classificationAttributes).onProperty("attributeAssignment").contains(dimensionsAssignment, weightAssignment);
	}

	private ExcelClassificationAttribute createExcelAttributeWithAssignment(final ClassAttributeAssignmentModel assignmentModel)
	{
		final ExcelClassificationAttribute attribute = new ExcelClassificationAttribute();
		attribute.setAttributeAssignment(assignmentModel);
		return attribute;
	}
}
