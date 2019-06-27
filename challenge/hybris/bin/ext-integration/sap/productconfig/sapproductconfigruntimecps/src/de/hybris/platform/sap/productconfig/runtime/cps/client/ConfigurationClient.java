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
package de.hybris.platform.sap.productconfig.runtime.cps.client;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.OAuth;


/**
 * Decorating the {@link ConfigurationClientBase} with enforced authorization via OAuth.
 */
@OAuth
@Control(timeout = "${sapproductconfigruntimecps.charon.timeout:15000}")
public interface ConfigurationClient extends ConfigurationClientBase
{
	// empty - just to enforce authorization via OAuth2
}
