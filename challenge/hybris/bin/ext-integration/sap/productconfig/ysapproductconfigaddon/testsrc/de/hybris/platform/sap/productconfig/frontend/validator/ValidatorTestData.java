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
package de.hybris.platform.sap.productconfig.frontend.validator;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConflictData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;

import java.util.ArrayList;
import java.util.List;


public class ValidatorTestData
{
	
	private ValidatorTestData()
	{
		// Utility class - private constructor to hide implicit public one
	}

	/**
	 * @return empty Configuration
	 */
	public static ConfigurationData createEmptyConfigurationWithDefaultGroup()
	{
		final ConfigurationData configuration = new ConfigurationData();
		final List<UiGroupData> csticGroups = new ArrayList<>();
		final List<CsticData> cstics = new ArrayList<>();
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId("DEFAULT");
		defaultGroup.setGroupType(GroupType.CSTIC_GROUP);
		defaultGroup.setCstics(cstics);
		defaultGroup.setGroupStatus(GroupStatusType.DEFAULT);
		csticGroups.add(defaultGroup);
		configuration.setGroups(csticGroups);
		return configuration;
	}

	public static UiGroupData createGroupWithNumeric(final String field, final String value)
	{
		return createGroupWithNumeric("DEFAULT", field, value);
	}

	public static UiGroupData createGroupWithNumeric(final String groupId, final String field, final String value)
	{
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId(groupId);
		defaultGroup.setGroupStatus(GroupStatusType.DEFAULT);
		defaultGroup.setVisited(true);
		final CsticData cstic = new CsticData();

		cstic.setType(UiType.NUMERIC);
		cstic.setValidationType(UiValidationType.NUMERIC);
		cstic.setFormattedValue(value);
		cstic.setName(field);
		cstic.setEntryFieldMask("-_____._____");
		cstic.setTypeLength(10);
		cstic.setNumberScale(5);

		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);
		defaultGroup.setCstics(cstics);

		return defaultGroup;
	}

	public static ConfigurationData createConfigurationWithNumeric(final String field, final String value)
	{
		final ConfigurationData configuration = createEmptyConfigurationWithDefaultGroup();

		final List<UiGroupData> csticGroups = new ArrayList<>();
		csticGroups.add(createGroupWithNumeric(field, value));
		configuration.setGroups(csticGroups);

		return configuration;
	}

	public static ConfigurationData createConfigurationWithNumericInSubGroup(final String field, final String value)
	{
		final ConfigurationData configuration = createEmptyConfigurationWithDefaultGroup();

		final List<UiGroupData> csticGroups = new ArrayList<>();
		final UiGroupData defaultGroup = new UiGroupData();
		defaultGroup.setId("GROUPWITHSUBGROUP");
		defaultGroup.setGroupStatus(GroupStatusType.DEFAULT);
		csticGroups.add(defaultGroup);
		configuration.setGroups(csticGroups);

		final ArrayList<UiGroupData> subGroups = new ArrayList<>();
		subGroups.add(createGroupWithNumeric(field, value));
		defaultGroup.setSubGroups(subGroups);

		return configuration;
	}

	public static ConfigurationData createConfigurationWithConflict(final String conflictText)
	{
		final ConfigurationData configuration = createEmptyConfigurationWithDefaultGroup();
		final CsticData cstic = new CsticData();

		final List<ConflictData> conflicts = new ArrayList<>();
		final ConflictData conflict = new ConflictData();
		conflict.setText(conflictText);
		conflicts.add(conflict);
		cstic.setConflicts(conflicts);

		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(cstic);

		configuration.getGroups().get(0).setCstics(cstics);
		return configuration;
	}

	public static ConfigurationData createConfigurationWithConflictHeader()
	{
		final ConfigurationData configuration = createEmptyConfigurationWithDefaultGroup();

		final UiGroupData conflictHeader = new UiGroupData();
		conflictHeader.setGroupType(GroupType.CONFLICT_HEADER);
		conflictHeader.setConfigurable(true);
		conflictHeader.setCollapsed(false);
		conflictHeader.setId(GroupType.CONFLICT_HEADER.toString());
		conflictHeader.setName(GroupType.CONFLICT_HEADER.toString());

		final ArrayList<UiGroupData> conflictGroups = new ArrayList<>();

		final UiGroupData conflictGroup = new UiGroupData();
		conflictGroup.setGroupType(GroupType.CONFLICT);
		conflictGroup.setConfigurable(true);
		conflictGroup.setCollapsed(false);
		conflictGroup.setId(GroupType.CONFLICT.toString());
		conflictGroup.setName(GroupType.CONFLICT.toString());
		conflictGroups.add(conflictGroup);

		conflictHeader.setSubGroups(conflictGroups);
		conflictHeader.setCstics(new ArrayList<>());

		configuration.getGroups().add(conflictHeader);

		return configuration;
	}

}
