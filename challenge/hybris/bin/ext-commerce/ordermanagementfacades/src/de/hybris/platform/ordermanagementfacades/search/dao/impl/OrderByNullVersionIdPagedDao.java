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
package de.hybris.platform.ordermanagementfacades.search.dao.impl;

import de.hybris.platform.commerceservices.search.dao.impl.DefaultPagedGenericDao;
import de.hybris.platform.core.model.order.OrderModel;


/**
 * OrderByNullVersionIdPagedDao is a specific dao that will get all orders that do not have a {@link de.hybris.platform.core.model.order.OrderModel#VERSIONID}.
 * Orders that have a VersionId are snapshots and should be discarded.
 */
public class OrderByNullVersionIdPagedDao extends DefaultPagedGenericDao
{
	protected final String typeCode;

	public OrderByNullVersionIdPagedDao(final String typeCode)
	{
		super(typeCode);
		this.typeCode = typeCode;
	}

	@Override
	protected StringBuilder createQueryString()
	{
		final StringBuilder builder = new StringBuilder(25);
		builder.append("SELECT {c:").append(OrderModel.PK).append("} ");
		builder.append("FROM {").append(typeCode).append(" AS c} ");
		builder.append("WHERE {c:").append(OrderModel.VERSIONID).append("} IS NULL ");
		return builder;
	}
}
