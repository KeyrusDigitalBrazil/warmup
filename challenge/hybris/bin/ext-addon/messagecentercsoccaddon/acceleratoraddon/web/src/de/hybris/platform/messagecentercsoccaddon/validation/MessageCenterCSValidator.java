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
package de.hybris.platform.messagecentercsoccaddon.validation;

import de.hybris.platform.messagecentercsfacades.ConversationFacade;
import de.hybris.platform.messagecentercsfacades.data.ConversationData;
import de.hybris.platform.messagecentercsoccaddon.constants.ErrorConstants;
import de.hybris.platform.messagecentercsoccaddon.exceptions.MessageCenterCSException;
import de.hybris.platform.messagecentercsservices.enums.ConversationStatus;

import java.util.Objects;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;


@Component("messageCenterCSValidator")
public class MessageCenterCSValidator
{

	@Resource(name = "conversationFacade")
	private ConversationFacade conversationFacade;
	private static final String STATUS_OPEN = "open";
	private static final String STATUS_UNASSIGNED = "unassigned";

	public ConversationData checkIfConversationExists(final String conversationId)
	{
		final ConversationData conversation = conversationFacade.getConversationById(conversationId);
		if (Objects.isNull(conversation))
		{
			throw new MessageCenterCSException(ErrorConstants.NOT_EXIST_ERROR, ErrorConstants.NOT_EXIST_MESSAGE);
		}
		return conversation;
	}

	public void checkIfConversationClosed(final ConversationData conversation)
	{
		if (ConversationStatus.CLOSED.getCode().equals(conversation.getStatus()))
		{
			throw new MessageCenterCSException(ErrorConstants.ALREADY_CLOSED_ERROR, ErrorConstants.ALREADY_CLOSED_MESSAGE);
		}
	}

	public void checkIfConversationAssigned(final ConversationData conversation)
	{
		if (Objects.nonNull(conversation.getAgent()))
		{
			throw new MessageCenterCSException(ErrorConstants.ALREADY_ASSIGNED_ERROR, ErrorConstants.ALREADY_ASSIGNED_MESSAGE);
		}
	}

	public void checkIfConversationAccessible(final ConversationData conversation)
	{
		if (!conversationFacade.isConversationAccessible(conversation))
		{
			throw new MessageCenterCSException(ErrorConstants.NO_ACCESS_ERROR, ErrorConstants.NO_ACCESS_MESSAGE);
		}
	}


	public void checkIfConversationCreatable()
	{
		if (!conversationFacade.isCustomer())
		{
			throw new MessageCenterCSException(ErrorConstants.AGENT_CREATE_CONVERSATION_ERROR,
					ErrorConstants.AGENT_CREATE_CONVERSATION_MESSAGE);
		}
	}

	public void checkIfStatusCorrect(final String status)
	{
		if (!STATUS_OPEN.equals(status) && !STATUS_UNASSIGNED.equals(status))
		{
			throw new MessageCenterCSException(ErrorConstants.STATUS_INVALID_ERROR, ErrorConstants.STATUS_INVALID_MESSAGE);
		}
	}

}
