/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.constants;

/**
 * Constants used for Merchandising Addons
 *
 */
public class MerchandisingConstants
{
	public static final String PAGE_CONTEXT_CATEGORY = "ItemCategory";
	public static final String PAGE_CONTEXT_FACETS = "ContextFacets";
	public static final String PAGE_CONTEXT_BREADCRUMBS = "ContextBreadcrumbs";
	public static final String JS_ADDONS_CONTEXT_VARIABLES = "jsAddOnsVariables";
	public static final String URI_SITE_ID = "siteId";
	public static final String PAGE_CONTEXT_TENANT = "hybrisTenant";
	public static final String CONTEXT_STORE_KEY = "hybrisMerchandisingContextStore";

	private MerchandisingConstants()
	{
		throw new IllegalStateException("Merchandising Constants class shouldn't be instantiated");
	}

}
