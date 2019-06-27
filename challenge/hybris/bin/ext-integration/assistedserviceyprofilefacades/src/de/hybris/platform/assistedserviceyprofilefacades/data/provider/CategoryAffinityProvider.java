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
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityData;
import de.hybris.platform.assistedserviceyprofilefacades.data.CategoryAffinityParameterData;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Category affinity provider responsible for getting category affinities
 */
public class CategoryAffinityProvider implements FragmentModelProvider<List<CategoryAffinityData>>
{
	private YProfileAffinityFacade yProfileAffinityFacade;

	@Override
	public List<CategoryAffinityData> getModel(final Map<String, String> parameters)
	{
		final String listSize = parameters.get("listSize");

		if (StringUtils.isEmpty(listSize))
		{
			throw new IllegalArgumentException(
					"Fragment arguments are not provided for provider [" + CategoryAffinityProvider.class.getName() + "] !");
		}

		int categorySizeList = 5;

		try
		{
			categorySizeList = Integer.parseInt(listSize);
		}
		catch (final NumberFormatException formatException)
		{
			throw new IllegalArgumentException("Provided value [" + listSize + "] is not in a valid integer range!",
					formatException);
		}

		final CategoryAffinityParameterData categoryAffinityParam = new CategoryAffinityParameterData();
		categoryAffinityParam.setSizeLimit(categorySizeList);


		return getyProfileAffinityFacade().getCategoryAffinities(categoryAffinityParam);

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
