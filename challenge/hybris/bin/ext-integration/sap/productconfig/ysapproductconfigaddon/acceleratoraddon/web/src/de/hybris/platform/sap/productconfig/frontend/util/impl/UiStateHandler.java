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

import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupStatusType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageUISeverity;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiGroupForDisplayData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.impl.UniqueUIKeyGeneratorImpl;
import de.hybris.platform.sap.productconfig.frontend.UiCsticStatus;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;


/**
 * This class contains utility methods for UI-Status related controller logics, such as:
 * <ul>
 * <li>Expand/Collapse of UI-Groups</li>
 * <li>Conflict/Error handling</li>
 * <li>cstic-/Groupstate handling</li>
 * <li>message handling</li>
 * </ul>
 */
public class UiStateHandler
{
	private static final Logger LOG = Logger.getLogger(UiStateHandler.class.getName());
	private static final String LOG_CONFIG_DATA = "configuration data with [CONFIG_ID: '";
	static final String PATHELEMENT_GROUPS = "groups";
	static final String PATHELEMENT_SUBGROUPS = "subGroups";

	/**
	 * counts the number of UI-Errors for the given group.
	 *
	 * @param uiGroups
	 *           group to check
	 * @return number of errors
	 */
	public int countNumberOfUiErrorsPerGroup(final List<UiGroupData> uiGroups)
	{
		if (uiGroups == null)
		{
			return 0;
		}

		int allErrors = 0;
		for (final UiGroupData uiGroup : uiGroups)
		{
			if (uiGroup.getGroupType() != null && !uiGroup.getGroupType().toString().contains(GroupType.CONFLICT.toString()))
			{
				int numberErrors = 0;
				if (CollectionUtils.isNotEmpty(uiGroup.getCstics()))
				{
					numberErrors = (int) uiGroup.getCstics().stream().filter(new CsticStatusErrorWarningPredicate()).count();
				}
				final int subErrors = countNumberOfUiErrorsPerGroup(uiGroup.getSubGroups());
				numberErrors += subErrors;

				uiGroup.setNumberErrorCstics(numberErrors);
				allErrors += numberErrors;
			}
		}
		return allErrors;
	}

	protected UiGroupData expandFirstGroupWithCondition(final List<UiGroupData> list, final Predicate<GroupType> typeCondtion,
			final Predicate<UiGroupData> statusCondition)
	{
		if (list == null || list.isEmpty())
		{
			return null;
		}

		UiGroupData expandedGroup = null;
		for (final UiGroupData uiGroup : list)
		{
			final GroupType groupType = uiGroup.getGroupType();
			final boolean isLeafWithError = typeCondtion.test(groupType) && statusCondition.test(uiGroup);
			if (isLeafWithError)
			{
				expandedGroup = uiGroup;
			}
			else
			{
				expandedGroup = expandFirstGroupWithCondition(uiGroup.getSubGroups(), typeCondtion, statusCondition);
			}
			if (expandedGroup != null)
			{
				uiGroup.setCollapsed(false);
				uiGroup.setCollapsedInSpecificationTree(false);
				break;
			}
		}
		return expandedGroup;
	}

	protected UiGroupData expandFirstGroupWithError(final List<UiGroupData> list)
	{
		final Predicate<GroupType> csticGroupType = GroupType.CSTIC_GROUP::equals;
		return expandFirstGroupWithCondition(list, csticGroupType, new GroupStatusErrorWarningConflictPredicate());
	}

	protected UiGroupData expandFirstGroupWithErrorOrConflict(final List<UiGroupData> list)
	{
		final Predicate<GroupType> csticOrConflictGroupType = groupType -> GroupType.CSTIC_GROUP.equals(groupType)
				|| GroupType.CONFLICT.equals(groupType);
		return expandFirstGroupWithCondition(list, csticOrConflictGroupType, new GroupStatusErrorWarningConflictPredicate());
	}

	protected void expandGroupCloseOthers(final List<UiGroupData> list, final UiGroupData expandedGroup)
	{
		if (CollectionUtils.isNotEmpty(list))
		{
			for (final UiGroupData uiGroup : list)
			{
				uiGroup.setCollapsed(!uiGroup.equals(expandedGroup));
			}
		}
		return;
	}

