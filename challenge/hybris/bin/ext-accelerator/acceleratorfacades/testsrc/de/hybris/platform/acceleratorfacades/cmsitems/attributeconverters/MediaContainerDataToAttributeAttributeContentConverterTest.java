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

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.AbstractMediaContainerComponentModel;
import de.hybris.platform.cms2.exceptions.TypePermissionException;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFormatService;
import de.hybris.platform.cmsfacades.mediacontainers.MediaContainerFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.security.permissions.PermissionsConstants;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MediaContainerDataToAttributeAttributeContentConverterTest
{
	private static final String UNKNOWN_MEDIA_CODE = "unknownMediaCode";
	private static final String MEDIA_CODE_1 = "media-code1";
	private static final String MEDIA_CODE_2 = "media-code2";

	private static final String MEDIA_FORMAT_1 = "media-format1";
	private static final String MEDIA_FORMAT_2 = "media-format2";
	@Mock
	private CMSMediaFormatService mediaFormatService;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private PermissionCRUDService permissionCRUDService;

	@Spy
	@InjectMocks
	private MediaContainerDataToAttributeContentConverter converter;

	private Map<String, String> source;

	@Mock
	private MediaModel media1;
	@Mock
	private MediaModel media2;

	@Mock
	private MediaFormatModel mediaFormat1;

	@Mock
	private MediaFormatModel mediaFormat2;

	@Mock
	private MediaContainerFacade mediaContainerFacade;

	@Before
	public void setup()
	{

		when(mediaFormatService.getMediaFormatsByComponentType(AbstractMediaContainerComponentModel.class))
				.thenReturn(asList(mediaFormat1, mediaFormat2));
		when(mediaFormat1.getQualifier()).thenReturn(MEDIA_FORMAT_1);
		when(mediaFormat2.getQualifier()).thenReturn(MEDIA_FORMAT_2);

		source = new HashMap<String, String>();
		source.put(MEDIA_FORMAT_1, MEDIA_CODE_1);
		source.put(MEDIA_FORMAT_2, MEDIA_CODE_2);

		when(uniqueItemIdentifierService.getItemModel(MEDIA_CODE_1, MediaModel.class)).thenReturn(ofNullable(media1));
		when(uniqueItemIdentifierService.getItemModel(MEDIA_CODE_2, MediaModel.class)).thenReturn(ofNullable(media2));
		when(uniqueItemIdentifierService.getItemModel(UNKNOWN_MEDIA_CODE, MediaModel.class)).thenReturn(empty());

		when(mediaContainerFacade.createMediaContainer()).thenReturn(new MediaContainerModel());

		when(permissionCRUDService.canChangeType(MediaModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadType(MediaFormatModel._TYPECODE)).thenReturn(true);
		when(permissionCRUDService.canReadType(MediaModel._TYPECODE)).thenReturn(true);
	}

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void whenConvertingValidMapWillReturnConverterModel()
	{
		final MediaContainerModel mediaContainer = converter.convert(source);
		assertThat(mediaContainer.getMedias(), Matchers.containsInAnyOrder(media1, media2));

		verify(media1, times(1)).setMediaFormat(mediaFormat1);
		verify(media2, times(1)).setMediaFormat(mediaFormat2);
	}

	@Test(expected = ConversionException.class)
	public void whenConvertingInValidMapWillThrowException()
	{
		source.put(MEDIA_FORMAT_2, UNKNOWN_MEDIA_CODE);

		converter.convert(source);
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
