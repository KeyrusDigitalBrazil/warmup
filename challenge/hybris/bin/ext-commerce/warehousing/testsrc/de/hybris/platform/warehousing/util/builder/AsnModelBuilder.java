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


public class AsnModelBuilder
{
	private final AdvancedShippingNoticeModel model;

	private AsnModelBuilder()
	{
		model = new AdvancedShippingNoticeModel();
	}

	private AdvancedShippingNoticeModel getModel()
	{
		return this.model;
	}

	public static AsnModelBuilder aModel()
	{
		return new AsnModelBuilder();
	}

	public AdvancedShippingNoticeModel build()
	{
		return getModel();
	}

	public AsnModelBuilder withExternalId(final String externalId)
	{
		getModel().setExternalId(externalId);
		return this;
	}

	public AsnModelBuilder withInternalId(final String internalId)
	{
		getModel().setInternalId(internalId);
		return this;
	}

	public AsnModelBuilder withStatus(final AsnStatus status)
	{
		getModel().setStatus(status);
		return this;
	}

	public AsnModelBuilder withReleaseDate(final Date date)
	{
		getModel().setReleaseDate(date);
		return this;
	}

	public AsnModelBuilder withWarehouse(final WarehouseModel warehouse)
	{
		getModel().setWarehouse(warehouse);
		return this;
	}

	public AsnModelBuilder withPoS(final PointOfServiceModel pos)
	{
		getModel().setPointOfService(pos);
		return this;
	}

	public AsnModelBuilder withComments(final List<CommentModel> comments)
	{
		getModel().setComments(comments);
		return this;
	}

	public AsnModelBuilder withAsnEntries(final List<AdvancedShippingNoticeEntryModel> asnEntries)
	{
		getModel().setAsnEntries(asnEntries);
		return this;
	}
}
