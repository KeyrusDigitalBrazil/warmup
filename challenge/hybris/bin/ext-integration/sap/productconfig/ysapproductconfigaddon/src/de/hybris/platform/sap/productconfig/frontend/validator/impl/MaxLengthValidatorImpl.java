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
package de.hybris.platform.sap.productconfig.frontend.validator.impl;

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.frontend.validator.CsticValueValidator;

import org.apache.log4j.Logger;
import org.springframework.validation.Errors;


/**
 * Enforeces that a cstic value does not exceed the maximum length.<br>
 * This is also checked on the UI, for example by setting the maxLength attribute of the corresponding input field.
 * However we can not no trust the UI. An attacker might manipulate the request and/or the UI to inject malicious code
 * by sending much longer values. Enforcing the maxLength of the value, makes it much more difficult for an Atttacker to
 * inject malicious code.
 */
public class MaxLengthValidatorImpl implements CsticValueValidator
{

	private static final String NULL_VALUE = "NULL_VALUE";
	private static final Logger LOG = Logger.getLogger(MaxLengthValidatorImpl.class);

	private static final int MAX_LENGTH_FALLBACK = 60;

	protected int getMaxLengthFallback()
	{
		return MAX_LENGTH_FALLBACK;
	}

	@Override
	public String validate(final CsticData cstic, final Errors errorObj, final String value)
	{
		String newValue = value;
		final int maxlength = getMaxLength(cstic);
		if (value.length() > maxlength && !NULL_VALUE.equals(value))
		{
			newValue = value.substring(0, maxlength);
			LOG.warn("Value for cstic '" + cstic.getKey() + "' exceeded max value length of " + maxlength
					+ " and was therefore chopped to the max length. Original value was: " + value);
		}
		return newValue;
	}

	protected int getMaxLength(final CsticData cstic)
	{
		int maxlength = cstic.getMaxlength();
		if (0 == maxlength)
		{
			maxlength = getMaxLengthFallback();
		}
		return maxlength;
	}

	@Override
	public boolean appliesTo(final CsticData cstic)
	{
		return true;
	}

	@Override
	public boolean appliesToValues()
	{
		return true;
	}

	@Override
	public boolean appliesToFormattedValues()
	{
		return true;
	}

	@Override
	public boolean appliesToAdditionalValues()
	{
		return true;
	}

}
