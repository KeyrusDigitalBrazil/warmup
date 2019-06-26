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
package de.hybris.platform.commerceservices.search.solrfacetsearch.populators;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.DocumentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.search.Document;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

/**
 * Populator that populates the variants in document data from search results.
 */
public class DocumentSearchResultValuePopulator implements Populator<DocumentData<SearchQuery, Document>, SearchResultValueData>
{
	@Override
	public void populate(final DocumentData<SearchQuery, Document> source, final SearchResultValueData target)
	{
		populateBasic(source, target);
		target.setVariants(convertVariants(source));
	}

	protected void populateBasic(final DocumentData<SearchQuery, Document> source, final SearchResultValueData target)
	{
		target.setValues(getValues(source));
		target.setFeatureValues(getFeatureValues(source));
		target.setTags(source.getDocument().getTags());
	}

	protected Map<String, Object> getValues(final DocumentData<SearchQuery, Document> source)
	{
		final Map<String, Object> values = new HashMap<>();
		values.putAll(source.getDocument().getFields());
		return values;
	}

	protected Map<ClassAttributeAssignmentModel, Object> getFeatureValues(final DocumentData<SearchQuery, Document> source)
	{
		final IndexedType indexedType = source.getSearchQuery().getIndexedType();
		final Document document = source.getDocument();

		final Map<ClassAttributeAssignmentModel, Object> featureValues = new LinkedHashMap<>();

		for (final IndexedProperty indexedProperty : indexedType.getIndexedProperties().values())
		{
			final ClassAttributeAssignmentModel classAttributeAssignment = indexedProperty.getClassAttributeAssignment();
			if (classAttributeAssignment != null && Boolean.TRUE.equals(classAttributeAssignment.getListable()))
			{
				final Object value = document.getFieldValue(indexedProperty.getName());
				if (value != null)
				{
					featureValues.put(classAttributeAssignment, value);
				}
			}
		}

		return featureValues;
	}

	protected Collection<SearchResultValueData> convertVariants(final DocumentData<SearchQuery, Document> source)
	{
		if (CollectionUtils.isEmpty(source.getVariants()))
		{
			return CollectionUtils.EMPTY_COLLECTION;
		}
		final SearchQuery searchQuery = source.getSearchQuery();
		final Collection<Document> variants = source.getVariants();
		final Collection<SearchResultValueData> targetVariants = new ArrayList<>(variants.size());

		for (final Document variant : variants)
		{
			targetVariants.add(convertVariant(variant, searchQuery));
		}
		return targetVariants;
	}

	protected SearchResultValueData convertVariant(final Document variant, final SearchQuery searchQuery)
	{
		final SearchResultValueData targetVariant = new SearchResultValueData();

		final DocumentData<SearchQuery, Document> documentData = new DocumentData<>();
		documentData.setSearchQuery(searchQuery);
		documentData.setDocument(variant);

		populateBasic(documentData, targetVariant);

		return targetVariant;
	}
}
