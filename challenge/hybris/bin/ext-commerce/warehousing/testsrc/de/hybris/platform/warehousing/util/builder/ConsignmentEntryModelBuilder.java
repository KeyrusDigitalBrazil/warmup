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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;


public class ConsignmentEntryModelBuilder
{
	private final ConsignmentEntryModel model;

	private ConsignmentEntryModelBuilder()
	{
		model = new ConsignmentEntryModel();
	}

	private ConsignmentEntryModel getModel()
	{
		return this.model;
	}

	public static ConsignmentEntryModelBuilder aModel()
	{
		return new ConsignmentEntryModelBuilder();
	}

	public ConsignmentEntryModel build()
	{
		return getModel();
	}

	public ConsignmentEntryModelBuilder withQuantity(final Long quantity)
	{
		getModel().setQuantity(quantity);
		return this;
	}

	public ConsignmentEntryModelBuilder withConsignment(final ConsignmentModel consignment)
	{
		getModel().setConsignment(consignment);
		return this;
	}

	public ConsignmentEntryModelBuilder withOrderEntry(final AbstractOrderEntryModel entry)
	{
		getModel().setOrderEntry(entry);
		return this;
	}

}
