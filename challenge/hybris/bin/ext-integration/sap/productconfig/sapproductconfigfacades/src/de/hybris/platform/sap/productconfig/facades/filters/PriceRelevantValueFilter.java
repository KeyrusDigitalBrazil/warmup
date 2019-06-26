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
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Filters out all cstics that have no influence on the price, meaning that they do not have any delta price.
 */
public class PriceRelevantValueFilter extends AbstractConfigOverviewFilter
{

	@Override
	public boolean isActive(final List<FilterEnum> appliedFilters)
	{
		return appliedFilters.contains(FilterEnum.PRICE_RELEVANT);
	}

	@Override
	public List<CsticValueModel> filter(final List<CsticValueModel> values, final CsticModel cstic)
	{
		return values.stream() //
				.filter(new FilterPredicate()) //
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<CsticValueModel> noMatch(final List<CsticValueModel> values, final CsticModel cstic)
	{
		return values.stream() //
				.filter(new FilterPredicate().negate()) //
				.collect(Collectors.toCollection(ArrayList::new));
	}

	static class FilterPredicate implements Predicate<CsticValueModel>
	{
		@Override
		public boolean test(final CsticValueModel value)
		{
			return !PriceModel.NO_PRICE.equals(value.getValuePrice());
		}
	}
}
