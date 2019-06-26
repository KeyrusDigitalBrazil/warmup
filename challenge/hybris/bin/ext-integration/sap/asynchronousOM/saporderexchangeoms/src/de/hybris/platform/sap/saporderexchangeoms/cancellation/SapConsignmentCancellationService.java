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
package de.hybris.platform.sap.saporderexchangeoms.cancellation;

import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.warehousing.cancellation.ConsignmentCancellationService;

/**
 * Interface to provide Integration processing for consignment cancellation
 */
public interface SapConsignmentCancellationService extends ConsignmentCancellationService
{

	/**
	 * processSapConsignmentCancellation
	 *
	 * @param orderCancelResponse
	 * 			Order cancel response
	 */
	void processSapConsignmentCancellation(OrderCancelResponse orderCancelResponse);


}
