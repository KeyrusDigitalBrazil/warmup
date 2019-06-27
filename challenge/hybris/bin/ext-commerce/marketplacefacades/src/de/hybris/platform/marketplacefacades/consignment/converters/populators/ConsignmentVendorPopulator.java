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
package de.hybris.platform.marketplacefacades.consignment.converters.populators;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.VendorModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import org.springframework.beans.factory.annotation.Required;


/**
 * A populator for setting 'reviewable' of ConsignmentData
 */
public class ConsignmentVendorPopulator implements Populator<ConsignmentModel, ConsignmentData>
{

	private Converter<VendorModel, VendorData> vendorConverter;

	@Override
	public void populate(final ConsignmentModel source, final ConsignmentData target)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		if (source.getWarehouse() != null && source.getWarehouse().getVendor() != null)
		{
			target.setVendor(getVendorConverter().convert(source.getWarehouse().getVendor()));
		}
	}

	protected Converter<VendorModel, VendorData> getVendorConverter()
	{
		return vendorConverter;
	}

	@Required
	public void setVendorConverter(final Converter<VendorModel, VendorData> vendorConverter)
	{
		this.vendorConverter = vendorConverter;
	}

}