	/**
	 * Get all UI-Errors that are attached to currently not visible cstics, because the UI group the cstic is assigned to
	 * is currently collapsed.
	 *
	 * @param userInputToRestore
	 *           Erroneous user input that should be kept and restored during next round trip.
	 * @param latestConfiguration
	 *           actual configuration
	 * @return UI-Errors currently not visible
	 */
	public Map<String, FieldError> findCollapsedErrorCstics(final Map<String, FieldError> userInputToRestore,
			final ConfigurationData latestConfiguration)
	{
		final Map<String, FieldError> userInputToRemeber;
		if (MapUtils.isEmpty(userInputToRestore))
		{
			userInputToRemeber = Collections.emptyMap();
		}
		else
		{
			userInputToRemeber = new HashMap<>();
			for (final UiGroupData group : latestConfiguration.getGroups())
			{
				findCollapsedErrorCstics(userInputToRestore, userInputToRemeber, group, false);
			}
		}
		return userInputToRemeber;
	}

	protected void findCollapsedErrorCstics(final Map<String, FieldError> userInputToRestore,
			final Map<String, FieldError> userInputToRemeber, final UiGroupData group, final boolean isRootCollapsed)
	{
		boolean groupCollapsed = isRootCollapsed;

		if (!isRootCollapsed)
		{
			groupCollapsed = group.isCollapsed();
		}
		if (groupCollapsed)
		{
			for (final CsticData cstic : group.getCstics())
			{
				final String key = cstic.getKey();
				if (userInputToRestore.containsKey(key))
				{
					userInputToRemeber.put(key, userInputToRestore.get(key));
				}
			}
		}

		if (group.getSubGroups() != null)
		{
			for (final UiGroupData subGroup : group.getSubGroups())
			{
				findCollapsedErrorCstics(userInputToRestore, userInputToRemeber, subGroup, groupCollapsed);
			}
		}
	}

	/**
	 * Returns the first cstic with status {@link CsticStatusType#ERROR}, {@link CsticStatusType#WARNING} or
	 * {@link CsticStatusType#CONFLICT}.<br>
	 * Searches the subgroups recursively.
	 *
	 * @param group
	 *           root group
	 * @return the first erroneous cstic, or <code>null</code> in case the give group and all subgroups are error free
	 */
	public CsticData getFirstCsticWithErrorInGroup(final UiGroupData group)
	{
		final Optional<CsticData> result = group.getCstics().stream().filter(new CsticStatusErrorWarningConflictPredicate())
				.findFirst();
		if (result.isPresent())
		{
			return result.get();
		}

		if (group.getSubGroups() != null)
		{
			return group.getSubGroups().stream().filter(new GroupStatusErrorWarningPredicate())
					.map(this::getFirstCsticWithErrorInGroup).findFirst().orElse(null);
		}
		return null;
	}

	/**
	 * Handles the AutoExpand Mode. This means the first erroneous group is automatically expanded, until the user leaves
	 * the auto expand mode or nor more such group exists.
	 *
	 * @param configData
	 *           current configuration
	 * @param uiStatus
	 *           current UI status
	 * @return group to expand
	 */
	public UiGroupData handleAutoExpand(final ConfigurationData configData, final UiStatus uiStatus)
	{
		if (!configData.isAutoExpand())
		{
			return null;
		}

		final UiGroupData expandedGroup = expandFirstGroupWithErrorOrConflict(configData.getGroups());
		final boolean tabMode = configData.getStartLevel() > 0;
		if (expandedGroup != null && tabMode)
		{
			expandGroupCloseOthers(configData.getGroups(), expandedGroup);
		}

		if (expandedGroup == null)
		{
			// all ok - quit auto expand mode
			configData.setAutoExpand(false);
			uiStatus.setFirstErrorCsticId(null);
		}
		else
		{
			final CsticData errorCstic = getFirstCsticWithErrorInGroup(expandedGroup);
			if (errorCstic != null)
			{
				String errorCsticId = errorCstic.getKey();
				//cstic id for conflict groups is changed in javascript to "conflict." + id
				if (GroupType.CONFLICT.equals(expandedGroup.getGroupType()))
				{
					errorCsticId = "conflict." + getConflictGroupNo(configData, expandedGroup) + "." + errorCsticId;
				}
				configData.setFocusId(errorCsticId);
			}
		}
		return expandedGroup;
	}

