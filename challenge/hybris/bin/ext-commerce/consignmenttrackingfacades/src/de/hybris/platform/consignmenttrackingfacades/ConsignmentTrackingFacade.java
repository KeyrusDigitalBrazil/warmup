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
package de.hybris.platform.consignmenttrackingfacades;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;

import java.util.List;
import java.util.Optional;


/**
 * The facade interface of ConsignmentTracking
 */
public interface ConsignmentTrackingFacade
{

	/**
	 * find consignment by its code, if events is null, send a request to carrier to retrieve
	 *
	 * @param orderCode
	 *           the code of this consignment's order
	 * @param consignmentCode
	 *           the consignment's code
	 * @return Option of the consignment if it exists and empty optional otherwise
	 */
	Optional<ConsignmentData> getConsignmentByCode(String orderCode, String consignmentCode);


	/**
	 * get tracking provider service url
	 *
	 * @param orderCode
	 *           the code of this consignment's order
	 * @param consignmentCode
	 *           the code of specific consignment
	 * @return the url of tracking provider service for retrieving tracking events
	 */
	String getTrackingUrlForConsignmentCode(String orderCode, String consignmentCode);

	/**
	 * get all consignments under the order
	 *
	 * @param orderCode
	 *           the order code
	 * @return the all consignment for the order
	 */
	List<ConsignmentData> getConsignmentsByOrder(String orderCode);
}
