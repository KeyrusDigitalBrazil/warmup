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
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaAttributeToDataAttributeContentConverterTest
{
	private static final String MEDIA_CODE = "media-code";

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@InjectMocks
	private final UniqueIdentifierAttributeToDataContentConverter<MediaModel> converter = new UniqueIdentifierAttributeToDataContentConverter<MediaModel>()
	{
		// Intentionally left empty.
	};


	@Mock
	private MediaModel media;

	@Before
	public void setup()
	{
		when(media.getCode()).thenReturn(MEDIA_CODE);

		final ItemData itemData = new ItemData();
		itemData.setItemId(MEDIA_CODE);
		when(uniqueItemIdentifierService.getItemData(media)).thenReturn(Optional.of(itemData));
	}

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void whenConvertingValidContainerModelShouldReturnValidMap()
	{
		final String mediaCode = converter.convert(media);
		assertThat(mediaCode, is(MEDIA_CODE));
	}

}
