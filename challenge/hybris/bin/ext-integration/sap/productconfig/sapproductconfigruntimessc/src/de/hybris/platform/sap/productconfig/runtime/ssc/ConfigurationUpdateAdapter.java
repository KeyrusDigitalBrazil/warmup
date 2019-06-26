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
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;


/**
 * Responsible for updating a configuration to SSC
 */
public interface ConfigurationUpdateAdapter
{

	/**
	 * Updates a configuration in SSC
	 *
	 * @param configModel
	 *           config model
	 * @param plainId
	 * @param session
	 *           corresponding config session
	 * @return <code>true</code>, only if it was necessary to send an updare to the configuration egnine
	 */
	boolean updateConfiguration(ConfigModel configModel, String plainId, IConfigSessionClient session);

}
