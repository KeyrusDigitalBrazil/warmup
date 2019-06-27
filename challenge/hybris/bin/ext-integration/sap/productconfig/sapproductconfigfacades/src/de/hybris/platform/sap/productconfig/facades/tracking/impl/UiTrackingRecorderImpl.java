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
package de.hybris.platform.sap.productconfig.facades.tracking.impl;

import de.hybris.platform.sap.productconfig.facades.CPQActionType;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.tracking.UiTrackingRecorder;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.tracking.EventType;
import de.hybris.platform.sap.productconfig.services.tracking.RecorderParameters;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItem;
import de.hybris.platform.sap.productconfig.services.tracking.impl.AbstractTrackingRecorderImpl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * UI-Layer implementation of the {@link AbstractTrackingRecorderImpl}. Tracks UI-events.
 */
public class UiTrackingRecorderImpl extends AbstractTrackingRecorderImpl implements UiTrackingRecorder
{

	private static final String NO_CONFIG_ID = "NO_CONFIG_ID";
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;

	@Override
	public void recordPrevNextButtonClicked(final ConfigurationData configData, final String groupIdToDisplay)
	{

		if (isTrackingEnabled())
		{
			final EventType event;
			if (CPQActionType.NEXT_BTN.equals(configData.getCpqAction()))
			{
				event = EventType.UI_NEXT_BUTTON;
			}
			else
			{
				event = EventType.UI_PREVIOUS_BUTTON;
			}
			this.notifyWriter(createTrackingItem(configData.getConfigId(), event, RecorderParameters.GROUP, groupIdToDisplay));
		}
	}


	@Override
	public void recordGroupInteraction(final ConfigurationData configData, final String groupId, final boolean groupIsCollapsed)
	{
		if (isTrackingEnabled() && StringUtils.isNotEmpty(groupId))
		{
			TrackingItem item = null;
			if (CPQActionType.TOGGLE_GROUP.equals(configData.getCpqAction()))
			{
				item = recordGroupToggle(configData, groupId, groupIsCollapsed);
			}
			else if (CPQActionType.MENU_NAVIGATION.equals(configData.getCpqAction()))
			{
				item = recordGroupNavigationViaMenu(configData, groupId);
			}
			if (item != null)
			{
				this.notifyWriter(item);
			}
		}
	}

	protected TrackingItem recordGroupToggle(final ConfigurationData configData, final String groupId,
			final boolean groupIsCollapsed)
	{
		if (StringUtils.isEmpty(groupId))
		{
			return null;
		}
		final TrackingItem item = createTrackingItem(configData.getConfigId(), EventType.UI_GROUP_TOGGLE, RecorderParameters.GROUP,
				groupId);
		item.getParameters().put(RecorderParameters.COLLAPSED.toString(), Boolean.toString(groupIsCollapsed));
		return item;
	}

	protected TrackingItem recordGroupNavigationViaMenu(final ConfigurationData configData, final String groupId)
	{
		if (StringUtils.isEmpty(groupId))
		{
			return null;
		}
		return createTrackingItem(configData.getConfigId(), EventType.UI_MENU_NAVIGATION, RecorderParameters.GROUP, groupId);

	}

	@Override
	public void recordMenuToggle(final ConfigurationData configData, final String groupIdToToggleInMenu,
			final boolean menuNodeIsCollapsed)
	{

		if (isTrackingEnabled() && StringUtils.isNotEmpty(groupIdToToggleInMenu))
		{
			final TrackingItem item = createTrackingItem(configData.getConfigId(), EventType.UI_MENU_NODE_TOGGLE,
					RecorderParameters.GROUP, groupIdToToggleInMenu);
			item.getParameters().put(RecorderParameters.COLLAPSED.toString(), Boolean.toString(menuNodeIsCollapsed));
			this.notifyWriter(item);
		}
	}

	@Override
	public void recordLongTextToggle(final ConfigurationData configData)
	{
		if (isTrackingEnabled())
		{
			TrackingItem item = null;
			if (CPQActionType.HIDE_FULL_LONG_TEXT.equals(configData.getCpqAction()))
			{
				item = recordHideLongText(configData);
			}
			else if (CPQActionType.SHOW_FULL_LONG_TEXT.equals(configData.getCpqAction()))
			{
				item = recordShowLongText(configData);
			}
			if (item != null)
			{
				this.notifyWriter(item);
			}
		}
	}

	@Override
	public void recordExtendedMessageToggle(final ConfigurationData configData, final RecorderParameters recordParameter)
	{
		if (isTrackingEnabled())
		{
			TrackingItem item = null;
			if (CPQActionType.TOGGLE_EXTENDED_MESSAGE.equals(configData.getCpqAction()))
			{
				item = recordItemExtendedMessageToggle(configData, recordParameter);
			}
			if (item != null)
			{
				this.notifyWriter(item);
			}
		}
	}

	protected TrackingItem recordShowLongText(final ConfigurationData configData)
	{
		return createTrackingItem(configData.getConfigId(), EventType.UI_SHOW_LONG_TEXT, RecorderParameters.CSTIC,
				configData.getFocusId());
	}

