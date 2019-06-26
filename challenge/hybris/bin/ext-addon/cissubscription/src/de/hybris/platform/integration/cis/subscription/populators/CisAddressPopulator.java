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
package de.hybris.platform.integration.cis.subscription.populators;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.hybris.cis.api.model.CisAddress;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Populate the CisAddress with the AddressData information
 */
public class CisAddressPopulator implements Populator<AddressData, CisAddress>
{
	@Override
	public void populate(final AddressData source, final CisAddress target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("target", target);

		if (source == null)
		{
			return;
		}

		target.setCompany(source.getCompanyName());
		if (source.getCountry() != null)
		{
			target.setCountry(source.getCountry().getIsocode() == null ? source.getCountry().getName() : source.getCountry()
					.getIsocode());
		}
		target.setEmail(source.getEmail());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setAddressLine1(source.getLine1());
		target.setAddressLine2(source.getLine2());
		target.setPhone(source.getPhone());
		target.setZipCode(source.getPostalCode());
		if (source.getRegion() != null)
		{
			target.setState(source.getRegion().getName());
		}
		target.setCity(source.getTown());
	}
}
