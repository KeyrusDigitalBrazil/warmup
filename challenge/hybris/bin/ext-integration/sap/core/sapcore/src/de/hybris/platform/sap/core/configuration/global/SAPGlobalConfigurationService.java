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
package de.hybris.platform.sap.core.configuration.global;

import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;


/**
 * Interface to read global configuration properties.
 */
public interface SAPGlobalConfigurationService extends ConfigurationPropertyAccess
{
	/**
	 * Returns true if a SAPGlobalConfiguration exists.
	 *
	 * @return true if SAPGlobalConfiguration exists
	 */
	public boolean sapGlobalConfigurationExists();
}
