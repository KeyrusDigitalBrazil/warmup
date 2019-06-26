/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cissapdigitalpayment.populator;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentPollRegisteredCardResult;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


/**
 * Populates {@link CCPaymentInfoData} with {@link CisSapDigitalPaymentPollRegisteredCardResult Model}.
 */
public class CisSapDigitalPaymentPaymentInfoPopulator
		implements Populator<CisSapDigitalPaymentPollRegisteredCardResult, CCPaymentInfoData>
{

	@Override
	public void populate(final CisSapDigitalPaymentPollRegisteredCardResult source, final CCPaymentInfoData target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");



		if (null != source.getPaytCardByDigitalPaymentSrvc())
		{
			//Set the ID is also as the Token
			target.setId(source.getPaytCardByDigitalPaymentSrvc());
			//This subscription ID set is getting overridden while calling createSubscription method in CustomerAccountService
			target.setSubscriptionId(source.getPaytCardByDigitalPaymentSrvc());
		}

		if (null != source.getPaymentCardType())
		{
			target.setCardType(source.getPaymentCardType());
		}

		if (null != source.getPaymentCardExpirationMonth())
		{
			target.setExpiryMonth(source.getPaymentCardExpirationMonth());
		}
		if (null != source.getPaymentCardExpirationYear())
		{
			target.setExpiryYear(source.getPaymentCardExpirationYear());
		}

		if (null != source.getPaymentCardMaskedNumber())
		{
			target.setCardNumber(source.getPaymentCardMaskedNumber());
		}

		if (null != source.getPaymentCardHolderName())
		{
			target.setAccountHolderName(source.getPaymentCardHolderName());
		}
	}

}
