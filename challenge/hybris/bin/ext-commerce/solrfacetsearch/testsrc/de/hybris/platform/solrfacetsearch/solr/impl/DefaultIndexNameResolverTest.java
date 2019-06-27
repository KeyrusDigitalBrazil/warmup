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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultIndexNameResolverTest
{
	private static final String SEPARATOR = "_";
	private static final String TENANT_ID = "tenantID";
	private static final String INDEX_NAME_FROM_CONFIG = "indexNameFromConfig";
	private static final String INDEX_NAME = "INDEX_NAME";
	private static final String INDEX_CODE = "INDEX_Code";
	private static final String QUALIFIER = "qualifier";

	private static final String INDEX_NAME_FROM_CONFIG_WRONG = "index Name FromConfig";
	private static final String INDEX_NAME_WRONG = "IN[DEX_NA]ME";
	private static final String QUALIFIER_WRONG = "qual  ? ifier:";

	private DefaultIndexNameResolver indexNameResolver;

	@Mock
	private TenantService tenantService;

	private FacetSearchConfig facetSearchConfig;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);

		indexNameResolver = new DefaultIndexNameResolver();

		indexNameResolver.setTenantService(tenantService);
		indexNameResolver.setSeparator(SEPARATOR);

		when(tenantService.getCurrentTenantId()).thenReturn(TENANT_ID);

		facetSearchConfig = new FacetSearchConfig();
	}

	@Test
	public void testResolve()
	{
		// given
		final IndexedType indexedType = new IndexedType();
		indexedType.setIndexName(INDEX_NAME);
		indexedType.setIndexNameFromConfig(INDEX_NAME_FROM_CONFIG);

		// when
		final String resolvedName = indexNameResolver.resolve(facetSearchConfig, indexedType, null);

		//then
		assertEquals(resolvedName, TENANT_ID + SEPARATOR + INDEX_NAME_FROM_CONFIG + SEPARATOR + INDEX_NAME);
	}

	@Test
	public void testResolveWithQualifier()
	{
		// given
		final IndexedType indexedType = new IndexedType();
		indexedType.setIndexName(INDEX_NAME);
		indexedType.setIndexNameFromConfig(INDEX_NAME_FROM_CONFIG);

		// when
		final String resolvedName = indexNameResolver.resolve(facetSearchConfig, indexedType, QUALIFIER);

		//then
		assertEquals(resolvedName, TENANT_ID + SEPARATOR + INDEX_NAME_FROM_CONFIG + SEPARATOR + INDEX_NAME + SEPARATOR + QUALIFIER);
	}

	@Test
	public void testResolveWithIndexCode()
	{
		// given
		final IndexedType indexedType = new IndexedType();
		indexedType.setCode(INDEX_CODE);
		indexedType.setIndexNameFromConfig(INDEX_NAME_FROM_CONFIG);

		// when
		final String resolvedName = indexNameResolver.resolve(facetSearchConfig, indexedType, QUALIFIER);

		//then
		assertEquals(resolvedName, TENANT_ID + SEPARATOR + INDEX_NAME_FROM_CONFIG + SEPARATOR + INDEX_CODE + SEPARATOR + QUALIFIER);
	}

	@Test
	public void testResolveEscaped()
	{
		// given
		final IndexedType indexedType = new IndexedType();
		indexedType.setIndexName(INDEX_NAME_WRONG);
		indexedType.setIndexNameFromConfig(INDEX_NAME_FROM_CONFIG_WRONG);

		// when
		final String resolvedName = indexNameResolver.resolve(facetSearchConfig, indexedType, QUALIFIER_WRONG);

		//then
		assertEquals(resolvedName, TENANT_ID + SEPARATOR + INDEX_NAME_FROM_CONFIG + SEPARATOR + INDEX_NAME + SEPARATOR +
				QUALIFIER);
	}
}
