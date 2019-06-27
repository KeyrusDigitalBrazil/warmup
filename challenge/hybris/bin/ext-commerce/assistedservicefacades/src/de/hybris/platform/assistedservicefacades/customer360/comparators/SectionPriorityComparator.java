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
package de.hybris.platform.assistedservicefacades.customer360.comparators;

import de.hybris.platform.assistedservicefacades.customer360.Section;

import java.util.Comparator;


public class SectionPriorityComparator implements Comparator<Section>
{
	private static final Integer MAX_PRIORITY = Integer.valueOf(Integer.MAX_VALUE);

	@Override
	public int compare(final Section s1, final Section s2)
	{

		final Integer p1 = s1.getPriority() != null ? s1.getPriority() : MAX_PRIORITY;
		final Integer p2 = s2.getPriority() != null ? s2.getPriority() : MAX_PRIORITY;
		return p1.compareTo(p2);
	}
}