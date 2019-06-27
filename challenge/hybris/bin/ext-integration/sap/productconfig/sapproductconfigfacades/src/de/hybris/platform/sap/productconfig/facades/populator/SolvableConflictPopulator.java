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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConflictData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.FirstOrLastGroupType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;


/**
 * Populates conflicts from model to DTO representation
 */
public class SolvableConflictPopulator implements Populator<ConfigModel, ConfigurationData>
{

	private static final Logger LOG = Logger.getLogger(SolvableConflictPopulator.class);


	@Override
	public void populate(final ConfigModel source, final ConfigurationData target)
	{



		final List<SolvableConflictModel> solvableConflicts = source.getSolvableConflicts();
		if (solvableConflicts != null)
		{
			if (target.getGroups() == null || target.getCsticGroupsFlat() == null)
			{
				throw new IllegalArgumentException("ConfigData must have lists of groups available to add conflicts");
			}

			final List<ComparableConflictGroup> conflictGroups = createConflictList(target, solvableConflicts);

			//We need to have two instances of the list of conflicts, one for the group list
			//and one for the flat cstic list. Therefore we need to work with clone, thus ArrayList
			//instead of List
			final ArrayList<UiGroupData> conflictUiGroups = conflictGroups//
					.stream() //
					.map(SolvableConflictPopulator::conflictGroupToUiGroup) //
					.collect(Collectors.toCollection(ArrayList::new));

			final List<UiGroupData> conflictHeaderList = createConflictHeader(conflictUiGroups);

			final List<UiGroupData> conflictUiGroupsFlat = (List<UiGroupData>) conflictUiGroups.clone();

			conflictHeaderList.addAll(target.getGroups());
			target.setGroups(conflictHeaderList);

			conflictUiGroupsFlat.addAll(target.getCsticGroupsFlat());
			target.setCsticGroupsFlat(conflictUiGroupsFlat);

		}
	}

	protected List<UiGroupData> createConflictHeader(final List<UiGroupData> conflictGroups)
	{
		final List<UiGroupData> conflictHeaderList = new ArrayList(1); // By definition only one conflict header exists
		// Create conflict header only if conflicts exist
		if (!conflictGroups.isEmpty())
		{
			final UiGroupData conflictHeader = new UiGroupData();
			conflictHeader.setGroupType(GroupType.CONFLICT_HEADER);
			conflictHeader.setConfigurable(true);
			conflictHeader.setCollapsed(false);
			conflictHeader.setId(GroupType.CONFLICT_HEADER.toString());
			conflictHeader.setName(GroupType.CONFLICT_HEADER.toString());
			conflictHeader.setFirstOrLastGroup(FirstOrLastGroupType.INTERJACENT);
			conflictHeader.setSubGroups(conflictGroups);
			conflictHeader.setCstics(new ArrayList<CsticData>());
			conflictHeader.setNumberErrorCstics(conflictGroups.size());
			conflictHeaderList.add(conflictHeader);

		}
		return conflictHeaderList;
	}


	protected List<ComparableConflictGroup> createConflictList(final ConfigurationData target,
			final List<SolvableConflictModel> solvableConflicts)
	{
		final List<ComparableConflictGroup> conflictUiGroups = new ArrayList<>();


		for (final SolvableConflictModel solvableConflict : solvableConflicts)
		{
			conflictUiGroups.add(createConflictUiGroup(solvableConflict, target));
		}
		Collections.sort(conflictUiGroups);
		return conflictUiGroups;
	}

	protected static UiGroupData conflictGroupToUiGroup(final ComparableConflictGroup conflictGroup)
	{
		return conflictGroup;
	}

