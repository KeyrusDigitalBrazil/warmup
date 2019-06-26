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
package de.hybris.platform.sap.sapordermgmtcfgfacades.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.impl.ConfigurationBaseFacadeImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.sap.sapordermgmtb2bfacades.cart.CartRestorationFacade;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapproductconfigsomservices.cart.CPQCartService;
import de.hybris.platform.store.services.BaseStoreService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



/**
 *
 * DefaultCartIntegrationFacade implementation containing integration between the shopping cart and configurable
 * products.
 */
public class DefaultCartIntegrationFacade extends ConfigurationBaseFacadeImpl implements ConfigurationCartIntegrationFacade
{
	private static final Logger LOG = Logger.getLogger(DefaultCartIntegrationFacade.class);

	private CPQCartService cartService;
	private BackendAvailabilityService backendAvailabilityService;
	private CartRestorationFacade cartRestorationFacade;
	private SessionAccessService sessionAccessService;
	private BaseStoreService baseStoreService;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private ConfigurationCopyStrategy configCopyStrategy;

	private ConfigurationCartIntegrationFacade productConfigDefaultCartIntegrationFacade;

	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;

	protected ConfigurationProductLinkStrategy getProductLinkStrategy()
	{
		return configurationProductLinkStrategy;
	}

	/**
	 * @return the sessionAccessService
	 */
	public SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
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




