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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.ObjectFactory;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMediaModelUniqueIdentifierConverterTest
{

	private static final String MEDIA_CODE = "media-code";
	private static final java.lang.String CATALOG_ID = "catalog-id";
	private static final java.lang.String CATALOG_VERSION = "catalog-version-id";

	@Mock	
	private Converter<MediaModel, ItemData> mediaModelItemDataConverter;

	@InjectMocks
	private DefaultMediaModelUniqueIdentifierConverter conversionFunction;
	
	@Mock
	private ObjectFactory<ItemData> itemDataDataFactory;

	private ItemData itemData = new ItemData();
	@Mock
	private MediaModel mediaModel;
	@Mock
	private CatalogVersionModel catalogVersion;
	@Mock
	private CatalogModel catalog;

	@Before
	public void setup()
	{
		when(mediaModel.getCode()).thenReturn(MEDIA_CODE);
		when(mediaModel.getCatalogVersion()).thenReturn(catalogVersion);
		when(catalogVersion.getCatalog()).thenReturn(catalog);
		when(catalog.getId()).thenReturn(CATALOG_ID);
		when(catalogVersion.getVersion()).thenReturn(CATALOG_VERSION);
		itemData.setItemId(MEDIA_CODE);
		itemData.setItemType(MediaModel._TYPECODE);
		when(mediaModelItemDataConverter.convert(Mockito.any())).thenReturn(itemData);
		when(itemDataDataFactory.getObject()).thenReturn(new ItemData());
	}
	@Test
	public void testConverValidMediaModel()
	{
		final ItemData itemData = conversionFunction.convert(mediaModel);
		assertThat(itemData.getItemId(), notNullValue());
		assertThat(itemData.getItemType(), is(MediaModel._TYPECODE));
		assertThat(itemData.getName(), is(MEDIA_CODE));
	}

}
