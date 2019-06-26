/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercefacades.i18n.comparators;

import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.util.AbstractComparator;


/**
 * Comparator for {@link CountryData} instances.
 */
public class CountryComparator extends AbstractComparator<CountryData>
{
	public static final CountryComparator INSTANCE = new CountryComparator();

	private CountryComparator()
	{
		// avoid instantiation
	}

	@Override
	protected int compareInstances(final CountryData country1, final CountryData country2)
	{
		final int result = compareValues(country1.getName(), country2.getName(), false);
		return result == EQUAL ? compareValues(country1.getIsocode(), country2.getIsocode(), true) : result;
	}
}
