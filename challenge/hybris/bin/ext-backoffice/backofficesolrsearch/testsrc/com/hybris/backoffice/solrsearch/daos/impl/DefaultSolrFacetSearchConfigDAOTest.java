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
package com.hybris.backoffice.solrsearch.daos.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.enums.SolrServerModes;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrServerConfigModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.hybris.backoffice.solrsearch.model.BackofficeIndexedTypeToSolrFacetSearchConfigModel;


public class DefaultSolrFacetSearchConfigDAOTest extends ServicelayerTransactionalTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Resource
	private DefaultSolrFacetSearchConfigDAO defaultSolrFacetSearchConfigDAO;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
	}

	@Test
	public void testFindSearchConfigForType() throws Exception
	{
		final ComposedTypeModel productCT = typeService.getComposedTypeForCode(ProductModel._TYPECODE);
		final ComposedTypeModel variantCT = typeService.getComposedTypeForCode(VariantProductModel._TYPECODE);

		List<BackofficeIndexedTypeToSolrFacetSearchConfigModel> searchConfigForTypes = defaultSolrFacetSearchConfigDAO
				.findSearchConfigurationsForTypes(Lists.newArrayList(productCT, variantCT));
		Assertions.assertThat(searchConfigForTypes).isEmpty();

		createBackofficeIndexedTypeConfig(ProductModel._TYPECODE);
		searchConfigForTypes = defaultSolrFacetSearchConfigDAO
				.findSearchConfigurationsForTypes(Lists.newArrayList(productCT, variantCT));
		Assertions.assertThat(searchConfigForTypes).isNotEmpty();
		Assertions.assertThat(searchConfigForTypes.size()).isEqualTo(1);

		createBackofficeIndexedTypeConfig(VariantProductModel._TYPECODE);
		searchConfigForTypes = defaultSolrFacetSearchConfigDAO
				.findSearchConfigurationsForTypes(Lists.newArrayList(productCT, variantCT));
		Assertions.assertThat(searchConfigForTypes).isNotEmpty();
		Assertions.assertThat(searchConfigForTypes.size()).isEqualTo(2);
	}

	private String createBackofficeIndexedTypeConfig(final String typeCode)
	{
		final String configName = "testConfig" + System.currentTimeMillis();
		final SolrFacetSearchConfigModel testSolrConfig = createFacetSearchConfig(configName, typeCode);
		final ComposedTypeModel composedTypeForCode = typeService.getComposedTypeForCode(typeCode);

		final BackofficeIndexedTypeToSolrFacetSearchConfigModel model = modelService
				.create(BackofficeIndexedTypeToSolrFacetSearchConfigModel.class);
		model.setIndexedType(composedTypeForCode);
		model.setSolrFacetSearchConfig(testSolrConfig);
		modelService.save(model);
		return configName;
	}

	private SolrFacetSearchConfigModel createFacetSearchConfig(final String configName, final String typeCode)
	{
		final SolrSearchConfigModel solrConfig = modelService.create(SolrSearchConfigModel.class);
		solrConfig.setPageSize(Integer.valueOf(100));
		solrConfig.setLegacyMode(false);

		final SolrServerConfigModel serverConfigModel = modelService.create(SolrServerConfigModel.class);
		serverConfigModel.setName(configName);
		serverConfigModel.setMode(SolrServerModes.STANDALONE);

		final SolrIndexConfigModel indexConfigModel = modelService.create(SolrIndexConfigModel.class);
		indexConfigModel.setName(configName);


		final ComposedTypeModel composedTypeForCode = typeService.getComposedTypeForCode(typeCode);
		final SolrIndexedTypeModel indexedType = modelService.create(SolrIndexedTypeModel.class);
		indexedType.setIdentifier(configName);
		indexedType.setType(composedTypeForCode);

		final SolrFacetSearchConfigModel cfgModel = modelService.create(SolrFacetSearchConfigModel.class);
		cfgModel.setName(configName);
		cfgModel.setEnabledLanguageFallbackMechanism(true);
		cfgModel.setSolrSearchConfig(solrConfig);
		cfgModel.setSolrServerConfig(serverConfigModel);
		cfgModel.setSolrIndexedTypes(Collections.singletonList(indexedType));
		cfgModel.setSolrIndexConfig(indexConfigModel);

		modelService.saveAll();
		return cfgModel;
	}
}
