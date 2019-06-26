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

import de.hybris.platform.cms2.servicelayer.data.impl.DefaultCMSDataFactory;
import de.hybris.platform.marketplaceservices.data.MarketplaceCMSDataFactory;
import de.hybris.platform.marketplaceservices.data.MarketplaceRestrictionData;
import de.hybris.platform.ordersplitting.model.VendorModel;

public class MarketplaceCMSDataFactoryImpl extends DefaultCMSDataFactory implements MarketplaceCMSDataFactory
{

	@Override
	public MarketplaceRestrictionData createRestrictionData(final VendorModel vendor)
	{
		final MarketplaceRestrictionDataImpl data = new MarketplaceRestrictionDataImpl();
		data.setVendor(vendor);
		return data;
	}

}
