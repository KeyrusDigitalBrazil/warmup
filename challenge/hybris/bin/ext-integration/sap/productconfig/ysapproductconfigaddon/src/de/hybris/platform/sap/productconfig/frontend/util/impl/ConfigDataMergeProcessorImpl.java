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
package de.hybris.platform.sap.productconfig.frontend.util.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiGroupForDisplayData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigDataMergeProcessor;


/**
 * Default implementation of {@link ConfigDataMergeProcessor}.<br>
 * Used to merge the UI user input with the existing data from the session. This enables us to not send the entire
 * configuration to the UI and to keep the request small. <br>
 * <br>
 * <b>Implementation note: The bean must have prototype scope as we use class members to process the path to the group
 * which is currently selected for display.</b>
 */
public class ConfigDataMergeProcessorImpl implements ConfigDataMergeProcessor
{

	private static final Logger LOG = Logger.getLogger(ConfigDataMergeProcessorImpl.class);

	private ConfigurationFacade configFacade;

	/**
	 * We use this tokenizer to process the path of group IDs we got from the UI, e.g.
	 * "mainGroup,subGroup,subGroug3rdLevel"
	 */
	private StringTokenizer tokenizerGroupId;
	/**
	 * This tokenizer to process the path to the current subgroup to display, e.g. "groups[1].subGroups[2].subGroups[0]
	 */
	private StringTokenizer tokenizerPath;
	private int indexGroupToDisplay;
	private String idGroupToDisplay;
	private final Map<String, String> changedValue = new HashMap<>();

	protected void setTokenizerGroupId(final StringTokenizer tokenizerGroupId)
	{
		this.tokenizerGroupId = tokenizerGroupId;
	}

	protected void setTokenizerPath(final StringTokenizer tokenizerPath)
	{
		this.tokenizerPath = tokenizerPath;
	}

	@Override
	public void completeInput(final ConfigurationData targetConfigData)
	{
		if (!targetConfigData.isInputMerged())
		{

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Complete input for config data with [CONFIG_ID: " + targetConfigData.getConfigId() + "']");
			}

			final ConfigurationData configData = new ConfigurationData();
			configData.setConfigId(targetConfigData.getConfigId());
			configData.setKbKey(targetConfigData.getKbKey());
			final ConfigurationData sourceConfigData = getConfigFacade().getConfiguration(configData);
			mergeConfigurationData(sourceConfigData, targetConfigData);
		}
	}

