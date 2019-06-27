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
package de.hybris.platform.sap.productconfig.runtime.cps.strategy;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;


/**
 * Strategy for adding parent reference within {@link CPS Configuration}
 */
public interface CPSConfigurationParentReferenceStrategy
{
	/**
	 * Adds parent references to passed runtime configuration
	 * 
	 * @param cpsConfig
	 *           runtime configuration
	 */
	void addParentReferences(final CPSConfiguration cpsConfig);
}
