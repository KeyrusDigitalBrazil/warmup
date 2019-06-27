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

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


/**
 * Utility class for logging, collects statistical data regarding the size of the dynamic runtime configuration.<br>
 * The following data is collected:<br>
 * <ul>
 * <li>total number of values</li>
 * <li>total number of cstics</li>
 * <li>total number of UI groups</li>
 * <li>maximum nesting depth</li>
 * </ul>
 */
public class UiDataStats
{
	private int numValues = 0;
	private int numCstics = 0;
	private int numUiGroups = 0;
	private int level = 0;
	private int maxlevel = 0;

	/**
	 * Analyzes recursively the runtime configuration, or the provided subtree, using the provided group as root.
	 * {@link UiDataStats#toString()} will return the results as formatted string.<br>
	 * <b>The result will only be accurate for the first invocation. To count again, create a new instance.</b>
	 *
	 * @param groups
	 *           root group
	 */
	public void countCstics(final List<UiGroupData> groups)
	{
		if (null != groups)
		{
			level += 1;
			if (maxlevel < level)
			{
				maxlevel = level;
			}
			for (final UiGroupData group : groups)
			{
				numUiGroups += 1;
				final List<CsticData> cstics = group.getCstics();
				if (null != cstics)
				{
					numCstics += cstics.size();
					countValues(cstics);
				}
				countCstics(group.getSubGroups());

			}
			level -= 1;
		}
	}

	protected void countValues(final List<CsticData> cstics)
	{
		for (final CsticData cstic : cstics)
		{
			if (null != cstic.getDomainvalues())
			{
				numValues += cstic.getDomainvalues().size();
			}
		}
	}

	@Override
	public String toString()
	{
		final NumberFormat decFormat = DecimalFormat.getInstance(Locale.ENGLISH);
		return "UiModelStats: numberValues=" + decFormat.format(numValues) + "; numberCstics=" + decFormat.format(numCstics)
				+ "; numUiGroups=" + decFormat.format(numUiGroups) + "; maxlevel=" + maxlevel;
	}
}
