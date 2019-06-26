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
package de.hybris.platform.acceleratorservices.urlresolver.impl;

import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import org.springframework.beans.factory.annotation.Required;

/**
 * Resolve the URL for the content home page
 */
public class DefaultHomepageContentPageUrlResolver extends AbstractUrlResolver<ContentPageModel>
{
	private final String CACHE_KEY = DefaultHomepageContentPageUrlResolver.class.getName();

	private String pattern;

	protected String getPattern()
	{
		return pattern;
	}

	@Required
	public void setPattern(final String pattern)
	{
		this.pattern = pattern;
	}

	@Override
	protected String getKey(final ContentPageModel source)
	{
		return CACHE_KEY + "." + source.getPk().toString();
	}

	@Override
	protected String resolveInternal(final ContentPageModel source)
	{
		// default url is /
		// Replace pattern values
		return getPattern();
	}
}
