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
package de.hybris.platform.solrfacetsearch.indexer.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.solrfacetsearch.indexer.SolrIndexedTypeCodeResolver;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultSolrIndexedTypeCodeResolverTest
{
	private SolrIndexedTypeCodeResolver solrIndexedTypeCodeResolver;
	@Mock
	private ComposedTypeModel mockType;

	@Before
	public void setUp()
	{
		solrIndexedTypeCodeResolver = new DefaultSolrIndexedTypeCodeResolver();
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullSolrIndexedType()
	{
		solrIndexedTypeCodeResolver.resolveIndexedTypeCode(null);
	}

	@Test
	public void testSolrIndexedTypeWithoutIndexedName()
	{
		Mockito.when(mockType.getCode()).thenReturn("Product");
		final SolrIndexedTypeModel solrIndexedTypeModel = new SolrIndexedTypeModel();
		solrIndexedTypeModel.setType(mockType);
		Assert.assertEquals("Product", solrIndexedTypeCodeResolver.resolveIndexedTypeCode(solrIndexedTypeModel));
	}

	@Test
	public void testSolrIndexedTypeWithIndexedName()
	{
		Mockito.when(mockType.getCode()).thenReturn("Product");
		final SolrIndexedTypeModel solrIndexedTypeModel = new SolrIndexedTypeModel();
		solrIndexedTypeModel.setType(mockType);
		solrIndexedTypeModel.setIndexName("myName");
		Assert.assertEquals("Product_myName", solrIndexedTypeCodeResolver.resolveIndexedTypeCode(solrIndexedTypeModel));
	}
}
