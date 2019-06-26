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
package de.hybris.platform.commerceservices.util;

import de.hybris.platform.commerceservices.util.builder.CommerceCartMetadataParameterBuilder;


/**
 * Utility class for commerce cart metadata parameter bean.
 */
public final class CommerceCartMetadataParameterUtils
{
	private CommerceCartMetadataParameterUtils()
	{
		throw new IllegalAccessError("Utility class may not be instantiated");
	}

	/**
	 * Creates a new {@link CommerceCartMetadataParameterBuilder} instance.
	 *
	 * @return the new {@link CommerceCartMetadataParameterBuilder}
	 */
	public static CommerceCartMetadataParameterBuilder parameterBuilder()
	{
		return new CommerceCartMetadataParameterBuilder();
	}
}
