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

package de.hybris.platform.messagecentercsfacades.populators

import de.hybris.platform.commercefacades.user.data.PrincipalData
import de.hybris.platform.core.model.security.PrincipalModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.messagecentercsfacades.data.ConversationData
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageData
import de.hybris.platform.messagecentercsfacades.populators.ConversationPopulator
import de.hybris.platform.messagecentercsservices.enums.ConversationStatus
import de.hybris.platform.messagecentercsservices.model.ConversationModel
import de.hybris.platform.servicelayer.dto.converter.Converter

import org.junit.Test

import spock.lang.Specification

class ConversationPopulatorTest extends Specification
{
	def private static final String CONVERSATION_ID = "1000"
	def private static final String CONVERSATION_STATUS = "OPEN"
	def private Date createDate
	def private Date closeDate
	def private static final String MESSAGE_CONTENT = "Can you help me"
	def private static final String MESSAGE_CONTENT_JSON = "[{\"content\":\"Can you help me\",\"sentTime\":1513871457000,\"sender\":{\"uid\":\"testuser@126.com\",\"name\":\"Test User\"}}]"

	def private ConversationModel source 
	def private CustomerModel customer 
	def private EmployeeModel agent
	def private PrincipalData princial 
	def private Converter<PrincipalModel, PrincipalData> principalConverter  

	def private ConversationPopulator populator;
	def private ConversationData target;
	def private ConversationMessageData conversationMessageData;

	def setup()
	{
		source = Mock()
		customer = Mock()
		agent = Mock()
		princial = Mock()
		principalConverter = Mock()
		conversationMessageData = Mock()
		
		populator = new ConversationPopulator()
		populator.setPrincipalConverter(principalConverter)
		target = new ConversationData();
		final Calendar calendar = Calendar.getInstance()
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 2)
		createDate = calendar.getTime()
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 10)
		closeDate = calendar.getTime()
		
		source.getAgent() >> agent
		source.getCustomer >> customer
		source.getStatus() >> ConversationStatus.OPEN
		source.getMessages() >> MESSAGE_CONTENT_JSON
		principalConverter.convert(_) >> princial
		source.getUid() >> CONVERSATION_ID
		source.getCloseTime() >> closeDate
		source.getCreationtime() >> createDate
		conversationMessageData.getContent() >> MESSAGE_CONTENT
	}

	@Test
	def "testPopulator"()
	{

		when:
		populator.populate(source, target)
		then:
		CONVERSATION_ID.equals(target.getId())
		CONVERSATION_STATUS.equals(target.getStatus())
		princial.equals(target.getAgent())
		princial.equals(target.getCustomer())
		createDate.equals(target.getCreateDate())
		closeDate.equals(target.getCloseDate())
		MESSAGE_CONTENT.equals(target.getLatestMessage().getContent())

		when:
		populator.populate(source, null)
		then:
		thrown(IllegalArgumentException)

		when:
		populator.populate(null, target)
		then:
		thrown(IllegalArgumentException)
	}
}