	protected ComparableConflictGroup createConflictUiGroup(final SolvableConflictModel solvableConflict,
			final ConfigurationData configurationData)
	{
		final String conflictDescription = solvableConflict.getDescription();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Creating conflict UI group for: " + conflictDescription);
		}
		final ComparableConflictGroup uiGroup = new ComparableConflictGroup();
		uiGroup.setCsticGroupsFlat(configurationData.getCsticGroupsFlat());
		uiGroup.setDescription(conflictDescription);
		uiGroup.setGroupType(GroupType.CONFLICT);
		uiGroup.setConfigurable(true);
		uiGroup.setCollapsed(false);
		final List<CsticData> cstics = createCstics(solvableConflict, configurationData);
		uiGroup.setCstics(cstics);
		uiGroup.setName(determineConflictGroupName(conflictDescription, cstics));
		uiGroup.setId(GroupType.CONFLICT.toString() + solvableConflict.getId());
		final List<UiGroupData> subGroups = new ArrayList<>();
		uiGroup.setSubGroups(subGroups);
		return uiGroup;
	}


	/**
	 * Determine the name of the conflict group. By default it is the language dependent name of the first
	 * characteristic. Fallback is conflictDescription
	 *
	 * @param conflictDescription
	 * @param cstics
	 * @return conflict group name
	 */
	protected String determineConflictGroupName(final String conflictDescription, final List<CsticData> cstics)
	{
		String conflictGroupName = conflictDescription;
		if (cstics != null && !cstics.isEmpty())
		{
			conflictGroupName = cstics.get(0).getLangdepname();
		}
		return conflictGroupName;
	}


	protected List<CsticData> createCstics(final SolvableConflictModel solvableConflict, final ConfigurationData configurationData)
	{
		final List<CsticData> cstics = new ArrayList<>();
		final List<ConflictingAssumptionModel> conflictingAssumptions = solvableConflict.getConflictingAssumptions();
		if (conflictingAssumptions != null)
		{
			for (final ConflictingAssumptionModel conflictingAssumption : conflictingAssumptions)
			{
				final List<CsticData> foundCstics = findCsticsInConfiguration(configurationData, conflictingAssumption);
				if (!foundCstics.isEmpty())
				{
					// add conflict data to all found cstics
					createConflictData(foundCstics, solvableConflict);
					// The first found occurrence is added to list of cstics of conflict
					cstics.add(foundCstics.get(0));
				}
			}
		}
		return cstics;
	}


	protected void createConflictData(final List<CsticData> cstics, final SolvableConflictModel solvableConflict)
	{
		final String conflictDescription = solvableConflict.getDescription();
		final ConflictData conflictData = new ConflictData();
		conflictData.setText(conflictDescription);

		for (int i = 0; i < cstics.size(); i++)
		{
			final CsticData currentCstic = cstics.get(i);
			List<ConflictData> conflicts = currentCstic.getConflicts();
			if (null == conflicts || conflicts.equals(Collections.emptyList()))
			{
				conflicts = new ArrayList<>(1);
				currentCstic.setConflicts(conflicts);
			}
			conflicts.add(conflictData);
			currentCstic.setCsticStatus(CsticStatusType.CONFLICT);
		}
	}


	/**
	 * Compiles list of cstics which match a conflicting assumption
	 *
	 * @param configurationData
	 * @param conflictingAssumption
	 * @return List of cstics, not null
	 */
	protected List<CsticData> findCsticsInConfiguration(final ConfigurationData configurationData,
			final ConflictingAssumptionModel conflictingAssumption)
	{
		final String csticName = conflictingAssumption.getCsticName();
		final String instanceId = conflictingAssumption.getInstanceId();
		final List<CsticData> cstics = findCsticsInGroups(configurationData.getGroups(), csticName, instanceId);
		if (cstics != null && cstics.isEmpty())
		{
			return Collections.emptyList();
		}
		else
		{
			return cstics;
		}

	}


	protected List<CsticData> findCsticsInGroups(final List<UiGroupData> groups, final String csticName, final String instanceId)
	{
		final List<CsticData> cstics = new ArrayList(1);
		if (groups != null)
		{
			for (final UiGroupData group : groups)
			{
				final CsticData cstic = findCsticInCsticList(group.getCstics(), csticName, instanceId);
				if (cstic != null)
				{
					cstics.add(cstic);
				}
				final List<CsticData> returnedCstics = findCsticsInGroups(group.getSubGroups(), csticName, instanceId);
				if (returnedCstics != null && !returnedCstics.isEmpty())
				{
					cstics.addAll(returnedCstics);
				}
			}
		}
		return cstics;
	}

	protected CsticData findCsticInCsticList(final List<CsticData> cstics, final String csticName, final String instanceId)
	{
		if (cstics != null)
		{
			for (final CsticData cstic : cstics)
			{

				final String instanceAtChar = cstic.getInstanceId();
				if (instanceAtChar == null)
				{
					throw new IllegalArgumentException("Cstic " + cstic.getName() + " must carry an instance ID");
				}
				if (cstic.getName().equals(csticName) && instanceAtChar.equals(instanceId))
				{
					return cstic;
				}
			}
		}
		return null;
	}



}
