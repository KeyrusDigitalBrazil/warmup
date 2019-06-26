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

import de.hybris.platform.commerceservices.order.CommerceCartHashCalculationStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCartHashCalculationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceOrderParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;


/**
 * Default implementation of the {@link CommerceCartHashCalculationStrategy}
 */
public class DefaultCisCartHashCalculationStrategy implements CommerceCartHashCalculationStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCommerceCartHashCalculationStrategy.class);
	private static final String HASH_ALGORITHM = "SHA-256";

	@Override
	@Deprecated
	public String buildHashForAbstractOrder(final AbstractOrderModel abstractOrderModel, final List<String> additionalValues)
	{
		final CommerceOrderParameter parameter = new CommerceOrderParameter();
		parameter.setOrder(abstractOrderModel);
		parameter.setAdditionalValues(additionalValues);
		return this.buildHashForAbstractOrder(parameter);
	}

	@Override
	public String buildHashForAbstractOrder(final CommerceOrderParameter parameter)
	{
		final AbstractOrderModel abstractOrderModel = parameter.getOrder();
		final List<String> additionalValues = parameter.getAdditionalValues();

		final StringBuilder orderValues = new StringBuilder();

		orderValues.append(abstractOrderModel.getItemtype());

		appendDeliveryAddress(abstractOrderModel.getDeliveryAddress(), orderValues);

		if (abstractOrderModel.getDeliveryMode() != null)
		{
			orderValues.append(abstractOrderModel.getDeliveryMode().getCode());
		}

		if (abstractOrderModel.getCurrency() != null)
		{
			orderValues.append(abstractOrderModel.getCurrency().getIsocode());
		}

		if (abstractOrderModel.getNet() != null)
		{
			orderValues.append((abstractOrderModel.getNet().toString()));
		}

		if (abstractOrderModel.getDate() != null)
		{
			orderValues.append(abstractOrderModel.getDate().getTime());
		}

		abstractOrderModel.getEntries()
				.forEach(abstractOrderEntryModel -> orderValues.append(buildHashForAbstractOrderEntry(abstractOrderEntryModel)));

		if (additionalValues != null)
		{
			additionalValues.forEach(additionalValue -> orderValues.append(additionalValue));
		}

		final String orderValue = orderValues.toString();
		try
		{
			final MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(orderValue.getBytes());
			return String.valueOf(Hex.encode(digest.digest()));
		}
		catch (final NoSuchAlgorithmException e)
		{
			LOG.error("NoSuchAlgorithmException while computing the order hash. This should never happen.", e);
		}
		return orderValue;

	}

	/**
	 * Calculate a hash for the {@link AbstractOrderEntryModel}
	 *
	 * @return the calculated hash
	 */
	protected String buildHashForAbstractOrderEntry(final AbstractOrderEntryModel abstractOrderEntryModel)
	{
		final StringBuilder entryValues = new StringBuilder();

		entryValues.append(abstractOrderEntryModel.getTotalPrice().toString());
		entryValues.append(abstractOrderEntryModel.getProduct().getCode());
		entryValues.append(abstractOrderEntryModel.getQuantity().toString());

		if (abstractOrderEntryModel.getDeliveryMode() != null)
		{
			entryValues.append(abstractOrderEntryModel.getDeliveryMode().getCode());
		}

		appendDeliveryAddress(abstractOrderEntryModel.getDeliveryAddress(), entryValues);

		if (abstractOrderEntryModel.getDeliveryPointOfService() != null
				&& abstractOrderEntryModel.getDeliveryPointOfService().getAddress() != null)
		{
			entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getLine1());
			entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getLine2());
			entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getTown());
			if (abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getRegion() != null)
			{
				entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getRegion().getIsocode());
			}
			if (abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getCountry() != null)
			{
				entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getCountry().getIsocode());
			}
			entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getDistrict());
			entryValues.append(abstractOrderEntryModel.getDeliveryPointOfService().getAddress().getPostalcode());
		}


		return entryValues.toString();
	}

	/**
	 * Appends the given {@link StringBuilder} with the given {@link AddressModel}
	 *
	 * @param address
	 * 		the delivery address that you are looking to append to the {@link StringBuilder}
	 * @param stringBuilder
	 * 		the {@link StringBuilder}
	 */
	protected void appendDeliveryAddress(final AddressModel address, final StringBuilder stringBuilder)
	{
		if (address != null)
		{
			stringBuilder.append(address.getLine1());
			stringBuilder.append(address.getLine2());
			stringBuilder.append(address.getTown());
			if (address.getRegion() != null)
			{
				stringBuilder.append(address.getRegion().getIsocode());
			}
			if (address.getCountry() != null)
			{
				stringBuilder.append(address.getCountry().getIsocode());
			}
			stringBuilder.append(address.getDistrict());
			stringBuilder.append(address.getPostalcode());
		}
	}

}
