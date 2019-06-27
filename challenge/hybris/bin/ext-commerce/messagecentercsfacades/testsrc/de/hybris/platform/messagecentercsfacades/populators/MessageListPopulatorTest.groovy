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

import de.hybris.platform.messagecentercsfacades.data.ConversationMessageData
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageListData
import de.hybris.platform.messagecentercsfacades.populators.MessageListPopulator

import org.junit.Test

import spock.lang.Specification

class MessageListPopulatorTest extends Specification
{
	def private List<ConversationMessageData> source 
	def private MessageListPopulator populator
	def private ConversationMessageListData target

	def setup()
	{
		source = Mock()
		populator = new MessageListPopulator();
		target = new ConversationMessageListData();
	}
	@Test
	def "testPopulator"()
	{
		when:
		populator.populate(source, target);
		then:
		source.equals(target.getMessages())

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
