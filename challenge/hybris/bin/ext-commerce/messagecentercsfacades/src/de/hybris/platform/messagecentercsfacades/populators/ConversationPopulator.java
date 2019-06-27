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

import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.messagecentercsfacades.data.ConversationData;
import de.hybris.platform.messagecentercsfacades.data.ConversationMessageData;
import de.hybris.platform.messagecentercsfacades.util.JsonUtils;
import de.hybris.platform.messagecentercsservices.model.ConversationModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * A implementation of consignment populator for Conversation
 */
public class ConversationPopulator implements Populator<ConversationModel, ConversationData>
{

	private Converter<PrincipalModel, PrincipalData> principalConverter;

	@Override
	public void populate(final ConversationModel source, final ConversationData target)
	{

		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setId(source.getUid());
		target.setStatus(source.getStatus().toString());
		target.setCustomer(getPrincipalConverter().convert(source.getCustomer()));
		target.setAgent(source.getAgent() != null ? getPrincipalConverter().convert(source.getAgent()) : null);
		target.setCreateDate(source.getCreationtime());
		target.setCloseDate(source.getCloseTime());
		final ConversationMessageData latestMessage = getLatestMessage(source);
		if (Objects.nonNull(latestMessage))
		{
			target.setLatestMessage(latestMessage);
		}
	}

	@Required
	public void setPrincipalConverter(final Converter<PrincipalModel, PrincipalData> principalConverter)
	{
		this.principalConverter = principalConverter;
	}

	protected Converter<PrincipalModel, PrincipalData> getPrincipalConverter()
	{
		return principalConverter;
	}

	protected ConversationMessageData getLatestMessage(final ConversationModel source)
	{
		final List<ConversationMessageData> messages = JsonUtils.fromJson(source.getMessages(), ConversationMessageData.class);
		if (CollectionUtils.isNotEmpty(messages))
		{
			messages.sort((a, b) -> b.getSentTime().compareTo(a.getSentTime()));
			return messages.get(0);
		}
		return null;
	}

}
