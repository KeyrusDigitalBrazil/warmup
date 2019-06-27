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
package com.hybris.backoffice.excel.template.populator.typesheet;

import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TYPE_SYSTEM;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_DISPLAYED_NAME;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_LOCALIZED;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_LOC_LANG;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_NAME;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_OPTIONAL;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_QUALIFIER;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_TYPE_CODE;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_TYPE_ITEMTYPE;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.ATTR_UNIQUE;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.REFERENCE_FORMAT;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.TYPE_CODE;
import static com.hybris.backoffice.excel.template.ExcelTemplateConstants.TypeSystem.TYPE_NAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.cell.DefaultExcelCellService;
import com.hybris.backoffice.excel.template.mapper.ExcelMapper;


@RunWith(MockitoJUnitRunner.class)
public class TypeSystemSheetPopulatorTest
{
	@Spy
	DefaultExcelCellService excelCellService;
	@Mock
	ExcelMapper<ExcelExportResult, AttributeDescriptorModel> mapper;
	@Mock
	TypeSystemRowFactory mockedTypeSystemRowFactory;
	@InjectMocks
	TypeSystemSheetPopulator populator;

	@Test
	public void shouldPopulateRowsWithAttributeDescriptorsData()
	{
		// given
		final Sheet typeSystemSheet = mock(Sheet.class);
		final Map<Integer, Cell> row = createRowMock(typeSystemSheet, 1);
		final Workbook workbook = mock(Workbook.class);
		given(workbook.getSheet(TYPE_SYSTEM)).willReturn(typeSystemSheet);

		final AttributeDescriptorModel attributeDescriptorModel = mock(AttributeDescriptorModel.class);
		given(attributeDescriptorModel.getQualifier()).willReturn("product");

		final TypeSystemRow typeSystemRow = createTypeSystemRow();
		given(mockedTypeSystemRowFactory.create(attributeDescriptorModel)).willReturn(typeSystemRow);


		// when
		populator.populate(workbook.getSheet(TYPE_SYSTEM), Collections.singletonList(attributeDescriptorModel));

		// then
		verify(excelCellService).insertAttributeValue(row.get(TYPE_CODE.getIndex()), "typeCode");
		verify(excelCellService).insertAttributeValue(row.get(TYPE_NAME.getIndex()), "typeName");
		verify(excelCellService).insertAttributeValue(row.get(ATTR_QUALIFIER.getIndex()), "attrQualifier");
		verify(excelCellService).insertAttributeValue(row.get(ATTR_NAME.getIndex()), "attrName");
		verify(excelCellService).insertAttributeValue(row.get(ATTR_TYPE_CODE.getIndex()), "attrTypeCode");
		verify(excelCellService).insertAttributeValue(row.get(ATTR_TYPE_ITEMTYPE.getIndex()), "attrTypeItemType");
		verify(excelCellService).insertAttributeValue(row.get(ATTR_LOC_LANG.getIndex()), "attrLocLang");
		verify(excelCellService).insertAttributeValue(row.get(ATTR_DISPLAYED_NAME.getIndex()), "attrDisplayName");
		verify(excelCellService).insertAttributeValue(row.get(REFERENCE_FORMAT.getIndex()), "attrReferenceFormat");
		verify(excelCellService).insertAttributeValue(row.get(ATTR_OPTIONAL.getIndex()), true);
		verify(excelCellService).insertAttributeValue(row.get(ATTR_LOCALIZED.getIndex()), true);
		verify(excelCellService).insertAttributeValue(row.get(ATTR_UNIQUE.getIndex()), true);
	}

	private Map<Integer, Cell> createRowMock(final Sheet sheet, final int rowIndex)
	{
		final Row row = mock(Row.class);
		final Function<Integer, Cell> cellProducer = index -> {
			final Cell cell = mock(Cell.class);
			given(row.createCell(index)).willReturn(cell);
			return cell;
		};
		given(sheet.createRow(rowIndex)).willReturn(row);
		return IntStream.rangeClosed(TYPE_CODE.getIndex(), REFERENCE_FORMAT.getIndex()) //
				.boxed() //
				.collect(Collectors.toMap(Function.identity(), cellProducer));
	}

	private static TypeSystemRow createTypeSystemRow()
	{
		final TypeSystemRow typeSystemRow = new TypeSystemRow();
		typeSystemRow.setTypeCode("typeCode");
		typeSystemRow.setTypeName("typeName");
		typeSystemRow.setAttrQualifier("attrQualifier");
		typeSystemRow.setAttrName("attrName");
		typeSystemRow.setAttrOptional(true);
		typeSystemRow.setAttrTypeCode("attrTypeCode");
		typeSystemRow.setAttrTypeItemType("attrTypeItemType");
		typeSystemRow.setAttrLocalized(true);
		typeSystemRow.setAttrLocLang("attrLocLang");
		typeSystemRow.setAttrDisplayName("attrDisplayName");
		typeSystemRow.setAttrUnique(true);
		typeSystemRow.setAttrReferenceFormat("attrReferenceFormat");
		return typeSystemRow;
	}
}
