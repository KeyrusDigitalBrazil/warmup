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
package de.hybris.platform.ycommercewebservices.mapping.mappers;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercewebservicescommons.dto.product.ImageWsDTO;
import de.hybris.platform.webservicescommons.mapping.mappers.AbstractCustomMapper;

import ma.glasnost.orika.MappingContext;


public class ImageUrlMapper extends AbstractCustomMapper<ImageData, ImageWsDTO>
{
	@Override
	public void mapAtoB(final ImageData a, final ImageWsDTO b, final MappingContext context)
	{
		// other fields are mapped automatically

		context.beginMappingField("url", getAType(), a, "url", getBType(), b);
		try
		{
			if (shouldMap(a, b, context))
			{
				b.setUrl(a.getUrl());
			}
		}
		finally
		{
			context.endMappingField();
		}
	}
}
