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
package de.hybris.platform.cmsfacades.common.predicate;

import java.util.function.Predicate;
import java.util.regex.Pattern;


/**
 * Predicate to test if a given string is a supported one.
 * <p>
 * Returns <tt>TRUE</tt> if the input is composed of alphanumeric or hyphen characters;  <tt>FALSE</tt> otherwise.
 * </p>
 */
public class OnlyHasSupportedCharactersPredicate implements Predicate<String>
{

	@Override
	public boolean test(final String target)
	{
		final Pattern regex = Pattern.compile("^[a-zA-Z0-9-_]+$");
		return regex.matcher(target).find();
	}

}
