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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.Set;


/**
 * Handling group filtering.
 */
public interface ConfigOverviewGroupFilter
{
	/**
	 * Checks which groups are filtered out and which are displayed.
	 *
	 * @param instanceModel
	 *           current instance
	 * @param filteredOutgroups
	 *           already filtered out groups
	 * @return ids of the groups to be displayed
	 */
	Set<String> getGroupsToBeDisplayed(final InstanceModel instanceModel, final Set<String> filteredOutgroups);
}
