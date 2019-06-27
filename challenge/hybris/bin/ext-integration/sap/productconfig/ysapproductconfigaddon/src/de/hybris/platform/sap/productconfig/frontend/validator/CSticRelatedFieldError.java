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
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigaddonConstants;

import org.springframework.validation.FieldError;


/**
 * A {@link FieldError} which can be associated with a cstic on the UI.
 */
public class CSticRelatedFieldError extends FieldError
{

	private final CsticData cstic;
	private static final int PRIME = 31;

	/**
	 * Default constructor.
	 *
	 * @param cstic
	 *           cstic causing the error
	 * @param path
	 *           path to the UI field causing the error
	 * @param rejectedValue
	 *           user input causing the error
	 * @param errorCodes
	 *           error codes
	 * @param defaultMessage
	 *           message to be displayed on the UI
	 */
	public CSticRelatedFieldError(final CsticData cstic, final String path, final String rejectedValue, final String[] errorCodes,
			final String defaultMessage)
	{
		super(SapproductconfigaddonConstants.CONFIG_ATTRIBUTE, path, rejectedValue, false, errorCodes, null, defaultMessage);
		this.cstic = cstic;
	}

	/**
	 * @return cstic causing this error
	 */
	public CsticData getCstic()
	{
		return cstic;
	}


	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = PRIME * result + ((cstic == null) ? 0 : cstic.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (!(this.getClass() == obj.getClass()))
		{
			return false;
		}
		final CSticRelatedFieldError other = (CSticRelatedFieldError) obj;
		if (cstic == null)
		{
			if (other.cstic != null)
			{
				return false;
			}
		}
		else if (!cstic.equals(other.cstic))
		{
			return false;
		}
		return true;
	}

}
