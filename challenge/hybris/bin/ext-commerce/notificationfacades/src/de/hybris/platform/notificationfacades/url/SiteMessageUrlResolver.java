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
package de.hybris.platform.notificationfacades.url;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.ItemModel;

import org.apache.commons.lang3.StringUtils;


/**
 * Abstract site message URL resolver to provide default URL.
 */
public abstract class SiteMessageUrlResolver<T extends ItemModel> implements UrlResolver<T>
{

	private String defaultUrl = StringUtils.EMPTY;

	protected String getDefaultUrl()
	{
		return defaultUrl;
	}

	public void setDefaultUrl(final String defaultUrl)
	{
		this.defaultUrl = defaultUrl;
	}

}
