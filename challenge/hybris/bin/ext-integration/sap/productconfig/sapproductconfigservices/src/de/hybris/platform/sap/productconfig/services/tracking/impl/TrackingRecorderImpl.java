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
package de.hybris.platform.sap.productconfig.services.tracking.impl;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueDelta;
import de.hybris.platform.sap.productconfig.services.tracking.EventType;
import de.hybris.platform.sap.productconfig.services.tracking.RecorderParameters;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItem;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingItemKey;
import de.hybris.platform.sap.productconfig.services.tracking.TrackingRecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * Service-Layer implementation of the {@link AbstractTrackingRecorderImpl}. Tracks Service-Layer-events.
 */
public class TrackingRecorderImpl extends AbstractTrackingRecorderImpl implements TrackingRecorder
{
	@Override
	public void recordCreateConfiguration(final ConfigModel configModel, final KBKey kbKey)
	{
		if (isTrackingEnabled())
		{
			this.notifyWriter(createTrackingItem(configModel.getId(), EventType.CREATE_CONFIGURATION, RecorderParameters.KB_KEY,
					this.getKBKey(kbKey)));
		}
	}

	@Override
	public void recordUpdateConfiguration(final ConfigModel configModel)
	{
		if (isTrackingEnabled())
		{
			final List<CsticValueDelta> updateData = configModel.getCsticValueDeltas();
			for (final CsticValueDelta delta : updateData)
			{
				recordChangeValue(configModel, delta);
			}
			configModel.setCsticValueDeltas(new ArrayList<>());
		}
	}

	@Override
	public void recordConfigurationStatus(final ConfigModel configModel)
	{
		if (isTrackingEnabled())
		{
			final TrackingItem item = new TrackingItem();
			final TrackingItemKey itemKey = fillItemKey(configModel.getId(), EventType.CONFIGURATION_STATUS);
			item.setTrackingItemKey(itemKey);
			item.setParameters(new HashMap<>());

			fillConfigStatus(item, configModel);

			this.notifyWriter(item);
		}
	}


	@Override
	public void recordCreateConfigurationForVariant(final ConfigModel configModel, final String baseProduct,
			final String variantProduct)
	{
		if (isTrackingEnabled())
		{
			final TrackingItem item = createTrackingItem(configModel.getId(), EventType.CREATE_CONFIGURATION_FOR_VARIANT,
					RecorderParameters.VARIANT, variantProduct);
			item.getParameters().put(RecorderParameters.PRODUCT.toString(), baseProduct);

			this.notifyWriter(item);
		}
	}


	@Override
	public void recordCreateConfigurationFromExternalSource(final ConfigModel configModel)
	{
		if (isTrackingEnabled())
		{
			final TrackingItem item = new TrackingItem();
			final TrackingItemKey itemKey = fillItemKey(configModel.getId(), EventType.CREATE_CONFIGURATION_FROM_EXTERNAL);
			item.setTrackingItemKey(itemKey);
			item.setParameters(new HashMap<>());

			this.notifyWriter(item);
		}
	}


	@Override
	public void recordAddToCart(final AbstractOrderEntryModel entry, final CommerceCartParameter parameters)
	{
		if (isTrackingEnabled())
		{
			final TrackingItem item = recordCartEvent(entry, parameters, EventType.ADD_TO_CART);


			this.notifyWriter(item);
		}
	}

	@Override
	public void recordUpdateCartEntry(final AbstractOrderEntryModel entry, final CommerceCartParameter parameters)
	{
		if (isTrackingEnabled() && !parameters.isCreateNewEntry())
		{
			final TrackingItem item = recordCartEvent(entry, parameters, EventType.UPDATE_CART_ENTRY);


			this.notifyWriter(item);
		}
	}

	@Override
	public void recordDeleteCartEntry(final AbstractOrderEntryModel entry, final CommerceCartParameter parameters)
	{
		if (isTrackingEnabled())
		{
			final TrackingItem item = recordCartEvent(entry, parameters, EventType.DELETE_CART_ENTRY);


			this.notifyWriter(item);
		}
	}

	protected TrackingItem recordCartEvent(final AbstractOrderEntryModel entry, final CommerceCartParameter parameters,
			final EventType event)
	{


		String configId;
		if (EventType.DELETE_CART_ENTRY.equals(event))
		{
			configId = parameters.getConfigToBeDeleted();
		}
		else
		{
			configId = parameters.getConfigId();
		}

		final TrackingItem item = createTrackingItem(configId, event, RecorderParameters.PRODUCT, entry.getProduct().getCode());
		item.getParameters().put(RecorderParameters.CART_ITEM_PK.toString(), DigestUtils.sha256Hex(entry.getPk().toString()));
		item.getParameters().put(RecorderParameters.QUANTITY.toString(), entry.getQuantity().toString());
		item.getParameters().put(RecorderParameters.CART.toString(), DigestUtils.sha256Hex(parameters.getCart().getGuid()));
		return item;
	}

	protected String getKBKey(final KBKey kb)
	{
		final StringBuilder builder = new StringBuilder(60);
		builder.append("productCode=");
		builder.append(kb.getProductCode());
		builder.append(", kbName=");
		builder.append(kb.getKbName());
		builder.append(", kbLogsys=");
		builder.append(kb.getKbLogsys());
		builder.append(", kbVersion=");
		builder.append(kb.getKbVersion());
		builder.append(", date=");
		builder.append(kb.getDate());
		return builder.toString();
	}

	protected void recordChangeValue(final ConfigModel configModel, final CsticValueDelta delta)
	{
		for (final String value : delta.getValueNames())
		{
			final TrackingItem item = createTrackingItem(configModel.getId(), EventType.CHANGE_CONFIGURATION,
					RecorderParameters.CHANGE_TYPE, delta.getChangeType().toString());

			item.getParameters().put(RecorderParameters.INSTANCE.toString(), delta.getInstanceName());
			item.getParameters().put(RecorderParameters.INSTANCE_ID.toString(), delta.getInstanceId());
			item.getParameters().put(RecorderParameters.CSTIC.toString(), delta.getCsticName());
			item.getParameters().put(RecorderParameters.CSTIC_VALUE.toString(), value);

			this.notifyWriter(item);
		}
	}


	protected void fillConfigStatus(final TrackingItem item, final ConfigModel configModel)
	{
		item.getParameters().put(RecorderParameters.CONSISTENT.toString(), Boolean.toString(configModel.isConsistent()));
		item.getParameters().put(RecorderParameters.COMPLETE.toString(), Boolean.toString(configModel.isComplete()));
	}


}
