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
package de.hybris.platform.messagecentercsfacades;

import de.hybris.platform.messagecentercsfacades.data.ConversationData;
import de.hybris.platform.messagecentercsfacades.data.ConversationDataList;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageData;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageListData;

import java.util.List;


/**
 * The facade interface of MessageCenter
 */
public interface ConversationFacade
{
	/**
	 * Gets conversations for customer
	 *
	 * @return List of the Conversation Data
	 */
	List<ConversationData> getConversationsForCustomer();

	/**
	 * Gets Unassigned conversations of agent
	 *
	 * @return the list of Conversation Data
	 */
	List<ConversationData> getUnassignedConversations();

	/**
	 *
	 * pick Conversation by Customer Support Agent
	 *
	 * @param conversationId
	 *           id of the conversation
	 * @return the picked Conversation Data
	 */
	ConversationData pickConversation(String conversationId);


	/**
	 * Gets open list of the Customer Support Agent
	 *
	 * @return Assigned and Open List of the Customer Support Agent
	 */
	List<ConversationData> getOpenConversations();

	/**
	 * Gets Conversation by Id
	 *
	 * @param conversationId
	 *           id of the conversation
	 * @return the Conversation Data
	 */

	ConversationData getConversationById(String conversationId);

	/**
	 * Closes a conversation for given uid
	 *
	 * @param uid
	 *           the conversation's uid
	 * @return the closed conversation
	 */
	ConversationData closeConversation(String uid);

	/**
	 * Check the specific conversation if is accessible
	 *
	 * @param conversation
	 *           the specific conversation data
	 * @return true is accessible or otherwise
	 */
	boolean isConversationAccessible(ConversationData conversation);

	/**
	 * get conversation data list
	 *
	 * @param conversations
	 *           the conversation data list
	 * @return conversation datas
	 */
	ConversationDataList getConversationDataList(List<ConversationData> conversations);

	/**
	 * Get all history messages for conversation
	 *
	 * @param conversationId
	 *           the specific conversationId
	 * @return list of history messages
	 */
	List<ConversationMessageData> getMessagesForConversation(String conversationId);


	/**
	 * Get all history messages for conversation into
	 *
	 * @param messages
	 *           the messages list
	 * @return conversation messages list data
	 */
	ConversationMessageListData getConversationMessageList(List<ConversationMessageData> messages);

	/**
	 * Send message
	 *
	 * @param conversationMessage
	 *           message in content
	 * @return ConversationModel message sending used conversation
	 */
	ConversationData sendMessage(final ConversationMessageListData conversationMessage);

	/**
	 *
	 * @return true if current user is customer
	 */
	boolean isCustomer();


}
