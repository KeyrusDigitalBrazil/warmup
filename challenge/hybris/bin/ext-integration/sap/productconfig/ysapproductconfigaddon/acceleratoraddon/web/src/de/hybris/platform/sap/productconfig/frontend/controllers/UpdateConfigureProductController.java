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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.frontend.UiGroupStatus;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.validator.ConflictError;
import de.hybris.platform.sap.productconfig.frontend.validator.MandatoryFieldError;
import de.hybris.platform.sap.productconfig.services.exceptions.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.services.tracking.RecorderParameters;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.sap.security.core.server.csi.XSSEncoder;


/**
 * Controller for updating the dynamic product configuration content page.
 *
 * @see ConfigureProductController
 */
@Controller("asdf")
@RequestMapping()
public class UpdateConfigureProductController extends AbstractProductConfigController
{
	private static final String AJAX_VIEW_NAME = SapproductconfigfrontendWebConstants.CONFIG_PAGE_VIEW_NAME
			+ SapproductconfigfrontendWebConstants.AJAX_SUFFIX;
	private static final Logger LOGGER = Logger.getLogger(UpdateConfigureProductController.class);
	private static final String CONFLICT_ID_START_LITERAL = "conflict.";

	/**
	 * Updates a configuration based on the user inputs and renders back the changes.
	 *
	 * @param configData
	 *           current configuration
	 * @param bindingResult
	 *           error message store
	 * @param model
	 *           view model
	 * @param request
	 *           HTTP-Request
	 * @return view name
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/cpq/config", method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateConfigureProduct(
			@ModelAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE) @Valid final ConfigurationData configData,
			final BindingResult bindingResult, final Model model, final HttpServletRequest request) throws BusinessException
	{
		final UpdateDataHolder updateData = initUpdateData(configData, bindingResult, request);
		ModelAndView view;
		try
		{
			if (!isConfigLinkedToCart(configData.getConfigId()) && isConfigRemoved(updateData.getProductCode()))
			{
				throw new ConfigurationNotFoundException("Configuration was removed");
			}
			else
			{
				updateConfiguration(updateData, model);
				view = render(model, request, updateData);
			}
		}
		catch (final ConfigurationNotFoundException ex)
		{
			view = getConfigurationErrorHandler().handleErrorForAjaxRequest(request, model);
			LOGGER.debug("Configuration not found anymore", ex);
			LOGGER.warn("Configuration not found anymore, see debug log for details.");
		}
		return view;
	}

	protected ModelAndView render(final Model model, final HttpServletRequest request, final UpdateDataHolder updateData)
			throws CMSItemNotFoundException
	{
		model.addAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, updateData.getConfigData());
		model.addAttribute(BindingResult.MODEL_KEY_PREFIX + SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE,
				updateData.getBindingResult());
		populateCMSAttributes(model);
		populateProductData(updateData.getProductCode(), model, request);

		return new ModelAndView(AJAX_VIEW_NAME);
	}

	protected void updateConfiguration(final UpdateDataHolder updateData, final Model model) throws BusinessException
	{
		if (LOGGER.isDebugEnabled())
		{
			final long startTime = updateData.timeElapsed();
			LOGGER.debug("UPDATE started at: '" + startTime + "'");
		}

		beforeUpdate(updateData);

		if (LOGGER.isDebugEnabled())
		{
			final long duration = updateData.timeElapsed();
			LOGGER.debug("BEFORE UPDATE took " + duration + " ms");
		}

		executeUpdate(updateData);

		if (LOGGER.isDebugEnabled())
		{
			final long duration = updateData.timeElapsed();
			LOGGER.debug("EXECUTE UPDATE took " + duration + " ms");
		}

		afterUpdate(updateData, model);

		if (LOGGER.isDebugEnabled())
		{
			final long duration = updateData.timeElapsed();
			LOGGER.debug("AFTER UPDATE took " + duration + " ms");
		}
	}

	protected void afterUpdate(final UpdateDataHolder updateData, final Model model) throws BusinessException
	{
		final ConfigurationData configData = updateData.getConfigData();
		getUiStateHandler().resetGroupStatus(configData);
		handleCPQAction(updateData);

		setCartEntryLinks(configData);

		// first apply last UI-State from last roundtrip, then re-create actual UI-State from actual Config
		getUiStatusSync().applyUiStatusToConfiguration(configData, updateData.getUiStatus());
		getUiStateHandler().compileGroupForDisplay(configData, updateData.getUiStatus());

		// UI-Errors
		final Map<String, FieldError> userInputToRemember = updateData.getUiStatus().getUserInputToRemember();
		final Map<String, FieldError> userInputToRestore = getUiStateHandler()
				.mergeUiErrors(updateData.getUiStatus().getUserInputToRestore(), userInputToRemember);
		final BindingResult bindingResult = getUiStateHandler().restoreValidationErrorsAfterUpdate(userInputToRestore, configData,
				updateData.getBindingResult());

		// conflicts
		getProductConfigurationConflictChecker().checkConflicts(configData, bindingResult);
		if (isConfigLinkedToCart(configData.getConfigId()))
		{
			getProductConfigurationConflictChecker().checkMandatoryFields(configData, bindingResult);
		}
		getProductConfigurationConflictChecker().checkCompletness(configData);
		getUiStateHandler().countNumberOfUiErrorsPerGroup(configData.getGroups());

		handleAutoExpand(updateData, configData);
		getUiStateHandler().handleConflictSolverMessage(updateData.getUiStatus(),
				getUiStatusSync().getNumberOfConflicts(configData), model);
		getUiStateHandler().handleProductConfigMessages(configData, model);

		final UiStatus oldUiStatus = updateData.getUiStatus();
		updateData.setUiStatus(getUiStatusSync().extractUiStatusFromConfiguration(updateData.getConfigData()));
		getUiStatusSync().updateNewUiStateFromOld(oldUiStatus, updateData.getUiStatus());

		updateData.getUiStatus().setUserInputToRestore(userInputToRestore);
		updateData.getUiStatus().setUserInputToRemember(removeOutdatedValidationErrors(updateData));
		setUiStatusForConfig(updateData.getConfigData(), updateData.getUiStatus());

		logModelmetaData(configData);

		updateData.setBindingResult(bindingResult);
		resetCPQActionType(updateData.getConfigData());
	}


	protected void handleCPQAction(final UpdateDataHolder updateData)
	{
		final CPQActionType action = updateData.getConfigData().getCpqAction();

		// Handle groupId to display when solving conflicts
		handleGroupIdToDisplayWhenSolvingConflicts(action, updateData);

		// Handle groupId to display for navigation links
		handleGroupIdToDisplayForNavigationLinks(action, updateData);

		// Handle show full longtext flag consistently for conflict and normal cstic groups
		handleShowFullLongTextFlag(action, updateData);

		// Set groupIdToDisplay of UiStatus according to previous/next button clicked (or do nothing if those buttons haven't been clicked)
		identifyPrevNextGroup(action, updateData);

		handleToggleImageGallery(action, updateData);

		handleShowExtendedMessageFlag(action, updateData);
	}

	protected void handleShowExtendedMessageFlag(final CPQActionType action, final UpdateDataHolder updateData)
	{
		final ConfigurationData configData = updateData.getConfigData();
		final String focusId = configData.getFocusId();
		if (isFocusIdSet(focusId) && (CPQActionType.TOGGLE_EXTENDED_MESSAGE.equals(action)))
		{
			final List<UiGroupStatus> uiStatusGroups = updateData.getUiStatus().getGroups();

			final String[] messageElement = focusId.split(";");
			if (messageElement.length != 4)
			{
				LOGGER.error("ID for TOOGLE_EXTENDED_MESSAGE is incorrect.");
				return;
			}

			String csticId = messageElement[1];
			if (csticId.startsWith(CONFLICT_ID_START_LITERAL))
			{
				csticId = csticId.substring(9);
			}

			final String csticValueId = messageElement[2];
			String messageKey = null;
			try
			{
				messageKey = XSSEncoder.encodeHTML(messageElement[3]);
			}
			catch (final UnsupportedEncodingException e)
			{
				LOGGER.debug("Failed to encode message key", e);
				LOGGER.error("Failed to encode message key, see debug log for details.");
			}

			if (StringUtils.isNotEmpty(csticId) && StringUtils.isNotEmpty(messageKey))
			{
				getUiStatusSync().toggleShowExtendedMessageOnUIStatusGroups(csticId, csticValueId, messageKey, uiStatusGroups);
				getUiRecorder().recordExtendedMessageToggle(configData,
						(StringUtils.isNotEmpty(csticValueId) ? RecorderParameters.CSTIC_VALUE : RecorderParameters.CSTIC));
			}

			String newFocusId = messageElement[0].substring(0, messageElement[0].lastIndexOf('.'));
			if (messageElement[0].endsWith("linkLessDetails"))
			{
				newFocusId += ".linkMoreDetails";
			}
			else
			{
				newFocusId += ".linkLessDetails";
			}
			configData.setFocusId(newFocusId);
		}
	}

	protected void handleCPQActionBeforeUpdate(final UpdateDataHolder updateData)
	{
		final CPQActionType action = updateData.getConfigData().getCpqAction();
		checkAutoExpandMode(action, updateData);
	}

	protected void checkAutoExpandMode(final CPQActionType action, final UpdateDataHolder updateData)
	{
		final ConfigurationData configData = updateData.getConfigData();
		final String firstErrorCsticId = updateData.getUiStatus().getFirstErrorCsticId();
		if (CPQActionType.VALUE_CHANGED.equals(action) || CPQActionType.RETRACT_VALUE.equals(action))
		{
			checkAutoExpandModeOnValueChange(configData, firstErrorCsticId);
			configData.setFocusId(null);
		}
		else
		{
			// User has done any other action on the screen -> therefore the autoExpand mode is ended
			configData.setAutoExpand(false);
		}
		updateData.getUiStatus().setFirstErrorCsticId(null);
	}

	protected void checkAutoExpandModeOnValueChange(final ConfigurationData configData, final String firstErrorCsticId)
	{
		final String csticId = getCsticKeyForFocusIdPath(configData);
		getUiRecorder().recordValueChanges(configData, csticId);
		if (firstErrorCsticId == null)
		{
			return;
		}
		if (!isFirstErrorCurrentFocusCstic(firstErrorCsticId, csticId))
		{
			// User has not changed the cstic recommended by autoExpand mode -> therefore the autoExpand mode is ended
			configData.setAutoExpand(false);
		}
	}

	protected boolean isFirstErrorCurrentFocusCstic(final String firstErrorCsticId, final String focusId)
	{
		String checkCsticId = firstErrorCsticId;
		if (checkCsticId.startsWith(CONFLICT_ID_START_LITERAL))
		{
			checkCsticId = checkCsticId.substring(9);
		}
		return checkCsticId.equals(focusId);
	}

	protected String getCsticKeyForFocusIdPath(final ConfigurationData configData)
	{
		String csticKey = null;
		final String fieldPath = configData.getFocusId();

		final CsticData cstic = getUiStateHandler().getCsticForFieldPath(configData, fieldPath);
		if (cstic != null)
		{
			csticKey = cstic.getKey();
		}

		return csticKey;
	}

	protected void handleToggleImageGallery(final CPQActionType action, final UpdateDataHolder updateData)
	{
		if (CPQActionType.TOGGLE_IMAGE_GALLERY.equals(action))
		{
			final UiStatus uiStatus = updateData.getUiStatus();
			uiStatus.setHideImageGallery(!uiStatus.isHideImageGallery());
			getUiRecorder().recordImageGalleryToggle(updateData.getConfigData(), uiStatus.isHideImageGallery());
		}
	}

	protected void handleGroupIdToDisplayWhenSolvingConflicts(final CPQActionType action, final UpdateDataHolder updateData)
	{
		if (!CPQActionType.MENU_NAVIGATION.equals(action)
				&& (!CPQActionType.SHOW_FULL_LONG_TEXT.equals(action) && !CPQActionType.HIDE_FULL_LONG_TEXT.equals(action))
				&& (updateData.getUiStatus().getGroupIdToDisplay().startsWith(SapproductconfigfrontendWebConstants.CONFLICT_PREFIX)))
		{
			final String groupIdToDisplay = getUiStateHandler()
					.getGroupIdToDisplayAfterResolvingConflicts(updateData.getConfigData(), updateData.getUiStatus());
			updateData.getUiStatus().setGroupIdToDisplay(groupIdToDisplay);
		}
	}

	/**
	 * This method handles the show long text flag consistently for all occurrences of a cstic.<br>
	 * The same cstic can appear in several groups (e.g. in conflict groups and in normal cstic groups). The user clicks
	 * "show long text" for one occurence of a cstic. This should trigger the display/hide of the long text for all
	 * occurences of this cstic not only for the one where it was clicked.<br>
	 * Therefore the value of the show long text flag has to be propagated to all occurences of the same cstic in all
	 * groups.
	 *
	 * @param updateData
	 * @param action
	 */
	protected void handleShowFullLongTextFlag(final CPQActionType action, final UpdateDataHolder updateData)
	{
		final ConfigurationData configData = updateData.getConfigData();
		final String focusId = configData.getFocusId();
		if (isFocusIdSet(focusId)
				&& (CPQActionType.SHOW_FULL_LONG_TEXT.equals(action) || CPQActionType.HIDE_FULL_LONG_TEXT.equals(action)))
		{
			final List<UiGroupStatus> uiStatusGroups = updateData.getUiStatus().getGroups();

			boolean showFullLongText = false;
			if (CPQActionType.SHOW_FULL_LONG_TEXT.equals(action))
			{
				showFullLongText = true;
			}
			getUiStatusSync().updateShowFullLongTextinUIStatusGroups(focusId, showFullLongText, uiStatusGroups);
			getUiRecorder().recordLongTextToggle(configData);
			configData.setFocusId(null);
		}
	}


