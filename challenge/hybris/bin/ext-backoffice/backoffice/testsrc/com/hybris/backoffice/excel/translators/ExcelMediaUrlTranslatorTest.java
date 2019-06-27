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

import com.hybris.backoffice.BackofficeTestUtil;


@RunWith(MockitoJUnitRunner.class)
public class ExcelMediaUrlTranslatorTest
{
	private static final String MEDIA_TYPE_CODE = "MediaChild";

	@InjectMocks
	@Spy
	private ExcelMediaUrlTranslator translator;
	@Mock
	private TypeService typeService;

	@Before
	public void setUp()
	{
		when(typeService.isAssignableFrom(MediaModel._TYPECODE, MEDIA_TYPE_CODE)).thenReturn(true);
	}

	@Test
	public void shouldExportDataBeNullSafe()
	{
		assertThat(translator.exportData(null).isPresent()).isFalse();
	}

	@Test
	public void shouldExportedDataBeInProperFormat()
	{
		final CatalogVersionModel cv = mock(CatalogVersionModel.class);
		final CatalogModel catalog = mock(CatalogModel.class);
		when(catalog.getId()).thenReturn("default");
		when(cv.getVersion()).thenReturn("staged");
		when(cv.getCatalog()).thenReturn(catalog);

		final MediaModel media = mock(MediaModel.class);
		when(media.getCode()).thenReturn("theCode");
		when(media.getDownloadURL()).thenReturn("https://a.host/catalog/media.ext");
		when(media.getCatalogVersion()).thenReturn(cv);
		final MediaFolderModel mediaFolder = mock(MediaFolderModel.class);
		doReturn(mediaFolder).when(media).getFolder();
		doReturn("folder").when(mediaFolder).getQualifier();

		assertThat(translator.exportData(media).isPresent()).isTrue();
		assertThat(translator.exportData(media).get())
				.isEqualTo(":theCode:default:staged:folder:\"https://a.host/catalog/media.ext\"");
	}

	@Test
	public void shouldReturnReferenceFormat()
	{
		assertThat(translator.referenceFormat(null)).isEqualTo("filePath:code:catalog:version:folder:url");
	}

	@Test
	public void shouldGivenTypeBeHandled()
	{
		final AttributeDescriptorModel attributeDescriptor = BackofficeTestUtil.mockAttributeDescriptor(MEDIA_TYPE_CODE);

		assertThat(translator.canHandle(attributeDescriptor)).isTrue();
	}
}
