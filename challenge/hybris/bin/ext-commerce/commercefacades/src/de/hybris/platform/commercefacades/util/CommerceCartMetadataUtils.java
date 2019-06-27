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
package de.hybris.platform.commercefacades.util;

import de.hybris.platform.commercefacades.util.builder.CommerceCartMetadataBuilder;


/**
 * Utility class for commerce cart metadata bean.
 */
public final class CommerceCartMetadataUtils
{
	private CommerceCartMetadataUtils()
	{
		throw new IllegalAccessError("Utility class may not be instantiated");
	}

	/**
	 * Creates a new {@link CommerceCartMetadataBuilder} instance.
	 *
	 * @return the new {@link CommerceCartMetadataBuilder}
	 */
	public static CommerceCartMetadataBuilder metadataBuilder()
	{
		return new CommerceCartMetadataBuilder();
	}
}
