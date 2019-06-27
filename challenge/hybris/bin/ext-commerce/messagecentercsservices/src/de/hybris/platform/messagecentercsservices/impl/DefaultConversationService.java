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
package de.hybris.platform.messagecentercsservices.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.messagecentercsservices.ConversationService;
import de.hybris.platform.messagecentercsservices.daos.ConversationDao;
import de.hybris.platform.messagecentercsservices.enums.ConversationStatus;
import de.hybris.platform.messagecentercsservices.model.ConversationModel;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ConversationService}
 */
public class DefaultConversationService implements ConversationService
{
   private static final String UID_NULL_MESSAGE = "Parameter uid must not be null";
	private ConversationDao conversationDao;
	private UserService userService;
	private ModelService modelService;
	private KeyGenerator conversationUidGenerator;

	@Override
	public Optional<ConversationModel> createConversation(final String messages)
	{
		final ConversationModel conversation = getModelService().create(ConversationModel.class);
		final UserModel user = getUserService().getCurrentUser();
		if (user instanceof CustomerModel)
		{
			conversation.setUid(getConversationUidGenerator().generate().toString());
			conversation.setCustomer((CustomerModel) user);
			conversation.setMessages(messages);
			modelService.save(conversation);
			return Optional.of(conversation);
		}

		return Optional.empty();
	}

	@Override
	public Optional<ConversationModel> getConversationForUid(final String uid)
	{
		validateParameterNotNull(uid, UID_NULL_MESSAGE);

		return getConversationDao().find(Collections.singletonMap(ConversationModel.UID, uid)).stream().findFirst();
	}

	@Override
	public List<ConversationModel> getAllConversationsForCustomer(final CustomerModel customer)
	{
		validateParameterNotNull(customer, "Parameter customer must not be null");

		return getConversationDao().find(Collections.singletonMap(ConversationModel.CUSTOMER, customer));
	}

	@Override
	public List<ConversationModel> getOpenConversationsForAgent(final EmployeeModel agent)
	{
		validateParameterNotNull(agent, "Parameter agent must not be null");

		final Map<String, Object> params = new HashMap<>();
		params.put(ConversationModel.AGENT, agent);
		params.put(ConversationModel.STATUS, ConversationStatus.OPEN);

		return getConversationDao().find(params);
	}

	@Override
	public List<ConversationModel> getUnassignedConversations()
	{
		return getConversationDao().findUnassignedConversations();
	}

	@Override
	public Optional<ConversationModel> pickConversation(final String uid, final EmployeeModel agent)
	{
		validateParameterNotNull(uid, UID_NULL_MESSAGE);
		validateParameterNotNull(agent, "Parameter agent must not be null");

		return getConversationForUid(uid).map(c -> {
			if (c.getAgent() == null && ConversationStatus.OPEN.equals(c.getStatus()))
			{
				c.setAgent(agent);
				getModelService().save(c);
				return c;
			}
			return null;
		});
	}

	@Override
	public Optional<ConversationModel> closeConversation(final String uid, final UserModel user)
	{
		validateParameterNotNull(uid, UID_NULL_MESSAGE);
		validateParameterNotNull(user, "Parameter user must not be null");

		return getConversationForUidAndUser(uid, user).map(c -> {
			if (!ConversationStatus.CLOSED.equals(c.getStatus()))
			{
				c.setStatus(ConversationStatus.CLOSED);
				c.setCloseTime(Calendar.getInstance().getTime());
				getModelService().save(c);
			}
			return c;
		});
	}

	@Override
	public Optional<ConversationModel> updateConversation(final String uid, final String messages)
	{
		validateParameterNotNull(uid, UID_NULL_MESSAGE);
		validateParameterNotNull(messages, "Parameter messages must not be null");

		final UserModel user = getUserService().getCurrentUser();
		return getConversationForUidAndUser(uid, user).map(c -> {
			c.setMessages(messages);
			getModelService().save(c);
			return c;
		});
	}

	protected Optional<ConversationModel> getConversationForUidAndUser(final String uid, final UserModel user)
	{
		final Map<String, Object> params = new HashMap<>(0);
		params.put(ConversationModel.UID, uid);
		if (user instanceof CustomerModel)
		{
			params.put(ConversationModel.CUSTOMER, user);
		}
		else
		{
			params.put(ConversationModel.AGENT, user);
		}

		return getConversationDao().find(params).stream().findFirst();
	}

	protected ConversationDao getConversationDao()
	{
		return conversationDao;
	}

	@Required
	public void setConversationDao(final ConversationDao conversationDao)
	{
		this.conversationDao = conversationDao;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected KeyGenerator getConversationUidGenerator()
	{
		return conversationUidGenerator;
	}

	@Required
	public void setConversationUidGenerator(final KeyGenerator conversationUidGenerator)
	{
		this.conversationUidGenerator = conversationUidGenerator;
	}


}
