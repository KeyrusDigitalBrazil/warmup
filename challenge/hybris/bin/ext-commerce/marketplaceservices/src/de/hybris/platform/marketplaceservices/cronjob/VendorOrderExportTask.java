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
package de.hybris.platform.marketplaceservices.cronjob;

import de.hybris.platform.marketplaceservices.strategies.VendorOrderExportStrategy;


/**
 * A task to export vendor orders
 */
public class VendorOrderExportTask implements Runnable
{
	private final VendorOrderExportStrategy vendorOrderExportStrategy;
	private final String vendorCode;

	public VendorOrderExportTask(final VendorOrderExportStrategy vendorOrderexportStrategy, final String vendorCode)
	{
		this.vendorOrderExportStrategy = vendorOrderexportStrategy;
		this.vendorCode = vendorCode;
	}

	@Override
	public void run()
	{
		vendorOrderExportStrategy.exportOrdersForVendor(vendorCode);
	}

}
