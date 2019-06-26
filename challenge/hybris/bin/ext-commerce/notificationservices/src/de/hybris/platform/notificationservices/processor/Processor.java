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
package de.hybris.platform.notificationservices.processor;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Map;


/**
 * Interface for channel processor
 */
public interface Processor
{


	/**
	 * Render and send the notification
	 *
	 * @param customer
	 *           the customer to send the result
	 * @param dataMap
	 *           the map containing variables
	 */
	void process(final CustomerModel customer, final Map<String, ? extends ItemModel> dataMap);
}
