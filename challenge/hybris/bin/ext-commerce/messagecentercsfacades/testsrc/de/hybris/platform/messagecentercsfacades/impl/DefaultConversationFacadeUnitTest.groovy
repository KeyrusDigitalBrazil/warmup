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

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.commercefacades.user.data.PrincipalData
import de.hybris.platform.converters.Converters
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.messagecentercsfacades.data.ConversationData
import de.hybris.platform.messagecentercsfacades.impl.DefaultConversationFacade
import de.hybris.platform.messagecentercsservices.ConversationService
import de.hybris.platform.messagecentercsservices.model.ConversationModel
import de.hybris.platform.servicelayer.dto.converter.Converter
import de.hybris.platform.servicelayer.user.UserService

import org.junit.Test

import spock.lang.Specification

@UnitTest
class DefaultConversationFacadeUnitTest extends Specification
{
	private static final String CONVERSATION_UID = "546232563"
	private static final String CUSTOMER_UID = "customer"
	private static final String AGENT_UID = "agent"

	def private UserService userService 

	def private ConversationService conversationService 

	def private Converter<ConversationModel, ConversationData> conversationConverter

	def private CustomerModel customer 

	def private EmployeeModel agent 

	def private PrincipalData customerData 

	def private PrincipalData agentData 

	def private List<ConversationData> conversationDatas


	def private ConversationModel conversationModel 

	def private ConversationData conversationData 

	def private List<ConversationModel> conversationModels

	def private Optional<ConversationModel> conversationOptional

	def private DefaultConversationFacade conversationFacade

	def setup()
	{
		userService = Mock()
		conversationService = Mock()
		conversationConverter = Mock()
		customer = Mock()
		agent = Mock()
		customerData = Mock()
		agentData = Mock()
		conversationDatas = Mock()
		conversationModel = Mock()
		conversationData = Mock()
		conversationModels = Mock()
		
		conversationFacade = new DefaultConversationFacade()
		conversationFacade.setUserService(userService)
		conversationFacade.setConversationService(conversationService)
		conversationFacade.setConversationConverter(conversationConverter)
		conversationOptional = Optional.of(conversationModel)

		conversationConverter.convert(conversationModel) >> conversationData
	}

	@Test
	def "testGetConversationsForCustomer"()
	{
		when:
		conversationService.getAllConversationsForCustomer(customer) >> conversationModels
		Converters.convertAll(conversationModels, conversationConverter) >> conversationDatas

		then:
		conversationFacade.getConversationsForCustomer() == conversationDatas
	}

	@Test
	def "testCloseConversation_by_customer"()
	{
		when:
		userService.getCurrentUser() >> customer
		conversationService.closeConversation(CONVERSATION_UID, customer) >> conversationOptional
		then:
		conversationFacade.closeConversation(CONVERSATION_UID) == conversationData
	}

	@Test
	def "testCloseConversation_by_agent"()
	{
		when:
		userService.getCurrentUser() >> agent
		conversationService.closeConversation(CONVERSATION_UID, agent) >> conversationOptional

		then:
		conversationFacade.closeConversation(CONVERSATION_UID) == conversationData
	}

	@Test
	def "testIsConversationAccessible_customer"()
	{
		when:
		userService.getCurrentUser() >> customer
		customer.getUid() >> CUSTOMER_UID
		customerData.getUid() >> CUSTOMER_UID
		conversationData.getCustomer() >> customerData
		
		then:
		conversationFacade.isConversationAccessible(conversationData) == true
	}
	
	@Test
	public testGetUnassignedConversationsWithEntries()
	{
		when:
		conversationService.getUnassignedConversations(customer) >> conversationModels
		Converters.convertAll(conversationModels, conversationConverter) >> conversationDatas
		
		then:
		conversationFacade.getUnassignedConversations() == conversationDatas
	}
		
	@Test
	public testPickConversationSuccess()
	{
		when:
		userService.getCurrentUser() >> agent
		conversationService.pickConversation(_ as String, !null) >> conversationOptional
		then:
		conversationFacade.pickConversation(CONVERSATION_UID) == conversationData
	}
	
	@Test
	public testPickConversation_Null()
	{
   	when:
   	userService.getCurrentUser() >> agent
   	conversationService.pickConversation(_ as String, !null) >> Optional.empty()
   	then:
   	conversationFacade.pickConversation(CONVERSATION_UID) == null
	}

	@Test
	public testGetConversationById()
	{
		when:
		conversationService.getConversationForUid(_ as String) >> conversationOptional
		then:
		conversationFacade.getConversationById(CONVERSATION_UID) == conversationData
	}
	
	@Test
	public testGetConversationById_Null()
	{	
		when:
		conversationService.getConversationForUid(_ as String) >> Optional.empty()
		then:
		conversationFacade.getConversationById(CONVERSATION_UID) == null
	}

	@Test
	public testGetOpenConversations()
	{	
		conversationModels = new ArrayList()
		
		when:
		userService.getCurrentUser() >> agent
		conversationService.getOpenConversationsForAgent(!null) >> conversationModels
		Converters.convertAll(conversationModels, conversationConverter) >> conversationDatas
		
		then:
		conversationFacade.getOpenConversations() == conversationDatas
	}
	
	@Test
	public testGetOpenConversations_Empty()
	{
		when:
		userService.getCurrentUser() >> agent
		conversationService.getOpenConversationsForAgent(!null) >> Collections.emptyList()
		
		then:
		conversationFacade.getOpenConversations() == Collections.emptyList()
	}
	
}