	protected TrackingItem recordHideLongText(final ConfigurationData configData)
	{
		return createTrackingItem(configData.getConfigId(), EventType.UI_HIDE_LONG_TEXT, RecorderParameters.CSTIC,
				configData.getFocusId());
	}

	protected TrackingItem recordItemExtendedMessageToggle(final ConfigurationData configData, final RecorderParameters recordParameter)
	{
		return createTrackingItem(configData.getConfigId(), EventType.UI_EXTENDED_MESSAGE_TOGGLE, recordParameter, configData.getFocusId());
	}

	@Override
	public void recordNavigationToCsticInGroup(final ConfigurationData configData, final UiGroupData group)
	{
		if (isTrackingEnabled() && group != null)
		{
			final TrackingItem item = recordNavigationToCstic(configData, group, EventType.UI_NAV_TO_CSTIC_IN_GROUP);
			if (item != null)
			{
				this.notifyWriter(item);
			}
		}
	}

	@Override
	public void recordNavigationToCsticInConflict(final ConfigurationData configData, final UiGroupData group)
	{
		if (isTrackingEnabled() && group != null)
		{
			final TrackingItem item = recordNavigationToCstic(configData, group, EventType.UI_NAV_TO_CSTIC_IN_CONFLICT);
			if (item != null)
			{
				this.notifyWriter(item);
			}
		}
	}

	protected TrackingItem recordNavigationToCstic(final ConfigurationData configData, final UiGroupData group,
			final EventType event)
	{
		final TrackingItem item = createTrackingItem(configData.getConfigId(), event, RecorderParameters.CSTIC,
				configData.getFocusId());
		item.getParameters().put(RecorderParameters.GROUP.toString(), group.getId());
		return item;
	}

	@Override
	public void recordImageGalleryToggle(final ConfigurationData configData, final boolean hideImageGallery)
	{
		if (isTrackingEnabled())
		{
			this.notifyWriter(createTrackingItem(configData.getConfigId(), EventType.UI_IMAGE_GALLERY_TOGGLE,
					RecorderParameters.HIDE_IMAGE, Boolean.toString(hideImageGallery)));
		}
	}

	@Override
	public void recordValueChanges(final ConfigurationData configData, final String csticId)
	{
		if (isTrackingEnabled())
		{
			TrackingItem item = null;
			if (CPQActionType.VALUE_CHANGED.equals(configData.getCpqAction()))
			{
				item = recordValueChanged(configData, csticId);
			}
			else if (CPQActionType.RETRACT_VALUE.equals(configData.getCpqAction()))
			{
				item = recordValueRetracted(configData, csticId);
			}
			if (item != null)
			{
				this.notifyWriter(item);
			}
		}
	}

	protected TrackingItem recordValueRetracted(final ConfigurationData configData, final String csticId)
	{
		final TrackingItem item = createTrackingItem(configData.getConfigId(), EventType.UI_VALUE_RETRACTED,
				RecorderParameters.CSTIC, csticId);
		item.getParameters().put(RecorderParameters.CSTIC_PATH.toString(), configData.getFocusId());
		return item;

	}

	protected TrackingItem recordValueChanged(final ConfigurationData configData, final String csticId)
	{
		final TrackingItem item = createTrackingItem(configData.getConfigId(), EventType.UI_VALUE_CHANGED, RecorderParameters.CSTIC,
				csticId);
		item.getParameters().put(RecorderParameters.CSTIC_PATH.toString(), configData.getFocusId());
		return item;

	}

	@Override
	public void recordUiAccess(final ConfigurationData configData, final String productId)
	{
		if (isTrackingEnabled())
		{
			this.notifyWriter(
					createTrackingItem(configData.getConfigId(), EventType.UI_ACCESS, RecorderParameters.PRODUCT, productId));
		}
	}

	@Override
	public void recordUiAccessOverview(final ConfigurationOverviewData configOverviewData, final String productId)
	{
		if (isTrackingEnabled())
		{
			this.notifyWriter(createTrackingItem(configOverviewData.getId(), EventType.UI_ACCESS_OVERVIEW,
					RecorderParameters.PRODUCT, productId));
		}
	}

	@Override
	public void recordUiAccessVariantOverview(final String variantId)
	{
		if (isTrackingEnabled())
		{
			this.notifyWriter(
					createTrackingItem(NO_CONFIG_ID, EventType.UI_ACCESS_VARIANT_OVERVIEW, RecorderParameters.VARIANT, variantId));
		}
	}

	@Override
	public void recordUiAccessFromCart(final ConfigurationData configData, final String productId)
	{
		if (isTrackingEnabled())
		{
			final String configId = configData.getConfigId();
			final TrackingItem item = createTrackingItem(configId, EventType.UI_ACCESS_FROM_CART, RecorderParameters.PRODUCT,
					productId);
			String cartItemKey = getAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId);
			if (null == cartItemKey)
			{
				cartItemKey = getAbstractOrderEntryLinkStrategy().getCartEntryForDraftConfigId(configId);
			}

			item.getParameters().put(RecorderParameters.CART_ITEM_PK.toString(), DigestUtils.sha256Hex(cartItemKey));
			this.notifyWriter(item);
		}
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setAbstractOrderEntryLinkStrategy(final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}
}
