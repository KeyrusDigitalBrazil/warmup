/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.builder;

import java.math.BigDecimal;
import java.util.Date;

import de.hybris.platform.basecommerce.enums.RefundReason;
import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;


public class RefundEntryModelBuilder
{
	private final RefundEntryModel model;

	private RefundEntryModelBuilder()
	{
		model = new RefundEntryModel();
	}

	private RefundEntryModel getModel()
	{
		return this.model;
	}

	public static RefundEntryModelBuilder aModel()
	{
		return new RefundEntryModelBuilder();
	}

	public RefundEntryModel build()
	{
		return getModel();
	}
	
	public RefundEntryModelBuilder withStatus(final ReturnStatus status)
	{
		getModel().setStatus(status);
		return this;
	}
	
	public RefundEntryModelBuilder withReason(final RefundReason reason)
	{
		getModel().setReason(reason);
		return this;
	}
	
	public RefundEntryModelBuilder withAmount(final BigDecimal amount)
	{
		getModel().setAmount(amount);
		return this;
	}
	
	public RefundEntryModelBuilder withExpectedQTY(final Long quantity)
	{
		getModel().setExpectedQuantity(quantity);
		return this;
	}
	
	public RefundEntryModelBuilder withRefundedDate(final Date date)
	{
		getModel().setRefundedDate(date);
		return this;
	}
	
	public RefundEntryModelBuilder withAction(final ReturnAction action)
	{
		getModel().setAction(action);
		return this;
	}

	public RefundEntryModelBuilder withOrderEntry(final AbstractOrderEntryModel orderEntry)
	{
		getModel().setOrderEntry(orderEntry);
		return this;
	}

	public RefundEntryModelBuilder withReturnRequest(final ReturnRequestModel returnRequest)
	{
		getModel().setReturnRequest(returnRequest);
		return this;
	}

}
