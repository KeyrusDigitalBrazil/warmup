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
package de.hybris.platform.yaasyprofileconnect.constants;

/**
 * Global class for all Yaasyprofileconnect constants. You can add global constants for your extension into this class.
 */
public final class YaasyprofileconnectConstants extends GeneratedYaasyprofileconnectConstants
{
	public static final String EXTENSIONNAME = "yaasyprofileconnect";

	public static final String SCHEMA_COMMERCE_PRODUCT_AFFINITY = "insights.affinities.products";
	public static final String SCHEMA_COMMERCE_CATEGORY_AFFINITY = "insights.affinities.categories";
	public static final String SCHEMA_COMMERCE_DEVICE_AFFINITY = "userAgents";

	//Yaas Identity Service
	public static final String IDENTITY_TYPE_EMAIL = "email";
	public static final String IDENTITY_ORIGIN_USER_ACCOUNT = "userAccount";

	private YaasyprofileconnectConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
