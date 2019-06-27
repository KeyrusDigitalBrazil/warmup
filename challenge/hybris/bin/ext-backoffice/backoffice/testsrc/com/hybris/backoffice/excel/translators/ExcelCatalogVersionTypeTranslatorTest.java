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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.catalog.CatalogTypeService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ImpexValue;
import com.hybris.backoffice.excel.data.ImportParameters;
import com.hybris.backoffice.excel.template.filter.ExcelFilter;


@RunWith(MockitoJUnitRunner.class)
public class ExcelCatalogVersionTypeTranslatorTest
{

	@Mock
	private ExcelFilter<AttributeDescriptorModel> uniqueFilter;

	@Mock
	private ExcelFilter<AttributeDescriptorModel> mandatoryFilter;

	@Mock
	private TypeService typeService;

	@Mock
	public CatalogTypeService catalogTypeService;

	@InjectMocks
	private ExcelCatalogVersionTypeTranslator translator;

	@Before
	public void setUp()
	{
		doAnswer(inv -> ((AttributeDescriptorModel) inv.getArguments()[0]).getUnique()).when(uniqueFilter).test(any());
		when(catalogTypeService.getCatalogVersionContainerAttribute(any())).thenReturn(ProductModel.CATALOGVERSION);
	}

	@Test
	public void shouldExportDataBeNullSafe()
	{
		// expect
		assertThat(translator.exportData(null).isPresent()).isTrue();
	}

	@Test
	public void shouldExportedDataBeInProperFormat()
	{
		// given
		final String id = "defaultcatalog";
		final String version = "Staged";

		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final CatalogModel catalog = mock(CatalogModel.class);
		given(catalog.getId()).willReturn(id);
		given(catalogVersion.getVersion()).willReturn(version);
		given(catalogVersion.getCatalog()).willReturn(catalog);

		// when
		final String output = translator.exportData(catalogVersion).map(String.class::cast).get();

		// then
		assertThat(output).isEqualTo(String.format("%s:%s", id, version));
	}

	@Test
	public void shouldImportCatalogVersion()
	{
		// given
		final AttributeDescriptorModel attributeDescriptor = mock(AttributeDescriptorModel.class);
		given(attributeDescriptor.getQualifier()).willReturn(ProductModel.CATALOGVERSION);
		final List<Map<String, String>> parameters = new ArrayList<>();
		final Map<String, String> catalogVersionParams = new HashMap<>();
		catalogVersionParams.put(CatalogVersionModel.CATALOG, "Clothing");
		catalogVersionParams.put(CatalogVersionModel.VERSION, "Online");
		parameters.add(catalogVersionParams);
		final ImportParameters importParameters = new ImportParameters(ProductModel._TYPECODE, null, null,
				UUID.randomUUID().toString(), parameters);

		// when
		final ImpexValue impexValue = translator.importValue(attributeDescriptor, importParameters);

		// then
		assertThat(impexValue.getValue()).isEqualTo("Online:Clothing");
		assertThat(impexValue.getHeaderValue().getName()).isEqualTo("catalogVersion(version,catalog(id))");
	}
}
