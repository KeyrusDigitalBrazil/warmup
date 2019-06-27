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
package de.hybris.platform.solrfacetsearch.solr.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.SolrConfig;
import de.hybris.platform.solrfacetsearch.config.SolrServerMode;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test class for SolrSearchProviderFactory
 */
@UnitTest
public class DefaultSolrSearchProviderFactoryTest
{
	private DefaultSolrSearchProviderFactory solrSearchProviderFactory;

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Mock
	private FacetSearchConfig facetSearchConfig;

	@Mock
	private SolrConfig solrConfig;

	@Mock
	private IndexedType indexedType;

	@Mock
	private SolrCloudSearchProvider solrCloudSearchProvider;

	@Mock
	private SolrStandaloneSearchProvider solrStandaloneSearchProvider;

	@Mock
	private XmlExportSearchProvider xmlExportSearchProvider;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		solrSearchProviderFactory = new DefaultSolrSearchProviderFactory();
		solrSearchProviderFactory.setSolrStandaloneSearchProvider(solrStandaloneSearchProvider);
		solrSearchProviderFactory.setSolrCloudSearchProvider(solrCloudSearchProvider);
		solrSearchProviderFactory.setXmlExportSearchProvider(xmlExportSearchProvider);

		when(facetSearchConfig.getSolrConfig()).thenReturn(solrConfig);
	}

	@Test
	public void testGetSearchProviderWithNoSupportedSolrMode() throws SolrServiceException
	{
		// given
		when(solrConfig.getMode()).thenReturn(SolrServerMode.EMBEDDED);

		// expect
		expectedException.expect(SolrServiceException.class);

		// when
		solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);
	}

	@Test
	public void testGetStandaloneSearchProvider() throws SolrServiceException
	{
		// given
		when(solrConfig.getMode()).thenReturn(SolrServerMode.STANDALONE);

		// when
		final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);

		// then
		assertSame(solrStandaloneSearchProvider, solrSearchProvider);
	}

	@Test
	public void testGetCloudSearchProvider() throws SolrServiceException
	{
		// given
		when(solrConfig.getMode()).thenReturn(SolrServerMode.CLOUD);

		// when
		final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);

		// then
		assertSame(solrCloudSearchProvider, solrSearchProvider);
	}

	@Test
	public void testGetXmlExportSearchProvider() throws SolrServiceException
	{
		// given
		when(solrConfig.getMode()).thenReturn(SolrServerMode.XML_EXPORT);

		// when
		final SolrSearchProvider solrSearchProvider = solrSearchProviderFactory.getSearchProvider(facetSearchConfig, indexedType);

		// then
		assertSame(xmlExportSearchProvider, solrSearchProvider);
	}
}
