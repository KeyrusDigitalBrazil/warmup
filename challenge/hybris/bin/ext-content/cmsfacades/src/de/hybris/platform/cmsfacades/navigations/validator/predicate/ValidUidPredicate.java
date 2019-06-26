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
package de.hybris.platform.cmsfacades.navigations.validator.predicate;


import de.hybris.platform.cms2.constants.Cms2Constants;

import java.util.function.Predicate;

import org.apache.commons.lang.StringUtils;


/**
 * Validates if the Navigation Node UID is valid
 * @deprecated since 1811 - no longer needed
 */
@Deprecated
public class ValidUidPredicate implements Predicate<String>
{
	@Override
	public boolean test(final String target)
	{
		if (StringUtils.endsWithIgnoreCase(Cms2Constants.ROOT, target))
		{
			return false;
		}
		return true;
	}
}
