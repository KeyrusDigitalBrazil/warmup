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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Utility class for jsps and tags related to the Configuration Overview Page.
 */
public class ConfigOverviewFilterEvaluator
{

	private ConfigOverviewFilterEvaluator()
	{
		// Utility class - private constructor
	}

	/**
	 * @param overviewData
	 * @return <code>true</code> only if there is a least either one cstic- or one group-related filter active
	 */
	public static boolean hasAppliedFilters(final OverviewUiData overviewData)
	{
		if (CollectionUtils.isNotEmpty(overviewData.getCsticFilterList())
				|| CollectionUtils.isNotEmpty(overviewData.getGroupFilterList()))
		{
			return overviewData.getCsticFilterList().stream().anyMatch(filter -> filter.isSelected())
					|| overviewData.getGroupFilterList().stream().anyMatch(filter -> filter.isSelected());
		}
		return false;
	}
}
