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
package de.hybris.platform.marketplaceservices.dataimport.batch.util;

import de.hybris.platform.marketplaceservices.vendor.daos.VendorDao;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.io.File;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Strategy to extract the catalog of a vendor from a file name.
 */
public class VendorProductCatalogParser
{
	private VendorDao vendorDao;

	public String getVendorCatalog(final File file)
	{
		final String vendorCode = DataIntegrationUtils.resolveVendorCode(file);
		final Optional<VendorModel> option = vendorDao.findVendorByCode(vendorCode);
		return option.map(x -> x.getCatalog().getId())
				.orElseThrow(() -> new IllegalArgumentException("Cannot find vendor in " + file.getPath()));
	}

	@Required
	public void setVendorDao(VendorDao vendorDao)
	{
		this.vendorDao = vendorDao;
	}

	protected VendorDao getVendorDao()
	{
		return vendorDao;
	}

}
