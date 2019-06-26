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
package de.hybris.platform.integration.cis.tax.strategies.impl;

import java.util.ArrayList;
import java.util.List;
import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisAddressType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.integration.cis.tax.strategies.CisShippingAddressStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default shipping address strategy to send the appropriate addresses to the tax service. The address is selected from the given order.
 * 
 */
public class DefaultCisShippingAddressStrategy implements CisShippingAddressStrategy
{
	private Converter<AddressModel, CisAddress> cisAddressConverter;

	@Override
	public List<CisAddress> getAddresses(final AbstractOrderModel abstractOrder)
	{
		final AddressModel deliveryAddressForOrder = abstractOrder.getDeliveryAddress();

		final List<CisAddress> addresses = new ArrayList<CisAddress>();

		if (deliveryAddressForOrder != null)
		{
			final CisAddress shipTo = getCisAddressConverter().convert(deliveryAddressForOrder);
			shipTo.setType(CisAddressType.SHIP_TO);
			addresses.add(shipTo);

			final CisAddress shipFrom = getCisAddressConverter().convert(deliveryAddressForOrder);
			shipFrom.setType(CisAddressType.SHIP_FROM);
			addresses.add(shipFrom);
		}

		return addresses;
	}

	protected Converter<AddressModel, CisAddress> getCisAddressConverter()
	{
		return cisAddressConverter;
	}

	@Required
	public void setCisAddressConverter(final Converter<AddressModel, CisAddress> cisAddressConverter)
	{
		this.cisAddressConverter = cisAddressConverter;
	}
}
