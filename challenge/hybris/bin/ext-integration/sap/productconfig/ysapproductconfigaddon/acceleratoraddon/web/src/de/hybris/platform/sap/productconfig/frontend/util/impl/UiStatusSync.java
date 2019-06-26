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

import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.ProductConfigMessageData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.tracking.UiTrackingRecorder;
import de.hybris.platform.sap.productconfig.frontend.UiCsticStatus;
import de.hybris.platform.sap.productconfig.frontend.UiCsticValueStatus;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiPromoMessageStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * Used to sync the configuration DTO (which has request scope) with the UIStatus which we store in the session
 */
public class UiStatusSync
{

	private static final Logger LOG = Logger.getLogger(UiStatusSync.class);
	final BiPredicate<UiCsticStatus, CsticData> checkCsticUiStatusMatch = (uiCsticStatus, cstic) -> cstic.getKey()
			.equals(uiCsticStatus.getId());
	final BiPredicate<UiCsticValueStatus, CsticValueData> checkCsticValueUiStatusMatch = (uiCsticValueStatus,
			csticValueData) -> csticValueData.getName().equals(uiCsticValueStatus.getId());
	final BiPredicate<UiPromoMessageStatus, ProductConfigMessageData> checkPromoMessageUiStatusMatch = (uiMessage,
			message) -> getMessageId(message).equals(uiMessage.getId());

	final BiConsumer<UiCsticStatus, CsticData> applyUiStatusToCsticConsumer = (uiCsticStatus,
			cstic) -> applyUiStatusToCstic(uiCsticStatus, cstic);
	final BiConsumer<UiPromoMessageStatus, ProductConfigMessageData> applyUiStatusToMessagesConsumer = (uiMessage,
			message) -> message.setShowExtendedMessage(uiMessage.isShowExtendedMessage());
	final BiConsumer<UiCsticValueStatus, CsticValueData> applyUiStatusToCsticValueConsumer = (uiCsticValueStatus,
			csticValueData) -> applyUiStatusToData(uiCsticValueStatus.getPromoMessages(), csticValueData.getMessages(),
					checkPromoMessageUiStatusMatch, applyUiStatusToMessagesConsumer);


