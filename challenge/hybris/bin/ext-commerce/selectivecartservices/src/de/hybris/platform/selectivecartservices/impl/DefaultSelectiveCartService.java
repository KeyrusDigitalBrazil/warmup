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
package de.hybris.platform.selectivecartservices.impl;

import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.selectivecartservices.SelectiveCartService;
import de.hybris.platform.selectivecartservices.daos.SelectiveCartDao;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.wishlist2.Wishlist2Service;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link SelectiveCartService}
 */
public class DefaultSelectiveCartService implements SelectiveCartService
{
	private static final String WISHLIST_FOR_SELECTIVE_CART = "WishlistForSelectiveCart";

	private SelectiveCartDao selectiveCartDao;
	private ModelService modelService;
	private Wishlist2Service wishlistService;

	@Override
	public Wishlist2Model getWishlistForSelectiveCart()
	{
		try
		{
			return getSelectiveCartDao().findWishlistByName(getCurrentUser(), WISHLIST_FOR_SELECTIVE_CART);
		}
		catch (final ModelNotFoundException e) //NOSONAR
		{
			return null;
		}
	}

	@Override
	public Wishlist2EntryModel getWishlistEntryForProduct(final ProductModel product)
	{
		final Wishlist2Model wishlist = getWishlistForSelectiveCart();
		if (wishlist != null)
		{
			try
			{
				return getWishlistService().getWishlistEntryForProduct(product, wishlist);
			}
			catch (final UnknownIdentifierException e) //NOSONAR
			{
				return null;
			}
		}
		return null;
	}

	@Override
	public Wishlist2EntryModel getWishlistEntryForProduct(final ProductModel product, final Wishlist2Model wishlist)
	{
		if (wishlist != null)
		{
			try
			{
				return getWishlistService().getWishlistEntryForProduct(product, wishlist);
			}
			catch (final UnknownIdentifierException e) //NOSONAR
			{
				return null;
			}
		}
		return null;
	}

	@Override
	public void removeWishlistEntryForProduct(final ProductModel product, final Wishlist2Model wishlist)
	{
		getWishlistService().removeWishlistEntryForProduct(product, wishlist);
		if (wishlist.getEntries().isEmpty())
		{
			getModelService().remove(wishlist);
		}
	}

	@Override
	public void updateQuantityForWishlistEntry(final Wishlist2EntryModel wishlistEntry, final Integer quantity)
	{
		wishlistEntry.setQuantity(quantity);
		getModelService().save(wishlistEntry);
	}

	@Override
	public Wishlist2Model createWishlist()
	{
		return getWishlistService().createWishlist(WISHLIST_FOR_SELECTIVE_CART,
				"Special wishlist for selective cart for current user");
	}

	@Override
	public Wishlist2EntryModel saveWishlistEntryForProduct(final ProductModel product, final Wishlist2Model wishlist,
			final Date addToCartTime)
	{
		try
		{
			return getWishlistService().getWishlistEntryForProduct(product, wishlist);
		}
		catch (final UnknownIdentifierException e) //NOSONAR
		{
			final Wishlist2EntryModel entry = new Wishlist2EntryModel();
			entry.setWishlist(wishlist);
			entry.setProduct(product);
			entry.setAddedDate(Calendar.getInstance().getTime());
			entry.setPriority(Wishlist2EntryPriority.MEDIUM);
			entry.setAddToCartTime(addToCartTime);
			getWishlistService().addWishlistEntry(wishlist, entry);
			return entry;
		}
	}

	@Override
	public void updateCartTimeForOrderEntry(final String cartCode, final int entryNumber, final Date addToCartTime)
	{
		final CartEntryModel cartEntry = getSelectiveCartDao().findCartEntryByCartCodeAndEntryNumber(cartCode, entryNumber);
		cartEntry.setAddToCartTime(addToCartTime);
		getModelService().save(cartEntry);
	}

	protected UserModel getCurrentUser()
	{
		return  getModelService().get(JaloSession.getCurrentSession().getUser());
	}

	protected SelectiveCartDao getSelectiveCartDao()
	{
		return selectiveCartDao;
	}

	@Required
	public void setSelectiveCartDao(final SelectiveCartDao selectiveCartDao)
	{
		this.selectiveCartDao = selectiveCartDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected Wishlist2Service getWishlistService()
	{
		return wishlistService;
	}

	@Required
	public void setWishlistService(final Wishlist2Service wishlistService)
	{
		this.wishlistService = wishlistService;
	}

}
