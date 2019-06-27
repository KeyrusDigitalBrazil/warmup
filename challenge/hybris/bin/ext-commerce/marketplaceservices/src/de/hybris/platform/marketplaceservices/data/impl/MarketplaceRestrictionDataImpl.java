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
package de.hybris.platform.marketplaceservices.data.impl;

import de.hybris.platform.cms2.servicelayer.data.impl.DefaultRestrictionData;
import de.hybris.platform.marketplaceservices.data.MarketplaceRestrictionData;
import de.hybris.platform.ordersplitting.model.VendorModel;

public class MarketplaceRestrictionDataImpl extends DefaultRestrictionData implements MarketplaceRestrictionData
{
	private static final long serialVersionUID = -2190700602976596400L;
	private VendorModel vendor;

	@Override
	public void setVendor(VendorModel vendor)
	{
		this.vendor = vendor;
	}

	@Override
	public VendorModel getVendor()
	{
		return this.vendor;
	}

}