	/**
	 * Get the conflict group index, of the given uiGroup, within the conflict groups. If it is a multi level product
	 * always 1 will be returned.
	 *
	 * @param configData The configuration data
	 * @param uiGroup    The searched conflict group
	 * @return The index of the given conflict group (index count starts with 1)
	 */
	public int getConflictGroupNo(final ConfigurationData configData, final UiGroupData uiGroup)
	{
		int conflictGroupNo = 1;
		if (configData.isSingleLevel())
		{
			conflictGroupNo = configData.getGroups().get(0).getSubGroups().indexOf(uiGroup) + 1;
		}
		return conflictGroupNo;
	}

	/**
	 * Merges both given uiError maps into one, taking care of null values.
	 *
	 * @param uiErrorSource
	 *           UI errors
	 * @param otherUiErrorSource
	 *           UI erros
	 * @return merged map
	 */
	public Map<String, FieldError> mergeUiErrors(final Map<String, FieldError> uiErrorSource,
			final Map<String, FieldError> otherUiErrorSource)
	{
		final Map<String, FieldError> mergedUiErrors = new HashMap<>();

		if (otherUiErrorSource != null)
		{
			mergedUiErrors.putAll(otherUiErrorSource);
		}

		if (uiErrorSource != null)
		{
			mergedUiErrors.putAll(uiErrorSource);
		}
		return mergedUiErrors;
	}

