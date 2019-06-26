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
package de.hybris.platform.b2b.punchout.actions;

import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.enums.PunchOutOrderOperationAllowed;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.cxml.BuyerCookie;
import org.cxml.CXML;
import org.cxml.Message;
import org.cxml.Money;
import org.cxml.PunchOutOrderMessage;
import org.cxml.PunchOutOrderMessageHeader;
import org.cxml.Total;
import org.springframework.beans.factory.annotation.Required;


/**
 * Process the body of the Punch Out Order Message.
 */
public class PunchOutOrderMessageProcessingAction implements PunchOutProcessingAction<CartModel, CXML>
{

	private Converter<CartModel, PunchOutOrderMessage> punchOutOrderMessageConverter;

	private PunchOutSessionService punchOutSessionService;

	@Override
	public void process(final CartModel input, final CXML transaction)
	{

		final PunchOutOrderMessage punchOutOrderMessage = getPunchOutOrderMessageConverter().convert(input);
		final PunchOutSession currentPunchOutSession = getPunchOutSessionService().getCurrentPunchOutSession();
		final BuyerCookie buyerCookie = new BuyerCookie();
		buyerCookie.getContent().add(currentPunchOutSession.getBuyerCookie());
		punchOutOrderMessage.setBuyerCookie(buyerCookie);
		punchOutOrderMessage.setPunchOutOrderMessageHeader(new PunchOutOrderMessageHeader());
		punchOutOrderMessage.getPunchOutOrderMessageHeader().setOperationAllowed(PunchOutOrderOperationAllowed.EDIT.getCode());

		final Total total = new Total();
		total.setMoney(new Money());
		total.getMoney().setCurrency(input.getCurrency().getIsocode());
		total.getMoney().setvalue(String.valueOf(input.getTotalPrice()));

		punchOutOrderMessage.getPunchOutOrderMessageHeader().setTotal(total);

		final Message message = new Message();
		message
				.getPunchOutOrderMessageOrProviderDoneMessageOrSubscriptionChangeMessageOrDataAvailableMessageOrSupplierChangeMessageOrOrganizationChangeMessageOrProductActivityMessage()
				.add(punchOutOrderMessage);
		transaction.getHeaderOrMessageOrRequestOrResponse().add(message);
	}

	protected Converter<CartModel, PunchOutOrderMessage> getPunchOutOrderMessageConverter()
	{
		return punchOutOrderMessageConverter;
	}

	@Required
	public void setPunchOutOrderMessageConverter(final Converter<CartModel, PunchOutOrderMessage> punchOutOrderMessageConverter)
	{
		this.punchOutOrderMessageConverter = punchOutOrderMessageConverter;
	}

	protected PunchOutSessionService getPunchOutSessionService()
	{
		return punchOutSessionService;
	}

	@Required
	public void setPunchOutSessionService(final PunchOutSessionService punchOutSessionService)
	{
		this.punchOutSessionService = punchOutSessionService;
	}

}
