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
package de.hybris.platform.solrfacetsearch.integration;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfigService;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.daos.SolrFacetSearchConfigDao;
import de.hybris.platform.solrfacetsearch.enums.SolrServerModes;
import de.hybris.platform.solrfacetsearch.indexer.impl.DefaultIndexerServiceIntegrationTest;
import de.hybris.platform.solrfacetsearch.model.SolrIndexModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrFacetSearchConfigModel;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrClientPool;
import de.hybris.platform.solrfacetsearch.solr.SolrIndexService;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;


public abstract class AbstractIntegrationTest extends ServicelayerTest
{
	protected static final String DEFAULT_ENCODING = "UTF-8";

	protected static final String SOLR_SERVER_MODE = "solrfacetsearch.test.server.mode";
	protected static final String SOLR_SERVER_STANDALONE_ENDPOINT = "solrfacetsearch.test.server.standalone.endpoint";
	protected static final String SOLR_SERVER_CLOUD_ENDPOINT = "solrfacetsearch.test.server.cloud.endpoint";

	protected static final String HW_CATALOG = "hwcatalog";
	protected static final String ONLINE_CATALOG_VERSION = "Online";
	protected static final String STAGED_CATALOG_VERSION = "Staged";

	protected static final String PRODUCT_CODE = "HW1100-0024";
	protected static final String FACET_SEARCH_CONFIG_NAME = "testFacetSearchConfig";

	private String testId;
	private String solrServerEndpoint;
	private String solrServerMode;

	@Resource
	private ConfigurationService configurationService;

	@Resource
	private FacetSearchConfigService facetSearchConfigService;

	@Resource
	private SolrFacetSearchConfigDao solrFacetSearchConfigDao;

	@Resource
	private SolrIndexService solrIndexService;

	@Resource
	private SolrSearchProviderFactory solrSearchProviderFactory;

	@Resource
	private SolrClientPool solrClientPool;

	private FacetSearchConfig facetSearchConfig;
	private SolrFacetSearchConfigModel solrFacetSearchConfigModel;
	private SolrIndexedTypeModel indexedTypeModel;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	protected String getProductCode()
	{
		return PRODUCT_CODE;
	}

	@Before
	public void setUp() throws Exception
	{
		testId = Long.toString(Thread.currentThread().getId()) + System.currentTimeMillis();

		solrServerMode = configurationService.getConfiguration().getString(SOLR_SERVER_MODE);
		if (SolrServerModes.CLOUD.getCode().equals(solrServerMode))
		{
			solrServerEndpoint = configurationService.getConfiguration().getString(SOLR_SERVER_CLOUD_ENDPOINT);
		}
		else
		{
			solrServerEndpoint = configurationService.getConfiguration().getString(SOLR_SERVER_STANDALONE_ENDPOINT);
		}

		beforeLoadData();

		createCoreData();
		importCsv("/test/solrBasics.csv", DEFAULT_ENCODING);
		importCsv("/test/solrHwcatalogStaged.csv", DEFAULT_ENCODING);
		importCsv("/test/solrHwcatalogOnline.csv", DEFAULT_ENCODING);
		importConfig("/test/solrConfigBase.csv");

		loadData();
	}

	@After
	public void tearDown() throws Exception
	{
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();

		for (final IndexedType indexedType : facetSearchConfig.getIndexConfig().getIndexedTypes().values())
		{
			final List<SolrIndexModel> indexes = solrIndexService.getIndexesForConfigAndType(facetSearchConfig.getName(),
					indexedType.getIdentifier());
			for (final SolrIndexModel index : indexes)
			{
				final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig,
						indexedType);
				final Index solrIndex = solrSearchProvider.resolveIndex(facetSearchConfig, indexedType, index.getQualifier());
				solrSearchProvider.deleteIndex(solrIndex);

				solrIndexService.deleteIndex(facetSearchConfig.getName(), indexedType.getIdentifier(), index.getQualifier());
			}
		}
		solrClientPool.invalidateAll();
	}

	protected void beforeLoadData() throws Exception
	{
		// Do nothing by default
	}

	protected void loadData() throws Exception
	{
		// Do nothing by default
	}

	protected String getFacetSearchConfigName()
	{
		return FACET_SEARCH_CONFIG_NAME + testId;
	}

	protected FacetSearchConfig getFacetSearchConfig() throws FacetConfigServiceException
	{
		if (facetSearchConfig == null)
		{
			facetSearchConfig = facetSearchConfigService.getConfiguration(getFacetSearchConfigName());
		}

		return facetSearchConfig;
	}

	protected void importConfig(final String resource) throws IOException, ImpExException
	{
		importConfig(resource, Collections.emptyMap());
	}

	protected void importConfig(final String resource, final Map<String, String> params) throws IOException, ImpExException
	{
		final InputStream inputStream = DefaultIndexerServiceIntegrationTest.class.getResourceAsStream(resource);
		String impexContent = IOUtils.toString(inputStream, DEFAULT_ENCODING);

		final Map<String, String> impexParams = new HashMap<String, String>();
		impexParams.put("testId", testId);
		impexParams.put("solrServerMode", solrServerMode);
		impexParams.put("solrServerEndpoint", solrServerEndpoint);
		impexParams.putAll(params);

		final StrSubstitutor substitutor = new StrSubstitutor(impexParams);
		impexContent = substitutor.replace(impexContent);

		final InputStream newInputStream = IOUtils.toInputStream(impexContent, DEFAULT_ENCODING);
		importStream(newInputStream, DEFAULT_ENCODING, resource);
	}

	protected String readFile(final String pathName) throws IOException
	{
		final InputStream inputStream = AbstractIntegrationTest.class.getResourceAsStream(pathName);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		final StringBuffer buffer = new StringBuffer();

		String line = "";

		while ((line = reader.readLine()) != null)
		{
			buffer.append(line);
		}

		return buffer.toString();
	}

	protected void initializeIndexedType()
	{
		if (indexedTypeModel == null)
		{
			solrFacetSearchConfigModel = solrFacetSearchConfigDao.findFacetSearchConfigByName(getFacetSearchConfigName());
			indexedTypeModel = solrFacetSearchConfigModel.getSolrIndexedTypes().get(0);
		}
	}

	public SolrIndexedTypeModel getIndexedTypeModel()
	{
		initializeIndexedType();
		return indexedTypeModel;
	}

	public SolrFacetSearchConfigModel getSolrFacetSearchConfigModel()
	{
		return solrFacetSearchConfigModel;
	}

	public String getTestId()
	{
		return testId;
	}

}
