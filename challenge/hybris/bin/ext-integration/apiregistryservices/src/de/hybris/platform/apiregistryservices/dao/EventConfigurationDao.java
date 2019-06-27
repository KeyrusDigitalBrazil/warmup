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
package de.hybris.platform.apiregistryservices.dao;

import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
import de.hybris.platform.apiregistryservices.model.events.EventConfigurationModel;

import java.util.List;


/**
 * DAO for the {@link EventConfigurationModel}
 */
public interface EventConfigurationDao
{
	/**
	 * Fetch list of all active events. The Active means export flag of such events is true.
	 *
	 * @param eventClass
	 *           EventConfigurationModel.eventClass
	 * @return List<EventConfigurationModel>
	 */
	List<EventConfigurationModel> findActiveEventConfigsByClass(String eventClass);

	/**
	 * Fetch list of active events for DestinationTarget. The Active means export flag of such events is true.
	 *
	 * @param destinationTargetId
	 *           the id of the DestinationTarget
	 * @return List<EventConfigurationModel>
	 */
	List<EventConfigurationModel> findActiveEventConfigsByDestinationTargetId(String destinationTargetId);

	/**
	 * Fetch list of active events for eventChannel. The Active means export flag of such events is true.
	 *
	 * @param channel
	 *           the id of the DestinationTarget
	 * @return List<EventConfigurationModel>
	 */
	List<EventConfigurationModel> findActiveEventConfigsByChannel(DestinationChannel channel);
}
