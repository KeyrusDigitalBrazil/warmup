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
package de.hybris.platform.sap.productconfig.facades.filters;

import de.hybris.platform.sap.productconfig.facades.ConfigOverviewGroupFilter;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Default implementation of {@link ConfigOverviewGroupFilter}
 */
public class DefaultOverviewGroupFilter implements ConfigOverviewGroupFilter
{

	@Override
	public Set<String> getGroupsToBeDisplayed(final InstanceModel instanceModel, final Set<String> filteredOutgroups)
	{
		final List<CsticGroup> csticModelGroups = instanceModel.retrieveCsticGroupsWithCstics();
		final List<InstanceModel> subInstances = instanceModel.getSubInstances();
		final Set<String> groupsToBeDisplayed = new HashSet<>();

		if (filteredOutgroups.isEmpty())
		{
			fillGroupToBeDisplayedFromCsticGroup(csticModelGroups, groupsToBeDisplayed);
			fillGroupToBeDisplayedFromInstanceModel(subInstances, groupsToBeDisplayed);
		}
		else
		{
			for (final String groupId : filteredOutgroups)
			{
				groupsToBeDisplayed.add(groupId);
			}
		}
		return groupsToBeDisplayed;
	}

	protected void fillGroupToBeDisplayedFromCsticGroup(final List<CsticGroup> csticModelGroups,
			final Set<String> groupsToBeDisplayed)
	{
		for (final CsticGroup csticGroup : csticModelGroups)
		{
			groupsToBeDisplayed.add(csticGroup.getName());
		}
	}

	protected void fillGroupToBeDisplayedFromInstanceModel(final List<InstanceModel> subInstances,
			final Set<String> groupsToBeDisplayed)
	{
		for (final InstanceModel subInstance : subInstances)
		{
			groupsToBeDisplayed.add(subInstance.getName());
		}
	}

}
