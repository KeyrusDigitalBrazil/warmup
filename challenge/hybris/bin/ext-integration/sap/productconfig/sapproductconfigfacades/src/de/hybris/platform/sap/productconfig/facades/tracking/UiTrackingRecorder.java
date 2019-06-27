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
package de.hybris.platform.sap.productconfig.facades.tracking;

import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.services.tracking.RecorderParameters;


/**
 * Records UI events into the hybris session
 */
public interface UiTrackingRecorder
{

	/**
	 * Records the UI-event of clicking previous/next button and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param groupIdToDisplay
	 *           The group Id which is displayed next by clicking on the button
	 */
	void recordPrevNextButtonClicked(ConfigurationData configData, String groupIdToDisplay);

	/**
	 * Records the UI-event of group interaction and notifies writers. This includes expanding/collapsing groups and
	 * navigation to groups via the menu.
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param groupId
	 *           The group id that should be used for the interaction.
	 * @param groupIsCollapsed
	 *           Flag if group is collapsed.
	 */
	void recordGroupInteraction(ConfigurationData configData, String groupId, boolean groupIsCollapsed);

	/**
	 * Records the UI-event of menu node expanding/collapsing and notifies writers.
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param groupIdToToggleInMenu
	 *           The group id (menu node) that should be expanded/collapsed in the menu.
	 * @param menuNodeIsCollapsed
	 *           Flag if menu node is collapsed.
	 */
	void recordMenuToggle(ConfigurationData configData, String groupIdToToggleInMenu, boolean menuNodeIsCollapsed);

	/**
	 * Records the UI-event of displaying or hiding the long text and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 */
	void recordLongTextToggle(ConfigurationData configData);

	/**
	 * Records the UI-event of displaying or hiding the extended message text and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param recorderParameters
	 *           params for recording
	 */
	void recordExtendedMessageToggle(ConfigurationData configData, RecorderParameters recorderParameters);

	/**
	 * Records the UI-event of navigating from characteristic in conflict group to its genuine group and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param group
	 *           The target group
	 */
	void recordNavigationToCsticInGroup(ConfigurationData configData, UiGroupData group);

	/**
	 * Records the UI-event of navigating from characteristic in to its occurrence in conflict group and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param group
	 */
	void recordNavigationToCsticInConflict(ConfigurationData configData, UiGroupData group);

	/**
	 * Records the UI-event of displaying/hiding the image gallery and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param hideImageGallery
	 *           Flag: is image gallery hidden
	 */
	void recordImageGalleryToggle(ConfigurationData configData, boolean hideImageGallery);

	/**
	 * Records the UI-event of changing a value on the UI and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param csticId
	 *           The Id of characteristic that has been changed.
	 */
	void recordValueChanges(ConfigurationData configData, String csticId);

	/**
	 * Records the UI-event of accessing the CPQ UI and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param productId
	 *           The product that is configured.
	 */
	void recordUiAccess(ConfigurationData configData, String productId);

	/**
	 * Records the UI-event of accessing the CPQ UI from cart and notifies writers
	 *
	 * @param configData
	 *           The configuration data DTO
	 * @param productId
	 *           The product that is configured.
	 */
	void recordUiAccessFromCart(ConfigurationData configData, String productId);

	/**
	 * Records the UI-event of accessing the configuration overview and notifies writers
	 *
	 * @param configOverviewData
	 *           The configuration-overview data DTO
	 * @param productId
	 *           The product that is displayed.
	 */
	void recordUiAccessOverview(ConfigurationOverviewData configOverviewData, String productId);

	/**
	 * Records the UI-event of accessing the variant overview and notifies writers
	 *
	 * @param variantId
	 *           The variant that is displayed.
	 */
	void recordUiAccessVariantOverview(String variantId);

}
