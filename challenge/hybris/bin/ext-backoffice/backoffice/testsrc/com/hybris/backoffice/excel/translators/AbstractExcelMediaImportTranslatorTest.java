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
import static org.mockito.Mockito.verify;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.backoffice.excel.data.ImpexHeaderValue;


@RunWith(MockitoJUnitRunner.class)
public class AbstractExcelMediaImportTranslatorTest
{
	@InjectMocks
	@Spy
	private ExcelMediaImportTranslator translator;

	@Test
	public void shouldCreateMediaFolderHeader()
	{
		// given
		final AttributeDescriptorModel descriptor = BackofficeTestUtil.mockAttributeDescriptor(MediaModel._TYPECODE);
		final Map<String, String> params = new HashMap<>();

		// when
		final ImpexHeaderValue header = translator.createMediaFolderHeader(descriptor, params);

		// then
		assertThat(header.getName()).isEqualTo("folder(qualifier)");
	}

	@Test
	public void shouldHaveImportData()
	{
		// given
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_CODE, "code");
		params.put(ExcelMediaImportTranslator.PARAM_FILE_PATH, "filePath");
		params.put(CatalogVersionModel.CATALOG, "catalog");
		params.put(CatalogVersionModel.VERSION, "version");
		params.put(ExcelMediaImportTranslator.PARAM_FOLDER, "folder");

		// when
		final boolean hasImportData = translator.hasImportData(params);

		// then
		assertThat(hasImportData).isTrue();
	}

	@Test
	public void shouldNotHaveImportDataWhenFolderIsNotDefined()
	{
		// given
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_CODE, "code");
		params.put(ExcelMediaImportTranslator.PARAM_FILE_PATH, "filePath");
		params.put(CatalogVersionModel.CATALOG, "catalog");
		params.put(CatalogVersionModel.VERSION, "version");

		// when
		final boolean hasImportData = translator.hasImportData(params);

		// then
		assertThat(hasImportData).isTrue();
	}

	@Test
	public void shouldNotHaveImportData()
	{
		// given
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_CODE, "code");
		params.put(ExcelMediaImportTranslator.PARAM_FILE_PATH, "filePath");
		params.put(ExcelMediaImportTranslator.PARAM_FOLDER, "folder");

		// when
		final boolean hasImportData = translator.hasImportData(params);

		// then
		assertThat(hasImportData).isFalse();
	}

	@Test
	public void shouldCreateMediaRow() {
		// given
		final AttributeDescriptorModel attributeDescriptor = BackofficeTestUtil.mockAttributeDescriptor("desc");
		final Map<String, String> params = mock(Map.class);
		final String mediaRefId = "mediaRefId";
		final ImpexHeaderValue mediaReferenceIdHeader = mock(ImpexHeaderValue.class);
		doReturn(mediaReferenceIdHeader).when(translator).createMediaReferenceIdHeader(attributeDescriptor, params);
		final ImpexHeaderValue mediaCodeHeader = mock(ImpexHeaderValue.class);
		doReturn(mediaCodeHeader).when(translator).createMediaCodeHeader(attributeDescriptor, params);
		doReturn("Code").when(translator).getCode(attributeDescriptor, params);
		final ImpexHeaderValue mediaCatalogVersionHeader = mock(ImpexHeaderValue.class);
		doReturn(mediaCatalogVersionHeader).when(translator).createMediaCatalogVersionHeader(attributeDescriptor, params);
		doReturn("catalogVersionData").when(translator).catalogVersionData(params);
		doReturn(null).when(translator).getFolder(attributeDescriptor, params);
		doReturn("").when(translator).getFilePath(attributeDescriptor, params);

		// when
		final Map<ImpexHeaderValue, Object> mediaRow = translator.createMediaRow(attributeDescriptor, mediaRefId, params);

		// then
		verify(translator, Mockito.times(0)).createMediaFolderHeader(attributeDescriptor, params);
		verify(translator, Mockito.times(0)).createMediaContentHeader(attributeDescriptor, params);

		assertThat(mediaRow).containsEntry(mediaReferenceIdHeader, mediaRefId);
		assertThat(mediaRow).containsEntry(mediaCodeHeader, "Code");
	}

}
