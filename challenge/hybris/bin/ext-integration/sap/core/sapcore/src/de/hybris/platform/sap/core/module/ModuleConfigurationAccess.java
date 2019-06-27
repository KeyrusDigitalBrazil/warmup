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
package de.hybris.platform.sap.core.module;

import de.hybris.platform.sap.core.configuration.SAPConfigurationService;


/**
 * Interface to access runtime module configuration data.
 */
public interface ModuleConfigurationAccess extends SAPConfigurationService
{

	/**
	 * Returns the module id of the module configuration access.
	 * 
	 * @return Module id
	 */
	public String getModuleId();

}
