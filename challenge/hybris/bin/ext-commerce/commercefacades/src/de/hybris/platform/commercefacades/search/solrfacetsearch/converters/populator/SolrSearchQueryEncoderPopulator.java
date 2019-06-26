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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Converter implementation for
 * {@link de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData} as source and
 * {@link de.hybris.platform.commercefacades.search.data.SearchQueryData} as target type.
 */
public class SolrSearchQueryEncoderPopulator implements Populator<SolrSearchQueryData, SearchQueryData>
{
	private static final Logger LOG = Logger.getLogger(SolrSearchQueryEncoderPopulator.class);

	@Override
	public void populate(final SolrSearchQueryData source, final SearchQueryData target)
	{
		final StringBuilder builder = new StringBuilder();

		if (source != null)
		{
			if (StringUtils.isNotBlank(source.getFreeTextSearch()))
			{
				builder.append(source.getFreeTextSearch());
			}

			builder.append(':');

			if (StringUtils.isNotBlank(source.getSort()))
			{
				builder.append(source.getSort());
			}

			final List<SolrSearchQueryTermData> terms = source.getFilterTerms();
			if (terms != null && !terms.isEmpty())
			{
				for (final SolrSearchQueryTermData term : terms)
				{
					if (StringUtils.isNotBlank(term.getKey()) && StringUtils.isNotBlank(term.getValue()))
					{
						try
						{
							builder.append(':').append(term.getKey()).append(':').append(URLEncoder.encode(term.getValue(), "UTF-8"));
						}
						catch (final UnsupportedEncodingException e)
						{
							// UTF-8 is supported encoding, so it shouldn't come here
							LOG.error("Solr search query URLencoding failed.", e);
						}
					}
				}
			}

			target.setFilterQueries(createSearchFilterQueries(source));

			target.setSearchQueryContext(source.getSearchQueryContext());
		}

		final String result = builder.toString();

		// Special case for empty query
		if (":".equals(result))
		{
			target.setValue("");
		}
		else
		{
			target.setValue(result);
		}
	}

	protected List<SearchFilterQueryData> createSearchFilterQueries(final SolrSearchQueryData source)
	{
		final List<SearchFilterQueryData> searchFilterQueries = new ArrayList();
		if (CollectionUtils.isNotEmpty(source.getFilterQueries()))
		{
			for (final SolrSearchFilterQueryData solrSearchFilterQueryData : source.getFilterQueries())
			{
				final SearchFilterQueryData solrSearchFilterQuery = new SearchFilterQueryData();
				solrSearchFilterQuery.setKey(solrSearchFilterQueryData.getKey());
				solrSearchFilterQuery.setValues(solrSearchFilterQueryData.getValues());
				solrSearchFilterQuery.setOperator(solrSearchFilterQueryData.getOperator());
				searchFilterQueries.add(solrSearchFilterQuery);
			}
		}
		return searchFilterQueries;
	}
}
