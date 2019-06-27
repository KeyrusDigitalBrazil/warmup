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
package de.hybris.platform.sap.sapordermgmtb2bfacades.order.impl;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtb2bfacades.ProductImageHelper;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtb2bfacades.hook.SapCartFacadeHook;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;
import de.hybris.platform.sap.sapordermgmtservices.constants.SapordermgmtservicesConstants;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;


/**
 * Implementation for {@link CartFacade}. Delivers main functionality for cart.
 */
public class DefaultSapCartFacade extends DefaultCartFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultSapCartFacade.class);



	private CartService sapCartService;

	private BackendAvailabilityService backendAvailabilityService;

	private BaseStoreService baseStoreService;

	private ProductImageHelper productImageHelper;

	private CartRestorationFacade cartRestorationFacade;

	private List<SapCartFacadeHook> sapCartFacadeHooks;

	private SessionService sessionService;

	private I18NService i18nService;

	private MessageSource messageSource;


	/**
	 * @return the i18nService
	 */
	public I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * @param i18nService
	 *           the i18nService to set
	 */
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	/**
	 * @return the messageSource
	 */
	public MessageSource getMessageSource()
	{
		return messageSource;
	}

	/**
	 * @param messageSource
	 *           the messageSource to set
	 */
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}



	@Override
	public boolean hasSessionCart()
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.hasSessionCart();
		}

		if (isBackendDown())
		{
			return super.hasSessionCart();
		}
		return getSapCartService().hasSessionCart();
	}

	@Override
	public CartData getMiniCart()
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.getSessionCart();
		}

		return getSessionCart();
	}

	@Override
	public CartData getSessionCart()
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.getSessionCart();
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCart");
		}
		if (isBackendDown())
		{
			addErrorMessages();

			final CartData cartWhenSessionDown = super.getSessionCart();
			cartWhenSessionDown.setBackendDown(true);
			cartWhenSessionDown.getTotalPrice().setFormattedValue("");
			return cartWhenSessionDown;
		}
		else
		{
			if (!isUserLoggedOn())
			{
				return createEmptyCart();
			}

			final CartData sessionCart = getSapCartService().getSessionCart();

			if (sessionCart.getTotalUnitCount() == null)
			{
				sessionCart.setTotalUnitCount(Integer.valueOf(0));
			}

			sessionCart.setCode(super.getSessionCart().getCode());
			productImageHelper.enrichWithProductImages(sessionCart);
			return sessionCart;
		}
	}

	@Override
	public CartData getSessionCartWithEntryOrdering(final boolean recentlyAddedFirst)
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.getSessionCartWithEntryOrdering(recentlyAddedFirst);
		}


		if (LOG.isDebugEnabled())
		{
			LOG.debug("getSessionCartWithEntryOrdering called with: " + recentlyAddedFirst);
		}
		if (isBackendDown())
		{
			final CartData cartWhenSessionDown = super.getSessionCartWithEntryOrdering(recentlyAddedFirst);
			cartWhenSessionDown.setBackendDown(true);
			cartWhenSessionDown.getTotalPrice().setFormattedValue("");
			return cartWhenSessionDown;
		}
		else
		{
			if (!isUserLoggedOn())
			{
				return createEmptyCart();
			}
			final CartData sessionCart = getSapCartService().getSessionCart(recentlyAddedFirst);
			sessionCart.setCode(super.getSessionCart().getCode());
			productImageHelper.enrichWithProductImages(sessionCart);
			return sessionCart;
		}
	}

	@Override
	public CartModificationData addToCart(final String code, final long quantity) throws CommerceCartModificationException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.addToCart(code, quantity);
		}


		if (isBackendDown())
		{
			addErrorMessages();

			final CartModificationData cartModificationBackendDown = super.addToCart(code, quantity);
			final OrderEntryData entry = cartModificationBackendDown.getEntry();
			entry.setBackendDown(true);
			return cartModificationBackendDown;
		}
		else
		{
			final CartModificationData cartModification = getSapCartService().addToCart(code, quantity);
			productImageHelper.enrichWithProductImages(cartModification.getEntry());

			if (getCartRestorationFacade() != null)
			{
				getCartRestorationFacade().setSavedCart(getSapCartService().getSessionCart());
			}

			return cartModification;
		}
	}

	/**
	 *
	 */
	private void addErrorMessages()
	{
		final List<CartModificationData> modifications = new ArrayList<>();
		final CartModificationData modificationData = new CartModificationData();

		final OrderEntryData entryMsg = new OrderEntryData();
		final ProductData product = new ProductData();
		product.setName(
				getMessageSource().getMessage("sap.checkout.backend.offline.message", null, getI18nService().getCurrentLocale()));
		entryMsg.setProduct(product);
		modificationData.setStatusMessage(
				getMessageSource().getMessage("sap.checkout.backend.offline.message", null, getI18nService().getCurrentLocale()));
		modificationData.setEntry(entryMsg);
		modificationData.setStatusCode(SapordermgmtservicesConstants.STATUS_SAP_ERROR);
		modifications.add(modificationData);

		getSessionService().setAttribute("validations", modifications);
	}

	@Override
	public CartModificationData addToCart(final String code, final long quantity, final String storeId)
			throws CommerceCartModificationException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.addToCart(code, quantity, storeId);
		}

		LOG.info("addToCart called with store ID, ignoring: " + storeId);
		return this.addToCart(code, quantity);
	}

	/**
	 * Add quick order entries to cart
	 *
	 * @param cartEntries
	 *           list of cart entries
	 * @return cart modification data
	 * @throws CommerceCartModificationException
	 *            exception
	 */
	public List<CartModificationData> addItemsToCart(final List<OrderEntryData> cartEntries)
			throws CommerceCartModificationException
	{
		List<CartModificationData> modificationDataList;
		modificationDataList = getSapCartService().addEntriesToCart(cartEntries);
		return modificationDataList;
	}

	@Override
	public List<CartModificationData> validateCartData() throws CommerceCartModificationException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.validateCartData();
		}

		return getSapCartService().validateCartData();
	}

	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final long quantity)
			throws CommerceCartModificationException
	{

		if (!isSyncOrdermgmtEnabled())
		{
			return super.updateCartEntry(entryNumber, quantity);
		}

		if (isBackendDown())
		{
			final List<OrderEntryData> entries = getSessionCart().getEntries();
			beforeCartEntryUpdate(quantity, entryNumber, entries);
			return super.updateCartEntry(entryNumber, quantity);
		}
		else
		{
			final CartModificationData cartModification = getSapCartService().updateCartEntry(entryNumber, quantity);

			if (this.cartRestorationFacade != null)
			{
				this.cartRestorationFacade.setSavedCart(getSapCartService().getSessionCart());
			}

			return cartModification;
		}
	}

	/**
	 * @param quantity
	 * @param entryNumber
	 * @param entries
	 */
	private void beforeCartEntryUpdate(final long quantity, final long entryNumber, final List<OrderEntryData> entries)
	{
		if (getSapCartFacadeHooks() != null)
		{
			for (final SapCartFacadeHook defaultSapCartFacadeHook : getSapCartFacadeHooks())
			{
				defaultSapCartFacadeHook.beforeCartEntryUpdate(quantity, entryNumber, entries);
			}
		}
	}

	protected boolean isSyncOrdermgmtEnabled()
	{
		return (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null)
				&& (getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled());
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}


	/**
	 * @return the sapCartService
	 */
	public CartService getSapCartService()
	{
		return sapCartService;
	}


	/**
	 * @param sapCartService
	 *           the sapCartService to set
	 */
	@Required
	public void setSapCartService(final CartService sapCartService)
	{
		this.sapCartService = sapCartService;
	}


	/**
	 * @return the backendAvailabilityService
	 */
	public BackendAvailabilityService getBackendAvailabilityService()
	{
		return backendAvailabilityService;
	}


	/**
	 * @param backendAvailabilityService
	 *           the backendAvailabilityService to set
	 */
	@Required
	public void setBackendAvailabilityService(final BackendAvailabilityService backendAvailabilityService)
	{
		this.backendAvailabilityService = backendAvailabilityService;
	}

	private boolean isUserLoggedOn()
	{
		final UserModel userModel = super.getUserService().getCurrentUser();
		return !super.getUserService().isAnonymousUser(userModel);
	}




	/**
	 * @return the productImageHelper
	 */
	public ProductImageHelper getProductImageHelper()
	{
		return productImageHelper;
	}


	/**
	 * @param productImageHelper
	 *           the productImageHelper to set
	 */
	@Required
	public void setProductImageHelper(final ProductImageHelper productImageHelper)
	{
		this.productImageHelper = productImageHelper;
	}

	/**
	 * @return Is Backend down?
	 */
	private boolean isBackendDown()
	{
		return backendAvailabilityService.isBackendDown();
	}


	/**
	 * @return the cartRestorationFacade
	 */
	public CartRestorationFacade getCartRestorationFacade()
	{
		return cartRestorationFacade;
	}


	/**
	 * @param cartRestorationFacade
	 *           the cartRestorationFacade to set
	 */
	@Required
	public void setCartRestorationFacade(final CartRestorationFacade cartRestorationFacade)
	{
		this.cartRestorationFacade = cartRestorationFacade;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCartFacade#updateCartEntry(long, java.lang.String)
	 */
	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final String storeId)
			throws CommerceCartModificationException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.updateCartEntry(entryNumber, storeId);
		}

		throw new ApplicationBaseRuntimeException("Not supported: updateCartEntry(final long entryNumber, final String storeId)");
	}

	@Override
	public CartRestorationData restoreSavedCart(final String code) throws CommerceCartRestorationException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.restoreSavedCart(code);
		}

		if (isBackendDown())
		{
			return super.restoreSavedCart(code);
		}
		else
		{
			if (this.cartRestorationFacade != null)
			{
				return this.cartRestorationFacade.restoreSavedCart(code, super.getUserService().getCurrentUser());
			}
			return null;
		}
	}

	@Override
	public List<CountryData> getDeliveryCountries()
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.getDeliveryCountries();
		}

		//No delivery countries available, only choosing from existing addresses supported
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCartFacade#estimateExternalTaxes(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public CartData estimateExternalTaxes(final String deliveryZipCode, final String countryIsoCode)
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.estimateExternalTaxes(deliveryZipCode, countryIsoCode);
		}

		//We cannot support this, as the delivery costs are based on the ship-to party address in the ERP case
		throw new ApplicationBaseRuntimeException("Not supported: estimateExternalTaxes");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCartFacade#removeStaleCarts()
	 */
	@Override
	public void removeStaleCarts()
	{
		if (!isSyncOrdermgmtEnabled())
		{
			super.removeStaleCarts();

			return;
		}

		//No stale carts in this scenario

	}

	@Override
	public CartRestorationData restoreAnonymousCartAndTakeOwnership(final String guid) throws CommerceCartRestorationException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.restoreAnonymousCartAndTakeOwnership(guid);
		}

		//No anonymous carts in our scenario
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCartFacade#removeSessionCart()
	 */
	@Override
	public void removeSessionCart()
	{
		if (!isSyncOrdermgmtEnabled())
		{
			super.removeSessionCart();

			return;
		}

		if (this.cartRestorationFacade != null)
		{
			this.cartRestorationFacade.removeSavedCart();
		}

		getSapCartService().removeSessionCart();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCartFacade#getCartsForCurrentUser()
	 */
	@Override
	public List<CartData> getCartsForCurrentUser()
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.getCartsForCurrentUser();

		}

		return Arrays.asList(getSessionCart());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCartFacade#restoreAnonymousCartAndMerge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public CartRestorationData restoreAnonymousCartAndMerge(final String fromAnonumousCartGuid, final String toUserCartGuid)
			throws CommerceCartMergingException, CommerceCartRestorationException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.restoreAnonymousCartAndMerge(fromAnonumousCartGuid, toUserCartGuid);

		}

		throw new ApplicationBaseRuntimeException("restoreAnonymousCartAndMerge not supported");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commercefacades.order.impl.DefaultCartFacade#restoreCartAndMerge(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public CartRestorationData restoreCartAndMerge(final String fromUserCartGuid, final String toUserCartGuid)
			throws CommerceCartRestorationException, CommerceCartMergingException
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.restoreCartAndMerge(fromUserCartGuid, toUserCartGuid);

		}

		throw new ApplicationBaseRuntimeException("restoreCartAndMerge not supported");
	}

	@Override
	public boolean hasEntries()
	{
		if (!isSyncOrdermgmtEnabled())
		{
			return super.hasEntries();

		}

		if (isBackendDown())
		{
			return super.hasEntries();
		}
		else
		{
			final CartData sessionCart = getSapCartService().getSessionCart();
			if (sessionCart != null && sessionCart.getEntries() != null)
			{
				return !sessionCart.getEntries().isEmpty();
			}
			return false;
		}
	}

	/**
	 * @return the sapCartFacadeHooks
	 */
	public List<SapCartFacadeHook> getSapCartFacadeHooks()
	{
		return sapCartFacadeHooks;
	}

	/**
	 * @param sapCartFacadeHooks
	 *           the sapCartFacadeHooks to set
	 */
	public void setSapCartFacadeHooks(final List<SapCartFacadeHook> sapCartFacadeHooks)
	{
		this.sapCartFacadeHooks = sapCartFacadeHooks;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}



}
