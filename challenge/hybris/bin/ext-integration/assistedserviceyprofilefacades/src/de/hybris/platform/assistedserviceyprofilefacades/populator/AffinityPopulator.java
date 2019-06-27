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

import de.hybris.platform.assistedserviceyprofilefacades.data.AffinityData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.yaasyprofileconnect.yaas.Affinity;

import java.util.Map;


/**
 * Default affinity populator that populates common affinity attributes : score, recentScore, recentViewCount.
 */
public class AffinityPopulator<SOURCE extends Map.Entry<String,Affinity>, TARGET extends AffinityData> implements Populator<SOURCE, TARGET>
{

	@Override
	public void populate(final SOURCE source, final TARGET affinityData)
	{
		final Affinity affinity = source.getValue();
		affinityData.setRecentScore(affinity.getRecentScore());
		affinityData.setRecentViewCount(affinity.getRecentViewCount());
		affinityData.setScore(affinity.getScore());
	}
}
