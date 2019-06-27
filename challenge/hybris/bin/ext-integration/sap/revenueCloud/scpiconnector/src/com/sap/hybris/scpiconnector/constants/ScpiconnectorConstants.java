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
package com.sap.hybris.scpiconnector.constants;

/**
 * Global class for all Scpiconnector constants. You can add global constants for your extension into this class.
 */
public final class ScpiconnectorConstants extends GeneratedScpiconnectorConstants
{
	public static final String EXTENSIONNAME = "scpiconnector";

	private ScpiconnectorConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

    public static final String PLATFORM_LOGO_CODE = "scpiconnectorPlatformLogo";
	
	public static final String BASE_URL = "scpi.connector.baseurl";
	public static final String USERNAME = "scpi.connector.username";
	public static final String PASSWORD = "scpi.connector.password";
	public static final String PROXY_URL = "scpi.connector.proxy.url";

}
