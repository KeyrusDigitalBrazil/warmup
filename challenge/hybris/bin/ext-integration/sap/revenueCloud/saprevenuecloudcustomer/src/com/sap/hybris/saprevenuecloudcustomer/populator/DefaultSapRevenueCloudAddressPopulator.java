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
package com.sap.hybris.saprevenuecloudcustomer.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.sap.hybris.saprevenuecloudcustomer.dto.Address;


/**
 *
 */
public class DefaultSapRevenueCloudAddressPopulator implements Populator<AddressModel, Address>
{

	@Override
	public void populate(final AddressModel addressModel, final Address addressJson) throws ConversionException
	{
		if (addressModel.getOwner() instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) addressModel.getOwner();
			addressJson.setEmail(customer.getUid());
		}
		addressJson.setPhone(addressModel.getPhone1());
		addressJson.setHouseNumber(addressModel.getLine1());
		addressJson.setStreet(addressModel.getLine2());
		addressJson.setCity(addressModel.getTown());
		addressJson.setState(addressModel.getRegion() != null ? addressModel.getRegion().getIsocodeShort() : "");
		addressJson.setPostalCode(addressModel.getPostalcode());
		addressJson.setCountry(addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : "");
	}

}
