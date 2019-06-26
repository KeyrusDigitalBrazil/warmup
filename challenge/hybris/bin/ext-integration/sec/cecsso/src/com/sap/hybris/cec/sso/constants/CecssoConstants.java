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
package com.sap.hybris.cec.sso.constants;

/**
 * Global class for all Cecsso constants. You can add global constants for your extension into this class.
 */
public final class CecssoConstants extends GeneratedCecssoConstants
{
	public static final String EXTENSIONNAME = "cecsso";

	private CecssoConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

	public static final String PLATFORM_LOGO_CODE = "cecssoPlatformLogo";
	public static final String SSO_REDIRECT_URL = "sso.redirect.url";
	public static final String DEFAULT_REDIRECT_URL = "https://localhost:9002/";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String JWT_PUBLIC_KEY = "sap.cec.jwt.public.key";
	public static final String JWT_PUBLIC_KEY_BEGIN = "-----BEGIN PUBLIC KEY-----";
	public static final String JWT_PUBLIC_KEY_END = "-----END PUBLIC KEY-----";
	public static final String JWT_SIGNATURE_ALGO = "SHA256withRSA";
	public static final String SSO_COOKIE_MAX_AGE = "sso.cookie.max.age";
	public static final String SSO_COOKIE_PATH = "sso.cookie.path";
	public static final String SSO_COOKIE_DOMAIN = "sso.cookie.domain";
	public static final String SSO_DEFAULT_COOKIE_DOMAIN = null;
	public static final int DEFAULT_COOKIE_MAX_AGE = 60;
	public static final String SSO_COOKIE_NAME = "sso.cookie.name";
	public static final String DEFAULT_COOKIE_PATH = "/";
	public static final String SSO_DEFAULT_COOKIE_NAME = "samlPassThroughToken";
}