	protected void resetCPQActionType(final ConfigurationData configData)
	{
		configData.setCpqAction(null);
	}

	/**
	 * Checks if previous or next button has been clicked and tries to identify the previous or next group respectively.
	 * <br/>
	 * The identified groupId is changed in UIStatus so that this group will be displayed next.<br/>
	 * The groupId is not changed if the previous button has been clicked on first group or if the next button has been
	 * clicked on the last group.
	 *
	 * @param action
	 * @param updateData
	 */
	protected void identifyPrevNextGroup(final CPQActionType action, final UpdateDataHolder updateData)
	{
		final ConfigurationData configData = updateData.getConfigData();
		if (CPQActionType.NEXT_BTN.equals(action) || CPQActionType.PREV_BTN.equals(action))
		{
			// Previous or next button has been clicked
			String currentGroupId = configData.getGroupIdToDisplay();
			final List<UiGroupData> csticGroupsFlat = configData.getCsticGroupsFlat();

			// Find index of current group in list
			if (csticGroupsFlat != null)
			{
				final int currentGroupIndex = findCurrentGroupIndex(currentGroupId, csticGroupsFlat);
				// Current group has been found in list if index is greater than -1
				if (currentGroupIndex > -1)
				{
					currentGroupId = getGroupIdForPrevNextButtonClick(action, currentGroupId, csticGroupsFlat, currentGroupIndex);
					getUiRecorder().recordPrevNextButtonClicked(configData, currentGroupId);
					updateData.getUiStatus().setGroupIdToDisplay(currentGroupId);
				}
			}
		}
	}

