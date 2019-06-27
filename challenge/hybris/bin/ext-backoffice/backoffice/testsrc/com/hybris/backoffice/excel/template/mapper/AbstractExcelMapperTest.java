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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import com.hybris.backoffice.excel.template.filter.ExcelFilter;


class AbstractExcelMapperTest
{

	AttributeDescriptorModel mockAttributeDescriptorUnique(final boolean unique)
	{
		final AttributeDescriptorModel uniqueAttributeDescriptor = mock(AttributeDescriptorModel.class);
		given(uniqueAttributeDescriptor.getUnique()).willReturn(unique);
		return uniqueAttributeDescriptor;
	}

	AttributeDescriptorModel mockAttributeDescriptorLocalized(final boolean localized)
	{
		final AttributeDescriptorModel uniqueAttributeDescriptor = mock(AttributeDescriptorModel.class);
		given(uniqueAttributeDescriptor.getLocalized()).willReturn(localized);
		return uniqueAttributeDescriptor;
	}

	ExcelFilter<AttributeDescriptorModel> getUniqueFilter()
	{
		return AttributeDescriptorModel::getUnique;
	}

}
