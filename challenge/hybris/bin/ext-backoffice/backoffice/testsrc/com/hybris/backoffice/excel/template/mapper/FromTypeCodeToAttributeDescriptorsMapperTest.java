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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FromTypeCodeToAttributeDescriptorsMapperTest extends AbstractExcelMapperTest
{

	@Mock
	private ExcelMapper<ComposedTypeModel, AttributeDescriptorModel> helperMapper;
	@Mock
	private TypeService typeService;

	private FromTypeCodeToAttributeDescriptorsMapper mapper = new FromTypeCodeToAttributeDescriptorsMapper();

	@Before
	public void setUp()
	{
		mapper.setTypeService(typeService);
		mapper.setMapper(helperMapper);
	}

	@Test
	public void shouldReturnCollectionOfAttributeDescriptors()
	{
		// given
		final String typeCode = "Product";
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		final Collection<AttributeDescriptorModel> givenAttributeDescriptors = Lists
				.newArrayList(mock(AttributeDescriptorModel.class), mock(AttributeDescriptorModel.class));
		given(typeService.getComposedTypeForCode(typeCode)).willReturn(composedTypeModel);
		given(helperMapper.apply(composedTypeModel)).willReturn(givenAttributeDescriptors);

		// when
		final Collection<AttributeDescriptorModel> returnedAttributeDescriptors = mapper.apply(typeCode);

		// then
		assertThat(returnedAttributeDescriptors).isNotNull();
		assertThat(returnedAttributeDescriptors).isEqualTo(givenAttributeDescriptors);
	}

	@Test
	public void shouldResultBeFiltered()
	{
		// given
		final String typeCode = "Product";
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);

		final AttributeDescriptorModel uniqueAttributeDescriptor = mockAttributeDescriptorUnique(true);

		final AttributeDescriptorModel nonUniqueAttributeDescriptor = mockAttributeDescriptorUnique(false);

		final Collection<AttributeDescriptorModel> givenAttributeDescriptors = Lists.newArrayList(uniqueAttributeDescriptor,
				nonUniqueAttributeDescriptor);

		given(typeService.getComposedTypeForCode(typeCode)).willReturn(composedTypeModel);
		given(helperMapper.apply(composedTypeModel)).willReturn(givenAttributeDescriptors);

		mapper.setFilters(Lists.newArrayList(getUniqueFilter()));

		// when
		final Collection<AttributeDescriptorModel> returnedAttributeDescriptors = mapper.apply(typeCode);

		// then
		assertThat(returnedAttributeDescriptors.size()).isEqualTo(1);
		assertThat(returnedAttributeDescriptors).containsOnly(uniqueAttributeDescriptor);
		assertThat(returnedAttributeDescriptors).doesNotContain(nonUniqueAttributeDescriptor);
	}

}