	protected String getGroupIdForPrevNextButtonClick(final CPQActionType prevNextButtonClicked, final String currentGroupId,
			final List<UiGroupData> csticGroupsFlat, final int currentGroupIndex)
	{
		final String groupId;

		// Get previous group
		if (CPQActionType.PREV_BTN.equals(prevNextButtonClicked))
		{
			groupId = getPreviousGroupId(currentGroupId, csticGroupsFlat, currentGroupIndex);
		}
		// Get next group
		else
		{
			groupId = getNextGroupId(currentGroupId, csticGroupsFlat, currentGroupIndex);
		}
		return groupId;
	}

	protected int findCurrentGroupIndex(final String currentGroupId, final List<UiGroupData> csticGroupsFlat)
	{
		int currentGroupIndex = -1;
		for (int i = 0; i < csticGroupsFlat.size(); i++)
		{
			if (csticGroupsFlat.get(i).getId().equals(currentGroupId))
			{
				currentGroupIndex = i;
				break;
			}
		}
		return currentGroupIndex;
	}

	protected String getNextGroupId(final String currentGroupId, final List<UiGroupData> csticGroupsFlat,
			final int currentGroupPosition)
	{
		String groupId = currentGroupId;

		final int nextPosition = currentGroupPosition + 1;
		if (nextPosition == csticGroupsFlat.size())
		{
			LOGGER.debug("Identify next group: Current group is already last group: do not change currentGroupId");
		}
		else
		{
			final UiGroupData nextGroup = csticGroupsFlat.get(nextPosition);
			// next found, change currentGroupId
			groupId = nextGroup.getId();
		}
		return groupId;
	}

