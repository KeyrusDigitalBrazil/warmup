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

import de.hybris.platform.cms2.model.pages.AbstractPageModel;


/**
 * Represents meta-information about a <code>AbstractPageModel</code> class and the page variation resolver required to
 * retrieve default or variation pages information.
 */
public interface PageVariationResolverType
{
	/**
	 * Get the typecode identifying the <code>AbstractPageModel</code>.
	 *
	 * @return the typecode
	 */
	String getTypecode();

	/**
	 * Get the resolver to be used when fetching default and variation pages for page type
	 * <code>AbstractPageModel</code>.
	 *
	 * @return the resolver
	 */
	PageVariationResolver<AbstractPageModel> getResolver();

}
