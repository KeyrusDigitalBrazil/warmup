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


import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.core.common.message.Message;
import de.hybris.platform.sap.core.common.message.MessageList;
import de.hybris.platform.sap.sapmodel.services.SalesAreaService;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.OrderMgmtMessage;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.factory.ItemFactory;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;



/**
 * Basic cart functions for SAP synchronous order management. In this case, the cart will be created in the back end
 * session, it does not touch the hybris persistence.<br>
 * The class synchronizes accesses to the BOL object representing the cart, as this is not thread safe. Multi-threaded
 * accesses can happen although we use request sequencing, since also filters might call cart facades.
 */
public class DefaultCartService extends DefaultCartCheckoutBaseService implements CartService
{
	private static final Logger LOG = Logger.getLogger(DefaultCartService.class);
	private static final String DEFAULT_TEXT_NOT_FOUND_MSG = "text for key not found:";

	private static final String SAP_INFO_STATUS = "sapInfo";

	private Converter<Item, OrderEntryData> cartItemConverter;
	private Converter<Message, CartModificationData> messageConverter;

	SalesAreaService salesAreaService;

	SessionService sessionService;

	private ItemFactory itemFactory;

	@Override
	public CartModificationData addToCart(final String code, final long quantity)
	{
		final Basket currentCart = getBolCartFacade().getCart();
		synchronized (currentCart)
		{

			final Item newItem = getBolCartFacade().addToCart(code, quantity);


			final CartModificationData cartModificationData = new CartModificationData();
			cartModificationData.setQuantity(newItem.getQuantity().longValue());
			cartModificationData.setQuantityAdded(quantity);

			final OrderEntryData cartEntryModel = getCartItemConverter().convert(newItem);
			cartEntryModel.setQuantity(Long.valueOf(cartModificationData.getQuantityAdded()));
			cartModificationData.setEntry(cartEntryModel);

			//Now do validation and add first message, if existing, to the result
			final List<CartModificationData> cartMessages = validateCartData();

			if (!CollectionUtils.isEmpty(cartMessages))
			{
				final CartModificationData message = cartMessages.get(0);
				cartModificationData.setStatusCode(message.getStatusCode());
				cartModificationData.setStatusMessage(message.getStatusMessage());
			}


			return cartModificationData;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.sap.sapordermgmtservices.cart.CartService#updateCartEntry(long, long)
	 */
	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final long quantityAsLong)
	{
		final Basket currentCart = getBolCartFacade().getCart();
		synchronized (currentCart)
		{

			final int number = convertToPositiveInt(entryNumber);
			final BigDecimal quantity = convertQuantity(quantityAsLong);
			final Item itemToUpdate = getBolCartFacade().getCartItem(number);
			if (itemToUpdate == null)
			{
				throw new ApplicationBaseRuntimeException("Could not find item for quantity update, number: " + entryNumber);
			}
			final long oldQuantity = itemToUpdate.getQuantity().longValue();
			itemToUpdate.setQuantity(quantity);
			checkForDeletion(quantity, itemToUpdate);

			getBolCartFacade().updateCart();


			// Return the modification data
			final CartModificationData modification = new CartModificationData();
			final Item updatedItem = getBolCartFacade().getCartItem(number);
			if (updatedItem != null)
			{

				final OrderEntryData cartEntryModel = getCartItemConverter().convert(updatedItem);

				modification.setEntry(cartEntryModel);
			}
			modification.setQuantity(quantity.longValue());
			modification.setQuantityAdded(quantity.longValue() - oldQuantity);

			modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);

			return modification;
		}

	}

	/**
	 * @param entryNumber
	 * @return Input converted into positive int
	 */
	protected int convertToPositiveInt(final long entryNumber)
	{
		final int number = new BigDecimal(entryNumber).intValueExact();
		if (number < 0)
		{
			throw new ApplicationBaseRuntimeException("quantity must not be negative");
		}
		return number;
	}

