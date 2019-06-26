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
package de.hybris.platform.acceleratorfacades.cmsitems.attributeconverters;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaContainerAttributeToDataAttributeContentConverterTest
{
	private static final String MEDIA_CODE = "media-code";
	private static final String MEDIA_FORMAT = "media-format";
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@Spy
	@InjectMocks
	private MediaContainerAttributeToDataContentConverter converter;

	@Mock
	private MediaContainerModel source;

	@Mock
	private MediaModel media;

	@Mock
	private MediaFormatModel mediaFormat;

	@Before
	public void setup()
	{
		when(media.getCode()).thenReturn(MEDIA_CODE);
		when(media.getMediaFormat()).thenReturn(mediaFormat);
		when(source.getMedias()).thenReturn(Arrays.asList(media));
		when(mediaFormat.getQualifier()).thenReturn(MEDIA_FORMAT);

		final ItemData itemData = new ItemData();
		itemData.setItemId(MEDIA_CODE);
		when(uniqueItemIdentifierService.getItemData(media)).thenReturn(Optional.of(itemData));

		when(permissionCRUDService.canReadType(MediaModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadType(MediaFormatModel._TYPECODE)).thenReturn(true);
	}

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void whenConvertingValidContainerModelShouldReturnValidMap()
	{
		final Map<String, String> map = converter.convert(source);
		assertThat(Integer.valueOf(map.size()), is(Integer.valueOf(1)));
		assertThat(map.get(MEDIA_FORMAT), is(MEDIA_CODE));
	}

	@Test(expected = TypePermissionException.class)
	public void whenNoReadTypePermissionForMediaFormatModelWillThrowTypePermissionException()
	{
		//GIVEN
		when(permissionCRUDService.canReadType(MediaFormatModel._TYPECODE)).thenReturn(false);
		doThrow(new TypePermissionException("exception")).when(converter).throwTypePermissionException(PermissionsConstants.READ,
				MediaFormatModel._TYPECODE);

		//THEN
		converter.convert(source);
	}
}
