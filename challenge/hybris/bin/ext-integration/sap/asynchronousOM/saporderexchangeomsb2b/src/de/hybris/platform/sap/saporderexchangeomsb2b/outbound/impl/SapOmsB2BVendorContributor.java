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
package de.hybris.platform.sap.saporderexchangeomsb2b.outbound.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.orderexchange.outbound.RawItemContributor;
import de.hybris.platform.sap.saporderexchangeoms.outbound.impl.SapOmsVendorContributor;

import java.util.List;
import java.util.Map;


/**
 * Partner contributor for B2B orders to be replicated to SAP ERP system. Considers partner roles soldTo (AG), contact
 * (AP), billTo (RE), shipTo (WE) and vendor (LF)
 *
 */
public class SapOmsB2BVendorContributor extends SapOmsVendorContributor
{

	private RawItemContributor<OrderModel> b2bPartnerContributor;

	@Override
	public List<Map<String, Object>> createRows(final OrderModel order)
	{
		final List<Map<String, Object>> result = getB2bPartnerContributor().createRows(order);

		super.enhanceWithVendorRows(order, result);

		return result;

	}

	/**
	 * @return the b2bPartnerContributor
	 */
	public RawItemContributor<OrderModel> getB2bPartnerContributor()
	{
		return b2bPartnerContributor;
	}

	/**
	 * @param b2bPartnerContributor
	 *           the b2bPartnerContributor to set
	 */
	public void setB2bPartnerContributor(final RawItemContributor<OrderModel> b2bPartnerContributor)
	{
		this.b2bPartnerContributor = b2bPartnerContributor;
	}




}
