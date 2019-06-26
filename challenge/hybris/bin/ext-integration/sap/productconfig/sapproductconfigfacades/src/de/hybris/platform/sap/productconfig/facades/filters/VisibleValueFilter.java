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

import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Filters out all cstics, that are marked as invisble.
 */
public class VisibleValueFilter extends AbstractConfigOverviewFilter
{

	@Override
	public boolean isActive(final List<FilterEnum> appliedFilters)
	{
		return appliedFilters.contains(FilterEnum.VISIBLE);
	}


	@Override
	public List<CsticValueModel> filter(final List<CsticValueModel> values, final CsticModel cstic)
	{
		return cstic.isVisible() ? new ArrayList<>(values) : Collections.emptyList();
	}


	@Override
	public List<CsticValueModel> noMatch(final List<CsticValueModel> values, final CsticModel cstic)
	{
		return cstic.isVisible() ? Collections.emptyList() : new ArrayList<>(values);
	}

}