	@Override
	public void mergeConfigurationData(final ConfigurationData source, final ConfigurationData target)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Merge source [SOURCE_CONFIG_ID: " + source.getConfigId() + "'] with target [TARGET_CONFIG_ID: "
					+ target.getConfigId() + "']");
		}

		if (CPQActionType.VALUE_CHANGED.equals(target.getCpqAction()) && source.isSingleLevel())
		{
			populateChangedValue(target);
		}

		// if all groups are collapsed, targetGroups is null
		final List<UiGroupData> targetGroups = target.getGroups();
		if (targetGroups != null)
		{
			long startTime = 0;
			if (LOG.isDebugEnabled())
			{
				startTime = System.currentTimeMillis();
			}

			final Map<String, Object> sourceConfigMap = prepareSourceConfiguration(source);
			updateTargetConfiguration(target, sourceConfigMap);
			target.setInputMerged(true);

			if (LOG.isDebugEnabled())
			{
				final long duration = System.currentTimeMillis() - startTime;
				LOG.debug("MERGE of UI META DATA  took " + duration + " ms");
			}

		}
	}

	protected void populateChangedValue(final ConfigurationData target)
	{
		String fieldPath = target.getFocusId();
		fieldPath = fieldPath.replaceAll("key", "value");

		final PathExtractor extractor = new PathExtractor(fieldPath);
		final int groupIndex = extractor.getGroupIndex();
		final int csticIndex = extractor.getCsticsIndex();
		UiGroupData group = target.getGroups().get(groupIndex);
		for (int i = 0; i < extractor.getSubGroupCount(); i++)
		{
			group = group.getSubGroups().get(extractor.getSubGroupIndex(i));
		}
		final CsticData cstic = group.getCstics().get(csticIndex);

		if (cstic.getFormattedValue() != null)
		{
			changedValue.put(cstic.getKey(), cstic.getFormattedValue());
		}
		else
		{
			changedValue.put(cstic.getKey(), cstic.getValue());
		}
	}

	protected Map<String, Object> prepareSourceConfiguration(final ConfigurationData source)
	{
		final Map<String, Object> sourceConfigMap = new HashMap<>();

		final List<UiGroupData> groups = source.getGroups();

		for (final UiGroupData group : groups)
		{
			processGroup(group, sourceConfigMap);
		}

		return sourceConfigMap;
	}

	protected void processGroup(final UiGroupData group, final Map<String, Object> sourceConfigMap)
	{
		sourceConfigMap.put(group.getId(), group);

		// process subgroups
		final List<UiGroupData> subGroups = group.getSubGroups();
		if (CollectionUtils.isNotEmpty(subGroups))
		{
			for (final UiGroupData subGroup : subGroups)
			{
				processGroup(subGroup, sourceConfigMap);
			}
		}

		// process cstics
		final List<CsticData> cstics = group.getCstics();
		if (CollectionUtils.isNotEmpty(cstics))
		{
			for (final CsticData cstic : cstics)
			{
				sourceConfigMap.put(cstic.getKey(), cstic);
			}
		}
	}

	/**
	 * Restores the data which was passed from the UI. If a group with empty parents is provided, the parent groups are
	 * also equipped with the group ID's, because we need this info later on when digesting the user inputs. <br>
	 * <br>
	 * This information is retrieved from to strings, containing the list of group ID's and a list of indices which
	 * indicate where to find these groups: {@link UiGroupForDisplayData#getPath()},
	 * {@link UiGroupForDisplayData#getGroupIdPath()}. <br>
	 * <br>
	 * . Content can look like this: "MainGroup,SubGroup" and "groups[0].subGroups[3]"
	 *
	 * @param target
	 * @param sourceConfigMap
	 */
	protected void updateTargetConfiguration(final ConfigurationData target, final Map<String, Object> sourceConfigMap)
	{

		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}

		final List<UiGroupData> groups = target.getGroups();

		// initialize members for restoring the group hierarchy of the currently
		// displayed group.
		// The merge processor needs to work also in case this info is not
		// provided
		indexGroupToDisplay = -1;
		idGroupToDisplay = null;

		final UiGroupForDisplayData groupToDisplay = target.getGroupToDisplay();
		if (groupToDisplay != null && groupToDisplay.getGroupIdPath() != null && groupToDisplay.getPath() != null)
		{
			tokenizerGroupId = new StringTokenizer(groupToDisplay.getGroupIdPath(), ",");
			tokenizerPath = new StringTokenizer(groupToDisplay.getPath(), ".");
			calculateCurrentIndicesForPathToDisplayGroup();
		}

		updateGroupList(groups, sourceConfigMap);

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("Update target config with [TARGET_CONFIG_ID: " + target.getConfigId() + "'] took " + duration + " ms");

		}

	}

	/**
	 * Update list of groups, calls itself recursively for sub groups
	 *
	 * @param groups
	 * @param sourceConfigMap
	 */
	protected void updateGroupList(final List<UiGroupData> groups, final Map<String, Object> sourceConfigMap)
	{
		if (groups == null)
		{
			return;
		}

		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
		}

		final ArrayList<Integer> outdatedConflictGroupIndices = new ArrayList();
		for (int i = 0; i < groups.size(); i++)
		{
			final UiGroupData group = groups.get(i);
			if (i == indexGroupToDisplay)
			{
				group.setId(idGroupToDisplay);
				calculateCurrentIndicesForPathToDisplayGroup();
			}


			if (group.getId() != null && !updateGroup(group, sourceConfigMap))
			{
				outdatedConflictGroupIndices.add(Integer.valueOf(i));
				continue;
			}
			updateGroupList(group.getSubGroups(), sourceConfigMap);

			if (!GroupType.CONFLICT_HEADER.equals(group.getGroupType()))
			{
				updateCstics(group, sourceConfigMap);
			}
		}

		// Remove out-dated conflict groups to avoid issues during executeUpdate later
		removeOutdatedConflictGroups(groups, outdatedConflictGroupIndices);

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("Update group list took " + duration + " ms");
		}

	}

	protected boolean updateGroup(final UiGroupData group, final Map<String, Object> sourceConfigMap)
	{
		final UiGroupData sourceGroup = (UiGroupData) sourceConfigMap.get(group.getId());
		if (sourceGroup == null)
		{
			if (group.getId().startsWith(GroupType.CONFLICT.toString()))
			{
				// conflict group is not valid anymore: mark it for deletion
				return false;
			}
			else
			{
				throw new IllegalArgumentException(
						"Group " + group.getId() + " is not part of the state which we want to merge with the UI input");
			}
		}

		group.setName(sourceGroup.getName());
		group.setGroupType(sourceGroup.getGroupType());

		return true;
	}

	protected void removeOutdatedConflictGroups(final List<UiGroupData> groups, final List<Integer> outdatedConflictGroupIndices)
	{
		for (int i = 0; i < outdatedConflictGroupIndices.size(); i++)
		{
			final int j = outdatedConflictGroupIndices.get(i).intValue();
			groups.remove(j);
		}
	}

	void calculateCurrentIndicesForPathToDisplayGroup()
	{
		indexGroupToDisplay = getCurrentIndex();
		idGroupToDisplay = getCurrentGroupId();
	}

	/**
	 * @return The next group from the list of group ID's (parents of the currently selected group)
	 */
	String getCurrentGroupId()
	{
		if (tokenizerGroupId.hasMoreTokens())
		{
			return tokenizerGroupId.nextToken();
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return The next index from the path to the currently selected group
	 */
	int getCurrentIndex()
	{
		if (tokenizerPath.hasMoreTokens())
		{
			final String firstPart = tokenizerPath.nextToken();
			final int start = firstPart.indexOf('[');
			final int end = firstPart.indexOf(']');
			if (start == -1 || start > end)
			{
				throw new IllegalArgumentException(
						"Path of group to display must be built using [], like groups[x].subGroups[y]. but was: " + firstPart);
			}
			return Integer.parseInt(firstPart.substring(start + 1, end));

		}
		else
		{
			return 0;
		}
	}

	protected void updateCstics(final UiGroupData group, final Map<String, Object> sourceConfigMap)
	{
		// update cstics
		final List<CsticData> cstics = group.getCstics();
		if (CollectionUtils.isNotEmpty(cstics))
		{

			long startTime = 0;

			if (LOG.isDebugEnabled())
			{
				startTime = System.currentTimeMillis();
			}

			for (final CsticData cstic : cstics)
			{
				// update cstic
				final CsticData sourceCstic = (CsticData) sourceConfigMap.get(cstic.getKey());
				if (sourceCstic == null)
				{
					continue;
				}
				cstic.setInstanceId(sourceCstic.getInstanceId());
				cstic.setName(sourceCstic.getName());
				cstic.setConflicts(sourceCstic.getConflicts());
				cstic.setLangdepname(sourceCstic.getLangdepname());
				cstic.setType(sourceCstic.getType());
				cstic.setTypeLength(sourceCstic.getTypeLength());
				cstic.setNumberScale(sourceCstic.getNumberScale());
				cstic.setEntryFieldMask(sourceCstic.getEntryFieldMask());
				cstic.setValidationType(sourceCstic.getValidationType());
				cstic.setVisible(sourceCstic.isVisible());
				cstic.setLastValidValue(sourceCstic.getLastValidValue());
				cstic.setMaxlength(sourceCstic.getMaxlength());

				insertChangedValue(cstic);

				updateCsticValues(cstic, sourceCstic);
			}

			if (LOG.isDebugEnabled())
			{
				final long duration = System.currentTimeMillis() - startTime;
				LOG.debug("Update cstics took " + duration + " ms");
			}

		}
	}

	protected void insertChangedValue(final CsticData cstic)
	{
		if (isChangedCsticWithAssignedValidationType(cstic))
		{
			if (UiType.NUMERIC.equals(cstic.getType()))
			{
				cstic.setFormattedValue(changedValue.get(cstic.getKey()));
			}
			else
			{
				cstic.setValue(changedValue.get(cstic.getKey()));
			}
		}
	}

	protected boolean isChangedCsticWithAssignedValidationType(final CsticData cstic)
	{
		return changedValue.containsKey(cstic.getKey()) && !UiValidationType.NONE.equals(cstic.getValidationType());
	}

	protected void updateCsticValues(final CsticData cstic, final CsticData sourceCstic)
	{
		final List<CsticValueData> values = cstic.getDomainvalues();
		final List<CsticValueData> sourceValues = sourceCstic.getDomainvalues();

		if (values != null && sourceValues != null)
		{

			long startTime = 0;

			if (LOG.isDebugEnabled())
			{
				startTime = System.currentTimeMillis();
			}

			final int valuesSsize = values.size();
			int i = 0;
			for (final CsticValueData sourceValue : sourceValues)
			{
				final CsticValueData value;
				if (valuesSsize > i)
				{
					value = values.get(i);
				}
				else
				{
					// can happen is last value is read-only
					value = new CsticValueData();
					value.setSelected(sourceValue.isSelected());
					values.add(value);

				}
				value.setName(sourceValue.getName());
				value.setLangdepname(sourceValue.getLangdepname());
				value.setKey(sourceValue.getKey());
				if (sourceValue.isReadonly())
				{
					// restore selected attribute for read-only values, as it is
					// not send from the UI-Control at all
					value.setSelected(sourceValue.isSelected());
				}
				i++;
			}

			if (LOG.isDebugEnabled())
			{
				final long duration = System.currentTimeMillis() - startTime;
				LOG.debug("Update cstic values took " + duration + " ms");
			}
		}
	}

	protected ConfigurationFacade getConfigFacade()
	{
		return configFacade;
	}

	/**
	 * @param configFacade
	 *           config facade, which is used to fetch the last complete configuration state
	 */
	public void setConfigFacade(final ConfigurationFacade configFacade)
	{
		this.configFacade = configFacade;
	}

}
