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
package com.hybris.backoffice.excel.template.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttribute;
import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.data.SelectedAttribute;
import com.hybris.backoffice.excel.data.SelectedAttributeQualifier;
import com.hybris.backoffice.excel.template.AttributeNameFormatter;
import com.hybris.backoffice.excel.template.CollectionFormatter;
import com.hybris.backoffice.excel.template.ExcelTemplateConstants;
import com.hybris.backoffice.excel.template.cell.ExcelCellService;
import com.hybris.backoffice.excel.template.sheet.ExcelSheetService;
import com.hybris.backoffice.excel.translators.ExcelTranslatorRegistry;
import com.hybris.backoffice.excel.translators.ExcelValueTranslator;


@RunWith(MockitoJUnitRunner.class)
public class DefaultExcelHeaderServiceTest
{

	@Mock
	private ExcelCellService cellService;
	@Mock
	private ExcelSheetService sheetService;
	@Mock
	private AttributeNameFormatter<ExcelAttributeDescriptorAttribute> attributeNameFormatter;
	@Mock
	private CollectionFormatter collectionFormatter;
	@Mock
	private ExcelTranslatorRegistry registry;
	@Mock
	private TypeService typeService;

	@Spy
	@InjectMocks
	private DefaultExcelHeaderService headerService;

	@Test
	public void testGetHeaders()
	{
		// when-then
		testGetHeadersAndGetSelectedAttributesQualifiers((typeSheet, data) -> {
			// when
			final Collection<SelectedAttribute> selectedAttributes = headerService.getHeaders(null, typeSheet);

			// then
			assertThat(selectedAttributes.size()).isEqualTo(data.size());
			data.forEach(triple -> {
				final List<SelectedAttribute> list = new ArrayList<>(selectedAttributes);
				assertThat(list.get(triple.getMiddle()).getReferenceFormat()).isEqualTo(triple.getRight());
				assertThat(list.get(triple.getMiddle()).getAttributeDescriptor().getQualifier()).isEqualTo(triple.getLeft());
			});
		});
	}

	@Test
	public void testGetSelectedAttributesQualifiers()
	{
		// when-then
		testGetHeadersAndGetSelectedAttributesQualifiers((typeSheet, data) -> {
			// when
			final Collection<SelectedAttributeQualifier> selectedAttributeQualifiers = headerService
					.getSelectedAttributesQualifiers(null, typeSheet);

			// then
			assertThat(selectedAttributeQualifiers.size()).isEqualTo(data.size());
			data.forEach(triple -> {
				final List<SelectedAttributeQualifier> list = new ArrayList<>(selectedAttributeQualifiers);
				assertThat(list.get(triple.getMiddle()).getName()).isEqualTo(triple.getLeft());
				assertThat(list.get(triple.getMiddle()).getQualifier()).isEqualTo(triple.getLeft());
			});
		});
	}

	protected void testGetHeadersAndGetSelectedAttributesQualifiers(
			final BiConsumer<Sheet, Collection<Triple<String, Integer, String>>> whenThen)
	{
		// given
		final Collection<Triple<String, Integer, String>> data = Lists.newArrayList(//
				ImmutableTriple.of(ProductModel.CATALOGVERSION, 0,
						String.format("%s:%s", CatalogVersionModel.CATALOG, CatalogVersionModel.VERSION)), //
				ImmutableTriple.of(ProductModel.APPROVALSTATUS, 1, StringUtils.EMPTY), //
				ImmutableTriple.of(ProductModel.CODE, 2, StringUtils.EMPTY), //
				ImmutableTriple.of(ProductModel.ORDER, 3, StringUtils.EMPTY) //
		);

		final Row headerRow = prepareHeaderRow( //
				data.stream().map(triple -> ImmutablePair.of(triple.getMiddle(), triple.getLeft())).collect(Collectors.toList()) //
		);

		data.stream() //
				.map(pair -> prepareTypeSystemRow(pair.getLeft(), pair.getRight())) //
				.forEach(pair -> prepareReferenceFormat(pair.getLeft(), pair.getMiddle(), pair.getRight()));

		final Sheet typeSheet = mock(Sheet.class);
		final Row valueRow = mock(Row.class);
		given(typeSheet.getRow(ExcelTemplateConstants.Header.DISPLAY_NAME.getIndex())).willReturn(headerRow);
		given(typeSheet.getLastRowNum()).willReturn(data.size());
		given(typeSheet.getRow(ExcelTemplateConstants.Header.DEFAULT_VALUE.getIndex())).willReturn(valueRow);

		// when - then
		whenThen.accept(typeSheet, data);
	}

