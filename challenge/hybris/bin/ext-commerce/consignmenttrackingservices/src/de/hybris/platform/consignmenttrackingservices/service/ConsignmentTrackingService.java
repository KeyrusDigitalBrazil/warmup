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
package de.hybris.platform.consignmenttrackingservices.service;

import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.consignmenttrackingservices.adaptors.CarrierAdaptor;
import de.hybris.platform.consignmenttrackingservices.delivery.data.ConsignmentEventData;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Service that provide consignment tracking related methods.
 */
public interface ConsignmentTrackingService
{

	/**
	 * tracking URL is given for display in frontend, if user wants to track package by jumping to carrier's website
	 *
	 * @param consignment
	 *           the specific consignment
	 * @return tracking URL
	 */
	URL getTrackingUrlForConsignment(ConsignmentModel consignment);

	/**
	 * check if the tracking ID is valid in a carrier provider
	 *
	 * @param consignment
	 *           the specific consignment
	 * @return true if valid, otherwise false
	 */
	boolean isTrackingIdValid(ConsignmentModel consignment);

	/**
	 * used by controller to handle incremental routes from carrier
	 *
	 * @param consignment
	 *           the specific consignment
	 * @return latest consignment events
	 */
	List<ConsignmentEventData> getConsignmentEvents(ConsignmentModel consignment);

	/**
	 * Query a ConsignmentModel for code.
	 *
	 * @param orderCode
	 *           the code of this consignment's order
	 * @param consignmentCode
	 *           code of consignment
	 * @return a instance of ConsignmentModel
	 */
	Optional<ConsignmentModel> getConsignmentForCode(String orderCode, String consignmentCode);

	/**
	 * update consignment status and set arrival date if complete
	 *
	 * @param orderCode
	 *           the code of this consignment's order
	 * @param consignmentCode
	 *           the specific consignmentCode
	 * @param status
	 *           the new status from carrier
	 */
	void updateConsignmentStatusForCode(String orderCode, String consignmentCode, ConsignmentStatus status);

	/**
	 * provide the lead time of estimation from shipped to arrival
	 *
	 * @param consignment
	 *           the specific consignment
	 * @return days of the lead time
	 */
	int getDeliveryLeadTime(ConsignmentModel consignment);

	/**
	 * Get all carrier adaptors.
	 *
	 * @return map of adaptors.
	 */
	Map<String, CarrierAdaptor> getAllCarrierAdaptors();

	/**
	 * Get all consignments under the order
	 *
	 * @param orderCode
	 *           the order code
	 *
	 * @return all consignment for the order
	 */
	List<ConsignmentModel> getConsignmentsForOrder(String orderCode);
}