	@Override
	public String addConfigurationToCart(final ConfigurationData configuration) throws CommerceCartModificationException
	{
		//isSapOrderMgmtEnabled
		if (!isSapOrderMgmtEnabled())
		{
			return getProductConfigDefaultCartIntegrationFacade().addConfigurationToCart(configuration);
		}


		final String configId = configuration.getConfigId();
		if (getBackendAvailabilityService().isBackendDown())
		{
			final String itemKey = getProductConfigDefaultCartIntegrationFacade().addConfigurationToCart(configuration);
			getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(itemKey, configId);

			return itemKey;
		}
		else
		{
			final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);
			String itemKey = getAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId);
			if (null == itemKey)
			{
				itemKey = getAbstractOrderEntryLinkStrategy().getCartEntryForDraftConfigId(configId);
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Configuration was draft for item: " + itemKey);
				}
				getAbstractOrderEntryLinkStrategy().removeDraftConfigIdForCartEntry(itemKey);
			}



			final boolean isItemAvailable = isItemInCartByKey(itemKey);

			if (isItemAvailable)
			{
				// itemKey may change during update when a variant is replaced by its configurable base product
				itemKey = getCartService().updateConfigurationInCart(itemKey, configModel);
			}
			else
			{
				itemKey = getCartService().addConfigurationToCart(configModel);
			}

			//this needs to be done before the call of cart restoration facade as the cart restoration relies on the
			//availability of the configuration reference in the session
			getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(itemKey, configId);

			//Persist the configuration for later cart restoration
			getCartRestorationFacade().setSavedCart(getCartService().getSessionCart());

			return itemKey;
		}
	}

	@Override
	public boolean isItemInCartByKey(final String key)
	{
		if (!isSapOrderMgmtEnabled())
		{
			return getProductConfigDefaultCartIntegrationFacade().isItemInCartByKey(key);
		}
		if (getBackendAvailabilityService().isBackendDown())
		{
			return getProductConfigDefaultCartIntegrationFacade().isItemInCartByKey(key);
		}
		else
		{
			return cartService.isItemAvailable(key);
		}
	}

	/**
	 * @return the cartService
	 */
	public CPQCartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CPQCartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * Creates a KB key for a given product ID, accessing the product model, and returns it.
	 *
	 * @param productId
	 * @return KBKey, containing KB data for the given product
	 */
	protected KBKey getKBKey(final String productId)
	{

		final KBKey kbKey = new KBKeyImpl(productId);

		return kbKey;

	}

	@Override
	public void resetConfiguration(final String configId)
	{
		if (!isSapOrderMgmtEnabled())
		{
			getProductConfigDefaultCartIntegrationFacade().resetConfiguration(configId);
		}

		//nothing needed for us as configuration must stay in session
		//even if backend is down, we want the CFG session to stay in the hybris session to offer a later
		//recovery
	}


	/**
	 * @return the productConfigDefaultCartIntegrationFacade
	 */
	public ConfigurationCartIntegrationFacade getProductConfigDefaultCartIntegrationFacade()
	{
		return productConfigDefaultCartIntegrationFacade;
	}

	/**
	 * @param productConfigDefaultCartIntegrationFacade
	 *           the productConfigDefaultCartIntegrationFacade to set
	 */
	public void setProductConfigDefaultCartIntegrationFacade(
			final ConfigurationCartIntegrationFacade productConfigDefaultCartIntegrationFacade)
	{
		this.productConfigDefaultCartIntegrationFacade = productConfigDefaultCartIntegrationFacade;
	}

	/**
	 * @return the backendAvailabilityService
	 */
	protected BackendAvailabilityService getBackendAvailabilityService()
	{
		return backendAvailabilityService;
	}

	/**
	 * @param backendAvailabilityService
	 *           the backendAvailabilityService to set
	 */
	public void setBackendAvailabilityService(final BackendAvailabilityService backendAvailabilityService)
	{
		this.backendAvailabilityService = backendAvailabilityService;
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
	public void setCartRestorationFacade(final CartRestorationFacade cartRestorationFacade)
	{
		this.cartRestorationFacade = cartRestorationFacade;
	}

	@Override
	public ConfigurationData restoreConfiguration(final KBKeyData kbKey, final String cartEntryKey)
	{
		if (!isSapOrderMgmtEnabled())
		{
			return getProductConfigDefaultCartIntegrationFacade().restoreConfiguration(kbKey, cartEntryKey);
		}
		return null;
	}

	/**
	 * Check if synchronous order management SOM is active
	 *
	 * @return true is SOM is active
	 */
	protected boolean isSapOrderMgmtEnabled()
	{
		return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null
				&& getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled();

	}

	@Override
	public ConfigurationData configureCartItem(final String cartItemKey)
	{
		if (!isSapOrderMgmtEnabled())
		{
			return getProductConfigDefaultCartIntegrationFacade().configureCartItem(cartItemKey);
		}
		final Item item = getCartEntry(cartItemKey);
		if (item == null)
		{
			return null;
		}
		final String productCode = item.getProductId();
		final long startTime = logFacadeCallStart("GET configuration FOR CART ITEM [PRODUCT_CODE='%s'; CART_ITEM='%s']",
				productCode, cartItemKey);
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productCode);

		boolean copyRequired = true;
		String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartItemKey);
		if (configId == null)
		{
			//creates default config
			configId = getConfigurationModel(kbKey).getId();
			copyRequired = false;
		}
		final ConfigurationData draftConfig = draftConfig(cartItemKey, kbKey, configId, copyRequired,
				getConfigurationService().retrieveExternalConfiguration(configId));
		updateKBKeyForVariants(draftConfig);
		logFacadeCallDone("GET configuration FOR CART ITEM", startTime);

		return draftConfig;
	}

	protected void updateKBKeyForVariants(final ConfigurationData draftConfig)
	{
		final ProductModel productModel = getProductService().getProductForCode(draftConfig.getKbKey().getProductCode());
		if (getConfigurationVariantUtil().isCPQVariantProduct(productModel))
		{
			draftConfig.getKbKey().setProductCode(getConfigurationVariantUtil().getBaseProductCode(productModel));
		}
	}

	@Override
	public CartModificationData addProductConfigurationToCart(final String productCode, final Long quantity, final String configId)
			throws CommerceCartModificationException
	{
		throw new IllegalStateException("This API call is not supported for SOM");
	}

	@Override
	public CartModificationData updateProductConfigurationInCart(final String productCode, final String configId)
	{
		throw new IllegalStateException("This API call is not supported for SOM");
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	@Required
	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	@Override
	public ConfigurationData draftConfig(final String cartItemHandle, final KBKeyData kbKey, final String configId,
			final boolean copyRequired, final String extConfig)
	{
		return getProductConfigDefaultCartIntegrationFacade().draftConfig(cartItemHandle, kbKey, configId, copyRequired, extConfig);
	}

	@Override
	public String copyConfiguration(final String configId, final String productCode)
	{
		return getConfigCopyStrategy().deepCopyConfiguration(configId, productCode, null, true);
	}

	protected ConfigurationCopyStrategy getConfigCopyStrategy()
	{
		return configCopyStrategy;
	}

	@Required
	public void setConfigCopyStrategy(final ConfigurationCopyStrategy configCopyStrategy)
	{
		this.configCopyStrategy = configCopyStrategy;
	}


	@Override
	public void removeConfigurationLink(final String productCode)
	{
		getProductLinkStrategy().removeConfigIdForProduct(productCode);

	}

	/**
	 * @param configurationProductLinkStrategy
	 */
	public void setProductLinkStrategy(final ConfigurationProductLinkStrategy configurationProductLinkStrategy)
	{
		this.configurationProductLinkStrategy = configurationProductLinkStrategy;

	}

	@Override
	public ConfigurationData configureCartItemOnExistingDraft(final String cartEntryKey)
	{

		if (!isSapOrderMgmtEnabled())
		{
			return getProductConfigDefaultCartIntegrationFacade().configureCartItemOnExistingDraft(cartEntryKey);
		}
		final Item item = getCartEntry(cartEntryKey);
		if (item == null)
		{
			return null;
		}

		final String configId = getAbstractOrderEntryLinkStrategy().getDraftConfigIdForCartEntry(cartEntryKey);
		if (configId == null)
		{
			throw new IllegalStateException("At this point a draft must exist");
		}
		final ConfigModel configModel = this.getConfigurationService().retrieveConfigurationModel(configId);
		final KBKeyData kbkey = new KBKeyData();
		kbkey.setProductCode(item.getProductId());
		return convert(kbkey, configModel);

	}

	/**
	 * @param cartEntryKey
	 * @return
	 */
	protected Item getCartEntry(final String cartEntryKey)
	{
		final Item item = getCartService().getItemByKey(cartEntryKey);
		if (item == null)
		{
			LOG.warn("Probably multi-session issue: Item not found in cart for key: " + cartEntryKey);
		}
		return item;
	}

}
