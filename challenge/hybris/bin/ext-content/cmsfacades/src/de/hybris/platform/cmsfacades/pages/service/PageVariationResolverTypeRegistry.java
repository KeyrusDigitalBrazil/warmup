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
package de.hybris.platform.cmsfacades.pages.service;

import java.util.Collection;
import java.util.Optional;


/**
 * Registry that stores a collection of <code>PageVariationResolverType</code> elements.
 */
public interface PageVariationResolverTypeRegistry
{
	/**
	 * Get a specific <code>PageVariationResolverType</code> by type code.
	 *
	 * @param typecode
	 *           - the model type code of the element to retrieve from the registry
	 * @return the element matching the page type. If none is found matching the given page type, return the resolver
	 *         type for <code>AbstractPageModel</code>
	 */
	Optional<PageVariationResolverType> getPageVariationResolverType(String typecode);

	/**
	 * Get all elements in the registry.
	 *
	 * @return all items in the registry; never <tt>null</tt>
	 */
	Collection<PageVariationResolverType> getPageVariationResolverTypes();
}
