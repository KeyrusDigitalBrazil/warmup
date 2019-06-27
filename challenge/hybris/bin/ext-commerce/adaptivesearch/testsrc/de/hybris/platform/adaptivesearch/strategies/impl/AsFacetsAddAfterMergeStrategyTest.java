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
package de.hybris.platform.adaptivesearch.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.adaptivesearch.data.AbstractAsFacetConfiguration;
import de.hybris.platform.adaptivesearch.data.AsConfigurationHolder;
import de.hybris.platform.adaptivesearch.data.AsExcludedFacet;
import de.hybris.platform.adaptivesearch.data.AsFacet;
import de.hybris.platform.adaptivesearch.data.AsPromotedFacet;
import de.hybris.platform.adaptivesearch.util.MergeMap;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class AsFacetsAddAfterMergeStrategyTest extends AbstractAsFacetsMergeStrategyTest
{
	private AsFacetsAddAfterMergeStrategy mergeStrategy;

	@Before
	public void createMergeStrategy()
	{
		mergeStrategy = new AsFacetsAddAfterMergeStrategy();
		mergeStrategy.setAsSearchProfileResultFactory(getAsSearchProfileResultFactory());
	}

	@Test
	public void mergePromotedFacets()
	{
		// given
		final AsPromotedFacet promotedFacet1 = new AsPromotedFacet();
		promotedFacet1.setIndexProperty(INDEX_PROPERTY_1);
		promotedFacet1.setUid(UID_1);

		final AsPromotedFacet promotedFacet2 = new AsPromotedFacet();
		promotedFacet2.setIndexProperty(INDEX_PROPERTY_2);
		promotedFacet2.setUid(UID_2);

		getTarget().getPromotedFacets().put(promotedFacet1.getIndexProperty(), createConfigurationHolder(promotedFacet1));
		getSource().getPromotedFacets().put(promotedFacet2.getIndexProperty(), createConfigurationHolder(promotedFacet2));

		// when
		mergeStrategy.mergeFacets(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getPromotedFacets().size());
		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getPromotedFacets()).orderedValues();

		final AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration> promotedFacet1Holder = promotedFacets.get(0);
		assertSame(promotedFacet1, promotedFacet1Holder.getConfiguration());

		final AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration> promotedFacet2Holder = promotedFacets.get(1);
		assertSame(promotedFacet2, promotedFacet2Holder.getConfiguration());
	}

	@Test
	public void mergePromotedFacetsWithDuplicates()
	{
		// given
		final AsPromotedFacet promotedFacet1 = new AsPromotedFacet();
		promotedFacet1.setIndexProperty(INDEX_PROPERTY_1);
		promotedFacet1.setUid(UID_1);

		final AsPromotedFacet promotedFacet2 = new AsPromotedFacet();
		promotedFacet2.setIndexProperty(INDEX_PROPERTY_2);
		promotedFacet2.setUid(UID_2);

		final AsFacet facet1 = new AsFacet();
		facet1.setIndexProperty(INDEX_PROPERTY_1);
		facet1.setUid(UID_3);

		final AsFacet facet2 = new AsFacet();
		facet2.setIndexProperty(INDEX_PROPERTY_3);
		facet2.setUid(UID_4);

		final AsExcludedFacet excludedFacet1 = new AsExcludedFacet();
		excludedFacet1.setIndexProperty(INDEX_PROPERTY_2);
		excludedFacet1.setUid(UID_5);

		final AsExcludedFacet excludedFacet2 = new AsExcludedFacet();
		excludedFacet2.setIndexProperty(INDEX_PROPERTY_4);
		excludedFacet2.setUid(UID_6);

		getTarget().getFacets().put(facet1.getIndexProperty(), createConfigurationHolder(facet1));
		getTarget().getFacets().put(facet2.getIndexProperty(), createConfigurationHolder(facet2));
		getTarget().getExcludedFacets().put(excludedFacet1.getIndexProperty(), createConfigurationHolder(excludedFacet1));
		getTarget().getExcludedFacets().put(excludedFacet2.getIndexProperty(), createConfigurationHolder(excludedFacet2));

		getSource().getPromotedFacets().put(promotedFacet1.getIndexProperty(), createConfigurationHolder(promotedFacet1));
		getSource().getPromotedFacets().put(promotedFacet2.getIndexProperty(), createConfigurationHolder(promotedFacet2));

		// when
		mergeStrategy.mergeFacets(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getPromotedFacets().size());
		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getPromotedFacets()).orderedValues();

		final AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration> promotedFacet1Holder = promotedFacets.get(0);
		assertSame(promotedFacet1, promotedFacet1Holder.getConfiguration());

		final AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration> promotedFacet2Holder = promotedFacets.get(1);
		assertSame(promotedFacet2, promotedFacet2Holder.getConfiguration());

		assertEquals(1, getTarget().getFacets().size());
		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getFacets()).orderedValues();

		final AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration> facet2Holder = facets.get(0);
		assertSame(facet2, facet2Holder.getConfiguration());

		assertEquals(1, getTarget().getExcludedFacets().size());
		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getExcludedFacets()).orderedValues();

		final AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration> excludedFacet2Holder = excludedFacets.get(0);
		assertSame(excludedFacet2, excludedFacet2Holder.getConfiguration());
	}

	@Test
	public void mergeFacets()
	{
		// given
		final AsFacet facet1 = new AsFacet();
		facet1.setIndexProperty(INDEX_PROPERTY_1);
		facet1.setUid(UID_1);

		final AsFacet facet2 = new AsFacet();
		facet2.setIndexProperty(INDEX_PROPERTY_2);
		facet2.setUid(UID_2);

		getTarget().getFacets().put(facet1.getIndexProperty(), createConfigurationHolder(facet1));
		getSource().getFacets().put(facet2.getIndexProperty(), createConfigurationHolder(facet2));

		// when
		mergeStrategy.mergeFacets(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getFacets().size());
		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getFacets()).orderedValues();

		final AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration> facet1Holder = facets.get(0);
		assertSame(facet1, facet1Holder.getConfiguration());

		final AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration> facet2Holder = facets.get(1);
		assertSame(facet2, facet2Holder.getConfiguration());
	}

	@Test
	public void mergeFacetsWithDuplicates()
	{
		// given
		final AsPromotedFacet promotedFacet1 = new AsPromotedFacet();
		promotedFacet1.setIndexProperty(INDEX_PROPERTY_1);
		promotedFacet1.setUid(UID_1);

		final AsPromotedFacet promotedFacet2 = new AsPromotedFacet();
		promotedFacet2.setIndexProperty(INDEX_PROPERTY_3);
		promotedFacet2.setUid(UID_2);

		final AsFacet facet1 = new AsFacet();
		facet1.setIndexProperty(INDEX_PROPERTY_1);
		facet1.setUid(UID_3);

		final AsFacet facet2 = new AsFacet();
		facet2.setIndexProperty(INDEX_PROPERTY_2);
		facet2.setUid(UID_4);

		final AsExcludedFacet excludedFacet1 = new AsExcludedFacet();
		excludedFacet1.setIndexProperty(INDEX_PROPERTY_2);
		excludedFacet1.setUid(UID_5);

		final AsExcludedFacet excludedFacet2 = new AsExcludedFacet();
		excludedFacet2.setIndexProperty(INDEX_PROPERTY_4);
		excludedFacet2.setUid(UID_6);

		getTarget().getPromotedFacets().put(promotedFacet1.getIndexProperty(), createConfigurationHolder(promotedFacet1));
		getTarget().getPromotedFacets().put(promotedFacet2.getIndexProperty(), createConfigurationHolder(promotedFacet2));
		getTarget().getExcludedFacets().put(excludedFacet1.getIndexProperty(), createConfigurationHolder(excludedFacet1));
		getTarget().getExcludedFacets().put(excludedFacet2.getIndexProperty(), createConfigurationHolder(excludedFacet2));

		getSource().getFacets().put(facet1.getIndexProperty(), createConfigurationHolder(facet1));
		getSource().getFacets().put(facet2.getIndexProperty(), createConfigurationHolder(facet2));

		// when
		mergeStrategy.mergeFacets(getSource(), getTarget());

		// then
		assertEquals(1, getTarget().getPromotedFacets().size());
		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getPromotedFacets()).orderedValues();

		final AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration> promotedFacet2Holder = promotedFacets.get(0);
		assertSame(promotedFacet2, promotedFacet2Holder.getConfiguration());

		assertEquals(2, getTarget().getFacets().size());
		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getFacets()).orderedValues();

		final AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration> facet1Holder = facets.get(0);
		assertSame(facet1, facet1Holder.getConfiguration());

		final AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration> facet2Holder = facets.get(1);
		assertSame(facet2, facet2Holder.getConfiguration());

		assertEquals(1, getTarget().getExcludedFacets().size());
		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getExcludedFacets()).orderedValues();

		final AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration> excludedFacet2Holder = excludedFacets.get(0);
		assertSame(excludedFacet2, excludedFacet2Holder.getConfiguration());
	}

	@Test
	public void mergeExcludedFacets()
	{
		// given
		final AsExcludedFacet excludedFacet1 = new AsExcludedFacet();
		excludedFacet1.setIndexProperty(INDEX_PROPERTY_1);
		excludedFacet1.setUid(UID_1);

		final AsExcludedFacet excludedFacet2 = new AsExcludedFacet();
		excludedFacet2.setIndexProperty(INDEX_PROPERTY_2);
		excludedFacet2.setUid(UID_2);

		getTarget().getExcludedFacets().put(excludedFacet1.getIndexProperty(), createConfigurationHolder(excludedFacet1));
		getSource().getExcludedFacets().put(excludedFacet2.getIndexProperty(), createConfigurationHolder(excludedFacet2));

		// when
		mergeStrategy.mergeFacets(getSource(), getTarget());

		// then
		assertEquals(2, getTarget().getExcludedFacets().size());
		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getExcludedFacets()).orderedValues();

		final AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration> excludedFacet1Holder = excludedFacets.get(0);
		assertSame(excludedFacet1, excludedFacet1Holder.getConfiguration());

		final AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration> excludedFacet2Holder = excludedFacets.get(1);
		assertSame(excludedFacet2, excludedFacet2Holder.getConfiguration());
	}

	@Test
	public void mergeExcludedItemsWithDuplicates()
	{
		// given
		final AsPromotedFacet promotedFacet1 = new AsPromotedFacet();
		promotedFacet1.setIndexProperty(INDEX_PROPERTY_1);
		promotedFacet1.setUid(UID_1);

		final AsPromotedFacet promotedFacet2 = new AsPromotedFacet();
		promotedFacet2.setIndexProperty(INDEX_PROPERTY_3);
		promotedFacet2.setUid(UID_2);

		final AsFacet facet1 = new AsFacet();
		facet1.setIndexProperty(INDEX_PROPERTY_2);
		facet1.setUid(UID_3);

		final AsFacet facet2 = new AsFacet();
		facet2.setIndexProperty(INDEX_PROPERTY_4);
		facet2.setUid(UID_4);

		final AsExcludedFacet excludedFacet1 = new AsExcludedFacet();
		excludedFacet1.setIndexProperty(INDEX_PROPERTY_1);
		excludedFacet1.setUid(UID_5);

		final AsExcludedFacet excludedFacet2 = new AsExcludedFacet();
		excludedFacet2.setIndexProperty(INDEX_PROPERTY_2);
		excludedFacet2.setUid(UID_6);

		getTarget().getFacets().put(facet1.getIndexProperty(), createConfigurationHolder(facet1));
		getTarget().getFacets().put(facet2.getIndexProperty(), createConfigurationHolder(facet2));
		getTarget().getPromotedFacets().put(promotedFacet1.getIndexProperty(), createConfigurationHolder(promotedFacet1));
		getTarget().getPromotedFacets().put(promotedFacet2.getIndexProperty(), createConfigurationHolder(promotedFacet2));

		getSource().getExcludedFacets().put(excludedFacet1.getIndexProperty(), createConfigurationHolder(excludedFacet1));
		getSource().getExcludedFacets().put(excludedFacet2.getIndexProperty(), createConfigurationHolder(excludedFacet2));


		// when
		mergeStrategy.mergeFacets(getSource(), getTarget());

		// then
		assertEquals(1, getTarget().getPromotedFacets().size());
		final List<AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>> promotedFacets = ((MergeMap<String, AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getPromotedFacets()).orderedValues();

		final AsConfigurationHolder<AsPromotedFacet, AbstractAsFacetConfiguration> promotedFacet2Holder = promotedFacets.get(0);
		assertSame(promotedFacet2, promotedFacet2Holder.getConfiguration());

		assertEquals(1, getTarget().getFacets().size());
		final List<AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>> facets = ((MergeMap<String, AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getFacets()).orderedValues();

		final AsConfigurationHolder<AsFacet, AbstractAsFacetConfiguration> facet2Holder = facets.get(0);
		assertSame(facet2, facet2Holder.getConfiguration());

		assertEquals(2, getTarget().getExcludedFacets().size());
		final List<AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>> excludedFacets = ((MergeMap<String, AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration>>) getTarget()
				.getExcludedFacets()).orderedValues();

		final AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration> excludedFacet1Holder = excludedFacets.get(0);
		assertSame(excludedFacet1, excludedFacet1Holder.getConfiguration());

		final AsConfigurationHolder<AsExcludedFacet, AbstractAsFacetConfiguration> excludedFacet2Holder = excludedFacets.get(1);
		assertSame(excludedFacet2, excludedFacet2Holder.getConfiguration());
	}
}
