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
package de.hybris.platform.selectivecartfacades.populators;

import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.selectivecartfacades.data.Wishlist2Data;
import de.hybris.platform.selectivecartfacades.data.Wishlist2EntryData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Populates {@link Wishlist2Model} to {@link Wishlist2Data}
 */
public class WishlistForSelectiveCartPopulator implements Populator<Wishlist2Model, Wishlist2Data>
{
	private Converter<Wishlist2EntryModel, Wishlist2EntryData> wishlistEntryConverter;
	private Converter<PrincipalModel, PrincipalData> principalConverter;

	@Override
	public void populate(final Wishlist2Model source, final Wishlist2Data target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setName(source.getName());
		target.setUser(getPrincipalConverter().convert(source.getUser()));
		target.setEntries(getWishlistEntryConverter().convertAll(source.getEntries()));
	}

	protected Converter<Wishlist2EntryModel, Wishlist2EntryData> getWishlistEntryConverter()
	{
		return wishlistEntryConverter;
	}

	@Required
	public void setWishlistEntryConverter(final Converter<Wishlist2EntryModel, Wishlist2EntryData> wishlistEntryConverter)
	{
		this.wishlistEntryConverter = wishlistEntryConverter;
	}

	protected Converter<PrincipalModel, PrincipalData> getPrincipalConverter()
	{
		return principalConverter;
	}

	@Required
	public void setPrincipalConverter(final Converter<PrincipalModel, PrincipalData> principalConverter)
	{
		this.principalConverter = principalConverter;
	}

}
