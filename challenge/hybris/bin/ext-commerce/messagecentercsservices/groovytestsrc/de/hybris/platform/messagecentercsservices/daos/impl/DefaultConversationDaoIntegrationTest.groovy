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
 package de.hybris.platform.messagecentercsservices.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.messagecentercsservices.enums.ConversationStatus;
import de.hybris.platform.messagecentercsservices.model.ConversationModel;
import de.hybris.platform.messagecentercsservices.daos.impl.DefaultConversationDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultConversationDaoIntegrationTest extends ServicelayerTransactionalSpockSpecification
{
	@Resource
	DefaultConversationDao conversationDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	private CustomerModel customer;
	private ConversationModel conversation;


	private static final String CUSTOMER_ID = "mcdaoTest1@hybris.com";
	private static final String CUSTOMER_WITHOUT_CONVERSATION_ID = "mcdaoTest2@hybris.com";
	private static final String CUSTOMERSUPPORTAGENT_ID = "mcdaoTestCSA";
	private static final int CONVERSATION_COUNT_CUSTOMER = 1;
	private static final String CONVERSATION_UID = "00000001";
	private static final String CONVERSATION_UID2 = "00000002";
	private static final String messageContent = "[{\"content\":\"testbulk\",\"sentTime\":1513871457000,\"sender\":{\"uid\":\"mcdaoTest1@hybris.com\",\"name\":\"mcdaotest\"}}]"
	
	def setup() 
	{
		
		customer = modelService.create(CustomerModel.class);
		customer.setUid(CUSTOMER_ID);
		modelService.save(customer);

		conversation = modelService.create(ConversationModel.class);
		conversation.setCustomer(customer);
		conversation.setUid(CONVERSATION_UID);
		conversation.setMessages(messageContent)
		modelService.save(conversation);
		
	}

	@Test
	def "testFindConversationsByCustomer"()
	{
		final List<ConversationModel> result = conversationDao.find(Collections.singletonMap(ConversationModel.CUSTOMER, customer));

		expect:
		result.size() == 1
	}

	@Test
	def "testFindConversationsByCustomerWithoutConversation"()
	{
		final CustomerModel customerWithoutConversation = modelService.create(CustomerModel.class);
		customerWithoutConversation.setUid(CUSTOMER_WITHOUT_CONVERSATION_ID);
		modelService.save(customerWithoutConversation);

		final List<ConversationModel> result = conversationDao
				.find(Collections.singletonMap(ConversationModel.CUSTOMER, customerWithoutConversation));
		expect:
		result.size() == 0
	}

	@Test
	def "testFindUnassignedConversationsWithEntries"()
	{
		final List<ConversationModel> unassignedConversations = conversationDao.findUnassignedConversations();

		expect:
		unassignedConversations.size() == 1;
	}

	@Test
	def "testFindUnassignedConversationsWithoutEntry"()
	{
		final String customerID = "mcdaoTest3@hybris.com";
		final CustomerModel customer = modelService.create(CustomerModel.class);
		customer.setUid(customerID);
		modelService.save(customer);

		final String conversationUidString = "00000002";
		final ConversationModel closedConversation = modelService.create(ConversationModel.class);
		closedConversation.setCustomer(customer);
		closedConversation.setUid(conversationUidString);
		closedConversation.setMessages(messageContent)
		closedConversation.setStatus(ConversationStatus.CLOSED);
		modelService.save(closedConversation);

		final List<ConversationModel> unassignedConversations = conversationDao.findUnassignedConversations();
		
		expect:
		unassignedConversations.size() == 1

	}

	@Test
	def "testFindOpenConversationWithEntries"()
	{
		final EmployeeModel agent = modelService.create(EmployeeModel.class);
		agent.setUid(CUSTOMERSUPPORTAGENT_ID);
		conversation.setAgent(agent);
		modelService.save(conversation);

		final Map<String, Object> params = new HashMap<>();
		params.put(ConversationModel.AGENT, agent);
		params.put(ConversationModel.STATUS, ConversationStatus.OPEN);

		final List<ConversationModel> openedConversation = conversationDao.find(params);
		
		expect:
		openedConversation.size() == 1

	}

	@Test
	def "testFindOpenConversationWithoutEntry"()
	{
		final EmployeeModel agent = modelService.create(EmployeeModel.class);
		agent.setUid(CUSTOMERSUPPORTAGENT_ID);
		modelService.save(agent);

		final Map<String, Object> params = new HashMap<>();
		params.put(ConversationModel.AGENT, agent);
		params.put(ConversationModel.STATUS, ConversationStatus.OPEN);

		final List<ConversationModel> openedConversation = conversationDao.find(params);
		
		expect:
		openedConversation.size() == 0
	}

}
