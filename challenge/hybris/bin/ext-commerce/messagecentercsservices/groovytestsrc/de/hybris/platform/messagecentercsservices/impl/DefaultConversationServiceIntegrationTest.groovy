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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.messagecentercsservices.enums.ConversationStatus;
import de.hybris.platform.messagecentercsservices.model.ConversationModel;
import de.hybris.platform.messagecentercsservices.impl.DefaultConversationService
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



@IntegrationTest
public class DefaultConversationServiceIntegrationTest extends ServicelayerTransactionalSpockSpecification
{

	private static final String CUSTOMER_ID1 = "customer1@hybris.com";
	private static final String CUSTOMER_ID2 = "customer2@hybris.com";
	private static final String CUSTOMER_ID3 = "customer3@hybris.com";
	private static final String CUSTOMER_ID4 = "customer4@hybris.com";
	private static final String AGENT_ID1 = "agent1@hybris.com";
	private static final String AGENT_ID2 = "agent2@hybris.com";
	private static final String CONVERSATION_ID1 = "00000001";
	private static final String CONVERSATION_ID2 = "00000002";
	private static final String CONVERSATION_ID3 = "00000003";
	private static final int CONVERSATION_COUNT_CUSTOMER1 = 2;

	private static final String CONVERSATION_TO_CLOSE = "conversation_to_close";
	private static final String CUSTOMER_CLOSE_CONVERSATION = "customer_close_conversation";
	private static final String AGENT_CLOSE_CONVERSATION = "agent_close_conversation";
	private static final String messageContent = "[{\"content\":\"Can you help me\",\"sentTime\":1513871457000,\"sender\":{\"uid\":\"testuser@126.com\",\"name\":\"Test User\"}}]";
	private static final String messageContent2 = "[{\"content\":\"2nd Message for test\",\"sentTime\":1513871800000,\"sender\":{\"uid\":\"testuser@126.com\",\"name\":\"Test User\"}},{\"content\":\"Can you help me\",\"sentTime\":1513871457000,\"sender\":{\"uid\":\"testuser@126.com\",\"name\":\"Test User\"}}]"
	
	@Resource(name = "conversationService")
	private DefaultConversationService conversationService;

	@Resource
	private ModelService modelService;

	@Resource
	private UserService userService;

	private CustomerModel customer1;
	private CustomerModel customer2;
	private CustomerModel customer3;
	private CustomerModel customer4;
	private EmployeeModel agent1;
	private EmployeeModel agent2;
	private ConversationModel conversation;

	
	def setup()
	{
		
		customer1 = modelService.create(CustomerModel.class);
		customer1.setUid(CUSTOMER_ID1);
		modelService.save(customer1);

		customer2 = modelService.create(CustomerModel.class);
		customer2.setUid(CUSTOMER_ID2);
		modelService.save(customer2);

		customer3 = modelService.create(CustomerModel.class);
		customer3.setUid(CUSTOMER_ID3);
		modelService.save(customer3);

		customer4 = modelService.create(CustomerModel.class);
		customer4.setUid(CUSTOMER_ID4);
		modelService.save(customer4);

		agent1 = modelService.create(EmployeeModel.class);
		agent1.setUid(AGENT_ID1);
		modelService.save(agent1);
		agent2 = modelService.create(EmployeeModel.class);
		agent2.setUid(AGENT_ID2);
		modelService.save(agent2);

		conversation = modelService.create(ConversationModel.class);
		conversation.setCustomer(customer4);
		conversation.setUid(CONVERSATION_ID2);
		conversation.setAgent(agent2);
		conversation.setMessages(messageContent);
		modelService.save(conversation);

		importCsv("/messagecentercsservices/test/DefaultConversationServiceTest.impex", "UTF-8");
	}

	@Test
	def "testGetAllConversationsForCustomer"()
	{
		final List<ConversationModel> result = conversationService.getAllConversationsForCustomer(customer1);

		expect:
		result.size() == CONVERSATION_COUNT_CUSTOMER1;
		result.get(0).getCustomer() == customer1;
		
	}

	@Test
	def "testGetAllConversationsForCustomerWithoutConversation"()
	{
		final List<ConversationModel> result = conversationService.getAllConversationsForCustomer(customer2);

		expect:
		result.size() == 0;

	}

	@Test
	def "testGetAllConversationsForNullCustomer"()
	{

		when: "try to get conversations for null customer"
		conversationService.getAllConversationsForCustomer(null);
		
		then: "an exception should be thrown"
		thrown (IllegalArgumentException)
	}

	@Test
	def "testGetAllConversationsForNonExistCustomer"()
	{
		setup:
		final CustomerModel customer = modelService.create(CustomerModel.class);
		customer.setUid("NonExist@hybris.com");
		
		when: "try to get conversations for non-exist customer"
		conversationService.getAllConversationsForCustomer(customer);
		
		then: "an exception should be thrown"
		thrown (IllegalStateException)
	}

	@Test
	def "testCreateConversation"()
	{

		
		final List<ConversationModel> conversations1 = conversationService.getAllConversationsForCustomer(customer2);
		final int size1 = conversations1.size();
		userService.setCurrentUser(customer2);
		conversationService.setUserService(userService);
		final ConversationModel conversation = conversationService.createConversation(messageContent).get();
		final int size2 = conversationService.getAllConversationsForCustomer(customer2).size();

		expect:
		conversation.getCustomer() == customer2
		size2 == size1 + 1

	}

