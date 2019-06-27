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
package de.hybris.platform.acceleratorwebservicesaddon.urlresolver.impl;


import de.hybris.platform.acceleratorservices.urlresolver.impl.DefaultSiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;


/**
 * Implementation of the UrlResolutionService customised for commerce web services
 */
public class WsSiteBaseUrlResolutionService extends DefaultSiteBaseUrlResolutionService
{

	@Override
	public String getWebsiteUrlForSite(final BaseSiteModel site, final String encodingAttributes, final boolean secure,
			final String path)
	{
		String urlForSite = super.getWebsiteUrlForSite(site, encodingAttributes, secure, path);
		if (urlForSite == null)
		{
			urlForSite = lookupConfig("webroot.commercewebservices." + site.getUid() + (secure ? ".https" : ".http"));
		}
		return urlForSite;
	}
}
