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
package de.hybris.platform.commerceservices.order.strategies.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.enums.QuoteUserType;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserTypeIdentificationStrategy;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link QuoteUserTypeIdentificationStrategy}.
 */
public class DefaultQuoteUserTypeIdentificationStrategy implements QuoteUserTypeIdentificationStrategy
{
	private UserService userService;
	private String buyerGroup;
	private String sellerGroup;
	private String sellerApproverGroup;

	@Override
	public Optional<QuoteUserType> getCurrentQuoteUserType(final UserModel userModel)
	{
		validateParameterNotNullStandardMessage("userModel", userModel);

		if (getUserService().isMemberOfGroup(userModel, getUserService().getUserGroupForUID(getSellerApproverGroup())))
		{
			return Optional.of(QuoteUserType.SELLERAPPROVER);
		}
		else if (getUserService().isMemberOfGroup(userModel, getUserService().getUserGroupForUID(getSellerGroup())))
		{
			return Optional.of(QuoteUserType.SELLER);
		}
		else if (getUserService().isMemberOfGroup(userModel, getUserService().getUserGroupForUID(getBuyerGroup())))
		{
			return Optional.of(QuoteUserType.BUYER);
		}
		return Optional.empty();
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected String getBuyerGroup()
	{
		return buyerGroup;
	}

	@Required
	public void setBuyerGroup(final String buyerGroup)
	{
		this.buyerGroup = buyerGroup;
	}

	protected String getSellerGroup()
	{
		return sellerGroup;
	}

	@Required
	public void setSellerGroup(final String sellerGroup)
	{
		this.sellerGroup = sellerGroup;
	}

	protected String getSellerApproverGroup()
	{
		return sellerApproverGroup;
	}

	@Required
	public void setSellerApproverGroup(final String sellerApproverGroup)
	{
		this.sellerApproverGroup = sellerApproverGroup;
	}
}