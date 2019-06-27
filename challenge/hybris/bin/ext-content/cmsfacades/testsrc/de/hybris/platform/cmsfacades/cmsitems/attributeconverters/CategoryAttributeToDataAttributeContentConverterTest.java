/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.cmsitems.attributeconverters;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CategoryAttributeToDataAttributeContentConverterTest
{
	private static final String UUID = "uuid";
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@InjectMocks
	private final UniqueIdentifierAttributeToDataContentConverter<CategoryModel> converter = new UniqueIdentifierAttributeToDataContentConverter<CategoryModel>()
	{
		// Intentionally left empty.
	};

	@Mock
	private CategoryModel category;

	private ItemData itemData;

	@Before
	public void setup()
	{
		itemData = new ItemData();
		itemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(category)).thenReturn(Optional.of(itemData));
	}

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void whenConvertingValidCategoryShouldReturnItsUuid()
	{
		assertThat(converter.convert(category), is(UUID));
	}

}
