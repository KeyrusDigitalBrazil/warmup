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
package de.hybris.platform.messagecentercsservices.daos;

import de.hybris.platform.messagecentercsservices.model.ConversationModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.List;


/**
 * DAO to provide methods to query for conversation.
 */
public interface ConversationDao extends GenericDao<ConversationModel>
{

	/**
	 * Find Unassigned conversations for agent
	 *
	 * @return the list of ConversationModel
	 */
	List<ConversationModel> findUnassignedConversations();

}
