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
package de.hybris.platform.assistedserviceyprofilefacades.data;

import java.util.Comparator;
import java.util.Map;

import de.hybris.platform.yaasyprofileconnect.yaas.Affinity;
import org.apache.log4j.Logger;


/**
 *
 * Used for Affinities sorting by their 'recentScore' field.
 */
public class RecentlyViewedComparator implements Comparator<Map.Entry<String,Affinity>>
{
	private static final Logger LOG = Logger.getLogger(RecentlyViewedComparator.class);

	@Override
	public int compare(final Map.Entry<String,Affinity> affinityData1, final Map.Entry<String,Affinity> affinityData2)
	{
		if (affinityData2.getValue().getRecentScore() != null
				&& affinityData1.getValue().getRecentScore() != null)
		{
			try
			{
				return affinityData2.getValue().getRecentScore().compareTo(affinityData1.getValue().getRecentScore());
			}
			catch (final Exception exp)
			{
				LOG.error("Problem happend during comparing recently updated affinities with invalid 'RecentScore' value", exp);

			}
		}
		return 0;
	}
}