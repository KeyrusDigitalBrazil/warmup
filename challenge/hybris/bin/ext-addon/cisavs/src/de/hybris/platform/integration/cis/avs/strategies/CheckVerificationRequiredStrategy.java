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
package de.hybris.platform.integration.cis.avs.strategies;

import de.hybris.platform.core.model.user.AddressModel;


/**
 * Strategy that defines if we should call the address verification external service.
 */
public interface CheckVerificationRequiredStrategy
{
	/**
	 * Check if the address should be verified.
	 * 
	 * @param addressToVerify
	 *           A hybris address model
	 * @return true if the address should be verified, false otherwise
	 */
	boolean isVerificationRequired(AddressModel addressToVerify);
}
