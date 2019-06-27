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
package de.hybris.platform.sap.productconfig.facades.filters;

import de.hybris.platform.sap.productconfig.facades.ConfigOverviewFilter;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import java.util.List;


/**
 * Abstract base class for filter implementations to be used on the product config overview page.
 */
public abstract class AbstractConfigOverviewFilter implements ConfigOverviewFilter
{

	@Override
	public List<CsticValueModel> filter(final CsticModel cstic)
	{
		return filter(cstic.getAssignedValues(), cstic);
	}

	@Override
	public List<CsticValueModel> filter(final CsticModel cstic, final List<FilterEnum> appliedFilters)
	{
		List<CsticValueModel> filterResult;
		if (isActive(appliedFilters))
		{
			filterResult = filter(cstic);
		}
		else
		{
			filterResult = cstic.getAssignedValues();
		}
		return filterResult;
	}



}
