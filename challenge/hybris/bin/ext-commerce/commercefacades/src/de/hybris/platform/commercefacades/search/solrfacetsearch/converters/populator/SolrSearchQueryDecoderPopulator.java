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
package de.hybris.platform.commercefacades.search.solrfacetsearch.converters.populator;

import de.hybris.platform.commercefacades.search.data.SearchFilterQueryData;
import de.hybris.platform.commercefacades.search.data.SearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchFilterQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.converters.Populator;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Converter implementation for {@link de.hybris.platform.commercefacades.search.data.SearchQueryData} as source and
 * {@link de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData} as target type.
 */
public class SolrSearchQueryDecoderPopulator implements Populator<SearchQueryData, SolrSearchQueryData>
{
	private static final Logger LOG = Logger.getLogger(SolrSearchQueryDecoderPopulator.class);

	@Override
	public void populate(final SearchQueryData source, final SolrSearchQueryData target)
	{
		if (source == null)
		{
			return;
		}

		if (StringUtils.isNotEmpty(source.getValue()))
		{
			final String[] split = source.getValue().split(":");

			if (split.length > 0)
			{
				target.setFreeTextSearch(split[0]);
			}
			if (split.length > 1)
			{
				target.setSort(split[1]);
			}

			final List<SolrSearchQueryTermData> terms = new ArrayList<SolrSearchQueryTermData>();

			for (int i = 2; (i + 1) < split.length; i += 2)
			{
				final SolrSearchQueryTermData termData = new SolrSearchQueryTermData();
				termData.setKey(split[i]);
				try
				{
					termData.setValue(URLDecoder.decode(split[i + 1], "UTF-8"));
				}
				catch (final UnsupportedEncodingException e)
				{
					// UTF-8 is supported encoding, so it shouldn't come here
					LOG.error("Solr search query URLdecoding failed.", e);
				}
				terms.add(termData);
			}

			target.setFilterTerms(terms);
		}

		target.setFilterQueries(createSolrSearchFilterQueries(source));

		target.setSearchQueryContext(source.getSearchQueryContext());
	}

	protected List<SolrSearchFilterQueryData> createSolrSearchFilterQueries(final SearchQueryData source)
	{
		final List<SolrSearchFilterQueryData> solrSearchFilterQueries = new ArrayList();
		if (CollectionUtils.isNotEmpty(source.getFilterQueries()))
		{
			for (final SearchFilterQueryData searchFilterQueryData : source.getFilterQueries())
			{
				final SolrSearchFilterQueryData solrSearchFilterQuery = new SolrSearchFilterQueryData();
				solrSearchFilterQuery.setKey(searchFilterQueryData.getKey());
				solrSearchFilterQuery.setValues(searchFilterQueryData.getValues());
				solrSearchFilterQuery.setOperator(searchFilterQueryData.getOperator());
				solrSearchFilterQueries.add(solrSearchFilterQuery);
			}
		}
		return solrSearchFilterQueries;
	}
}
