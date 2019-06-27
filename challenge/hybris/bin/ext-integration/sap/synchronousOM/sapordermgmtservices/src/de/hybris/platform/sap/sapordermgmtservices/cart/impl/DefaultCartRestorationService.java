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
package de.hybris.platform.sap.sapordermgmtservices.cart.impl;

import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.order.CartFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartRestorationService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory;
import de.hybris.platform.sap.sapordermgmtservices.hook.CartRestorationServiceHook;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.session.SessionService.SessionAttributeLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 * Default implementation of the restoration service. Task of this class is to create a BOL representation of the cart
 * (which has a counterpart in the SAP back end) from a persisted hybris cart.
 */
public class DefaultCartRestorationService implements CartRestorationService
{
	private static final Logger LOG = Logger.getLogger(DefaultCartRestorationService.class.getName());

	private ModelService modelService;
	private CartFactory cartFactory;
	private SessionService sessionService;

	/**
	 * Hybris session cart is stored in hybris session using this key
	 */
	protected static String SESSION_CART_PARAMETER_NAME = "cart";
	private CartService cartService;

	private List<CartRestorationServiceHook> cartRestorationServiceHooks;
	private ItemFactory itemFactory;



	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sapordermgmtservices.cart.CartServiceRestauration#restoreCart(java.lang.String)
	 */
	@Override
	public CartRestorationData restoreCart(final CartModel fromCart)
	{
		final CartRestorationData restorationData = new CartRestorationData();
		if (fromCart != null && fromCart.getEntries() != null && !fromCart.getEntries().isEmpty())
		{
			final List<Item> items = createItemsFromCart(fromCart);
			this.getCartService().addItemsToCart(items);
			setInternalSessionCart(fromCart);
		}

		return restorationData;
	}

	/**
	 * Creates a list of BOL items from a hybris cart model. Those cart items carry following attributes copied from the
	 * entities of type {@link AbstractOrderEntryModel}:
	 * <li>ProductId
	 * <li>Quantity
	 *
	 * @param fromCart
	 *           Cart model
	 * @return List of BOL items
	 */
	protected List<Item> createItemsFromCart(final CartModel fromCart)
	{
		final List<Item> items = new ArrayList<Item>();
		for (final AbstractOrderEntryModel entry : fromCart.getEntries())
		{

			final Item item = createItem(entry);

			items.add(item);

		}
		return items;
	}

	private Item createItem(final AbstractOrderEntryModel orderEntry)
	{
		final String productId = orderEntry.getProduct().getCode();
		final Item item = getItemFactory().createItem();
		item.setProductId(productId);
		item.setQuantity(new BigDecimal(orderEntry.getQuantity().longValue()));
		afterCreateItemHook(orderEntry, item);
		return item;
	}

	/**
	 * @param orderEntry
	 * @param item
	 */
	private void afterCreateItemHook(final AbstractOrderEntryModel orderEntry, final Item item)
	{
		if (getCartRestorationServiceHooks() != null)
		{
			for (final CartRestorationServiceHook defaultCartRestorationServiceHook : getCartRestorationServiceHooks())
			{
				defaultCartRestorationServiceHook.afterCreateItemHook(orderEntry, item);
			}
		}
	}



	@Override
	public void setInternalSessionCart(final CartModel cart)
	{
		if (cart == null)
		{
			removeInternalSessionCart();
		}
		else
		{
			getSessionService().setAttribute(SESSION_CART_PARAMETER_NAME, cart);
		}
	}

	@Override
	public CartModel getInternalSessionCart()
	{
		try
		{
			return internalGetSessionCart();
		}
		catch (final JaloObjectNoLongerValidException ex)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("Session Cart no longer valid. Removing from session. getSessionCart will create a new cart. " + ex);
			}
			getSessionService().removeAttribute(SESSION_CART_PARAMETER_NAME);
			return internalGetSessionCart();
		}

	}

	CartModel internalGetSessionCart()
	{
		final CartModel cart = getSessionService().getOrLoadAttribute(SESSION_CART_PARAMETER_NAME,
				new SessionAttributeLoader<CartModel>()
				{
					@Override
					public CartModel load()
					{
						return cartFactory.createCart();
					}
				});
		return cart;
	}




	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sapordermgmtservices.cart.CartServicePersistAndRestore#hasInternalSessionCart()
	 */
	@Override
	public boolean hasInternalSessionCart()
	{
		try
		{
			return getSessionService().getAttribute(SESSION_CART_PARAMETER_NAME) != null;
		}
		catch (final JaloObjectNoLongerValidException ex)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("Session Cart no longer valid. Removing from session. hasSessionCart will return false. " + ex);
			}
			getSessionService().removeAttribute(SESSION_CART_PARAMETER_NAME);
			return false;
		}
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sapordermgmtservices.cart.CartServicePersistAndRestore#removeInternalSessionCart()
	 */
	@Override
	public void removeInternalSessionCart()
	{
		if (hasInternalSessionCart())
		{
			final CartModel sessionCart = getInternalSessionCart();
			getModelService().remove(sessionCart);
			getSessionService().removeAttribute(SESSION_CART_PARAMETER_NAME);
		}

	}

	/**
	 * @return Standard session service (needed to access session cart and references to product configuration runtime
	 *         objects attached to cart entries)
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * Sets standard session service via spring injection
	 *
	 * @param sessionService
	 */
	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/**
	 * @return Model service
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets standard model service via spring injection
	 *
	 * @param modelService
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Sets standard cart factory via spring injection
	 *
	 * @param cartFactory
	 */
	@Required
	public void setCartFactory(final CartFactory cartFactory)
	{
		this.cartFactory = cartFactory;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}


	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}


	/**
	 * @return the defaultCartRestorationServiceHook
	 */
	public List<CartRestorationServiceHook> getCartRestorationServiceHooks()
	{
		return cartRestorationServiceHooks;
	}

	/**
	 * @param defaultCartRestorationServiceHook
	 *           the defaultCartRestorationServiceHook to set
	 */
	public void setCartRestorationServiceHooks(
			final List<CartRestorationServiceHook> defaultCartRestorationServiceHook)
	{
		this.cartRestorationServiceHooks = defaultCartRestorationServiceHook;
	}

	/**
	 * @return the itemFactory
	 */
	public ItemFactory getItemFactory()
	{
		return itemFactory;
	}

	/**
	 * @param itemFactory
	 *           the itemFactory to set
	 */
	public void setItemFactory(final ItemFactory itemFactory)
	{
		this.itemFactory = itemFactory;
	}
}
