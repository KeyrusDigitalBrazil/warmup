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
 package de.hybris.platform.messagecentercsfacades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.messagecentercsfacades.data.ConversationData;
import de.hybris.platform.messagecentercsfacades.data.ConversationDataList;

import java.util.List;

import org.springframework.util.Assert;


public class ConversationListPopulator implements Populator<List<ConversationData>, ConversationDataList>
{

	@Override
	public void populate(final List<ConversationData> source, final ConversationDataList target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setConversations(source);

	}

}
