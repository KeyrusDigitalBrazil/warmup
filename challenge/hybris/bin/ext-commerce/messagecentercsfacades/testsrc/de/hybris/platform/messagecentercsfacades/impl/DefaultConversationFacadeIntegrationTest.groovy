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

package de.hybris.platform.messagecentercsfacades.impl

import java.util.Collections
import javax.annotation.Resource

import org.junit.Test

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commercefacades.user.data.PrincipalData
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.messagecentercsfacades.ConversationFacade
import de.hybris.platform.messagecentercsfacades.data.ConversationData
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageData
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageListData
import de.hybris.platform.messagecentercsservices.ConversationService
import de.hybris.platform.messagecentercsservices.model.ConversationModel
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.dto.converter.Converter
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.user.UserService
import de.hybris.platform.messagecentercsfacades.util.JsonUtils

/**
 * Basic hybris integration test for spock.
 * Checks that we have correctly injected resource
 */
@IntegrationTest
class DefaultConversationFacadeIntegrationTest extends ServicelayerTransactionalSpockSpecification
{
	@Resource(name = "conversationFacade")
	private ConversationFacade conversationFacade;

	@Resource
	private UserService userService;

	@Resource
	private ConversationService conversationService;

	@Resource
	private ModelService modelService;

	@Resource
	private Converter<ConversationModel, ConversationData> conversationConverter;

	private ConversationMessageListData messagesWithoutId;

	private ConversationMessageListData messagesWithId;

	private List<ConversationMessageData> messages;

	private List<ConversationMessageData> messages2;

	private List<ConversationMessageData> dummyMessages;
	
	private ConversationMessageData message1;

	private ConversationMessageData message2;

	private ConversationMessageData dummymessage;
	
	private String messageContent;
	
	private PrincipalData principal;

	private CustomerModel customer;

	private CustomerModel customer2;

	private ConversationModel conversation;

	private static final String CUSTOMER_ID = "mcdaoTest1@hybris.com";
	private static final String CUSTOMER_ID2 = "mcdaoTest2@hybris.com";
	private static final String CONVERSATION_UID = "0000001";
	private static final String NAME = "mcdaoTest1@hybris.com";
	private static final String MESSAGECONTENT = "Can you help me?";
	private static final String MESSAGECONTENT2 = "Is there any one?";


	def setup()
	{
		customer = modelService.create(CustomerModel.class);
		customer.setUid(CUSTOMER_ID);
		modelService.save(customer);

		customer2 = modelService.create(CustomerModel.class);
		customer2.setUid(CUSTOMER_ID2);
		modelService.save(customer2);

		principal = new PrincipalData();
		principal.setUid(NAME);
		principal.setUid(CUSTOMER_ID);

		dummymessage = new ConversationMessageData();
		dummymessage.setContent(MESSAGECONTENT);
		dummymessage.setSentTime(new Date());
		dummyMessages = Collections.singletonList(dummymessage);
		
		message1 = new ConversationMessageData();
		message1.setContent(MESSAGECONTENT);
		message1.setSender(principal);

		message2 = new ConversationMessageData();
		message2.setContent(MESSAGECONTENT2);
		message2.setSender(principal);
		message2.setSentTime(new Date());

		messages = new ArrayList<>();
		messages.add(message1);
		messages2 = new ArrayList<>();
		messages2.add(message2);
		messagesWithoutId = new ConversationMessageListData();
		messagesWithoutId.setConversationId(null);
		messagesWithoutId.setMessages(messages);
		messagesWithId = new ConversationMessageListData();
		messagesWithId.setConversationId(CONVERSATION_UID);
		messagesWithId.setMessages(messages2);		
		
		conversation = modelService.create(ConversationModel.class);
		conversation.setUid(CONVERSATION_UID);
		conversation.setCustomer(customer2);
		conversation.setMessages(JsonUtils.toJson(dummyMessages));
		modelService.save(conversation);
	}

	@Test
	def "testSendMessage_CreateFirst" ()
	{
		userService.setCurrentUser(customer)
		conversationFacade.sendMessage(messagesWithoutId)

		final List<ConversationModel> conversations = conversationService.getAllConversationsForCustomer(customer)

		final String conversationID = conversations.get(0).getUid()
		final List<ConversationMessageData> conversationMessages = conversationFacade.getMessagesForConversation(conversationID)

		expect:
		conversations.size() == 1
		conversationMessages.size() == 1
		conversationMessages.get(0).getContent() == MESSAGECONTENT
	}

	@Test
	def "testSendMessage_SendDirect" ()
	{
		userService.setCurrentUser(customer2);
		conversationFacade.sendMessage(messagesWithId);

		final List<ConversationModel> conversations = conversationService.getAllConversationsForCustomer(customer2);

		final String conversationID = conversations.get(0).getUid()
		final List<ConversationMessageData> conversationMessages = conversationFacade.getMessagesForConversation(conversationID)


		expect:
		conversations.size() == 1
		conversationMessages.size() == 2
		conversationMessages.get(0).getContent() == MESSAGECONTENT2
	}
}
