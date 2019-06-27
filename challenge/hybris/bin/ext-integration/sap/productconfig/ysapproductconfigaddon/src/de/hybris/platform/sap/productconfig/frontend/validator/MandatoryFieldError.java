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


/**
 * A mandatory field error is a specific validation error indicating that on value was maintained on the UI for a cstic
 * that is flagged mandatory by the configuration engine.<br>
 * In contrast to other validation errors, it is still safe to send user input causing a mandatory field error to the
 * configuration engine, hence it is treated within CPQ more like a 'warning'.<br>
 * Object is immutable.
 */
public class MandatoryFieldError extends CSticRelatedFieldError
{

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
	public MandatoryFieldError(final CsticData cstic, final String path, final String rejectedValue, final String[] errorCodes,
			final String defaultMessage)
	{
		super(cstic, path, rejectedValue, errorCodes, defaultMessage);
	}


}
