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
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Collection;

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;


@RunWith(MockitoJUnitRunner.class)
public class FromComposedTypeToAttributeDescriptorsMapperTest extends AbstractExcelMapperTest
{

	@Mock
	private TypeService typeService;
	private FromComposedTypeToAttributeDescriptorsMapper mapper = new FromComposedTypeToAttributeDescriptorsMapper();

	@Before
	public void setUp()
	{
		mapper.setTypeService(typeService);
	}

	@Test
	public void shouldFilterReturnedCollection()
	{
		// given
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);

		final AttributeDescriptorModel uniqueAttributeDescriptor = mockAttributeDescriptorUnique(true);
		final AttributeDescriptorModel nonUniqueAttributeDescriptor = mockAttributeDescriptorUnique(false);

		given(typeService.getAttributeDescriptorsForType(composedTypeModel))
				.willReturn(Sets.newHashSet(uniqueAttributeDescriptor, nonUniqueAttributeDescriptor));

		mapper.setFilters(Lists.newArrayList(getUniqueFilter()));

		// when
		final Collection<AttributeDescriptorModel> returnedAttributeDescriptors = mapper.apply(composedTypeModel);

		// then
		assertThat(returnedAttributeDescriptors.size()).isEqualTo(1);
		assertThat(returnedAttributeDescriptors).containsOnly(uniqueAttributeDescriptor);
		assertThat(returnedAttributeDescriptors).doesNotContain(nonUniqueAttributeDescriptor);
	}
}
