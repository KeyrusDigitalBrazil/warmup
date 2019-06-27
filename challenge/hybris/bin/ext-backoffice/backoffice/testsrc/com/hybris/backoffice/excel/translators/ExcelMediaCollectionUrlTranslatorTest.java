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
package com.hybris.backoffice.excel.translators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.backoffice.BackofficeTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ExcelMediaCollectionUrlTranslatorTest
{
	private static final String MEDIA_TYPE_CODE = "MediaChild";

	@InjectMocks
	@Spy
	private ExcelMediaCollectionUrlTranslator translator;
	@Mock
	private TypeService typeService;

	@Before
	public void setUp()
	{
		when(typeService.isAssignableFrom(MEDIA_TYPE_CODE, MediaModel._TYPECODE)).thenReturn(true);
	}

	@Test
	public void shouldExportDataBeNullSafe()
	{
		assertThat(translator.exportData(null).isPresent()).isFalse();
	}

	@Test
	public void shouldExportedDataBeInProperFormat()
	{
		final MediaModel media1 = creteMediaModelMock("theCode1", "folder1", "https://a.host/catalog/media1.ext");
		final MediaModel media2 = creteMediaModelMock("theCode2", "folder2", "a.host/catalog/media1.ext");
		final MediaModel media3 = creteMediaModelMock("theCode3", "folder3", "https://a.host/catalog/media3.ext");

		assertThat(translator.exportData(Lists.newArrayList(media1, media2)).isPresent()).isTrue();

		final String media1Res = ":theCode1:default:staged:folder1:\"https://a.host/catalog/media1.ext\"";
		final String media2Res = ":theCode2:default:staged:folder2:";
		final String media3Res = ":theCode3:default:staged:folder3:\"https://a.host/catalog/media3.ext\"";

		assertThat(translator.exportData(Lists.newArrayList(media1, media2, media3)).get())
				.isEqualTo(media1Res + "," + media2Res + "," + media3Res);
	}

	private MediaModel creteMediaModelMock(final String code, final String folder, final String URL)
	{
		final MediaModel media = mock(MediaModel.class);
		doReturn(code).when(media).getCode();
		doReturn(URL).when(media).getDownloadURL();
		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		final CatalogModel catalog = mock(CatalogModel.class);
		when(catalog.getId()).thenReturn("default");
		when(cv.getVersion()).thenReturn("staged");
		when(cv.getCatalog()).thenReturn(catalog);

		doReturn(cv).when(media).getCatalogVersion();
		final MediaFolderModel mediaFolder = mock(MediaFolderModel.class);
		doReturn(mediaFolder).when(media).getFolder();
		doReturn(folder).when(mediaFolder).getQualifier();
		return media;
	}

	@Test
	public void shouldReturnReferenceFormat()
	{
		assertThat(translator.referenceFormat(null)).isEqualTo("filePath:code:catalog:version:folder:url");
	}

	@Test
	public void shouldGivenTypeBeHandled()
	{
		final AttributeDescriptorModel attributeDescriptor = BackofficeTestUtil
				.mockCollectionTypeAttributeDescriptor(MEDIA_TYPE_CODE);

		assertThat(translator.canHandle(attributeDescriptor)).isTrue();
	}

}