	@Test
	public void testHeaderValueWithoutMetadata()
	{
		// given
		final String qualifier = "xxx";
		final String uniqueAttr = String.format("%s%s", qualifier, ExcelTemplateConstants.SpecialMark.UNIQUE.getMark());
		final String mandatoryAttr = String.format("%s%s", qualifier, ExcelTemplateConstants.SpecialMark.MANDATORY.getMark());

		// except
		Lists.newArrayList(qualifier, uniqueAttr, mandatoryAttr) //
				.stream() //
				.map(headerService::getHeaderValueWithoutSpecialMarks) //
				.forEach(attr -> assertThat(attr).isEqualTo(qualifier));
	}

	@Test
	public void shouldNullBeReturnedWhenAttrIsNotLocalized()
	{
		// given
		given(cellService.getCellValue(any())).willReturn(String.valueOf(false));

		// when
		final String isoCode = headerService.loadIsoCode(mock(Row.class), null);

		// then
		assertThat(isoCode).isNull();
	}

	@Test
	public void shouldCorrectIsoCodeBeReturnedWhenAttrIsLocalized()
	{
		// given
		final String isoCode = "en";
		final String header = String.format("qualifier[%s]", isoCode);
		given(cellService.getCellValue(any())).willReturn(String.valueOf(true));

		// when
		final String returnedIsoCode = headerService.loadIsoCode(mock(Row.class), header);

		// then
		assertThat(returnedIsoCode).isEqualTo(isoCode);
	}

	@Test
	public void testInsertAttributesHeader()
	{
		// given
		final Collection<ExcelAttribute> excelAttributes = mockExcelAttributes(5);

		// when
		headerService.insertAttributesHeader(null, excelAttributes);

		// then
		verify(headerService, times(excelAttributes.size())).insertAttributeHeader(any(), any(), anyInt());
	}

