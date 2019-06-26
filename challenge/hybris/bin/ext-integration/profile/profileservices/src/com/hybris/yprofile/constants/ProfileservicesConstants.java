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
package com.hybris.yprofile.constants;

/**
 * Global class for all Profileservices constants. You can add global constants for your extension into this class.
 */
public final class ProfileservicesConstants extends GeneratedProfileservicesConstants
{
	public static final String EXTENSIONNAME = "profileservices";
    /**
     * Session attribute for logged in users
     */
    public static final String USER_CONSENTS = "user-consents";
    /**
     * Cookie for anonymous user
     */
    public static final String ANONYMOUS_CONSENTS = "anonymous-consents";
    /**
     * Consent code for Profile
     */
    public static final String PROFILE_CONSENT = "PROFILE";
    /**
     * Expected consent value
     */
    public static final String CONSENT_GIVEN = "GIVEN";
    /**
     * Expected consent value
     */
    public static final String CONSENT_WITHDRAWN = "WITHDRAWN";
    /**
     * Cookie and Session attribute key
     */
    public static final String PROFILE_CONSENT_GIVEN = "profile.consent.given";
    /**
     * Cookie and Session attribute key
     */
    public static final String PROFILE_TRACKING_PAUSE = "profile.tracking.pause";
    /**
     * Profile tag url identifier
     */
    public static final String PROFILE_TAG_URL = "ProfileTagUrl";
    /**
     * Profile tag configuration url identifier
     */
    public static final String PROFILE_TAG_CONFIG_URL = "ProfileTagConfigUrl";

    private ProfileservicesConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

    public static final String PLATFORM_LOGO_CODE = "profileservicesPlatformLogo";
}
