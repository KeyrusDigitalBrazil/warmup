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
package de.hybris.platform.marketplaceservices.dataimport.batch;

import org.springframework.beans.BeanUtils;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;



/**
 * Header containing all relevant process information for batch processing. This includes:
 */
public class MarketplaceBatchHeader extends BatchHeader
{
	private String vendorCode;
	private String taxGroup;

	public MarketplaceBatchHeader(final BatchHeader header)
	{
		BeanUtils.copyProperties(header, this);
	}

	public String getVendorCode()
	{
		return vendorCode;
	}

	public void setVendorCode(final String vendorCode)
	{
		this.vendorCode = vendorCode;
	}

	public String getTaxGroup()
	{
		return taxGroup;
	}

	public void setTaxGroup(final String taxGroup)
	{
		this.taxGroup = taxGroup;
	}

}
