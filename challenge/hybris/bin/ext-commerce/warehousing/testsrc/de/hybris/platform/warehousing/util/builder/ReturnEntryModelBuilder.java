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

import de.hybris.platform.basecommerce.enums.ReturnAction;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;


public class ReturnEntryModelBuilder
{
	private final ReturnEntryModel model;

	private ReturnEntryModelBuilder()
	{
		model = new ReturnEntryModel();
	}

	private ReturnEntryModel getModel()
	{
		return this.model;
	}

	public static ReturnEntryModelBuilder aModel()
	{
		return new ReturnEntryModelBuilder();
	}

	public ReturnEntryModel build()
	{
		return getModel();
	}
	
	public ReturnEntryModelBuilder withStatus(final ReturnStatus status)
	{
		getModel().setStatus(status);
		return this;
	}
	
	public ReturnEntryModelBuilder withAction(final ReturnAction action)
	{
		getModel().setAction(action);
		return this;
	}

	public ReturnEntryModelBuilder withOrderEntry(final AbstractOrderEntryModel orderEntry)
	{
		getModel().setOrderEntry(orderEntry);
		return this;
	}

	public ReturnEntryModelBuilder withReturnRequest(final ReturnRequestModel returnRequest)
	{
		getModel().setReturnRequest(returnRequest);
		return this;
	}

}
