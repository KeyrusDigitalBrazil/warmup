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

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given string has a valid length.
 * <p>
 * Returns <tt>TRUE</tt> if the string has a valid length; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class ValidStringLengthPredicate implements Predicate<String>
{

	private int maxLength;

	@Override
	public boolean test(String target)
	{
		return target == null || target.length() <= getMaxLength();
	}

	protected int getMaxLength()
	{
		return maxLength;
	}

	@Required
	public void setMaxLength(int maxLength)
	{
		this.maxLength = maxLength;
	}

}
