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
package de.hybris.platform.solrfacetsearch.solr;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.SolrClientConfig;
import de.hybris.platform.solrfacetsearch.config.exceptions.FacetConfigServiceException;
import de.hybris.platform.solrfacetsearch.integration.AbstractIntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;


public class SolrServerConnectionParametersTest extends AbstractIntegrationTest
{
	@Test
	public void testConnectionParameters() throws FacetConfigServiceException, IOException, ImpExException
	{
		// when
		importConfig("/test/integration/SolrServerConnectionParametersTest.csv");
		final FacetSearchConfig facetSearchConfig = getFacetSearchConfig();
		final SolrClientConfig searchClientConfig = facetSearchConfig.getSolrConfig().getClientConfig();
		final SolrClientConfig indexingClientConfig = facetSearchConfig.getSolrConfig().getIndexingClientConfig();

		// then
		Assert.assertNotNull(searchClientConfig);
		Assert.assertEquals(100, searchClientConfig.getAliveCheckInterval().intValue());
		Assert.assertEquals(200, searchClientConfig.getConnectionTimeout().intValue());
		Assert.assertEquals(300, searchClientConfig.getSocketTimeout().intValue());
		Assert.assertEquals(400, searchClientConfig.getMaxConnections().intValue());
		Assert.assertEquals(500, searchClientConfig.getMaxConnectionsPerHost().intValue());
		Assert.assertFalse(searchClientConfig.isTcpNoDelay());

		Assert.assertNotNull(indexingClientConfig);
		Assert.assertEquals(110, indexingClientConfig.getAliveCheckInterval().intValue());
		Assert.assertEquals(220, indexingClientConfig.getConnectionTimeout().intValue());
		Assert.assertEquals(330, indexingClientConfig.getSocketTimeout().intValue());
		Assert.assertEquals(440, indexingClientConfig.getMaxConnections().intValue());
		Assert.assertEquals(550, indexingClientConfig.getMaxConnectionsPerHost().intValue());
		Assert.assertFalse(indexingClientConfig.isTcpNoDelay());
	}
}
