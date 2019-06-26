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
package de.hybris.platform.commercefacades.util.builder;

import de.hybris.platform.commercefacades.order.data.CommerceCartMetadata;

import java.util.Date;
import java.util.Optional;


/**
 * Builder for {@link CommerceCartMetadata}. When the new instance of {@link CommerceCartMetadata} is created,
 * {@link java.util.Optional} based attributes are set to Optional.empty().
 */
public class CommerceCartMetadataBuilder
{
	private final CommerceCartMetadata metadata;

	public CommerceCartMetadataBuilder()
	{
		metadata = new CommerceCartMetadata();

		metadata.setName(Optional.empty());
		metadata.setDescription(Optional.empty());
		metadata.setExpirationTime(Optional.empty());
	}

	public CommerceCartMetadata build()
	{
		return metadata;
	}

	public CommerceCartMetadataBuilder name(final Optional<String> name)
	{
		metadata.setName(name);
		return this;
	}

	public CommerceCartMetadataBuilder description(final Optional<String> description)
	{
		metadata.setDescription(description);
		return this;
	}

	public CommerceCartMetadataBuilder expirationTime(final Optional<Date> expirationTime)
	{
		metadata.setExpirationTime(expirationTime);
		return this;
	}

	public CommerceCartMetadataBuilder removeExpirationTime(final boolean removeExpirationTime)
	{
		metadata.setRemoveExpirationTime(removeExpirationTime);
		return this;
	}
}
