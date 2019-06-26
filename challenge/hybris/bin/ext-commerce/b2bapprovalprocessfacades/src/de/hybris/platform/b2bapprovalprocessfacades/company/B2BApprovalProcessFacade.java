/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.b2bapprovalprocessfacades.company;

import java.util.Map;


/**
 * Provides a facade for getting the processes for OrderApproval
 */
public interface B2BApprovalProcessFacade
{

	/**
	 * Get a collection of available business processes for OrderApproval
	 *
	 * @return A map where the key is process code and value is process name based on the current session locale
	 */
	Map<String, String> getProcesses();

}
