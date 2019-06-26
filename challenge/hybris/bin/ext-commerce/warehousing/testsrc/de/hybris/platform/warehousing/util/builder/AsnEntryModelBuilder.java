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

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;

import java.util.Date;
import java.util.List;


public class AsnEntryModelBuilder
{
	private final AdvancedShippingNoticeEntryModel model;

	private AsnEntryModelBuilder()
	{
		model = new AdvancedShippingNoticeEntryModel();
	}

	private AdvancedShippingNoticeEntryModel getModel()
	{
		return this.model;
	}

	public static AsnEntryModelBuilder aModel()
	{
		return new AsnEntryModelBuilder();
	}

	public AdvancedShippingNoticeEntryModel build()
	{
		return getModel();
	}

	public AsnEntryModelBuilder withProductCode(final String productCode)
	{
		getModel().setProductCode(productCode);
		return this;
	}

	public AsnEntryModelBuilder withQuantity(final int qty)
	{
		getModel().setQuantity(qty);
		return this;
	}
}
