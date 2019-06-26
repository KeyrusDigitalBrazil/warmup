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
package com.hybris.backoffice.excel.template;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;
import com.hybris.backoffice.excel.data.ExcelAttributeDescriptorAttribute;
import com.hybris.backoffice.excel.template.populator.DefaultExcelAttributeContext;


@RunWith(MockitoJUnitRunner.class)
public class DisplayNameAttributeNameFormatterTest
{

	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private TypeService typeService;
	@Spy
	private DisplayNameAttributeNameFormatter formatter = new DisplayNameAttributeNameFormatter();

	@Before
	public void setUp()
	{
		formatter.setCommonI18NService(commonI18NService);
		formatter.setTypeService(typeService);
	}

	@Test
	public void shouldReturnCorrectNameForLocalizedAttribute()
	{
		// given
		final String attributeName = "Description";
		final String isoCode = "pl";
		final AttributeDescriptorModel attributeDescriptorModel = mockAttributeDescriptor(false, false, attributeName, isoCode);
		final ExcelAttributeDescriptorAttribute attribute = new ExcelAttributeDescriptorAttribute(attributeDescriptorModel,
				isoCode);

		// when
		final String returnedName = formatter.format(DefaultExcelAttributeContext.ofExcelAttribute(attribute));

		// then
		assertThat(String.format("%s[%s]", attributeName, isoCode)).isEqualTo(returnedName);
	}

	@Test
	public void shouldDisplayNameBeBasedOnQualifierWhenAttributesNameIsNull()
	{
		// given
		final String attributeName = "Order";
		final AttributeDescriptorModel attributeDescriptorModel = mockAttributeDescriptor(false, false, attributeName, null);
		given(attributeDescriptorModel.getQualifier()).willReturn(attributeName);
		final ExcelAttributeDescriptorAttribute attribute = new ExcelAttributeDescriptorAttribute(attributeDescriptorModel);

		// when
		final String returnedName = formatter.format(DefaultExcelAttributeContext.ofExcelAttribute(attribute));

		// then
		assertThat(attributeName).isEqualTo(returnedName);
	}

	private AttributeDescriptorModel mockAttributeDescriptor(final boolean mandatory, final boolean unique, final String name,
			final String isoCode)
	{
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		final ComposedTypeModel composedTypeModel = mock(ComposedTypeModel.class);
		given(attributeDescriptor.getName()).willReturn(name);
		given(attributeDescriptor.getEnclosingType()).willReturn(composedTypeModel);
		given(typeService.getAttributeDescriptorsForType(composedTypeModel)).willReturn(Sets.newHashSet(attributeDescriptor));
		given(attributeDescriptor.getLocalized()).willReturn(isoCode != null);
		given(attributeDescriptor.getUnique()).willReturn(unique);
		given(attributeDescriptor.getOptional()).willReturn(!mandatory);
		return attributeDescriptor;
	}

}
