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
package de.hybris.platform.addressservices.strategies;

/**
 * A strategy interface for validating specific postcode
 */
public interface PostcodeValidateStrategy
{

	/**
	 * validate the specific postcode
	 *
	 * @param postcode
	 *           the specific postcode
	 * @return validated result
	 */
	default boolean validate(String postcode)
	{
		return true;
	}
}
