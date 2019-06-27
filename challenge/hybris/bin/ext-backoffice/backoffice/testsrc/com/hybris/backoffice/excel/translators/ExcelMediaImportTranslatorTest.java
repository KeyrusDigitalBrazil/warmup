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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.hybris.backoffice.BackofficeTestUtil;
import com.hybris.backoffice.excel.data.Impex;
import com.hybris.backoffice.excel.data.ImpexForType;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;


@RunWith(MockitoJUnitRunner.class)
public class ExcelMediaImportTranslatorTest
{
	private static final String GENERATED_CODE = "generatedCode";

	@InjectMocks
	@Spy
	private ExcelMediaImportTranslator translator;
	@Mock
	private TypeService typeService;
	@Mock
	private CatalogTypeService catalogTypeService;
	@Mock
	private KeyGenerator mediaCodeGenerator;
	@Mock
	private MediaFolderModel mediaFolder;
	@Mock
	private ExcelFilter<AttributeDescriptorModel> mandatoryFilter;

	@Before
	public void setUp()
	{
		when(catalogTypeService.getCatalogVersionContainerAttribute(MediaModel._TYPECODE)).thenReturn(MediaModel.CATALOGVERSION);
		when(typeService.isAssignableFrom(MediaModel._TYPECODE, MediaModel._TYPECODE)).thenReturn(true);
		when(translator.generateMediaRefId(any(), any())).thenReturn(GENERATED_CODE);
		when(mediaCodeGenerator.generate()).thenReturn(GENERATED_CODE);
	}

	@Test
	public void shouldImportMediaWithGeneratedCode()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_FILE_PATH, "path");
		params.put(CatalogVersion.CATALOG, "default");
		params.put(CatalogVersion.VERSION, "staged");
		params.put(ExcelMediaImportTranslator.PARAM_FOLDER, "folder");
		final AttributeDescriptorModel attrDesc = BackofficeTestUtil.mockAttributeDescriptor(MediaModel._TYPECODE);

		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, "b", "c", "d",
				Lists.newArrayList(params));
		final Impex impex = translator.importData(attrDesc, importParameters);
		final ImpexForType mediaImpex = impex.findUpdates(MediaModel._TYPECODE);

		assertThat(mediaImpex).isNotNull();
		assertThat(mediaImpex.getImpexTable().row(0)).isNotNull();
		assertThat(mediaImpex.getImpexTable().row(0).keySet()).hasSize(5);
		assertThat(mediaImpex.getImpexTable().row(0).keySet()).containsOnly(
				translator.createMediaReferenceIdHeader(attrDesc, params), translator.createMediaCodeHeader(attrDesc, params),
				translator.createMediaCatalogVersionHeader(attrDesc, params), translator.createMediaContentHeader(attrDesc, params),
				translator.createMediaFolderHeader(attrDesc, params));
		assertThat(mediaImpex.getImpexTable().row(0).get(translator.createMediaCodeHeader(attrDesc, params)))
				.isEqualTo(GENERATED_CODE);

		final ImpexForType productImpex = impex.findUpdates(ProductModel._TYPECODE);
		assertThat(productImpex).isNotNull();
		assertThat(productImpex.getImpexTable().get(0, translator.createReferenceHeader(attrDesc))).isEqualTo(GENERATED_CODE);
	}

	@Test
	public void shouldNotImportContentIfFilePathIsEmpty()
	{
		final Map<String, String> params = new HashMap<>();
		params.put(ExcelMediaImportTranslator.PARAM_CODE, "theCode");
		params.put(CatalogVersion.CATALOG, "default");
		params.put(CatalogVersion.VERSION, "staged");
		params.put(ExcelMediaImportTranslator.PARAM_FOLDER, "folder");

		final AttributeDescriptorModel attrDesc = BackofficeTestUtil.mockAttributeDescriptor(MediaModel._TYPECODE);

		final ImportParameters importParameters = new ImportParameters("a", "b", "c", "d", Lists.newArrayList(params));
		final Impex impex = translator.importData(attrDesc, importParameters);
		final ImpexForType mediaImpex = impex.findUpdates(MediaModel._TYPECODE);

		assertThat(mediaImpex).isNotNull();
		assertThat(mediaImpex.getImpexTable().row(0)).isNotNull();
		assertThat(mediaImpex.getImpexTable().row(0).keySet()).hasSize(4);
		assertThat(mediaImpex.getImpexTable().row(0).keySet()).containsOnly(
				translator.createMediaReferenceIdHeader(attrDesc, params), translator.createMediaCodeHeader(attrDesc, params),
				translator.createMediaCatalogVersionHeader(attrDesc, params), translator.createMediaFolderHeader(attrDesc, params));
		assertThat(mediaImpex.getImpexTable().get(0, translator.createMediaCodeHeader(attrDesc, params))).isEqualTo("theCode");
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
		when(media.getCatalogVersion()).thenReturn(cv);
		when(media.getFolder()).thenReturn(mediaFolder);
		when(mediaFolder.getQualifier()).thenReturn("folder");

		assertThat(translator.exportData(media).isPresent()).isTrue();
		assertThat(translator.exportData(media).get()).isEqualTo(":theCode:default:staged:folder");
	}

	@Test
	public void shouldGivenTypeBeHandled()
	{
		final AttributeDescriptorModel attributeDescriptor = BackofficeTestUtil.mockAttributeDescriptor(MediaModel._TYPECODE);

		assertThat(translator.canHandle(attributeDescriptor)).isTrue();
	}

	@Test
	public void shouldNotHandleOtherTypes()
	{
		final AttributeDescriptorModel attributeDescriptor = BackofficeTestUtil.mockAttributeDescriptor(ProductModel._TYPECODE);

		assertThat(translator.canHandle(attributeDescriptor)).isFalse();
	}

}
