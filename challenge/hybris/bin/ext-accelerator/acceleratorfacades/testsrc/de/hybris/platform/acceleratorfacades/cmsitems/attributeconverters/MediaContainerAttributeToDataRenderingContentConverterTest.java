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
package de.hybris.platform.acceleratorfacades.cmsitems.attributeconverters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaContainerAttributeToDataRenderingContentConverterTest
{
	private static final String MEDIA_CODE = "media-code";
	private static final String MEDIA_FORMAT = "media-format";

	@Spy
	@InjectMocks
	private MediaContainerAttributeToDataRenderingContentConverter converter;

	@Mock
	private Converter<MediaModel, MediaData> mediaModelConverter;
	@Mock
	private PermissionCRUDService permissionCRUDService;

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
		when(permissionCRUDService.canReadType(MediaModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadType(MediaFormatModel._TYPECODE)).thenReturn(true);
	}

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		final Map<String, MediaData> map = converter.convert(null);

		assertThat(map, nullValue());
	}

	@Test
	public void whenConvertingValidContainerModelShouldReturnValidMap()
	{
		final MediaData mediaData = new MediaData();
		mediaData.setCode(MEDIA_CODE);
		when(mediaModelConverter.convert(media)).thenReturn(mediaData);

		final Map<String, MediaData> map = converter.convert(source);

		assertThat(map.keySet(), not(empty()));
		assertThat(map.keySet(), hasItem(MEDIA_FORMAT));
		assertThat(map.get(MEDIA_FORMAT), is(mediaData));

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
