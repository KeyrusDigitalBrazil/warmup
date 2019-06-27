/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.consignmenttrackingservices.adaptors;

import de.hybris.platform.consignmenttrackingservices.delivery.data.ConsignmentEventData;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.net.URL;
import java.util.List;


/**
 * Adaptor interface which should be implemented by each carrier
 */
public interface CarrierAdaptor
{

	/**
	 * check if the tracking ID is valid
	 *
	 * @param trackingID
	 * @return true if the id is valid and false otherwise
	 */
	default boolean isTrackingIdValid(final String trackingID)
	{
		return true;
	}

	/**
	 * request tracks from specified carrier by tracking ID
	 *
	 * @param trackingId
	 *           the specific tracking ID
	 * @return List of ConsignmentEventData
	 */
	List<ConsignmentEventData> getConsignmentEvents(String trackingId);

	/**
	 * implemented by each carrier to provide tracking URL
	 *
	 * @return tracking URL
	 */
	URL getTrackingUrl(String trackingID);

	/**
	 * implemented by each carrier to provide delivery lead time for every consignment
	 *
	 * @param consignment
	 *           the specific consignment
	 * @return
	 */
	int getDeliveryLeadTime(ConsignmentModel consignment);
}