	protected String getPreviousGroupId(final String currentGroupId, final List<UiGroupData> csticGroupsFlat,
			final int currentGroupPosition)
	{
		final int previousPosition = currentGroupPosition - 1;
		String groupId = currentGroupId;
		if (previousPosition < 0)
		{
			LOGGER.debug("Identify previous group: Current group is already first group: do not change currentGroupId");
		}
		else
		{
			final UiGroupData previousGroup = csticGroupsFlat.get(previousPosition);
			// previous found, change currentGroupId
			groupId = previousGroup.getId();
		}
		return groupId;
	}

	protected void handleAutoExpand(final UpdateDataHolder updateData, final ConfigurationData configData)
	{
		if (!configData.isAutoExpand())
		{
			return;
		}

		if (!configData.isForceExpand())
		{
			handleAutoExpandAndSyncUIStatus(updateData, configData);
			return;
		}

		final CsticData errorCstic = getUiStateHandler().getFirstCsticWithErrorInGroup(configData.getGroupToDisplay().getGroup());
		if (errorCstic != null)
		{
			configData.setFocusId(errorCstic.getKey());
		}
	}

	protected void handleGroupIdToDisplayForNavigationLinks(final CPQActionType action, final UpdateDataHolder updateData)
	{
		final ConfigurationData configData = updateData.getConfigData();
		final String focusId = configData.getFocusId();
		if (isFocusIdSet(focusId)
				&& (CPQActionType.NAV_TO_CSTIC_IN_GROUP.equals(action) || CPQActionType.NAV_TO_CSTIC_IN_CONFLICT.equals(action)))
		{
			UiGroupData uiGroup = null;
			if (CPQActionType.NAV_TO_CSTIC_IN_GROUP.equals(action))
			{
				uiGroup = findFirstGroupForCsticId(configData.getGroups(), focusId);
				getUiRecorder().recordNavigationToCsticInGroup(configData, uiGroup);
			}
			else if (CPQActionType.NAV_TO_CSTIC_IN_CONFLICT.equals(action))
			{
				uiGroup = findFirstConflictGroupForCsticId(configData.getGroups(), focusId);
				if (uiGroup != null)
				{
					// if conflict group has been found, manipulate the focus id that the cstic can be found in the conflict group
					configData.setFocusId(
							CONFLICT_ID_START_LITERAL + getUiStateHandler().getConflictGroupNo(configData, uiGroup) + "." + focusId);
					getUiRecorder().recordNavigationToCsticInConflict(configData, uiGroup);
				}
			}

			if (uiGroup == null)
			{
				return;
			}

			updateData.getUiStatus().setGroupIdToDisplay(uiGroup.getId());
		}
	}

