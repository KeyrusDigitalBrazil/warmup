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
package de.hybris.platform.assistedserviceyprofilefacades.data.provider;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedserviceyprofilefacades.YProfileAffinityFacade;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.ProductAffinityParameterData;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Product affinity provider responsible for getting product affinities
 */
public class ProductAffinityProvider implements FragmentModelProvider<List<ProductAffinityData>>
{
	private YProfileAffinityFacade yProfileAffinityFacade;

	@Override
	public List<ProductAffinityData> getModel(final Map<String, String> parameters)
	{
		final String listSize = parameters.get("listSize");

		if (StringUtils.isEmpty(listSize))
		{
			throw new IllegalArgumentException(
					"Fragment arguments are not provided for provider [" + ProductAffinityProvider.class.getName() + "] !");
		}

		int productListSize = 5;

		try
		{
			productListSize = Integer.parseInt(listSize);
		}
		catch (final NumberFormatException formatException)
		{
			throw new IllegalArgumentException("Provided value [" + listSize + "] is not in a valid integer range!",
					formatException);
		}

		final ProductAffinityParameterData productAffinityParam = new ProductAffinityParameterData();
		productAffinityParam.setSizeLimit(productListSize);

		return getyProfileAffinityFacade().getProductAffinities(productAffinityParam);
	}

	protected YProfileAffinityFacade getyProfileAffinityFacade()
	{
		return yProfileAffinityFacade;
	}

	@Required
	public void setyProfileAffinityFacade(final YProfileAffinityFacade yProfileAffinityFacade)
	{
		this.yProfileAffinityFacade = yProfileAffinityFacade;
	}


}
