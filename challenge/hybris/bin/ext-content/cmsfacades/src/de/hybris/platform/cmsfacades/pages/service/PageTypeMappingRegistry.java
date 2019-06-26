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

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;

import java.util.Optional;


/**
 * Registry that holds the {@code PageTypeMapping} to be used for each type of page.
 * 
 * @deprecated since 6.6
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.6")
public interface PageTypeMappingRegistry
{
	/**
	 * Get the page type mapping given a page type code.
	 *
	 * @param typecode
	 *           - the type code of a page
	 * @return the matching page type mapping
	 */
	Optional<PageTypeMapping> getPageTypeMapping(String typecode);
}