	/**
	 * Updates the configuration DTO with the UI state (e.g. which group is collapsed/opened, which has been visited
	 * already). Takes care of the currently selected group and expands it
	 *
	 * @param configData
	 * @param uiStatus
	 *           UI status (session scope)
	 * @param selectedGroup
	 *           ID of the currently selected group
	 */
	public void applyUiStatusToConfiguration(final ConfigurationData configData, final UiStatus uiStatus,
			final String selectedGroup)
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug(" Apply UI status to congfig with [CONFIG_ID='" + configData.getConfigId() + "'");
		}

		applyUiStatusToConfiguration(configData, uiStatus);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Expand group '" + selectedGroup + "' for config data with [CONFIG_ID='" + configData.getConfigId() + "']");
		}

		expandGroupAndCollapseOther(configData, selectedGroup);
	}

	protected void expandGroupAndCollapseOther(final ConfigurationData configData, final String selectedGroup)
	{
		for (final UiGroupData uiGroup : configData.getGroups())
		{
			if (selectedGroup.equals(uiGroup.getId()))
			{
				uiGroup.setCollapsed(false);
			}
			else
			{
				uiGroup.setCollapsed(true);
			}
		}
	}

	/**
	 * Updates the configuration DTO with the UI state (e.g. which group is collapsed/opened, which has been visited
	 * already).
	 *
	 * @param configData
	 * @param uiStatus
	 *           UI status (session scope)
	 */
	public void applyUiStatusToConfiguration(final ConfigurationData configData, final UiStatus uiStatus)
	{
		final List<UiGroupStatus> uiGroupsStatus = uiStatus.getGroups();
		final List<UiGroupData> uiGroups = configData.getGroups();

		configData.setPriceSummaryCollapsed(uiStatus.isPriceSummaryCollapsed());
		configData.setSpecificationTreeCollapsed(uiStatus.isSpecificationTreeCollapsed());
		configData.setHideImageGallery(uiStatus.isHideImageGallery());
		configData.setQuantity(uiStatus.getQuantity());

		// If config-menu is displayed make sure that a group navigation also expands the config-menu node correctly
		// (e.g. for prev-/next-button clicked, view in configuration link, conflict link)
		final boolean isNavigationAction = isNavigationAction(configData.getCpqAction());
		if (!configData.isSingleLevel() && isNavigationAction && uiStatus.getGroupIdToDisplay() != null)
		{
			expandGroupInSpecTreeAndExpandGroup(uiStatus);
		}
		applyUiStatusToUiGroup(uiGroupsStatus, uiGroups);
	}

	protected boolean isNavigationAction(final CPQActionType cpqAction)
	{
		boolean isNavigationAction = false;
		if (cpqAction != null)
		{
			isNavigationAction = cpqAction.equals(CPQActionType.NAV_TO_CSTIC_IN_CONFLICT)
					|| cpqAction.equals(CPQActionType.NAV_TO_CSTIC_IN_GROUP) || cpqAction.equals(CPQActionType.PREV_BTN)
					|| cpqAction.equals(CPQActionType.NEXT_BTN);
		}
		return isNavigationAction;
	}

	/**
	 * Expand the group in spec tree and expand the group itself
	 *
	 * @param uiStatus
	 */
	protected void expandGroupInSpecTreeAndExpandGroup(final UiStatus uiStatus)
	{
		// Find the group to expand and set the collapse-state in spec tree to expanded
		final UiGroupStatus toggledGroup = toggleGroupInSpecTree(uiStatus.getGroupIdToDisplay(), uiStatus.getGroups(), true);
		// Expand the group itself as well if there was a group to toggle
		if (toggledGroup != null)
		{
			toggledGroup.setCollapsed(false);
		}
	}


	protected void applyUiStatusToUiGroup(final List<UiGroupStatus> uiGroupsStatus, final List<UiGroupData> uiGroups)
	{
		if (notNullAndNotEmpty(uiGroups))
		{
			for (int groupIdx = 0; groupIdx < uiGroups.size(); groupIdx++)
			{
				final UiGroupData uiGroup = uiGroups.get(groupIdx);
				final UiGroupStatus statusGroup = findStatusGroup(uiGroupsStatus, uiGroup, groupIdx);
				if (statusGroup != null)
				{
					applyUiStatusToUiGroup(statusGroup.getSubGroups(), uiGroup.getSubGroups());

					applyUiStatusToData(statusGroup.getCstics(), uiGroup.getCstics(), //
							checkCsticUiStatusMatch, //
							applyUiStatusToCsticConsumer);
					uiGroup.setCollapsed(statusGroup.isCollapsed());
					uiGroup.setCollapsedInSpecificationTree(statusGroup.isCollapsedInSpecificationTree());
					uiGroup.setVisited(statusGroup.isVisited());
				}
				else if (uiGroup.getGroupType().equals(GroupType.CSTIC_GROUP) || uiGroup.getGroupType().equals(GroupType.INSTANCE))
				{
					uiGroup.setCollapsed(true);
				}
			}
		}
	}

	/**
	 * Update the flag of the show long full text in UI status groups
	 * 
	 * @param csticKey
	 * 	characteristic key
	 * @param showFullLongText
	 * 	show full long text
	 * @param uiStatusGroups
	 * 	list of UI status groups
	 */
	public void updateShowFullLongTextinUIStatusGroups(final String csticKey, final boolean showFullLongText,
			final List<UiGroupStatus> uiStatusGroups)
	{
		if (notNullAndNotEmpty(uiStatusGroups))
		{
			for (int index = 0; index < uiStatusGroups.size(); index++)
			{
				final UiGroupStatus statusGroup = uiStatusGroups.get(index);
				updateShowFullLongTextinUIStatusGroups(csticKey, showFullLongText, statusGroup.getSubGroups());
				updateShowFullLongTextInUiStatusCstics(csticKey, showFullLongText, statusGroup.getCstics());
			}
		}
	}

	/**
	 * @param csticKey
	 * 	characteristic key
	 * @param csticValueKey
	 * 	characteristic value key
	 * @param messageKey
	 * 	message key
	 * @param uiStatusGroups
	 *  list of UI status groups
	 */
	public void toggleShowExtendedMessageOnUIStatusGroups(final String csticKey, final String csticValueKey,
			final String messageKey, final List<UiGroupStatus> uiStatusGroups)
	{
		if (notNullAndNotEmpty(uiStatusGroups))
		{
			for (int index = 0; index < uiStatusGroups.size(); index++)
			{
				final UiGroupStatus statusGroup = uiStatusGroups.get(index);
				toggleShowExtendedMessageOnUIStatusGroups(csticKey, csticValueKey, messageKey, statusGroup.getSubGroups());
				toggleShowExtendedMessageOnStatusCstics(csticKey, csticValueKey, messageKey,
						Optional.ofNullable(statusGroup.getCstics()).orElse(Collections.emptyList()));
			}
		}
	}

	protected <T, K> void applyUiStatusToData(final List<T> uiStatusList, final List<K> dataList, final BiPredicate<T, K> idMatch,
			final BiConsumer<T, K> applyMethod)
	{
		if (checkIfEmpty(uiStatusList, dataList))
		{
			return;
		}

		for (int dataIdx = 0; dataIdx < dataList.size(); dataIdx++)
		{
			final K data = dataList.get(dataIdx);
			final Optional<T> uiOptstatus = findStatusValue(uiStatusList, uiStatus -> idMatch.test(uiStatus, data), dataIdx);
			uiOptstatus.ifPresent(uiStatus -> applyMethod.accept(uiStatus, data));
		}
	}

	protected void applyUiStatusToCstic(final UiCsticStatus uiCsticStatus, final CsticData csticData)
	{
		csticData.setShowFullLongText(uiCsticStatus.isShowFullLongText());

		applyUiStatusToData(uiCsticStatus.getPromoMessages(), csticData.getMessages(), checkPromoMessageUiStatusMatch,
				applyUiStatusToMessagesConsumer);
		applyUiStatusToData(uiCsticStatus.getCsticValuess(), csticData.getDomainvalues(), checkCsticValueUiStatusMatch,
				applyUiStatusToCsticValueConsumer);
	}

	protected void updateShowFullLongTextInUiStatusCstics(final String csticKey, final boolean showFullLongText,
			final List<UiCsticStatus> uiCsticsStatus)
	{
		if (notNullAndNotEmpty(uiCsticsStatus))
		{
			final Optional<UiCsticStatus> uiOptstatus = findStatusValue(uiCsticsStatus,
					uiStatus -> csticKey.equals(uiStatus.getId()));
			uiOptstatus.ifPresent(uiStatus -> uiStatus.setShowFullLongText(showFullLongText));
		}
	}

	protected void toggleShowExtendedMessageOnStatusCstics(@Nonnull final String csticKey, final String csticValueKey,
			@Nonnull final String messageKey, final List<UiCsticStatus> uiCsticsStatus)
	{
		final Optional<UiCsticStatus> uiCsticStatus = findStatusValue(uiCsticsStatus, uiCstic -> uiCstic.getId().equals(csticKey));

		if (uiCsticStatus.isPresent())
		{
			final Optional<List<UiPromoMessageStatus>> messages;
			if (StringUtils.isNotEmpty(csticValueKey))
			{
				final List<UiCsticValueStatus> uiCsticValueStatuses = uiCsticStatus.get().getCsticValuess();
				messages = Optional.ofNullable(getMessgesForCsticValue(csticValueKey, uiCsticValueStatuses));
			}
			else
			{
				messages = Optional.ofNullable(uiCsticStatus.get().getPromoMessages());
			}

			if (messages.isPresent())
			{
				final Optional<UiPromoMessageStatus> promoMessageStatus = findStatusValue(messages.get(),
						msg -> messageKey.equals(msg.getId()));
				promoMessageStatus.ifPresent(msg -> msg.setShowExtendedMessage(!msg.isShowExtendedMessage()));
			}
		}
	}

	protected List<UiPromoMessageStatus> getMessgesForCsticValue(final String csticValueKey,
			final List<UiCsticValueStatus> uiCsticValueStatuses)
	{
		List<UiPromoMessageStatus> messages = Collections.emptyList();
		final Optional<UiCsticValueStatus> uiCsticValueStatus = findStatusValue(uiCsticValueStatuses,
				value -> csticValueKey.equals(value.getId()));
		if (uiCsticValueStatus.isPresent())
		{
			messages = uiCsticValueStatus.get().getPromoMessages();
		}

		return messages;
	}

	/**
	 * Provides the configuration DTO with UI relevant settings valid in its initial state
	 *
	 * @param configData
	 */
	public void setInitialStatus(final ConfigurationData configData)
	{
		final List<UiGroupData> csticGroups = configData.getGroups();
		setInitialGroupStatus(csticGroups, 0);

		configData.setSpecificationTreeCollapsed(false);
		configData.setPriceSummaryCollapsed(false);
		configData.setHideImageGallery(true);
		configData.setGroupIdToDisplay(csticGroups.get(0).getId());
	}

	protected void setInitialGroupStatus(final List<UiGroupData> uiGroups, final int level)
	{
		final int subLevel = level + 1;
		boolean firstGroup = true;
		for (final UiGroupData uiGroup : uiGroups)
		{
			if (uiGroup.isConfigurable())
			{
				uiGroup.setCollapsed(!firstGroup);
				firstGroup = false;
			}
			else
			{
				uiGroup.setCollapsed(true);
			}

			uiGroup.setCollapsedInSpecificationTree(subLevel != 1);

			if (hasSubGroups(uiGroup))
			{
				setInitialGroupStatus(uiGroup.getSubGroups(), subLevel);
			}
			if (hasCstics(uiGroup))
			{
				setInitialCsticStatus(uiGroup.getCstics());
			}
		}
	}

	protected void setInitialCsticStatus(final List<CsticData> cstics)
	{
		for (final CsticData cstic : cstics)
		{
			cstic.setShowFullLongText(false);
		}
	}

	protected boolean hasSubGroups(final UiGroupData uiGroup)
	{
		final List<UiGroupData> subGroups = uiGroup.getSubGroups();
		return notNullAndNotEmpty(subGroups);
	}

	protected boolean notNullAndNotEmpty(final List subGroups)
	{
		return subGroups != null && !subGroups.isEmpty();
	}

	protected boolean hasCstics(final UiGroupData uiGroup)
	{
		final List<CsticData> cstics = uiGroup.getCstics();
		return notNullAndNotEmpty(cstics);
	}

	/**
	 * Updates UI status with the current state of the configuration DTO
	 *
	 * @param configData
	 * @return UI status which we put into the session to persist the currently selected group e.g.
	 */
	public UiStatus extractUiStatusFromConfiguration(final ConfigurationData configData)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Extract UI status from config with [CONFIG_ID='" + replaceNewLineForLog(configData.getConfigId()) + "']");
		}

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setNumberOfConflictsToDisplay(getNumberOfConflicts(configData));
		uiStatus.setPriceSummaryCollapsed(configData.isPriceSummaryCollapsed());
		uiStatus.setSpecificationTreeCollapsed(configData.isSpecificationTreeCollapsed());
		uiStatus.setHideImageGallery(configData.isHideImageGallery());
		uiStatus.setGroupIdToDisplay(configData.getGroupIdToDisplay());
		uiStatus.setQuantity(configData.getQuantity());
		final List<UiGroupStatus> uiGroups = new ArrayList<>();
		extractUiStatusFromUiGroup(configData.getGroups(), uiGroups, configData);
		if (configData.isAutoExpand())
		{
			uiStatus.setFirstErrorCsticId(configData.getFocusId());
		}
		uiStatus.setGroups(uiGroups);

		return uiStatus;
	}

	/**
	 * Determine number of conflicts by calculating the conflict groups
	 *
	 * @param configData
	 * @return Number of conflict groups
	 */

	public int getNumberOfConflicts(final ConfigurationData configData)
	{
		for (final UiGroupData group : configData.getGroups())
		{
			if (GroupType.CONFLICT_HEADER.equals(group.getGroupType()))
			{
				return group.getSubGroups().size();
			}

		}
		return 0;
	}


	/**
	 * Recursively checks a group hierarchy. If a group is found that matches the given group id its collapsed state is
	 * Toggled. If the toggle was an expand, all parent groups are expanded as well.
	 *
	 * @param groupIdToToggle
	 *           id of group to toggle
	 * @param groups
	 *           list of groups to check
	 * @param forceExpand
	 * @return the goup that was toggled, or <code>null</code> if no group with the given ID was found.
	 */
	protected UiGroupStatus toggleGroup(final String groupIdToToggle, final List<UiGroupStatus> groups, final boolean forceExpand)
	{
		UiGroupStatus toggledGroup = null;
		boolean foundToggledGroup = false;
		for (final UiGroupStatus uiGroup : groups)
		{
			if (groupIdToToggle.equals(uiGroup.getId()))
			{
				LOG.debug("Toggle group with id: '" + uiGroup.getId() + "' to collapsed=" + !(uiGroup.isCollapsed() || forceExpand));
				// toggle group itself
				uiGroup.setCollapsed(!(uiGroup.isCollapsed() || forceExpand));
				toggledGroup = uiGroup;
				foundToggledGroup = true;
			}
			else
			{
				final List<UiGroupStatus> subGroups = uiGroup.getSubGroups();
				if (notNullAndNotEmpty(subGroups))
				{
					toggledGroup = toggleGroup(groupIdToToggle, subGroups, forceExpand);
					foundToggledGroup = toggleParentIfNeeded(toggledGroup, uiGroup);
				}
			}
			if (foundToggledGroup)
			{
				break;
			}
		}
		return toggledGroup;
	}

	protected boolean toggleParentIfNeeded(final UiGroupStatus toggledGroup, final UiGroupStatus parentGroup)
	{
		boolean foundToggledGroup = false;
		if (toggledGroup != null && !toggledGroup.isCollapsed())
		{
			LOG.debug("Expand group with id: '" + parentGroup.getId() + "'");
			// if toggled child was expanded, make sure this group is expanded as well
			parentGroup.setCollapsed(false);
			foundToggledGroup = true;
		}
		return foundToggledGroup;
	}

	protected UiGroupStatus toggleGroupInSpecTree(final String groupIdToToggle, final List<UiGroupStatus> groups,
			final boolean forceExpand)
	{
		UiGroupStatus toggledGroup = null;
		for (final UiGroupStatus uiGroup : groups)
		{
			if (groupIdToToggle.equals(uiGroup.getId()))
			{
				LOG.debug("Toggle group in specification tree with id: '" + uiGroup.getId() + "' to collapsedInSpecificationTree='"
						+ !(uiGroup.isCollapsedInSpecificationTree() || forceExpand) + "'");
				// toggle group itself
				uiGroup.setCollapsedInSpecificationTree(!(uiGroup.isCollapsedInSpecificationTree() || forceExpand));
				toggledGroup = uiGroup;
			}
			else
			{
				final List<UiGroupStatus> subGroups = uiGroup.getSubGroups();
				if (notNullAndNotEmpty(subGroups))
				{
					toggledGroup = toggleGroupInSpecTree(groupIdToToggle, subGroups, forceExpand);
					toggleParentGroupInSpecTreeIfNeeded(toggledGroup, uiGroup);
				}
			}
			if (toggledGroup != null)
			{
				break;
			}
		}
		return toggledGroup;
	}

	protected void toggleParentGroupInSpecTreeIfNeeded(final UiGroupStatus toggledGroup, final UiGroupStatus parentGroup)
	{
		if (toggledGroup != null && !toggledGroup.isCollapsedInSpecificationTree())
		{
			LOG.debug("Expand group in specification tree with id: '" + parentGroup.getId() + "'");
			// if toggled child was expanded, make sure this group is expanded as well
			parentGroup.setCollapsedInSpecificationTree(false);
		}
	}

	protected void expandGroupAndCollapseOther(final String selectedGroup, final List<UiGroupStatus> groups)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Expand tab with id: '" + replaceNewLineForLog(selectedGroup) + "'");
		}

		for (final UiGroupStatus uiGroup : groups)
		{
			if (selectedGroup.equals(uiGroup.getId()))
			{
				uiGroup.setCollapsed(false);
			}
			else
			{
				uiGroup.setCollapsed(true);
			}
		}
	}

	protected void extractUiStatusFromUiGroup(final List<UiGroupData> uiGroups, final List<UiGroupStatus> uiGroupsStatus,
			final ConfigurationData configData)
	{
		for (final UiGroupData uiGroup : uiGroups)
		{
			final UiGroupStatus uiGroupStatus = new UiGroupStatus();
			final String groupId = uiGroup.getId();

			//mark: group has been visited
			if (configData.isSingleLevel())
			{
				uiGroupStatus.setVisited(uiGroup.isVisited() || !uiGroup.isCollapsed());
			}
			else
			{
				uiGroupStatus.setVisited(uiGroup.isVisited() || groupId.equals(configData.getGroupIdToDisplay()));
			}

			if (LOG.isDebugEnabled())
			{
				LOG.debug("UI group: '" + groupId + "' has been visited: '" + uiGroupStatus.isVisited() + "'");
			}

			uiGroupStatus.setId(groupId);
			uiGroupStatus.setCollapsed(uiGroup.isCollapsed());
			uiGroupStatus.setCollapsedInSpecificationTree(uiGroup.isCollapsedInSpecificationTree());
			uiGroupStatus.setCstics(extractUiStatusFromCstic(uiGroup.getCstics()));
			if (hasSubGroups(uiGroup))
			{
				final List<UiGroupStatus> uiSubGroups = new ArrayList<>();
				extractUiStatusFromUiGroup(uiGroup.getSubGroups(), uiSubGroups, configData);
				uiGroupStatus.setSubGroups(uiSubGroups);
			}
			uiGroupsStatus.add(uiGroupStatus);
		}
	}

	protected List<UiCsticStatus> extractUiStatusFromCstic(final List<CsticData> cstics)
	{
		if (CollectionUtils.isEmpty(cstics))
		{
			return Collections.emptyList();
		}

		final List<UiCsticStatus> uiCsticsStatus = new ArrayList<>();
		for (final CsticData cstic : cstics)
		{
			final UiCsticStatus uiCsticStatus = new UiCsticStatus();
			uiCsticStatus.setId(cstic.getKey());
			uiCsticStatus.setShowFullLongText(cstic.isShowFullLongText());
			uiCsticStatus.setPromoMessages(extractUiStatusFromMessages(cstic.getMessages()));
			uiCsticStatus.setCsticValuess(extractUiStatusFromCsticValue(cstic.getDomainvalues()));
			uiCsticsStatus.add(uiCsticStatus);
		}

		return uiCsticsStatus;
	}

	protected List<UiPromoMessageStatus> extractUiStatusFromMessages(final List<ProductConfigMessageData> messages)
	{
		if (CollectionUtils.isEmpty(messages))
		{
			return Collections.emptyList();
		}
		final List<UiPromoMessageStatus> uiProductConfigMessagesStatus = new ArrayList<>();

		for (final ProductConfigMessageData message : messages)
		{
			final UiPromoMessageStatus uiMessageStatus = new UiPromoMessageStatus();

			final String messageId = getMessageId(message);

			uiMessageStatus.setId(messageId);
			uiMessageStatus.setShowExtendedMessage(message.isShowExtendedMessage());
			uiProductConfigMessagesStatus.add(uiMessageStatus);
		}

		return uiProductConfigMessagesStatus;
	}

	protected String getMessageId(final ProductConfigMessageData message)
	{
		return message.getMessage() + (message.getExtendedMessage() != null ? message.getExtendedMessage() : "");
	}

	protected List<UiCsticValueStatus> extractUiStatusFromCsticValue(final List<CsticValueData> domainvalues)
	{
		if (CollectionUtils.isEmpty(domainvalues))
		{
			return Collections.emptyList();
		}

		final List<UiCsticValueStatus> uiCsticValuesStatus = new ArrayList<>();

		for (final CsticValueData csticValue : domainvalues)
		{
			final UiCsticValueStatus uiCsticValueStatus = new UiCsticValueStatus();
			uiCsticValueStatus.setId(csticValue.getName());
			uiCsticValueStatus.setPromoMessages(extractUiStatusFromMessages(csticValue.getMessages()));
			uiCsticValuesStatus.add(uiCsticValueStatus);
		}

		return uiCsticValuesStatus;
	}

	protected boolean hasCsticValues(final CsticData cstic)
	{
		return CollectionUtils.isNotEmpty(cstic.getDomainvalues());
	}

	/**
	 * Apply user changes to the UI status (session) object. User might e.g. open a group which was not visited before
	 *
	 * @param requestData
	 * @param oldUiState
	 *           Previous version of the UI status
	 * @param uiTrackingRecorder
	 *           recorder for ui activities
	 * @return New version of the UI status
	 */
	public UiStatus updateUIStatusFromRequest(final ConfigurationData requestData, final UiStatus oldUiState,
			final UiTrackingRecorder uiTrackingRecorder)
	{
		UiStatus newUiState = oldUiState;
		if (oldUiState == null)
		{
			LOG.info("No old UI-State provided for config '" + replaceNewLineForLog(requestData.getConfigId())
					+ "' while updating configuration; creating new UI-State from request");
			newUiState = extractUiStatusFromConfiguration(requestData);
		}
		else
		{
			newUiState.setPriceSummaryCollapsed(requestData.isPriceSummaryCollapsed());
			newUiState.setSpecificationTreeCollapsed(requestData.isSpecificationTreeCollapsed());
			newUiState.setGroupIdToDisplay(requestData.getGroupIdToDisplay());
			storeLastNoneConflictGroupId(newUiState, requestData);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Group ID for display from request: '" + replaceNewLineForLog(requestData.getGroupIdToDisplay()) + "'");
			}

			updateGroupUIStatusFromRequestData(newUiState.getGroups(), requestData.getGroups());
		}

		handleCPQAction(requestData, newUiState, uiTrackingRecorder);

		final String selectedGroup = requestData.getSelectedGroup();
		if (notNullAndNotEmpty(selectedGroup))
		{
			expandGroupAndCollapseOther(selectedGroup, newUiState.getGroups());
		}

		return newUiState;
	}

	protected void storeLastNoneConflictGroupId(final UiStatus newUiState, final ConfigurationData requestData)
	{
		if (null != requestData.getGroupIdToDisplay()
				&& !requestData.getGroupIdToDisplay().startsWith(SapproductconfigfrontendWebConstants.CONFLICT_PREFIX))
		{
			newUiState.setLastNoneConflictGroupId(requestData.getGroupIdToDisplay());
		}
	}

	protected void handleCPQAction(final ConfigurationData requestData, final UiStatus oldUiState,
			final UiTrackingRecorder uiTrackingRecorder)
	{
		final CPQActionType action = requestData.getCpqAction();
		if (CPQActionType.TOGGLE_GROUP.equals(action) || CPQActionType.MENU_NAVIGATION.equals(action))
		{
			final UiGroupStatus toggledGroup = toggleGroup(requestData.getGroupIdToToggle(), oldUiState.getGroups(),
					requestData.isForceExpand());
			if (toggledGroup != null)
			{
				uiTrackingRecorder.recordGroupInteraction(requestData, toggledGroup.getId(), toggledGroup.isCollapsed());
			}
			requestData.setGroupIdToToggle("");
		}
		if (CPQActionType.MENU_NAVIGATION.equals(action))
		{
			final UiGroupStatus toggledGroup = toggleGroupInSpecTree(requestData.getGroupIdToToggleInSpecTree(),
					oldUiState.getGroups(), false);
			if (toggledGroup != null)
			{
				uiTrackingRecorder.recordMenuToggle(requestData, toggledGroup.getId(), toggledGroup.isCollapsedInSpecificationTree());
			}
			requestData.setGroupIdToToggleInSpecTree("");
		}
	}

	protected boolean notNullAndNotEmpty(final String id)
	{
		return id != null && !id.isEmpty();
	}

	protected void updateGroupUIStatusFromRequestData(final List<UiGroupStatus> uiSateGroups,
			final List<UiGroupData> requestGroups)
	{
		if (notNullAndNotEmpty(requestGroups))
		{
			for (int groupIdx = 0; groupIdx < requestGroups.size(); groupIdx++)
			{
				final UiGroupData requestGroup = requestGroups.get(groupIdx);
				final UiGroupStatus statusGroup = findStatusGroup(uiSateGroups, requestGroup, groupIdx);
				updateSingleStatusGroupFromRequest(requestGroup, statusGroup);
			}
		}
	}

	protected void updateSingleStatusGroupFromRequest(final UiGroupData requestGroup, final UiGroupStatus statusGroup)
	{
		if (statusGroup != null)
		{
			if (requestGroup.isVisited())
			{
				LOG.debug("Setting uiGroup='" + statusGroup.getId() + "' eas displayed on the UI, setting visited=true");
				statusGroup.setVisited(true);
			}
			updateGroupUIStatusFromRequestData(statusGroup.getSubGroups(), requestGroup.getSubGroups());
			updateCsticUIStatusFromRequestData(statusGroup.getCstics(), requestGroup.getCstics());
		}
		else
		{
			if (requestGroup.getId() != null && LOG.isDebugEnabled())
			{
				LOG.debug("UI Status is inconsistent. For UiGroup '" + requestGroup.getId()
						+ "' no corresponding UIStatusGroup was found!");
			}
		}
	}

	protected void updateCsticUIStatusFromRequestData(final List<UiCsticStatus> statusCstics, final List<CsticData> requestCstics)
	{
		if (CollectionUtils.isEmpty(requestCstics) || CollectionUtils.isEmpty(statusCstics))
		{
			return;
		}

		for (int csticIdx = 0; csticIdx < requestCstics.size(); csticIdx++)
		{
			final CsticData requestCstic = requestCstics.get(csticIdx);
			final Optional<UiCsticStatus> optStatusCstic = findStatusValue(statusCstics,
					cstic -> requestCstic.getKey().equals(cstic.getId()), csticIdx);
			if (optStatusCstic.isPresent())
			{
				final UiCsticStatus statusCstic = optStatusCstic.get();
				statusCstic.setShowFullLongText(requestCstic.isShowFullLongText());

				updateCsticValueUIStatusFromRequestData(statusCstic.getCsticValuess(), requestCstic.getDomainvalues());
				updateMessagesUiStatusFromRequestData(statusCstic.getPromoMessages(), requestCstic.getMessages());
			}
			else
			{
				LOG.debug("UI Status is inconsistent. For Cstic '" + requestCstic.getKey()
						+ "' no corresponding UIStatusCstic was found!");
			}
		}
	}

	protected void updateCsticValueUIStatusFromRequestData(final List<UiCsticValueStatus> statusCsticValues,
			final List<CsticValueData> requestCsticValues)
	{
		if (checkIfEmpty(statusCsticValues, requestCsticValues))
		{
			return;
		}

		for (int csticValueIdx = 0; csticValueIdx < requestCsticValues.size(); csticValueIdx++)
		{
			final CsticValueData requestCsticValue = requestCsticValues.get(csticValueIdx);
			final Optional<UiCsticValueStatus> statusCsticValue = findStatusValue(statusCsticValues,
					value -> requestCsticValue.getName().equals(value.getId()), csticValueIdx);

			statusCsticValue.ifPresent(
					value -> updateMessagesUiStatusFromRequestData(value.getPromoMessages(), requestCsticValue.getMessages()));
		}
	}

	private static <T, K> boolean checkIfEmpty(final List<T> listOne, final List<K> listTwo)
	{
		return CollectionUtils.isEmpty(listOne) || CollectionUtils.isEmpty(listTwo);
	}

	protected void updateMessagesUiStatusFromRequestData(final List<UiPromoMessageStatus> statusMessages,
			final List<ProductConfigMessageData> requestMessages)
	{
		if (checkIfEmpty(statusMessages, requestMessages))
		{
			return;
		}

		for (int messageIdx = 0; messageIdx < requestMessages.size(); messageIdx++)
		{
			final ProductConfigMessageData requestMessage = requestMessages.get(messageIdx);
			final Optional<UiPromoMessageStatus> statusMessage = findStatusValue(statusMessages,
					message -> getMessageId(requestMessage).equals(message.getId()), messageIdx);
			statusMessage.ifPresent(msg -> msg.setShowExtendedMessage(requestMessage.isShowExtendedMessage()));
		}
	}

	protected <T> Optional<T> findStatusValue(@NotNull final List<T> statusValues, final Predicate<T> idMatch)
	{
		return statusValues.stream().filter(idMatch).findFirst();
	}


	protected <T> Optional<T> findStatusValue(@NotNull final List<T> statusValues, final Predicate<T> idMatch, final int index)
	{
		Optional<T> valueToReturn = Optional.empty();
		if (statusValues.size() > index)
		{
			valueToReturn = Optional.ofNullable(statusValues.get(index));
		}
		if (!(valueToReturn.isPresent() && idMatch.test(valueToReturn.get())))
		{
			valueToReturn = findStatusValue(statusValues, idMatch);
		}

		return valueToReturn;
	}

	protected UiGroupStatus findStatusGroup(final List<UiGroupStatus> uiStatusGroups, final UiGroupData requestGroup,
			final int groupIdx)
	{

		UiGroupStatus statusGroupToReturn = null;
		if (notNullAndNotEmpty(uiStatusGroups) && notNullAndNotEmpty(requestGroup.getId()))
		{
			statusGroupToReturn = findStatusGroupForExisting(uiStatusGroups, requestGroup, groupIdx);
		}
		return statusGroupToReturn;
	}

	protected UiGroupStatus findStatusGroupForExisting(final List<UiGroupStatus> uiSateGroups, final UiGroupData requestGroup,
			final int groupIdx)
	{
		UiGroupStatus statusGroupToReturn = null;
		if (groupIdx < uiSateGroups.size())
		{
			// guess same index
			statusGroupToReturn = uiSateGroups.get(groupIdx);
		}

		if (!uiStatusGroupMatchesUiGroup(requestGroup, statusGroupToReturn))
		{
			// full list scan
			statusGroupToReturn = null;
			for (final UiGroupStatus statusGroup : uiSateGroups)
			{
				if (uiStatusGroupMatchesUiGroup(requestGroup, statusGroup))
				{
					statusGroupToReturn = statusGroup;
					break;
				}
			}
		}
		return statusGroupToReturn;
	}

	protected boolean uiStatusGroupMatchesUiGroup(final UiGroupData uiGroup, final UiGroupStatus uiStatusGroup)
	{
		return uiGroup != null && uiStatusGroup != null && uiGroup.getId().equals(uiStatusGroup.getId());
	}

	protected String replaceNewLineForLog(final String str)
	{
		if (notNullAndNotEmpty(str))
		{
			return str.replace("\n", "_").replace("\r", "_");
		}
		return str;
	}

	/**
	 * Update old UI status
	 * 
	 * @param oldUiStatus
	 * 	old UI status
	 * @param uiStatus
	 * 	new UI status
	 */
	public void updateNewUiStateFromOld(final UiStatus oldUiStatus, final UiStatus uiStatus)
	{
		uiStatus.setLastNoneConflictGroupId(oldUiStatus.getLastNoneConflictGroupId());

	}
}