	@Test
	public void testInsertAttributeHeader()
	{
		// given
		final String headerValue = "Article Number*^";
		final String patternValue = "some formula";
		final ExcelAttributeDescriptorAttribute excelAttribute = mock(ExcelAttributeDescriptorAttribute.class);
		final Sheet sheet = mock(Sheet.class);
		final Row headerRow = mock(Row.class);
		final Row patternRow = mock(Row.class);
		final Cell headerCell = mock(Cell.class);
		final Cell patternCell = mock(Cell.class);
		final short firstCellNum = Integer.valueOf(1).shortValue();

		given(attributeNameFormatter.format(any())).willReturn(headerValue);
		given(sheet.getRow(ExcelTemplateConstants.Header.DISPLAY_NAME.getIndex())).willReturn(headerRow);
		given(sheet.getRow(ExcelTemplateConstants.Header.REFERENCE_PATTERN.getIndex())).willReturn(patternRow);
		given(headerRow.getFirstCellNum()).willReturn(firstCellNum);
		given(headerRow.createCell(firstCellNum)).willReturn(headerCell);
		given(patternRow.getFirstCellNum()).willReturn(firstCellNum);
		given(patternRow.getCell(firstCellNum)).willReturn(patternCell);
		given(patternCell.getCellFormula()).willReturn(patternValue);

		// when
		headerService.insertAttributeHeader(sheet, excelAttribute, 0);

		// then
		verify(cellService).insertAttributeValue(eq(headerCell), argThat(new ArgumentMatcher<String>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o.equals(headerValue);
			}
		}));
		verify(patternCell).setCellFormula(argThat(new ArgumentMatcher<String>()
		{
			@Override
			public boolean matches(final Object o)
			{
				return o.equals(patternValue);
			}
		}));
	}

	@Test
	public void shouldGetHeadersNames()
	{
		// given
		final Sheet sheet = mock(Sheet.class);
		final Row headerRow = prepareHeaderRow("Approval", "Catalog version*^", "Identifier[en]", "");
		when(sheet.getRow(0)).thenReturn(headerRow);

		// when
		final Collection<String> attributeNames = headerService.getHeaderDisplayNames(sheet);

		// then
		Assertions.assertThat(attributeNames).containsExactly("Approval", "Catalog version*^", "Identifier[en]");
	}

	@Test
	public void shouldMetadataBeRemovedFromHeaderValue()
	{
		// given
		final String articleNumber = "Article Number";
		final String headerValue = articleNumber + ExcelTemplateConstants.SpecialMark.MANDATORY.getMark()
				+ ExcelTemplateConstants.SpecialMark.UNIQUE.getMark();

		// when
		final String output = headerService.getHeaderValueWithoutSpecialMarks(headerValue);

		// then
		assertThat(output).isEqualTo(articleNumber);
	}

	protected Row prepareHeaderRow(final String... attributes)
	{
		final Row row = mock(Row.class);
		for (int index = 0; index < attributes.length; index++)
		{
			final Cell cell = mock(Cell.class);
			given(row.getCell(index)).willReturn(cell);
			given(cellService.getCellValue(cell)).willReturn(attributes[index]);
		}

		given(row.getLastCellNum()).willReturn((short) (attributes.length - 1));
		return row;
	}

	protected Row prepareHeaderRow(final Collection<Pair<Integer, String>> index_value)
	{
		final Row row = mock(Row.class);
		index_value.forEach(iv -> {
			final Cell cell = mock(Cell.class);
			given(row.getCell(iv.getKey())).willReturn(cell);
			given(cellService.getCellValue(cell)).willReturn(iv.getValue());
		});

		final short lastIndex = new Integer(new ArrayList<>(index_value).get(index_value.size() - 1).getKey() + 1).shortValue();
		given(row.getLastCellNum()).willReturn(lastIndex);
		return row;
	}


	protected Triple<Row, String, String> prepareTypeSystemRow(final String headerValue, final String referenceFormat)
	{
		final Row row = mock(Row.class);
		final Cell cell = mock(Cell.class);
		doReturn(Optional.of(row)).when(headerService).findTypeSystemRowForGivenHeader(any(), any(), eq(headerValue));
		given(row.getCell(ExcelTemplateConstants.TypeSystem.ATTR_QUALIFIER.getIndex())).willReturn(cell);
		given(cellService.getCellValue(cell)).willReturn(headerValue);
		return ImmutableTriple.of(row, headerValue, referenceFormat);
	}

	protected void prepareReferenceFormat(final Row row, final String qualifier, final String referenceFormat)
	{
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		given(attributeDescriptor.getQualifier()).willReturn(qualifier);
		doReturn(attributeDescriptor).when(headerService).loadAttributeDescriptor(eq(row), any());
		final ExcelValueTranslator translator = mock(ExcelValueTranslator.class);
		given(translator.referenceFormat(attributeDescriptor)).willReturn(referenceFormat);
		given(registry.getTranslator(attributeDescriptor)).willReturn(Optional.of(translator));
	}

	protected Collection<ExcelAttribute> mockExcelAttributes(final int size)
	{
		return IntStream.range(0, size).mapToObj(idx -> {
			final ExcelAttribute excelAttribute = mock(ExcelAttribute.class);
			doNothing().when(headerService).insertAttributeHeader(null, excelAttribute, idx);
			return excelAttribute;
		}).collect(Collectors.toList());
	}

}