	protected UiGroupData findFirstConflictGroupForCsticId(final List<UiGroupData> uiGroups, final String csticId)
	{
		if (uiGroups == null)
		{
			return null;
		}

		final Optional<UiGroupData> result = uiGroups.stream()
				.filter(group -> GroupType.CONFLICT_HEADER.equals(group.getGroupType())).findFirst();

		if (!result.isPresent())
		{
			return null;
		}

		final UiGroupData conflictHeaderGroup = result.get();

		return conflictHeaderGroup.getSubGroups().stream().filter(group -> isCsticPartOfGroup(group, csticId)).findFirst()
				.orElse(null);
	}

	protected boolean isFocusIdSet(final String focusId)
	{
		return focusId != null && !focusId.isEmpty();
	}

	protected void handleAutoExpandAndSyncUIStatus(final UpdateDataHolder updateData, final ConfigurationData configData)
	{
		final UiGroupData expandedGroup = getUiStateHandler().handleAutoExpand(configData, updateData.getUiStatus());

		if (expandedGroup != null)
		{
			updateData.getUiStatus().setGroupIdToDisplay(expandedGroup.getId());
			getUiStateHandler().compileGroupForDisplay(configData, updateData.getUiStatus());
		}
	}

	protected void executeUpdate(final UpdateDataHolder updateData)
	{
		if (updateData.getConfigData().getGroups() != null)
		{
			getConfigFacade().updateConfiguration(updateData.getConfigData());
		}
		updateData.setConfigData(getConfigFacade().getConfiguration(updateData.getConfigData()));

	}

