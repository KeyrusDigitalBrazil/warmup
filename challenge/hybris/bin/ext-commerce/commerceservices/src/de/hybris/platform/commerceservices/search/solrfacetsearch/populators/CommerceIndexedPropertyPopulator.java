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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.enums.SolrIndexedPropertyFacetType;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;


public class CommerceIndexedPropertyPopulator implements Populator<SolrIndexedPropertyModel, IndexedProperty>
{
	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SolrIndexedPropertyModel property, final IndexedProperty indexedProperty) throws ConversionException
	{
		indexedProperty.setCategoryField(property.isCategoryField());
		indexedProperty.setVisible(property.isVisible());
		indexedProperty.setClassAttributeAssignment(property.getClassAttributeAssignment());

		if (indexedProperty.isFacet())
		{
			if (SolrIndexedPropertyFacetType.MULTISELECTAND.equals(property.getFacetType()))
			{
				indexedProperty.setMultiSelect(true);
			}
			else if (SolrIndexedPropertyFacetType.MULTISELECTOR.equals(property.getFacetType()))
			{
				indexedProperty.setMultiSelect(true);
			}
			else
			{
				indexedProperty.setMultiSelect(false);
			}

			indexedProperty.setFacetSortProvider(property.getCustomFacetSortProvider());

		}
		indexedProperty.setValueProviderParameter(property.getValueProviderParameter());
		indexedProperty.setAutoSuggest(Boolean.TRUE.equals(property.getUseForAutocomplete()));
		indexedProperty.setSpellCheck(Boolean.TRUE.equals(property.getUseForSpellchecking()));
	}
}
