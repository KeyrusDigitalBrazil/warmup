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
package de.hybris.platform.assistedserviceyprofilefacades.populator;



import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.yaasyprofileconnect.yaas.Affinity;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Arrays;
import java.util.Map;

/**
 *
 * Populator for product affinity data.
 *
 * @param <SOURCE>
 *           Map.Entry<String,Affinity>
 * @param <TARGET>
 *           ProductAffinityData
 */
public class ProductAffinityPopulator<SOURCE extends Map.Entry<String,Affinity>, TARGET extends ProductAffinityData>
		implements Populator<SOURCE, TARGET>
{

	private static final Logger LOG = Logger.getLogger(ProductAffinityPopulator.class);
	private ProductFacade productFacade;


	@Override
	public void populate(final SOURCE affinityData, final TARGET productAffinityData)
	{
		if (StringUtils.isEmpty(affinityData.getKey()))
		{
			throw new ConversionException("Product Id not found for node " + affinityData.getKey());
		}

		try
		{
			ProductData productData = null;
			productData = getProductFacade().getProductForCodeAndOptions(affinityData.getKey(),
					Arrays.asList(ProductOption.BASIC, ProductOption.PRICE));

			productAffinityData.setProductData(productData);
		}
		catch (final UnknownIdentifierException e) // preventing errors for not-found products
		{
			LOG.error("Product with Id [" + affinityData.getKey() + "] on yProfile not found in hybris", e);

		}
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	@Required
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}
}
