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
package de.hybris.platform.chineseprofileservices.strategies.impl;

import de.hybris.platform.chineseprofileservices.strategies.VerificationCodeGenerationStrategy;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Required;


/**
 * A simple generation strategy by using Random.
 */
public class ChineseVerificationCodeGenerationStrategy implements VerificationCodeGenerationStrategy
{

	private int length;

	@Override
	public String generate()
	{
		final SecureRandom random = new SecureRandom();
		final StringBuilder builder = new StringBuilder();
		if (length <= 0)
		{
			length = 4;
		}
		for (int i = 0; i < length; i++)
		{
			builder.append(random.nextInt(10));
		}
		return builder.toString();
	}

	protected int getLength()
	{
		return length;
	}

	@Required
	public void setLength(int length)
	{
		this.length = length;
	}


}
