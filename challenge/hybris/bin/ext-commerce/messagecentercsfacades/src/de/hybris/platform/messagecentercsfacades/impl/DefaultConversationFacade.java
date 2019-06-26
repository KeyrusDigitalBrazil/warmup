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
package de.hybris.platform.messagecentercsfacades.impl;

import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.messagecentercsfacades.ConversationFacade;
import de.hybris.platform.messagecentercsfacades.data.ConversationData;
import de.hybris.platform.messagecentercsfacades.data.ConversationDataList;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageData;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageListData;
import de.hybris.platform.messagecentercsfacades.util.JsonUtils;
import de.hybris.platform.messagecentercsservices.ConversationService;
import de.hybris.platform.messagecentercsservices.model.ConversationModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
/**
 * A default implementation of ConversationFacade
 */
public class DefaultConversationFacade implements ConversationFacade
{
	private UserService userService;
	private ConversationService conversationService;
	private Converter<ConversationModel, ConversationData> conversationConverter;
	private Converter<List<ConversationMessageData>, ConversationMessageListData> messageListConverter;
	private Converter<List<ConversationData>, ConversationDataList> conversationListConverter;
	private Converter<PrincipalModel, PrincipalData> principalConverter;


	@Override
	public List<ConversationData> getConversationsForCustomer()
	{
		return Converters.convertAll(
				getConversationService().getAllConversationsForCustomer((CustomerModel) getUserService().getCurrentUser()),
				getConversationConverter());
	}

	@Override
	public ConversationData pickConversation(final String conversationId)
	{
		final EmployeeModel agent = (EmployeeModel) getUserService().getCurrentUser();
		final Optional<ConversationModel> conversation = getConversationService().pickConversation(conversationId, agent);
		return conversation.map(c -> getConversationConverter().convert(c)).orElse(null);
	}

	@Override
	public  List<ConversationData> getOpenConversations()
	{
		final EmployeeModel agent = (EmployeeModel) getUserService().getCurrentUser();
		final List<ConversationModel> todoConversationList = getConversationService().getOpenConversationsForAgent(agent);
		if (CollectionUtils.isEmpty(todoConversationList)) {
			return Collections.emptyList();
		}
		return Converters.convertAll(todoConversationList, getConversationConverter());

	}

	@Override
	public ConversationData getConversationById(final String conversationId)
	{
		final Optional<ConversationModel> conversation = getConversationService().getConversationForUid(conversationId);
		return conversation.map(c -> getConversationConverter().convert(c)).orElse(null);
	}

	@Override
	public List<ConversationData> getUnassignedConversations()
	{
		return Converters.convertAll(getConversationService().getUnassignedConversations(), getConversationConverter());
	}

	@Override
	public ConversationData closeConversation(final String uid)
	{
		return getConversationService().closeConversation(uid, getUserService().getCurrentUser())
				.map(c -> getConversationConverter().convert(c)).orElseGet(ConversationData::new);
	}

	@Override
	public ConversationDataList getConversationDataList(final List<ConversationData> conversations)
	{
		return getConversationListConverter().convert(conversations);
	}

	@Override
	public boolean isConversationAccessible(final ConversationData conversation)
	{
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof CustomerModel)
		{
			return user.getUid().equals(conversation.getCustomer().getUid());
		}
		else
		{
			final PrincipalData agent = conversation.getAgent();
			return user.getUid().equals(agent == null ? null : agent.getUid());
		}
	}

	@Override
	public List<ConversationMessageData> getMessagesForConversation(final String conversationId)
	{
		final ConversationModel conversation = getConversationService().getConversationForUid(conversationId).orElse(null);
		if (Objects.nonNull(conversation) && StringUtils.isNotBlank(conversation.getMessages()))
		{
			return JsonUtils.fromJson(conversation.getMessages(), ConversationMessageData.class);
		}
		return Collections.emptyList();
	}

	@Override
	public ConversationMessageListData getConversationMessageList(final List<ConversationMessageData> messages)
	{
		return getMessageListConverter().convert(messages);
	}

	@Override
	public boolean isCustomer()
	{
		return userService.getCurrentUser() instanceof CustomerModel;
	}

	@Override
	public ConversationData sendMessage(final ConversationMessageListData conversationMessage)
	{
		final List<ConversationMessageData> conversationMessages = conversationMessage.getMessages();

		if (CollectionUtils.isNotEmpty(conversationMessages))
		{				
			final Optional<ConversationModel> conversation = updateOrCreateConversationById(
					conversationMessage.getConversationId(), conversationMessages);
			return conversation.map(c -> getConversationConverter().convert(c)).orElse(null);
		}
		return null;
	}


	protected Optional<ConversationModel> updateOrCreateConversationById(final String conversationId,
			final List<ConversationMessageData> messages)
	{
		addSenderToMessage(messages);
		if (StringUtils.isNotBlank(conversationId))
		{
			final List<ConversationMessageData> currentMessages = getMessagesForConversation(conversationId);
			if (CollectionUtils.isNotEmpty(currentMessages))
			{
				messages.addAll(currentMessages);
				sortMessagesDesc(messages);
				return getConversationService().updateConversation(conversationId, JsonUtils.toJson(messages));
			}
		}
		else
		{
			sortMessagesDesc(messages);
			return getConversationService().createConversation(JsonUtils.toJson(messages));
		}
		return Optional.empty();
	}
	
	protected void sortMessagesDesc (final List<ConversationMessageData> messages) {
		messages.sort((a, b) -> b.getSentTime().compareTo(a.getSentTime()));
	}

	protected void addSenderToMessage(final List<ConversationMessageData> messages)
	{
		final UserModel sender = getUserService().getCurrentUser();
		for (final ConversationMessageData message : messages)
		{
			message.setSender(getPrincipalConverter().convert(sender));
		}

	}

	protected ConversationService getConversationService()
	{
		return conversationService;
	}

	@Required
	public void setConversationService(final ConversationService conversationService)
	{
		this.conversationService = conversationService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected Converter<ConversationModel, ConversationData> getConversationConverter()
	{
		return conversationConverter;
	}

	@Required
	public void setConversationConverter(final Converter<ConversationModel, ConversationData> conversationConverter)
	{
		this.conversationConverter = conversationConverter;
	}



	protected Converter<List<ConversationMessageData>, ConversationMessageListData> getMessageListConverter()
	{
		return messageListConverter;
	}

	@Required
	public void setMessageListConverter(
			final Converter<List<ConversationMessageData>, ConversationMessageListData> messageListConverter)
	{
		this.messageListConverter = messageListConverter;
	}

	protected Converter<List<ConversationData>, ConversationDataList> getConversationListConverter()
	{
		return conversationListConverter;
	}

	@Required
	public void setConversationListConverter(
			final Converter<List<ConversationData>, ConversationDataList> conversationListConverter)
	{
		this.conversationListConverter = conversationListConverter;
	}

	protected Converter<PrincipalModel, PrincipalData> getPrincipalConverter()
	{
		return principalConverter;
	}

	@Required
	public void setPrincipalConverter(final Converter<PrincipalModel, PrincipalData> principalConverter)
	{
		this.principalConverter = principalConverter;
	}

}
