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
package de.hybris.platform.customerticketingfacades.strategies;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;

import java.util.List;
import java.util.Map;


/**
 * This Interface is to make sure to list all object which can associate with tickets.
 *
 */
public interface TicketAssociationStrategies
{
	Map<String, List<TicketAssociatedData>> getObjects(UserModel currentUser);
}
