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
package com.hybris.backoffice.excel.template.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Collection;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.template.sheet.ExcelSheetService;


@RunWith(MockitoJUnitRunner.class)
public class FromExcelResultToAttributeDescriptorsMapperTest extends AbstractExcelMapperTest
{

	@Mock
	private ExcelMapper<String, AttributeDescriptorModel> helperMapper;
	@Mock
	private ExcelSheetService sheetService;
	private FromExcelResultToAttributeDescriptorsMapper mapper = new FromExcelResultToAttributeDescriptorsMapper();

	@Before
	public void setUp()
	{
		mapper.setMapper(helperMapper);
		mapper.setExcelSheetService(sheetService);
	}

	@Test
	public void shouldReturnCollectionOfAttributeDescriptors()
	{
		// given
		final String sheet1 = "sheet1";
		final String sheet2 = "sheet2";
		final Workbook workbook = mockWorkbook(sheet1, sheet2);
		doAnswer(invocationOnMock -> invocationOnMock.getArguments()[1]).when(sheetService).findTypeCodeForSheetName(any(), any());
		given(helperMapper.apply(sheet1)).willReturn(Lists.newArrayList(mock(AttributeDescriptorModel.class)));
		given(helperMapper.apply(sheet2))
				.willReturn(Lists.newArrayList(mock(AttributeDescriptorModel.class), mock(AttributeDescriptorModel.class)));

		// when
		final Collection<AttributeDescriptorModel> attributeDescriptors = mapper.apply(new ExcelExportResult(workbook));

		// then
		assertThat(attributeDescriptors.size()).isEqualTo(3);
	}

	@Test
	public void shouldReturnedCollectionBeFiltered()
	{
		// given
		final String sheet1 = "sheet1";
		final String sheet2 = "sheet2";
		final Workbook workbook = mockWorkbook(sheet1, sheet2);

		doAnswer(invocationOnMock -> invocationOnMock.getArguments()[1]).when(sheetService).findTypeCodeForSheetName(any(), any());

		final AttributeDescriptorModel uniqueAttributeDescriptor1 = mockAttributeDescriptorUnique(true);
		given(helperMapper.apply(sheet1)).willReturn(Lists.newArrayList(uniqueAttributeDescriptor1));
		final AttributeDescriptorModel uniqueAttributeDescriptor2 = mockAttributeDescriptorUnique(true);
		final AttributeDescriptorModel nonUniqueAttributeDescriptor = mockAttributeDescriptorUnique(false);
		given(helperMapper.apply(sheet2)).willReturn(Lists.newArrayList(uniqueAttributeDescriptor2, nonUniqueAttributeDescriptor));

		mapper.setFilters(Lists.newArrayList(getUniqueFilter()));

		// when
		final Collection<AttributeDescriptorModel> attributeDescriptors = mapper.apply(new ExcelExportResult(workbook));

		// then
		assertThat(attributeDescriptors.size()).isEqualTo(2);
		assertThat(attributeDescriptors).containsOnly(uniqueAttributeDescriptor1, uniqueAttributeDescriptor2);
		assertThat(attributeDescriptors).doesNotContain(nonUniqueAttributeDescriptor);

	}

	protected Workbook mockWorkbook(final String... sheetNames)
	{
		final Workbook workbook = mock(Workbook.class);

		IntStream.range(0, sheetNames.length).forEach(idx -> given(workbook.getSheetName(idx)).willReturn(sheetNames[idx]));
		given(workbook.getNumberOfSheets()).willReturn(sheetNames.length);

		return workbook;
	}

}
