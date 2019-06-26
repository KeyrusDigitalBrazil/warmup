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
package de.hybris.platform.acceleratorservices.payment.cybersource.converters.populators.response;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;


public class SubscriptionInfoResultPopulator extends AbstractResultPopulator<Map<String, String>, CreateSubscriptionResult>
{
	@Override
	public void populate(final Map<String, String> source, final CreateSubscriptionResult target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter [Map<String, String>] source cannot be null");
		validateParameterNotNull(target, "Parameter [CreateSubscriptionResult] target cannot be null");

		final SubscriptionInfoData data = new SubscriptionInfoData();
		data.setSubscriptionID(source.get("paySubscriptionCreateReply_subscriptionID"));
		data.setSubscriptionSignedValue(source.get("paySubscriptionCreateReply_subscriptionID"));
		data.setSubscriptionIDPublicSignature(source.get("paySubscriptionCreateReply_subscriptionIDPublicSignature"));

		target.setSubscriptionInfoData(data);
	}
}
