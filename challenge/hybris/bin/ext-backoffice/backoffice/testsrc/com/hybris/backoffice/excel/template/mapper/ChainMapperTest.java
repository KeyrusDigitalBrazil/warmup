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
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;


@RunWith(MockitoJUnitRunner.class)
public class ChainMapperTest extends AbstractExcelMapperTest
{
	@Mock
	private ExcelMapper<String, AttributeDescriptorModel> mapper1;
	@Mock
	private ExcelMapper<Collection<AttributeDescriptorModel>, ExcelAttributeDescriptorAttribute> mapper2;

	private ChainMapper<String, ExcelAttributeDescriptorAttribute> chainMapper = new ChainMapper<>();

	@Before
	public void setUp()
	{
		chainMapper.setMapper1(mapper1);
		chainMapper.setMapper2(mapper2);
	}

	@Test
	public void shouldReturnCollectionOfExcelAttributes()
	{
		// given
		final String input = "Product";

		final Collection<AttributeDescriptorModel> attributeDescriptors = Lists.newArrayList(mock(AttributeDescriptorModel.class),
				mock(AttributeDescriptorModel.class));
		given(mapper1.apply(input)).willReturn(attributeDescriptors);

		final ExcelAttributeDescriptorAttribute excelAttribute1 = mock(ExcelAttributeDescriptorAttribute.class);
		final ExcelAttributeDescriptorAttribute excelAttribute2 = mock(ExcelAttributeDescriptorAttribute.class);

		given(mapper2.apply(attributeDescriptors)).willReturn(Lists.newArrayList(excelAttribute1, excelAttribute2));

		// when
		final Collection<ExcelAttributeDescriptorAttribute> returnedAttributes = chainMapper.apply(input);

		// then
		assertThat(returnedAttributes.size()).isEqualTo(2);
		assertThat(returnedAttributes).containsOnly(excelAttribute1, excelAttribute2);
	}

	@Test
	public void shouldMapper1BeFilteredByFilters1()
	{
		// given
		final String input = "Product";

		final AttributeDescriptorModel uniqueAttributeDescriptor = mockAttributeDescriptorUnique(true);
		final AttributeDescriptorModel nonUniqueAttributeDescriptor = mockAttributeDescriptorUnique(false);
		final Collection<AttributeDescriptorModel> attributeDescriptors = Lists.newArrayList(uniqueAttributeDescriptor,
				nonUniqueAttributeDescriptor);
		given(mapper1.apply(input)).willReturn(attributeDescriptors);
		chainMapper.setFilters1(Lists.newArrayList(getUniqueFilter()));

		final ExcelAttributeDescriptorAttribute excelAttribute1 = mock(ExcelAttributeDescriptorAttribute.class);
		final ExcelAttributeDescriptorAttribute excelAttribute2 = mock(ExcelAttributeDescriptorAttribute.class);

		given(mapper2.apply(Lists.newArrayList(uniqueAttributeDescriptor)))
				.willReturn(Lists.newArrayList(excelAttribute1, excelAttribute2));

		// when
		final Collection<ExcelAttributeDescriptorAttribute> returnedAttributes = chainMapper.apply(input);

		// then
		assertThat(returnedAttributes.size()).isEqualTo(2);
		assertThat(returnedAttributes).containsOnly(excelAttribute1, excelAttribute2);
	}

	@Test
	public void shouldMapper2BeFilteredByFilters2()
	{
		// given
		final String input = "Product";

		final Collection<AttributeDescriptorModel> attributeDescriptors = Lists.newArrayList(mock(AttributeDescriptorModel.class),
				mock(AttributeDescriptorModel.class));
		given(mapper1.apply(input)).willReturn(attributeDescriptors);

		final String name1 = "name1";
		final String name2 = "name2";
		final ExcelAttributeDescriptorAttribute excelAttribute1 = mockExcelAttribute(name1);
		final ExcelAttributeDescriptorAttribute excelAttribute2 = mockExcelAttribute(name2);

		given(mapper2.apply(attributeDescriptors)).willReturn(Lists.newArrayList(excelAttribute1, excelAttribute2));

		final ExcelFilter<ExcelAttributeDescriptorAttribute> excelFilter = attr -> StringUtils.equals(attr.getName(), name2);
		chainMapper.setFilters2(Lists.newArrayList(excelFilter));

		// when
		final Collection<ExcelAttributeDescriptorAttribute> returnedAttributes = chainMapper.apply(input);

		// then
		assertThat(returnedAttributes.size()).isEqualTo(1);
		assertThat(returnedAttributes).containsOnly(excelAttribute2);
	}

	protected ExcelAttributeDescriptorAttribute mockExcelAttribute(final String name)
	{
		final ExcelAttributeDescriptorAttribute excelAttribute = mock(ExcelAttributeDescriptorAttribute.class);
		given(excelAttribute.getName()).willReturn(name);
		return excelAttribute;
	}

}
