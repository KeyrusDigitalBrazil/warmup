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
package de.hybris.platform.assistedservicestorefront.constants;

/**
 * Global class for all Assistedservicestorefront constants. You can add global constants for your extension into this
 * class.
 */
public final class AssistedservicestorefrontConstants extends GeneratedAssistedservicestorefrontConstants
{
	public static final String EXTENSIONNAME = "assistedservicestorefront";
	public static final String REDIRECT_WITH_CART = "assistedservicestorefront.redirect.customer_and_cart";
	public static final String REDIRECT_WITH_ORDER = "assistedservicestorefront.redirect.order";
	public static final String REDIRECT_CUSTOMER_ONLY = "assistedservicestorefront.redirect.customer_only";
	public static final String REDIRECT_ERROR = "assistedservicestorefront.redirect.error";
	public static final String AIF_TIMEOUT = "assistedservicestorefront.aif.timeout";
	public static final int AIF_DEFAULT_TIMEOUT = 7000; //default timeout in milliseconds
	public static final String AIF_OVERVIEW_CART_ITMES_TO_BE_DISPLAYED = "aif.overview.cart.items.to.display";
	public static final int AIF_OVERVIEW_CART_ITMES_TO_BE_DISPLAYED_DEFAULT = 6;
	public static final String PROFILE_COOKIE_NAME = "assistedservicestorefront.profile.cookie.name";
	public static final int IMPERSISTENCE_COOKIE_INDEX = -1;
	public static final String ASM_REQUEST_PARAM = "asm";
	public static final String ASM_PROFILE_TRACKING_PAUSE_COOKIE = "profile.tracking.pause";

	// Default parent group id for all AS agents
	public static final String AS_AGENT_GROUP_UID = "asagentgroup";


	private AssistedservicestorefrontConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
