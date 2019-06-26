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

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_CLONE_MODEL;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_LOCALE;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.AbstractMediaContainerComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.CloneComponentContextProvider;
import de.hybris.platform.cmsfacades.media.service.CMSMediaFormatService;
import de.hybris.platform.cmsfacades.mediacontainers.MediaContainerFacade;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CloneComponentMediaContainerDataToAttributeContentConverterTest
{
	private static final String MEDIA_CODE_1 = "media-code1";
	private static final String MEDIA_CODE_2 = "media-code2";
	private static final String MEDIA_FORMAT_1 = "media-format1";
	private static final String MEDIA_FORMAT_2 = "media-format2";
	private static final String UNKNOWN_MEDIA_CODE = "unknownMediaCode";

	@Mock
	private CMSMediaFormatService mediaFormatService;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private MediaContainerFacade mediaContainerFacade;
	@Mock
	private CloneComponentContextProvider cloneComponentContextProvider;
	@Mock
	private ModelService modelService;

	@InjectMocks
	private CloneComponentMediaContainerDataToAttributeContentConverter converter;

	@Mock
	private MediaModel media1;
	@Mock
	private MediaModel media2;
	@Mock
	private MediaFormatModel mediaFormat1;
	@Mock
	private MediaFormatModel mediaFormat2;
	@Mock
	private AbstractMediaContainerComponentModel cloneComponentModel;
	@Mock
	private MediaContainerModel mediaContainerModel;

	private Map<String, String> source;

	@Before
	public void setup()
	{
		when(mediaFormatService.getMediaFormatsByComponentType(AbstractMediaContainerComponentModel.class))
				.thenReturn(asList(mediaFormat1, mediaFormat2));
		when(mediaFormat1.getQualifier()).thenReturn(MEDIA_FORMAT_1);
		when(mediaFormat2.getQualifier()).thenReturn(MEDIA_FORMAT_2);
		when(media1.getMediaFormat()).thenReturn(mediaFormat1);
		when(media2.getMediaFormat()).thenReturn(mediaFormat2);

		source = new HashMap<String, String>();
		source.put(MEDIA_FORMAT_1, MEDIA_CODE_1);
		source.put(MEDIA_FORMAT_2, MEDIA_CODE_2);

		when(uniqueItemIdentifierService.getItemModel(MEDIA_CODE_1, MediaModel.class)).thenReturn(ofNullable(media1));
		when(uniqueItemIdentifierService.getItemModel(MEDIA_CODE_2, MediaModel.class)).thenReturn(ofNullable(media2));
		when(uniqueItemIdentifierService.getItemModel(UNKNOWN_MEDIA_CODE, MediaModel.class)).thenReturn(empty());

		final Map<String, Object> srcComponentMap = new HashMap<>();
		final HashMap srcMedia = new HashMap<String, String>();
		srcMedia.put(MEDIA_FORMAT_1, MEDIA_CODE_1);
		srcMedia.put(MEDIA_FORMAT_2, MEDIA_CODE_2);
		srcComponentMap.put(ENGLISH.toLanguageTag(), srcMedia);

		when(cloneComponentContextProvider.findItemForKey(SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE)).thenReturn(srcComponentMap);
		when(cloneComponentContextProvider.findItemForKey(SESSION_CLONE_COMPONENT_CLONE_MODEL)).thenReturn(cloneComponentModel);
		when(cloneComponentContextProvider.findItemForKey(SESSION_CLONE_COMPONENT_LOCALE)).thenReturn(ENGLISH.toLanguageTag());

		when(cloneComponentModel.getMedia(ENGLISH)).thenReturn(mediaContainerModel);
		when(mediaContainerModel.getMedias()).thenReturn(Arrays.asList(media1, media2));
	}

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void whenCloneComponentEqualsToSourceComponentReturnAllMedia()
	{
		final MediaContainerModel convert = converter.convert(source);

		assertThat(convert.getMedias(), Matchers.containsInAnyOrder(media1, media2));
	}

	@Test
	public void whenCloneComponentGreaterThanSourceComponentReturnAllMedia()
	{
		final Map<String, Object> srcComponentMap = new HashMap<>();
		final HashMap srcMedia = new HashMap<String, String>();
		srcMedia.put(MEDIA_FORMAT_1, MEDIA_CODE_1);
		srcComponentMap.put("en", srcMedia);

		when(cloneComponentContextProvider.findItemForKey(SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE)).thenReturn(srcComponentMap);

		final MediaContainerModel convert = converter.convert(source);

		assertThat(convert.getMedias(), Matchers.containsInAnyOrder(media1, media2));
	}

	@Test
	public void whenCloneComponentLessThanSourceComponentReturnLessMedia()
	{
		source.remove(MEDIA_FORMAT_2);

		converter.convert(source);

		verify(modelService).detach(media2);
	}

	@Test(expected = ConversionException.class)
	public void whenConvertingInValidMapWillThrowException()
	{
		source.put(MEDIA_FORMAT_2, UNKNOWN_MEDIA_CODE);

		converter.convert(source);
	}
}
