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
package ywebservicespackage.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import ywebservicespackage.data.AddressData;
import ywebservicespackage.data.UserData;



/**
 *
 *
 */
public class SampleUserPopulator implements Populator<UserModel, UserData>
{
	private Converter<AddressModel, AddressData> addressConverter;

	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	public Converter<AddressModel, AddressData> getAddressConverter()
	{
		return this.addressConverter;
	}

	@Override
	public void populate(final UserModel source, final UserData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setDescription(source.getDescription());
		if (source.getName() != null)
		{
			final String[] names = source.getName().split(" ", 2);
			target.setFirstName(names[0]);
			if (names.length > 1)
			{
				target.setLastName(names[1]);
			}
		}

		final List<AddressData> addresses = source.getAddresses().stream().map(a -> getAddressConverter().convert(a))
				.collect(Collectors.toList());
		target.setAddresses(addresses);

		addresses.stream().filter(a -> a.isBillingAddress()).findFirst().ifPresent(a -> target.setDefaultBillingAddress(a));
		addresses.stream().filter(a -> a.isShippingAddress()).findFirst().ifPresent(a -> target.setDefaultShippingAddress(a));
	}
}
