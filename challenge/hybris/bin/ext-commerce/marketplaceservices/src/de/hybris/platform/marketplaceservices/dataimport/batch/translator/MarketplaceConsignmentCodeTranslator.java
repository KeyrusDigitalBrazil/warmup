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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 * Marketplace translator for consignment update. Validation of consignment code is performed before importing.
 */
public class MarketplaceConsignmentCodeTranslator extends AbstractValueTranslator
{
	private static final String VENDOR_SERVICE = "vendorService";

	private VendorService vendorService;

	@Override
	public String exportValue(final Object arg0)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Object importValue(final String consignmentCode, final Item item)
	{
		if (StringUtils.isBlank(consignmentCode))
		{
			throw new IllegalArgumentException("Consignment code is missing");
		}

		final String vendorCode = getColumnDescriptor().getDescriptorData().getModifier("vendor");
		final Optional<VendorModel> vendorOptional = getVendorService().getVendorForConsignmentCode(consignmentCode);
		if (vendorOptional.isPresent() && vendorCode.equals(vendorOptional.get().getCode()))
		{
			return consignmentCode;
		}
		else
		{
			throw new IllegalArgumentException("Invalid vendor for consignment code " + consignmentCode);
		}
	}

	@Override
	public void init(final StandardColumnDescriptor descriptor)
	{
		super.init(descriptor);
		setVendorService((VendorService) Registry.getApplicationContext().getBean(VENDOR_SERVICE));
	}

	protected VendorService getVendorService()
	{
		return vendorService;
	}

	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}

}