	@Test
	def "testCreateConversationFailed"()
	{
		final EmployeeModel employee = modelService.create(EmployeeModel.class);
		employee.setUid("csTestEmployee@hybris.com");
		modelService.save(employee);
		userService.setCurrentUser(employee);
		conversationService.setUserService(userService);
		final Optional<ConversationModel> conversation = conversationService.createConversation(messageContent);

		expect:
		conversation.orElse(null) == null;
	}

	@Test
	def "testCloseConversation_by_customer"()
	{
		final UserModel user = userService.getUserForUID(CUSTOMER_CLOSE_CONVERSATION);
		userService.setCurrentUser(user);

		expect:
		conversationService.closeConversation(CONVERSATION_TO_CLOSE, user).get().getStatus() == ConversationStatus.CLOSED
	}

	@Test
	def "testCloseConversation_by_agent"()
	{
		final UserModel user = userService.getUserForUID(AGENT_CLOSE_CONVERSATION);
		userService.setCurrentUser(user);

		expect:
		conversationService.closeConversation(CONVERSATION_TO_CLOSE, user).get().getStatus() == ConversationStatus.CLOSED
	}

	@Test
	def "testCloseConversation_non_params"()
	{
		when:
		conversationService.closeConversation(null, null);
		then:
		thrown (IllegalArgumentException)
	}

	@Test
	def "testGetUnassignedConversationsWithEntries"()
	{
		final ConversationModel createdConversation = modelService.create(ConversationModel.class);
		createdConversation.setCustomer(customer3);
		createdConversation.setUid(CONVERSATION_ID1);
		createdConversation.setMessages(messageContent)
		modelService.save(createdConversation);

		final List<ConversationModel> unassignedList = conversationService.getUnassignedConversations();

		final ConversationModel unassignedconversation = unassignedList.get(0);
		
		expect:
		unassignedList.size() == 1
		unassignedconversation.getUid() == CONVERSATION_ID1
		unassignedconversation.getStatus() == ConversationStatus.OPEN
		unassignedconversation.getCustomer().getUid() == CUSTOMER_ID3.toLowerCase()
		unassignedconversation.getAgent() == null
		unassignedconversation.getCreationtime() != null
	}

	@Test
	def "testGetUnassignedConversationsWithoutEntry"()
	{
		final ConversationModel createdConversation = modelService.create(ConversationModel.class);
		createdConversation.setCustomer(customer3);
		createdConversation.setUid(CONVERSATION_ID1);
		createdConversation.setMessages(messageContent)
		createdConversation.setStatus(ConversationStatus.CLOSED);
		modelService.save(createdConversation);

		final List<ConversationModel> unassignedList = conversationService.getUnassignedConversations();

		expect:
		unassignedList.size() == 0
	}

	@Test
	def "testGetOpenConversationsWithEntries"()
	{
		final ConversationModel createdConversation = modelService.create(ConversationModel.class);
		createdConversation.setCustomer(customer1);
		createdConversation.setAgent(agent1);
		createdConversation.setUid(CONVERSATION_ID1);
		createdConversation.setMessages(messageContent);
		modelService.save(createdConversation);

		final List<ConversationModel> openedList = conversationService.getOpenConversationsForAgent(agent1);

		final ConversationModel openedconversation = openedList.get(0);

		expect:
		openedList.size() == 1
		openedconversation.getUid() == CONVERSATION_ID1
		openedconversation.getStatus() == ConversationStatus.OPEN
		openedconversation.getCustomer().getUid() == CUSTOMER_ID1.toLowerCase()
		openedconversation.getAgent().getUid() == AGENT_ID1.toLowerCase()
		openedconversation.getCreationtime() != null

	}

	@Test
	def "testGetOpenConversationsWithoutEntry"()
	{
		final ConversationModel createdConversation = modelService.create(ConversationModel.class);
		createdConversation.setCustomer(customer1);
		createdConversation.setAgent(agent1);
		createdConversation.setUid(CONVERSATION_ID1);
		createdConversation.setMessages(messageContent);
		createdConversation.setStatus(ConversationStatus.CLOSED);
		modelService.save(createdConversation);

		final List<ConversationModel> unassignedList = conversationService.getOpenConversationsForAgent(agent1);
	
		expect:
		unassignedList.size() == 0
	}

	@Test
	def "testGetOpenConversationsParamNull"()
	{
		when:
		conversationService.getOpenConversationsForAgent(null);
		
		then:
		thrown (IllegalArgumentException)
	}

	@Test
	def "testGetConversationForUid_WithPramNull"()
	{
		when:
		conversationService.getConversationForUid(null);
		
		then:
		thrown (IllegalArgumentException)
	}

	@Test
	def "testGetConversationForUid_NoConversation"()
	{
		final Optional<ConversationModel> conversation = conversationService.getConversationForUid(CONVERSATION_ID3);

		
		expect:
		conversation == Optional.empty()
	}

	@Test
	def "testGetConversationForUid"()
	{
		final Optional<ConversationModel> conversationOpyional = conversationService.getConversationForUid(CONVERSATION_ID2);

		expect:
		conversationOpyional.get() == conversation
	}

	
	@Test
	def "testUpdateConversation"()
	{
		userService.setCurrentUser(customer4);
		conversationService.setUserService(userService);
		
		final ConversationModel conversation = conversationService.updateConversation(CONVERSATION_ID2,messageContent2).get();

		expect:
		conversation.getMessages() == messageContent2
	}
	
	@Test
	def "testUpdateConversationFailed"()
	{
		userService.setCurrentUser(customer2);
		conversationService.setUserService(userService);
		
		final Optional<ConversationModel> conversationOptional = conversationService.updateConversation(CONVERSATION_ID2,messageContent2);

		expect:
		conversationOptional.orElse(null) == null
	}

}
