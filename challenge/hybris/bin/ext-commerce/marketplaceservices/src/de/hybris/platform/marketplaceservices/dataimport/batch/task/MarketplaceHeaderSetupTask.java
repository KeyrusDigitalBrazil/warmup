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
package de.hybris.platform.marketplaceservices.dataimport.batch.task;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.HeaderSetupTask;
import de.hybris.platform.marketplaceservices.dataimport.batch.util.DataIntegrationUtils;

import java.io.File;

import org.springframework.util.Assert;


/**
 * Override original task to add vendor folder after storeBaseDirectory
 */
public class MarketplaceHeaderSetupTask extends HeaderSetupTask
{
	@Override
	public BatchHeader execute(final File file)
	{
		Assert.notNull(file, "The file can not be null.");
		final BatchHeader result = super.execute(file);
		final String vendorCode = DataIntegrationUtils.resolveVendorCode(file);
		result.setStoreBaseDirectory(storeBaseDirectory + File.separator + vendorCode);
		return result;
	}
}
