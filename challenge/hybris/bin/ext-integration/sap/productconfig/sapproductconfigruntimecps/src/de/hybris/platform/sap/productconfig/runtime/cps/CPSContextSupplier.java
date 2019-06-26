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

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.common.CPSContextInfo;

import java.util.List;


/**
 * Provide context for configuration service calls.
 */
public interface CPSContextSupplier
{
	/**
	 * Retrieves context for configuration service calls
	 * 
	 * @param productCode
	 *           Code of product in internal SAP format
	 *
	 * @return configuration context
	 */
	List<CPSContextInfo> retrieveContext(String productCode);
}