	protected void beforeUpdate(final UpdateDataHolder updateData)
	{
		final UiStatus oldUiSate = getUiStatusForConfig(updateData.getConfigData());

		updateData.setUiStatus(getUiStatusSync().updateUIStatusFromRequest(updateData.getConfigData(), oldUiSate, getUiRecorder()));

		final Map<String, FieldError> userInputToRestore = getUiStateHandler()
				.handleValidationErrorsBeforeUpdate(updateData.getConfigData(), updateData.getBindingResult());
		updateData.getUiStatus().setUserInputToRestore(userInputToRestore);
		handleCPQActionBeforeUpdate(updateData);
		removeNullCstics(updateData.getConfigData().getGroups());
	}

	protected UpdateDataHolder initUpdateData(final ConfigurationData configData, final BindingResult bindingResult,
			final HttpServletRequest request)
	{
		logRequestMetaData(configData, request);

		final UpdateDataHolder updateData = new UpdateDataHolder();
		updateData.setConfigData(configData);
		updateData.setBindingResult(bindingResult);
		return updateData;
	}

	protected Map<String, FieldError> removeOutdatedValidationErrors(final UpdateDataHolder updateData)
	{
		final List<String> csticsDisplayed = new ArrayList<>();
		if (updateData.getConfigData().isSingleLevel())
		{
			csticsDisplayed.addAll(getCsticKeysForExpandedSingleLevelGroups(updateData.getConfigData().getGroups()));
		}
		else
		{
			csticsDisplayed.addAll(updateData.getConfigData().getGroupToDisplay().getGroup().getCstics().stream()
					.map(cstic -> cstic.getKey()).collect(Collectors.toList()));
		}
		final Map<String, FieldError> inputToRemember = updateData.getUiStatus().getUserInputToRestore();
		if (inputToRemember == null || inputToRemember.isEmpty())
		{
			return inputToRemember;
		}

		return inputToRemember.entrySet().stream().filter(entry -> !isOutdatedValidationError(entry, csticsDisplayed))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
	}

	protected List<String> getCsticKeysForExpandedSingleLevelGroups(final List<UiGroupData> groups)
	{
		final List<String> csticKeys = new ArrayList<>();
		if (GroupType.CONFLICT_HEADER.equals(groups.get(0).getGroupType()))
		{
			csticKeys.addAll(getCsticKeysForExpandedSingleLevelGroups(groups.get(0).getSubGroups()));
		}

		// get for all visible cstic the key as a list of strings
		csticKeys.addAll(groups.stream().filter(group -> !group.isCollapsed()).map(group -> group.getCstics())
				.flatMap(list -> list.stream()).map(cstic -> cstic.getKey()).collect(Collectors.toList()));

		return csticKeys;
	}

	protected boolean isOutdatedValidationError(final Entry<String, FieldError> entry, final List<String> csticsDisplayed)
	{
		final FieldError error = entry.getValue();
		final String csticKey = entry.getKey();
		return error instanceof MandatoryFieldError || error instanceof ConflictError || csticsDisplayed.contains(csticKey);
	}

	protected UiGroupData findFirstGroupForCsticId(final List<UiGroupData> uiGroups, final String csticId)
	{
		if (uiGroups == null)
		{
			return null;
		}

		for (final UiGroupData uiGroup : uiGroups)
		{
			if (GroupType.CONFLICT_HEADER.equals(uiGroup.getGroupType()))
			{
				continue;
			}

			if (isCsticPartOfGroup(uiGroup, csticId))
			{
				return uiGroup;
			}

			final UiGroupData foundGroup = findFirstGroupForCsticId(uiGroup.getSubGroups(), csticId);
			if (foundGroup != null)
			{
				return foundGroup;
			}
		}

		return null;
	}

	protected boolean isCsticPartOfGroup(final UiGroupData uiGroup, final String csticId)
	{
		if (uiGroup.getCstics() == null)
		{
			return false;
		}

		return uiGroup.getCstics().stream().anyMatch(c -> csticId.equals(c.getKey()));
	}
}
