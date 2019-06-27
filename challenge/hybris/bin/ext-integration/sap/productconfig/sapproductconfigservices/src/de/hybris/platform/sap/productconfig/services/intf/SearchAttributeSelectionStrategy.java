/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;

import java.util.Set;


/**
 * Determines search attributes
 */
public interface SearchAttributeSelectionStrategy
{

	/**
	 * Is there an indexed attribute with a specific name?
	 *
	 * @param attributeName
	 * @return True if attribute is available on search index
	 * @throws NoValidSolrConfigException
	 */
	boolean isAttributeAvailableOnSearchIndex(String attributeName, Set<String> solrIndexedProperties);

	Set<String> compileIndexedProperties() throws NoValidSolrConfigException;

}
