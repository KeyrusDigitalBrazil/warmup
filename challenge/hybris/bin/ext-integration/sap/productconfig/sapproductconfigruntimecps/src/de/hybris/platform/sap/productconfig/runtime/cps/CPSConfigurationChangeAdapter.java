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
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Prepares input for characteristic value change(s)
 */
public interface CPSConfigurationChangeAdapter
{

	/**
	 * Prepare changed configuration as input for service from config model
	 *
	 * @param model
	 *           config model
	 * @return changes to be sent to service
	 */
	CPSConfiguration prepareChangedConfiguration(ConfigModel model);

}
