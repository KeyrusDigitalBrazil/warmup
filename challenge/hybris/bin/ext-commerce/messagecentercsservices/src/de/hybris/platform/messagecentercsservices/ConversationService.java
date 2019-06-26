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
package de.hybris.platform.messagecentercsservices;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.messagecentercsservices.model.ConversationModel;

import java.util.List;
import java.util.Optional;


/**
 * Service to provide related methods for
 */
public interface ConversationService
{

	/**
	 * Create a ConversationModel
	 *
	 * @param messages
	 *           the Messages to be saved
	 * 
	 * @return the created conversation
	 */
	Optional<ConversationModel> createConversation(String messages);

	/**
	 * Gets conversation for given uid
	 *
	 * @param uid
	 *           the conversation uid
	 *
	 * @return the created conversation
	 */
	Optional<ConversationModel> getConversationForUid(String uid);

	/**
	 * Gets all conversations of specific customer
	 *
	 * @param customer
	 *           the specific customer
	 * @return the list of ConversationModel
	 */
	List<ConversationModel> getAllConversationsForCustomer(CustomerModel customer);

	/**
	 * Gets open conversations for agent
	 *
	 * @param agent
	 *           the specific agent
	 * @return the list of ConversationModel
	 */
	List<ConversationModel> getOpenConversationsForAgent(EmployeeModel agent);

	/**
	 * Gets Unassigned conversations of agent
	 *
	 * @return the list of ConversationModel
	 */
	List<ConversationModel> getUnassignedConversations();

	/**
	 * Agent chooses a conversation
	 *
	 * @param uid
	 *           the specific conversation uid
	 * @param agent
	 *           the agent
	 * @return the optional of picked conversation
	 */
	Optional<ConversationModel> pickConversation(String uid, EmployeeModel agent);

	/**
	 * Customer closes a given conversation
	 *
	 * @param uid
	 *           the specific conversation uid
	 * @param user
	 *           the user
	 * @return the optional of closed conversation
	 */
	Optional<ConversationModel> closeConversation(String uid, UserModel user);

	/**
	 * Create Or Update a ConversationModel
	 *
	 * @param uid
	 *           the specific conversation uid
	 * 
	 * @param messages
	 *           the messages to be updated
	 * 
	 * @return the created conversation
	 */
	Optional<ConversationModel> updateConversation(String uid, String messages);

}
