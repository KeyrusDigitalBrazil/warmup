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
package de.hybris.platform.commerceservices.util.builder;

import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;
import de.hybris.platform.core.model.order.CartModel;

import java.util.Date;
import java.util.Optional;


/**
 * Builder for {@link CommerceCartMetadataParameter}. When the new instance of {@link CommerceCartMetadataParameter} is
 * created, {@link java.util.Optional} based attributes are set to Optional.empty().
 */
public class CommerceCartMetadataParameterBuilder
{
	private final CommerceCartMetadataParameter parameter;

	public CommerceCartMetadataParameterBuilder()
	{
		parameter = new CommerceCartMetadataParameter();

		parameter.setName(Optional.empty());
		parameter.setDescription(Optional.empty());
		parameter.setExpirationTime(Optional.empty());
	}

	public CommerceCartMetadataParameter build()
	{
		return parameter;
	}

	public CommerceCartMetadataParameterBuilder name(final Optional<String> name)
	{
		parameter.setName(name);
		return this;
	}

	public CommerceCartMetadataParameterBuilder description(final Optional<String> description)
	{
		parameter.setDescription(description);
		return this;
	}

	public CommerceCartMetadataParameterBuilder expirationTime(final Optional<Date> expirationTime)
	{
		parameter.setExpirationTime(expirationTime);
		return this;
	}

	public CommerceCartMetadataParameterBuilder removeExpirationTime(final boolean removeExpirationTime)
	{
		parameter.setRemoveExpirationTime(removeExpirationTime);
		return this;
	}

	public CommerceCartMetadataParameterBuilder cart(final CartModel cart)
	{
		parameter.setCart(cart);
		return this;
	}

	public CommerceCartMetadataParameterBuilder enableHooks(final boolean enableHooks)
	{
		parameter.setEnableHooks(enableHooks);
		return this;
	}
}
