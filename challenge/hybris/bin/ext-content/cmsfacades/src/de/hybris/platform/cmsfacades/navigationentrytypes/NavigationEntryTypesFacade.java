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
package de.hybris.platform.cmsfacades.navigationentrytypes;


import de.hybris.platform.cmsfacades.data.NavigationEntryTypeData;

import java.util.List;


/**
 * Navigation Entry Types facade interface which deals with methods related to navigation node entry types operations.
 * @deprecated since 1811 - no longer needed
 */
@Deprecated
public interface NavigationEntryTypesFacade
{

	/**
	 * Get the navigation entry types available. The types are defined in configuration time and are used for clients
	 * that need to know which types that are supported on the current implementation.
	 *
	 * @return a list of {@link NavigationEntryTypeData}
	 */
	List<NavigationEntryTypeData> getNavigationEntryTypes();

}

