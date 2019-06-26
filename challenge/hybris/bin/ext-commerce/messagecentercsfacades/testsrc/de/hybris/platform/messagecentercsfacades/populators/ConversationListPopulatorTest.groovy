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

import de.hybris.platform.messagecentercsfacades.data.ConversationData
import de.hybris.platform.messagecentercsfacades.data.ConversationDataList
import de.hybris.platform.messagecentercsfacades.populators.ConversationListPopulator

import org.junit.Test

import spock.lang.Specification

class ConversationListPopulatorTest extends Specification
{

	def private List<ConversationData> source 
	def private ConversationListPopulator populator 
	def private ConversationDataList target
	def setup()
	{
		populator = new ConversationListPopulator()
		target = new ConversationDataList()
		source = Mock()
	}
	@Test
	def "testPopulator"()
	{
		when:
		populator.populate(source, target)
		then:
		source == target.getConversations()

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
