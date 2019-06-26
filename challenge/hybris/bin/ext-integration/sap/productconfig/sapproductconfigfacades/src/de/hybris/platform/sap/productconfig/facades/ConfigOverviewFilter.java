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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import java.util.List;


/**
 * Filter for characteristic values of the configuration overview.
 */
public interface ConfigOverviewFilter
{

	/**
	 * Filters the assigned values of the given characteristic. Filtering is only executed if the suitable filter ID is
	 * part of the passed appliedFilters. <br/>
	 * If the filter is not active the complete list of assigned values of the characteristic is returned.
	 *
	 * @param cstic
	 *           characteristic to be filtered
	 * @param appliedFilters
	 *           list of applied filters
	 * @return a list of assigned values valid for the filter
	 */
	List<CsticValueModel> filter(CsticModel cstic, List<FilterEnum> appliedFilters);

	/**
	 * Filters the assigned values of the given characteristic. Caller has to take care that filter is active.
	 *
	 * @param cstic
	 *           characteristic to be filtered
	 * @return a list of assigned values valid for the filter
	 */
	List<CsticValueModel> filter(CsticModel cstic);

	/**
	 * Filters the passed list of values and returns a list consisting of the values that match the given filter
	 * criterion.
	 *
	 * Caller has to take care that filter is active.
	 *
	 * @param values
	 *           list of values to be filtered
	 * @param cstic
	 *           characteristic to which values belong to
	 * @return list consisting of the values that match the given filter criterion
	 */
	List<CsticValueModel> filter(List<CsticValueModel> values, CsticModel cstic);

	/**
	 * Filters the passed list of values and returns a list consisting of the values that <b>DO NOT</b> match the given
	 * filter criterion.
	 *
	 * Caller has to take care that filter is active.
	 *
	 * @param values
	 *           list of values to be filtered
	 * @param cstic
	 *           characteristic to which values belong to
	 * @return list consisting of the values that do not match the given
	 */
	List<CsticValueModel> noMatch(List<CsticValueModel> values, CsticModel cstic);

	/**
	 * @param appliedFilters
	 *           list of applied filters
	 * @return true if the current filter is part of the list of applied filters
	 */
	boolean isActive(List<FilterEnum> appliedFilters);


}