	/**
	 * Checks if cart item is meant to be deleted, which is indicated by a quantity zero. In this case, the BOL item will be
	 * marked to be deleted
	 *
	 * @param quantity
	 *           New item quantity, if zero: Item will be deleted
	 * @param itemToUpdate
	 *           BOL item to be updated
	 */
	protected void checkForDeletion(final BigDecimal quantity, final Item itemToUpdate)
	{
		if (quantity.compareTo(BigDecimal.ZERO) == 0)
		{
			itemToUpdate.setProductId("");
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Item will be deleted");
			}
		}
	}

	/**
	 * Converts a quantity into a BigDecimal so that BOL can consume it.
	 *
	 * @param quantity
	 * @return Quantity as result from the input, if input is positive. Zero otherwise
	 */
	protected BigDecimal convertQuantity(final long quantity)
	{
		BigDecimal qty = BigDecimal.ZERO;
		if (quantity > 0)
		{
			qty = new BigDecimal(quantity);
		}
		return qty;
	}


	@Override
	public List<CartModificationData> validateCartData()
	{
		final List<CartModificationData> modifications = new ArrayList<>();
		final List<CartModificationData> modificationsinfo = new ArrayList<>();
		final MessageList cartErrors = getBolCartFacade().validateCart();
		for (final Message cartError : cartErrors)
		{
			//Don't add messages that are assigned explicitly to checkout
			if (cartError instanceof OrderMgmtMessage)
			{
				final OrderMgmtMessage orderMgmtMessage = (OrderMgmtMessage) cartError;
				if ("CH".equals(orderMgmtMessage.getProcessStep()))
				{
					continue;
				}
			}

			// Don't add messages if text are not found for them
			final String errorMessage = cartError.getMessageText();
			if (!(StringUtils.isNotEmpty(errorMessage) && errorMessage.startsWith(DEFAULT_TEXT_NOT_FOUND_MSG)))
			{
				modifications.add(messageConverter.convert(cartError));
			}

		}
		CartModificationData cartModificationData = new CartModificationData();

		for (final CartModificationData modificationData : modifications)
		{
			if (modificationData.getStatusCode().equals(SAP_INFO_STATUS))
			{
				cartModificationData = modificationData;
				modificationsinfo.add(modificationData);

			}
		}


		modifications.remove(cartModificationData);

		getSessionService().setAttribute("validations", modificationsinfo);
		return modifications;
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
	 * @return the cartItemConverter
	 */
	public Converter<Item, OrderEntryData> getCartItemConverter()
	{
		return cartItemConverter;
	}

	/**
	 * @param cartItemConverter
	 *           the cartItemConverter to set
	 */
	public void setCartItemConverter(final Converter<Item, OrderEntryData> cartItemConverter)
	{
		this.cartItemConverter = cartItemConverter;
	}

	/**
	 * @return the messageConverter
	 */
	public Converter<Message, CartModificationData> getMessageConverter()
	{
		return messageConverter;
	}

	/**
	 * @param messageConverter
	 *           the messageConverter to set
	 */
	public void setMessageConverter(final Converter<Message, CartModificationData> messageConverter)
	{
		this.messageConverter = messageConverter;
	}


	/**
	 * @return the salesAreaService
	 */
	public SalesAreaService getSalesAreaService()
	{
		return salesAreaService;
	}

	/**
	 * @param salesAreaService
	 *           the salesAreaService to set
	 */
	public void setSalesAreaService(final SalesAreaService salesAreaService)
	{
		this.salesAreaService = salesAreaService;
	}


	@Override
	public boolean isItemAvailable(final String itemKey)
	{
		final Basket currentCart = getBolCartFacade().getCart();
		synchronized (currentCart)
		{
			final Item item = currentCart.getItem(new TechKey(itemKey));
			return item != null;
		}
	}


	@Override
	public void addItemsToCart(final List<Item> items)
	{
		getBolCartFacade().addItemsToCart(items);
	}

	@Override
	public List<CartModificationData> addEntriesToCart(final List<OrderEntryData> orderEntries)
	{
		final List<CartModificationData> modificationDataList = new ArrayList<>();
		final List<Item> updatedItems = getBolCartFacade().addEntriesToCart(createItemsFromOrderEntries(orderEntries));

		for (final Item item : updatedItems)
		{
			final OrderEntryData cartEntryModel = getCartItemConverter().convert(item);
			final CartModificationData cartModificationData = new CartModificationData();

			cartModificationData.setEntry(cartEntryModel);
			cartModificationData.setQuantityAdded(cartEntryModel.getQuantity());
			cartModificationData.setQuantity(cartEntryModel.getQuantity());
			cartModificationData.setStatusCode(CommerceCartModificationStatus.SUCCESS);
			//might need to change this (messages)
			final List<CartModificationData> cartMessages = validateCartData();

			if (!CollectionUtils.isEmpty(cartMessages))
			{
				final CartModificationData message = cartMessages.get(0);
				cartModificationData.setStatusCode(message.getStatusCode());
				cartModificationData.setStatusMessage(message.getStatusMessage());
			}

			modificationDataList.add(cartModificationData);
		}


		return modificationDataList;
	}

	protected List<Item> createItemsFromOrderEntries(final List<OrderEntryData> cartEntries)
	{
		final List<Item> items = new ArrayList<>();
		for (final OrderEntryData entry : cartEntries)
		{

			final Item item = createItem(entry);

			items.add(item);

		}
		return items;
	}

	private Item createItem(final OrderEntryData orderEntry)
	{
		final String productId = orderEntry.getProduct().getCode();
		final Item item = getItemFactory().createItem();
		item.setProductId(productId);
		item.setQuantity(new BigDecimal(orderEntry.getQuantity().longValue()));
		return item;
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
