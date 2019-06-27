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
package de.hybris.platform.solrfacetsearch.suggester;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexUpdateException;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProviderFactory;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;
import de.hybris.platform.solrfacetsearch.suggester.exceptions.SolrAutoSuggestException;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.DirectXmlRequest;
import org.junit.Test;


public class SolrAutoSuggestServiceTest extends AbstractIntegrationTest
{
	@Resource
	private CommonI18NService commonI18NService;

	@Resource(name = "solrAutoSuggestService")
	private SolrAutoSuggestService solrAutoSuggestService;

	@Resource(name = "solrSearchProviderFactory")
	private SolrSearchProviderFactory solrSearchProviderFactory;

	@Override
	protected void loadData()
			throws ImpExException, IOException, FacetConfigServiceException, SolrServiceException, SolrServerException
	{
		importConfig("/test/integration/SolrAutoSuggestServiceTest.csv");

		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final IndexedType indexedType = facetSearchConfig.getIndexConfig().getIndexedTypes().values().iterator().next();

		final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);
		final Index index = solrSearchProvider.resolveIndex(facetSearchConfig, indexedType, "1");

		final String xmlFile = readFile("/test/integration/SolrAutoSuggestServiceTest.xml");
		final DirectXmlRequest xmlRequest = new DirectXmlRequest("/update", xmlFile);

		solrSearchProvider.createIndex(index);

		final SolrClient solrClient = solrSearchProvider.getClientForIndexing(index);

		solrClient.request(xmlRequest, index.getName());
		solrClient.commit(index.getName());
		solrClient.close();
	}

	@Test
	public void testSuggestions() throws SolrAutoSuggestException, IndexUpdateException
	{
		final LanguageModel english = commonI18NService.getLanguage("en");

		SolrSuggestion suggestion = solrAutoSuggestService.getAutoSuggestionsForQuery(english, getIndexedTypeModel(), "anyWord");
		assertThat(suggestion.getSuggestions().containsKey("anyWord")).isFalse();

		suggestion = solrAutoSuggestService.getAutoSuggestionsForQuery(english, getIndexedTypeModel(), "suggest"); //intentionally slightly
		// mis-spelled
		assertThat(suggestion.getSuggestions().keySet().size()).isGreaterThan(0);
		assertThat(suggestion.getSuggestions().get("suggest")).isNotEmpty();

		suggestion = solrAutoSuggestService.getAutoSuggestionsForQuery(english, getIndexedTypeModel(), "des");
		assertThat(suggestion.getSuggestions().get("des")).isNull();
	}

}
