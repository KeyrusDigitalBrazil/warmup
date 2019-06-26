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
package de.hybris.platform.commercefacades.product.impl;


import de.hybris.platform.commercefacades.product.ImageFormatMapping;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of ImageFormatMapping. Provides for a simple spring configured mapping.
 */
public class DefaultImageFormatMapping implements ImageFormatMapping
{
	private Map<String, String> mapping;

	protected Map<String, String> getMapping()
	{
		return mapping;
	}

	@Required
	public void setMapping(final Map<String, String> mapping)
	{
		this.mapping = mapping;
	}

	@Override
	public String getMediaFormatQualifierForImageFormat(final String imageFormat)
	{
		return getMapping().get(imageFormat);
	}
}