	/**
	 * Resets recursively the UI group status for the whole configuration to {@link GroupStatusType#DEFAULT}.
	 *
	 * @param configData
	 *           configuration to process
	 */
	public void resetGroupStatus(final ConfigurationData configData)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Reset group status for " + LOG_CONFIG_DATA + configData.getConfigId() + "']");
		}

		final List<UiGroupData> uiGroups = configData.getGroups();
		if (uiGroups != null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Reset group with subgroups for " + LOG_CONFIG_DATA + configData.getConfigId() + "']");
			}
			resetGroupWithSubGroups(uiGroups);
		}
	}

	protected void resetGroupWithSubGroups(final List<UiGroupData> uiGroups)
	{
		for (final UiGroupData group : uiGroups)
		{
			group.setGroupStatus(GroupStatusType.DEFAULT);
			if (CollectionUtils.isNotEmpty(group.getSubGroups()))
			{
				resetGroupWithSubGroups(group.getSubGroups());
			}
		}
	}

	/**
	 * Remembers any UI-Validation error including the causing user Input. Then replaces the erroneous value with the
	 * last know good value.<br>
	 * The underlying configuration engine requires valid data for processing, hence we do not send data that failed the
	 * UI validation to the engine. Instead the last known good data is send. After the update the erroneous user input
	 * is restored. So user is not blocked by UI validation error. Instead he can proceed and resolve the error any time
	 * laster.
	 *
	 * @see #restoreValidationErrorsAfterUpdate(Map, ConfigurationData, BindingResult)
	 * @param configData
	 *           actual configuration
	 * @param bindingResult
	 *           validation result
	 * @return UI errors to remember
	 */
	public Map<String, FieldError> handleValidationErrorsBeforeUpdate(final ConfigurationData configData,
			final BindingResult bindingResult)
	{
		final Map<String, FieldError> userInputToRestore;
		if (!bindingResult.hasErrors())
		{
			return Collections.emptyMap();
		}

		final int capacity = (int) (bindingResult.getErrorCount() / 0.75) + 1;
		userInputToRestore = new HashMap<>(capacity);
		for (final FieldError error : bindingResult.getFieldErrors())
		{
			final String fieldPath = error.getField();
			final CsticData cstic = getCsticForFieldPath(configData, fieldPath);

			userInputToRestore.put(cstic.getKey(), error);

			cstic.setFormattedValue(cstic.getLastValidValue());
			cstic.setAdditionalValue("");
		}
		return userInputToRestore;
	}

	/**
	 * Retrieves the cstic form the configuration, by analyzing the provided UI field path.<br>
	 * For example path "group[1].subGroup[2].cstic[0]", would extract the first cstic of the third subgroup of the
	 * second group of the configuration.
	 *
	 * @param configData
	 *           actual configuration
	 * @param fieldPath
	 *           UI path the cstic
	 * @return extracted cstic
	 */
	public CsticData getCsticForFieldPath(final ConfigurationData configData, final String fieldPath)
	{
		final PathExtractor extractor = new PathExtractor(fieldPath);
		final int groupIndex = extractor.getGroupIndex();
		final int csticIndex = extractor.getCsticsIndex();
		UiGroupData group = configData.getGroups().get(groupIndex);
		for (int i = 0; i < extractor.getSubGroupCount(); i++)
		{
			group = group.getSubGroups().get(extractor.getSubGroupIndex(i));
		}
		return group.getCstics().get(csticIndex);
	}

	/**
	 * Restores UI errors that were remembered before update, after the update was executed.<br>
	 * Foe more details see {@link #handleValidationErrorsBeforeUpdate(ConfigurationData, BindingResult)}.
	 *
	 * @param userInputToRestore
	 *           UI errors to restore
	 * @param latestConfiguration
	 *           actual configuration
	 * @param bindingResult
	 *           actual UI errors
	 * @return updated UI errors
	 */
	public BindingResult restoreValidationErrorsAfterUpdate(final Map<String, FieldError> userInputToRestore,
			final ConfigurationData latestConfiguration, final BindingResult bindingResult)
	{
		if (userInputToRestore.isEmpty())
		{
			return bindingResult;
		}

		//discard the old error binding and create a new one instead, that only contains errors for visible cstics
		final BindingResult restoredBindingResult = new BeanPropertyBindingResult(latestConfiguration,
				SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE);

		int groupNumber = 0;
		for (final UiGroupData group : latestConfiguration.getGroups())
		{
			final String prefix = "groups[" + groupNumber + "].";
			restoreValidationErrorsInGroup(prefix, userInputToRestore, restoredBindingResult, group);
			groupNumber++;
		}
		return restoredBindingResult;
	}

	protected void restoreValidationErrorsInGroup(final String prefix, final Map<String, FieldError> userInputToRestore,
			final BindingResult bindingResult, final UiGroupData group)
	{
		for (final CsticData latestCstic : group.getCstics())
		{
			final UiType uiType = latestCstic.getType();
			final String key = latestCstic.getKey();
			final boolean restoreValidationError = latestCstic.isVisible() && userInputToRestore.containsKey(key)
					&& isEditableUiType(uiType);
			if (restoreValidationError)
			{
				final FieldError fieldError = userInputToRestore.get(key);
				latestCstic.getConflicts().clear();
				latestCstic.setCsticStatus(CsticStatusType.ERROR);
				final String errorValue = fieldError.getRejectedValue().toString();
				if (UiType.DROPDOWN_ADDITIONAL_INPUT == uiType || UiType.RADIO_BUTTON_ADDITIONAL_INPUT == uiType)
				{
					latestCstic.setAdditionalValue(errorValue);
				}
				else
				{
					latestCstic.setFormattedValue(errorValue);
				}
				final FieldError newFieldError = new FieldError(fieldError.getObjectName(), fieldError.getField(),
						fieldError.getRejectedValue(), fieldError.isBindingFailure(), fieldError.getCodes(), fieldError.getArguments(),
						fieldError.getDefaultMessage());
				bindingResult.addError(newFieldError);
				group.setGroupStatus(GroupStatusType.ERROR);
			}
		}

		final List<UiGroupData> subGroups = group.getSubGroups();
		if (null == subGroups)
		{
			return;
		}

		int subGroupNumber = 0;
		for (final UiGroupData subGroup : subGroups)
		{
			final String subPrefix = prefix + "subGroups[" + subGroupNumber + "].";
			restoreValidationErrorsInGroup(subPrefix, userInputToRestore, bindingResult, subGroup);
			subGroupNumber++;
		}
	}

	protected boolean isEditableUiType(final UiType uiType)
	{
		return uiType != UiType.READ_ONLY && uiType != UiType.READ_ONLY_SINGLE_SELECTION_IMAGE
				&& uiType != UiType.READ_ONLY_MULTI_SELECTION_IMAGE;
	}

	/**
	 * Restores UI errors that were remembered before update, after the update was executed.<br>
	 * Foe more details see {@link #handleValidationErrorsBeforeUpdate(ConfigurationData, BindingResult)}.
	 *
	 * @param userInputToRestore
	 *           UI errors to restore
	 * @param latestConfiguration
	 *           actual configuration
	 * @param bindingResult
	 *           actual UI errors
	 * @return updated UI errors
	 */
	public BindingResult restoreValidationErrorsOnGetConfig(final Map<String, FieldError> userInputToRestore,
			final ConfigurationData latestConfiguration, final BindingResult bindingResult)
	{
		if (!userInputToRestore.isEmpty())
		{
			int groupNumber = 0;
			for (final UiGroupData group : latestConfiguration.getGroups())
			{
				final String prefix = "groups[" + groupNumber + "].";
				restoreValidationErrorsInGroup(prefix, userInputToRestore, bindingResult, group);
				groupNumber++;
			}
		}
		return bindingResult;
	}

	/**
	 * Create UI messages when conflicts occur or when one or all conflicts are solved.
	 *
	 * @param uiStatus
	 *           oldUiStatus
	 * @param newNumberOfConflicts
	 *           actual number of conflicts
	 * @param model
	 *           UI View Model
	 */
	public void handleConflictSolverMessage(final UiStatus uiStatus, final int newNumberOfConflicts, final Model model)
	{

		int oldNumberOfConflicts = 0;
		if (uiStatus != null)
		{
			oldNumberOfConflicts = uiStatus.getNumberOfConflictsToDisplay();
		}

		if (hasNoConflicts(oldNumberOfConflicts, newNumberOfConflicts))
		{
			return;
		}

		if (hasOnlyNewConflicts(oldNumberOfConflicts, newNumberOfConflicts))
		{
			GlobalMessages.addInfoMessage(model, "sapproductconfig.conflict.message.resolve.in.order");
			return;
		}

		if (hasOnlyOldConflicts(oldNumberOfConflicts, newNumberOfConflicts))
		{
			GlobalMessages.addConfMessage(model, "sapproductconfig.conflict.message.all.resolved");
			return;
		}

		handleDifferentNumberOfNewOldConflictMessages(model, oldNumberOfConflicts, newNumberOfConflicts);
	}

	protected void handleDifferentNumberOfNewOldConflictMessages(final Model model, final int oldNumberOfConflicts,
			final int newNumberOfConflicts)
	{
		final int result = Math.subtractExact(newNumberOfConflicts, oldNumberOfConflicts);
		if (result > 0)
		{
			GlobalMessages.addInfoMessage(model, "sapproductconfig.conflict.message.not.resolved");
			return;
		}
		else if (result < 0)
		{
			final Integer resultAbs = Integer.valueOf(Math.abs(result));
			if (Integer.valueOf(1).equals(resultAbs))
			{
				GlobalMessages.addConfMessage(model, "sapproductconfig.conflict.message.resolved");
				return;
			}
			GlobalMessages.addMessage(model, GlobalMessages.CONF_MESSAGES_HOLDER, "sapproductconfig.conflict.messages.resolved",
					new Object[]
					{ resultAbs });
			return;
		}
		GlobalMessages.addInfoMessage(model, "sapproductconfig.conflict.message.resolve.in.order");
	}

	/**
	 * Creates a UI-Message for every generic configuration message attached to the actual configuration.
	 *
	 * @param configData
	 *           actual configuration
	 * @param model
	 *           UI View Model
	 */
	public void handleProductConfigMessages(final ConfigurationData configData, final Model model)
	{
		for (final ProductConfigMessageData message : configData.getMessages())
		{
			if (ProductConfigMessageUISeverity.ERROR.equals(message.getSeverity()))
			{
				// red message box
				GlobalMessages.addErrorMessage(model, message.getMessage());
			}
			else if (ProductConfigMessageUISeverity.INFO.equals(message.getSeverity()))
			{
				// yellow message box
				GlobalMessages.addInfoMessage(model, message.getMessage());
			}
			else if (ProductConfigMessageUISeverity.CONFIG.equals(message.getSeverity()))
			{
				// blue message box
				GlobalMessages.addConfMessage(model, message.getMessage());
			}
			else
			{
				// custom
				GlobalMessages.addMessage(model, message.getSeverity().toString(), message.getMessage(), null);
			}
		}
	}

	protected boolean hasNoConflicts(final int oldNumberOfConflicts, final int newNumberOfConflicts)
	{
		return oldNumberOfConflicts == 0 && newNumberOfConflicts == 0;
	}

	protected boolean hasOnlyNewConflicts(final int oldNumberOfConflicts, final int newNumberOfConflicts)
	{
		return oldNumberOfConflicts == 0 && newNumberOfConflicts > 0;
	}

	protected boolean hasOnlyOldConflicts(final int oldNumberOfConflicts, final int newNumberOfConflicts)
	{
		return oldNumberOfConflicts > 0 && newNumberOfConflicts == 0;
	}


	protected UiGroupData getFirstGroupWithCsticsDeepSearch(final List<UiGroupData> uiGroups)
	{
		for (final UiGroupData uiGroup : uiGroups)
		{
			if (CollectionUtils.isNotEmpty(uiGroup.getCstics()))
			{
				return uiGroup;
			}
			final UiGroupData uiGroupResult = getFirstGroupWithCsticsDeepSearch(uiGroup.getSubGroups());
			if (uiGroupResult != null)
			{
				return uiGroupResult;
			}
		}
		return null;
	}

	/**
	 * This method identifies the group to which the UI should navigate after all conflicts are solved. For single-level
	 * models this would be the first group containing a cstic. (A UI group may be empty, or only containing other UI
	 * group, but no cstics.)<br>
	 * For multi-level models this would be the last non conflict group (i.e. the group that was displayed before the
	 * conflict solver was entered)
	 *
	 * @param configData
	 *           actual configuration
	 * @param uiStatus
	 * @return id of the group that should be navigated to after resolving conflicts
	 */
	public String getGroupIdToDisplayAfterResolvingConflicts(final ConfigurationData configData, final UiStatus uiStatus)
	{
		String firstGroupId = getFirstGroupWithCsticsDeepSearch(configData.getGroups()).getId();
		if (!configData.isSingleLevel() && !firstGroupId.startsWith(SapproductconfigfrontendWebConstants.CONFLICT_PREFIX))
		{
			firstGroupId = uiStatus.getLastNoneConflictGroupId();
		}
		return firstGroupId;
	}

	protected boolean checkGroupExistence(final ConfigurationData configData, final String groupIdToDisplayUiStatus)
	{
		final String groupIdToDisplayUiStatusWithoutInstanceName = getGroupIdWithoutInstanceName(groupIdToDisplayUiStatus);
		return configData.getCsticGroupsFlat().stream().map(group -> group.getId())
				.anyMatch(id -> getGroupIdWithoutInstanceName(id).equals(groupIdToDisplayUiStatusWithoutInstanceName));
	}

	protected String getGroupIdWithoutInstanceName(final String groupId)
	{
		if (groupId == null)
		{
			return groupId;
		}
		String groupIdWithoutInstanceName = groupId;
		final String instanceName = extractInstanceNameFromGroupId(groupId);
		if (instanceName != null)
		{
			groupIdWithoutInstanceName = StringUtils.replaceOnce(groupId, instanceName, StringUtils.EMPTY);
		}
		return groupIdWithoutInstanceName;
	}

	protected String extractInstanceNameFromGroupId(final String groupId)
	{
		final int instanceSeparatorIndex = groupId.indexOf(UniqueUIKeyGeneratorImpl.INSTANCE_SEPERATOR);
		int keySeparatorIndex = groupId.indexOf(UniqueUIKeyGeneratorImpl.KEY_SEPARATOR);
		if (instanceSeparatorIndex != -1)
		{
			if (keySeparatorIndex == -1)
			{
				keySeparatorIndex = groupId.length();
			}
			return groupId.substring(instanceSeparatorIndex + 1, keySeparatorIndex);

		}
		return null;
	}

	protected String determineReplacementGroupId(final ConfigurationData configData, final String groupIdToDisplay,
			final String groupIdToDisplayUiStatus)
	{
		String replacedGroupIdToDisplay = groupIdToDisplay;
		if (StringUtils.isNotEmpty(groupIdToDisplayUiStatus)
				&& checkGroupExistence(configData, groupIdToDisplayUiStatus))
		{
			replacedGroupIdToDisplay = groupIdToDisplayUiStatus;
		}
		return replacedGroupIdToDisplay;
	}

	protected String determineGroupIdForDisplayFromUiStatus(final ConfigurationData configData, final UiStatus uiStatus,
			final String groupIdToDisplay)
	{
		String replacedGroupIdToDisplay = groupIdToDisplay;
		if (uiStatus != null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("UI status available, group ID to display from UI status: " + uiStatus.getGroupIdToDisplay());
			}

			final String groupIdToDisplayUiStatus = uiStatus.getGroupIdToDisplay();
			replacedGroupIdToDisplay = determineReplacementGroupId(configData, groupIdToDisplay, groupIdToDisplayUiStatus);
		}
		return replacedGroupIdToDisplay;
	}

	/**
	 * Compiles the group which is currently active for display, and sets the respective attribute
	 * {@link ConfigurationData#setGroupToDisplay(UiGroupForDisplayData)} in configData
	 *
	 * @param configData
	 * @param uiStatus
	 *           Can be null, in this case we use the first group as group to display
	 */
	public void compileGroupForDisplay(final ConfigurationData configData, final UiStatus uiStatus)
	{
		if (CollectionUtils.isEmpty(configData.getGroups()))
		{
			LOG.debug("No groups provided");
			return;
		}

		String groupIdToDisplay = getFirstGroupWithCstics(configData.getGroups()).getId();
		final Deque<String> path = new ArrayDeque<>();
		final Deque<String> groupIdPath = new ArrayDeque<>();
		final UiGroupForDisplayData groupForDisplay = new UiGroupForDisplayData();
		groupIdToDisplay = determineGroupIdForDisplayFromUiStatus(configData, uiStatus, groupIdToDisplay);

		final UiGroupData matchingUiGroup = compileGroupForDisplay(configData.getGroups(), groupIdToDisplay, path, groupIdPath,
				PATHELEMENT_GROUPS);

		if (configData.getCpqAction() != null && (configData.getCpqAction().equals(CPQActionType.NAV_TO_CSTIC_IN_CONFLICT)
				|| configData.getCpqAction().equals(CPQActionType.NAV_TO_CSTIC_IN_GROUP)))
		{
			matchingUiGroup.setCollapsed(false);
		}
		groupForDisplay.setGroup(matchingUiGroup);
		groupForDisplay.setPath(extractPathAsString(path));
		groupForDisplay.setGroupIdPath(extractPathAsString(groupIdPath));
		configData.setGroupToDisplay(groupForDisplay);
		configData.setGroupIdToDisplay(matchingUiGroup.getId());

		if (LOG.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder("\nGroup to display:");
			debugOutput.append("\nID: ");
			debugOutput.append(configData.getGroupIdToDisplay());
			debugOutput.append("\nPath of group IDs, including parents:");
			debugOutput.append(groupForDisplay.getGroupIdPath());
			debugOutput.append("\nPath of group in entire configuration tree: ");
			debugOutput.append(groupForDisplay.getPath());
			LOG.debug(debugOutput);
		}
	}

	protected UiGroupData compileGroupForDisplay(final List<UiGroupData> groups, final String groupIdToDisplay,
			final Deque<String> path, final Deque<String> groupIdPath, final String groupsIdentifier)
	{
		final String nextLevel = groupsIdentifier + "[";
		final String groupIdToDisplayWithoutInstanceName = getGroupIdWithoutInstanceName(groupIdToDisplay);
		path.addLast(nextLevel);
		if (groups != null)
		{
			for (int i = 0; i < groups.size(); i++)
			{
				final UiGroupData group = groups.get(i);
				path.addLast(i + "].");
				groupIdPath.addLast(group.getId() + ",");
				if (getGroupIdWithoutInstanceName(group.getId()).equals(groupIdToDisplayWithoutInstanceName))
				{
					return group;
				}
				final UiGroupData matchingSubGroup = compileGroupForDisplay(group.getSubGroups(), groupIdToDisplay, path, groupIdPath,
						PATHELEMENT_SUBGROUPS);
				if (matchingSubGroup != null)
				{
					return matchingSubGroup;
				}

				groupIdPath.removeLast();
				path.removeLast();
			}

		}
		path.removeLast();
		return null;
	}

	protected String extractPathAsString(final Deque<String> path)
	{
		final StringBuilder pathAsString = new StringBuilder();
		for (final String element : path)
		{
			pathAsString.append(element);
		}
		return pathAsString.toString();
	}

	protected UiGroupData getFirstGroupWithCstics(final List<UiGroupData> uiGroups)
	{
		final Optional<UiGroupData> result = uiGroups.stream()
				.filter(group -> group.getCstics() != null && !group.getCstics().isEmpty()).findFirst();
		if (result.isPresent())
		{
			return result.get();
		}

		for (final UiGroupData uiGroup : uiGroups)
		{
			final UiGroupData uiGroupResult = getFirstGroupWithCstics(uiGroup.getSubGroups());
			if (uiGroupResult != null)
			{
				return uiGroupResult;
			}
		}

		return null;
	}

	static class CsticStatusErrorWarningPredicate implements Predicate<CsticData>
	{
		@Override
		public boolean test(final CsticData csticData)
		{
			return CsticStatusType.ERROR.equals(csticData.getCsticStatus())
					|| CsticStatusType.WARNING.equals(csticData.getCsticStatus());
		}
	}

	static class CsticStatusErrorWarningConflictPredicate implements Predicate<CsticData>
	{
		@Override
		public boolean test(final CsticData csticData)
		{
			return CsticStatusType.ERROR.equals(csticData.getCsticStatus())
					|| CsticStatusType.WARNING.equals(csticData.getCsticStatus())
					|| CsticStatusType.CONFLICT.equals(csticData.getCsticStatus());
		}
	}

	static class GroupStatusErrorWarningPredicate implements Predicate<UiGroupData>
	{
		@Override
		public boolean test(final UiGroupData uiGroup)
		{
			return GroupStatusType.ERROR.equals(uiGroup.getGroupStatus())
					|| GroupStatusType.WARNING.equals(uiGroup.getGroupStatus());
		}
	}

	static class GroupStatusErrorWarningConflictPredicate implements Predicate<UiGroupData>
	{
		@Override
		public boolean test(final UiGroupData uiGroup)
		{
			return GroupStatusType.ERROR.equals(uiGroup.getGroupStatus()) || GroupStatusType.WARNING.equals(uiGroup.getGroupStatus())
					|| GroupStatusType.CONFLICT.equals(uiGroup.getGroupStatus());
		}
	}

	/**
	 * Collects the cstic Ids of all cstic of thos group and any subgroup that are currently visible.
	 *
	 * @param groups
	 *           group to check
	 * @param csticList
	 *           list of visible cstics
	 */
	public void fillAllVisibleCsticIdsOfGroup(final List<UiGroupStatus> groups, final List<String> csticList)
	{
		if (CollectionUtils.isNotEmpty(groups))
		{
			for (final UiGroupStatus group : groups)
			{
				if (!group.isCollapsed())
				{
					fillAllVisibleCsticIdsOfGroup(group.getSubGroups(), csticList);
					fillAllVisibleCsticIds(group.getCstics(), csticList);
				}
			}
		}
	}

	protected void fillAllVisibleCsticIds(final List<UiCsticStatus> cstics, final List<String> pricingInput)
	{

		if (CollectionUtils.isNotEmpty(cstics))
		{
			for (final UiCsticStatus cstic : cstics)
			{
				pricingInput.add(cstic.getId());
			}
		}
	}
}
