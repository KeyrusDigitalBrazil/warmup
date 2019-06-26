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
package de.hybris.platform.sap.productconfig.services.tracking;

/**
 * Will persist or process {@link TrackingItem}s recorded by the {@link TrackingRecorder}.
 */
public interface TrackingWriter
{

	/**
	 * Callback to notify the write that a new history item was created.
	 *
	 * @param item
	 */
	void trackingItemCreated(TrackingItem item);

}
