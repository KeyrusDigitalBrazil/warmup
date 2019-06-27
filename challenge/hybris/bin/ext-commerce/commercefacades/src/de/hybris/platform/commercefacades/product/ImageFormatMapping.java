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
package de.hybris.platform.commercefacades.product;


/**
 * Image format mapping to media format qualifier.
 */
public interface ImageFormatMapping
{
	/**
	 * Get the media format qualifier for an image format. The image format is a useful frontend qualifier, e.g.
	 * "thumbnail"
	 * 
	 * @param imageFormat
	 *           the image format
	 * @return the media format qualifier
	 */
	String getMediaFormatQualifierForImageFormat(String imageFormat);
}
