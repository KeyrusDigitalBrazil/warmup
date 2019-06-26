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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.populator.ConfigurationOrderEntryProductInfoModelPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPricingStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderIntegrationStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationLifecycleStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ConfigurationCartIntegrationFacade}
 */
public class ConfigurationCartIntegrationFacadeImpl extends ConfigurationBaseFacadeImpl
		implements ConfigurationCartIntegrationFacade
{
	/**
	 * 
	 */
	private static final String REFERENCED_BY_CART_ENTRY_PK = "', referenced by cart entry PK '";
	/**
	 * 
	 */
	private static final String WITH_CONFIG_ID = "' with configId '";
	private CartService cartService;
	private ModelService modelService;
	private CommerceCartService commerceCartService;
	private ProductConfigurationOrderIntegrationService configurationPricingOrderIntegrationService;
	private ProductConfigurationPricingStrategy productConfigurationPricingStrategy;
	private ConfigurationOrderEntryProductInfoModelPopulator configInfoPopulator;
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	private ConfigurationCopyStrategy configCopyStrategy;
	private ConfigurationLifecycleStrategy configLifecycleStrategy;
	private ConfigConsistenceChecker configConsistenceChecker;

	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	private ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy;

	private static final Logger LOG = Logger.getLogger(ConfigurationCartIntegrationFacadeImpl.class);

	@Override
	public String addConfigurationToCart(final ConfigurationData configContent) throws CommerceCartModificationException
	{
		final String productCode = configContent.getKbKey().getProductCode();
		final long startTime = logFacadeCallStart("ADD configuration TO CART [CONFIG_ID='%s'; PRODUCT_CODE='%s']",
				configContent.getConfigId(), productCode);
		final ProductModel product = getProductService().getProductForCode(productCode);

		final AbstractOrderEntryModel cartItem = getOrCreateCartItem(product, configContent);

		addConfigAttributesToCartEntry(cartItem);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Added product '" + product.getCode() + WITH_CONFIG_ID + configContent.getConfigId()
					+ "' to cart with quantity '" + cartItem.getQuantity() + REFERENCED_BY_CART_ENTRY_PK
					+ cartItem.getPk().toString() + "'");
		}
		logFacadeCallDone("ADD configuration TO CART", startTime);
		return cartItem.getPk().toString();
	}

	protected void updateLinkToCartItem(final String configId, final String cartItemKey)
	{
		final String cartItemDraftKey = getAbstractOrderEntryLinkStrategy().getCartEntryForDraftConfigId(configId);
		if (null != cartItemDraftKey)
		{
			getAbstractOrderEntryLinkStrategy().removeDraftConfigIdForCartEntry(cartItemKey);
			final String oldConfigId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartItemKey);
			if (null != oldConfigId && !oldConfigId.equals(configId))
			{
				getConfigLifecycleStrategy().releaseSession(oldConfigId);
			}
		}
		getAbstractOrderEntryLinkStrategy().setConfigIdForCartEntry(cartItemKey, configId);
	}

	@Override
	public CartModificationData addProductConfigurationToCart(final String productCode, final Long quantity, final String configId)
			throws CommerceCartModificationException
	{
		final ProductModel product = getProductService().getProductForCode(productCode);
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cart = getCartService().getSessionCart();


		fillCommerceCartParameterForAddToCart(commerceCartParameter, cart, product, quantity == 0 ? 1 : quantity, product.getUnit(),
				true, configId);

		final CommerceCartModification commerceCartModification = getCommerceCartService().addToCart(commerceCartParameter);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Added product '" + product.getCode() + WITH_CONFIG_ID + configId + "' to cart with quantity '"
					+ commerceCartModification.getEntry().getQuantity() + REFERENCED_BY_CART_ENTRY_PK
					+ commerceCartModification.getEntry().getPk().toString() + "'");
		}

		return getCartModificationConverter().convert(commerceCartModification);
	}

	@Override
	public CartModificationData updateProductConfigurationInCart(final String productCode, final String configId)

	{
		final ProductModel product = getProductService().getProductForCode(productCode);
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cart = getCartService().getSessionCart();

		final AbstractOrderEntryModel entryToUpdate = findCartItemByPK(convertStringToPK(getPKStringForConfigId(configId)));
		updateCartItem(product, configId, entryToUpdate, commerceCartParameter, cart);

		final CommerceCartModification commerceCartModification = fillCommerceCartModification(entryToUpdate);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Updated product configuration'" + product.getCode() + WITH_CONFIG_ID + configId
					+ REFERENCED_BY_CART_ENTRY_PK + commerceCartModification.getEntry().getPk().toString() + "'");
		}

		return getCartModificationConverter().convert(commerceCartModification);
	}

	private CommerceCartModification fillCommerceCartModification(final AbstractOrderEntryModel entryToUpdate)
	{
		final CommerceCartModification modification = new CommerceCartModification();

		modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		modification.setQuantity(entryToUpdate.getQuantity().longValue());
		modification.setEntry(entryToUpdate);
		return modification;

	}

	/**
	 * Creates a new entry in the session cart or returns the entry belonging to the current configuration and updates
	 * the price and its external configuration. The link between cart entry and configuration is established via
	 * {@link ConfigurationData#getCartItemPK()}
	 *
	 * @param product
	 * @param configData
	 *           DTO representation of configuration runtime instance
	 * @return Corresponding cart entry model
	 * @throws CommerceCartModificationException
	 */
	protected AbstractOrderEntryModel getOrCreateCartItem(final ProductModel product, final ConfigurationData configData) throws CommerceCartModificationException
	{
		AbstractOrderEntryModel cartItem = findCartItemByPK(convertStringToPK(getPKStringForConfigId(configData.getConfigId())));
		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		final CartModel cart = getCartService().getSessionCart();
		if (cartItem == null)
		{
			cartItem = createCartItem(product, configData, commerceCartParameter, cart);
			removeLinkToProduct(product.getCode());
		}
		else
		{
			updateCartItem(product, configData.getConfigId(), cartItem, commerceCartParameter, cart);
		}
		return cartItem;
	}

	protected String getPKStringForConfigId(final String configId)
	{
		String pkString = getAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId);
		if (null == pkString)
		{
			pkString = getAbstractOrderEntryLinkStrategy().getCartEntryForDraftConfigId(configId);
		}
		return pkString;
	}

	protected AbstractOrderEntryModel createCartItem(final ProductModel product, final ConfigurationData configData,
			final CommerceCartParameter commerceCartParameter, final CartModel cart) throws CommerceCartModificationException
	{
		fillCommerceCartParameterForAddToCart(commerceCartParameter, cart, product,
				configData.getQuantity() == 0 ? 1 : configData.getQuantity(), product.getUnit(), true, configData.getConfigId());
		return getCommerceCartService().addToCart(commerceCartParameter).getEntry();
	}

	protected void updateCartItem(final ProductModel product, final String configId, final AbstractOrderEntryModel cartItem,
			final CommerceCartParameter commerceCartParameter, final CartModel cart)
	{

		getConfigurationPricingOrderIntegrationService().updateCartEntryProduct(cartItem, product, configId);
		fillCommerceCartParameterForUpdate(commerceCartParameter, cart, configId, CommerceCartParameter.DEFAULT_ENTRY_NUMBER);
		getConfigurationAbstractOrderIntegrationStrategy().updateAbstractOrderEntryOnLink(commerceCartParameter, cartItem);
		updateLinkToCartItem(configId, cartItem.getPk().toString());
		getProductConfigurationPricingStrategy().updateCartEntryPrices(cartItem, true, commerceCartParameter);
	}

	protected void removeLinkToProduct(final String code)
	{
		getProductLinkStrategy().removeConfigIdForProduct(code);
	}

	protected void addConfigAttributesToCartEntry(final AbstractOrderEntryModel entry)
	{
		final List<AbstractOrderEntryProductInfoModel> configInlineModels = new ArrayList<>();
		final String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(entry.getPk().toString());
		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);

		getConfigInfoPopulator().populate(configModel, configInlineModels);
		linkEntryWithConfigInfos(entry, configInlineModels);

		getConfigurationPricingOrderIntegrationService().fillSummaryMap(entry);
		modelService.save(entry);
	}

	protected void linkEntryWithConfigInfos(final AbstractOrderEntryModel entry,
			final List<AbstractOrderEntryProductInfoModel> configInlineModels)
	{
		entry.setProductInfos(configInlineModels);
		for (final AbstractOrderEntryProductInfoModel infoModel : configInlineModels)
		{
			infoModel.setOrderEntry(entry);
		}
	}

	/* fills CommerceCartParameter Object for addToCart */
	protected void fillCommerceCartParameterForAddToCart(final CommerceCartParameter parameter, final CartModel cart,
			final ProductModel product, final long l, final UnitModel unit, final boolean forceNewEntry, final String configId)
	{
		parameter.setEnableHooks(true);
		parameter.setCart(cart);
		parameter.setProduct(product);
		parameter.setQuantity(l);
		parameter.setUnit(unit);
		parameter.setCreateNewEntry(forceNewEntry);
		parameter.setConfigId(configId);
	}


	/**
	 * @param parameter
	 * @param sessionCart
	 * @param configId
	 */
	protected void fillCommerceCartParameterForUpdate(final CommerceCartParameter parameter, final CartModel sessionCart,
			final String configId, final Long entryNumber)
	{
		parameter.setEnableHooks(true);
		parameter.setCart(sessionCart);
		parameter.setConfigId(configId);
		parameter.setEntryNumber(entryNumber);

	}

	/**
	 * Converts a string to the primary key wrapping it
	 *
	 * @param pkString
	 * @return Primary key
	 */
	protected PK convertStringToPK(final String pkString)
	{
		final PK cartItemPk;
		if (pkString != null && !pkString.isEmpty())
		{
			cartItemPk = PK.parse(pkString);
		}
		else
		{
			cartItemPk = PK.NULL_PK;
		}
		return cartItemPk;
	}

	/**
	 * Searches the session cart for an entry specified by a primary key. In case nothing is found, null is returned.
	 *
	 * @param cartItemPk
	 *           Entry key
	 * @return Corresponding order entry model
	 * @deprecated since 18.08.0 - please read any cart related data via the CartFacade
	 *             {@link de.hybris.platform.commercefacades.order.CartFacade#getSessionCart()}
	 */
	@Override
	@Deprecated
	public AbstractOrderEntryModel findItemInCartByPK(final PK cartItemPk)
	{
		return findCartItemByPK(cartItemPk);
	}

	protected AbstractOrderEntryModel findCartItemByPK(final PK cartItemPk)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Search for cartItem with PK '" + cartItemPk + "'");
		}

		if (cartItemPk == null || PK.NULL_PK.equals(cartItemPk))
		{
			return null;
		}

		final Optional<AbstractOrderEntryModel> cartEntry = getCartService().getSessionCart().getEntries().stream()
				.filter(entry -> entry.getPk().equals(cartItemPk) && !getModelService().isRemoved(entry)).findFirst();
		if (cartEntry.isPresent())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("cartItem found for PK '" + cartItemPk + "'");
			}

			return cartEntry.get();
		}
		return null;
	}

	@Override
	public boolean isItemInCartByKey(final String key)
	{
		final PK cartItemPK = PK.parse(key);
		final AbstractOrderEntryModel item = findCartItemByPK(cartItemPK);

		final boolean itemExistsInCart = item != null;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Item with key '" + key + "' exists in cart: '" + itemExistsInCart + "'");
		}

		return itemExistsInCart;
	}

	/**
	 * @deprecated since 18.08 - use
	 *             {@link ConfigurationCopyStrategy#deepCopyConfiguration(String, String, String,boolean)} instead
	 */
	@Deprecated
	@Override
	public String copyConfiguration(final String configId, final String productCode)
	{
		return getConfigCopyStrategy().deepCopyConfiguration(configId, productCode, null, true);
	}

	@Override
	public void resetConfiguration(final String configId)
	{
		final long startTime = logFacadeCallStart("RELEASE configuration [CONFIG_ID='%s']", configId);
		getConfigurationService().releaseSession(configId);
		logFacadeCallDone("RELEASE configuration", startTime);
	}

	@Override
	public ConfigurationData restoreConfiguration(final KBKeyData kbKey, final String cartEntryKey)
	{
		final AbstractOrderEntryModel item = findCartItemByPK(PK.parse(cartEntryKey));
		if (item == null)
		{
			LOG.warn("Probably multi-session issue: Item not found in cart for key: " + cartEntryKey);
			return null;
		}
		final long startTime = logFacadeCallStart("RESTORE configuration FROM CART ITEM [PRODUCT_CODE='%s'; CART_ITEM='%s']",
				kbKey.getProductCode(), cartEntryKey);
		final ConfigModel configModel = restoreConfigModel(cartEntryKey);
		final ConfigurationData configData = convert(kbKey, configModel);
		logFacadeCallDone("RESTORE configuration FROM CART ITEM", startTime);
		return configData;
	}


	protected ConfigModel restoreConfigModel(final String cartEntryKey)
	{
		final AbstractOrderEntryModel item = findCartItemByPK(PK.parse(cartEntryKey));
		return getConfigurationAbstractOrderIntegrationStrategy().getConfigurationForAbstractOrderEntry(item);
	}


	@Override
	public ConfigurationData configureCartItem(final String cartItemKey)
	{
		final AbstractOrderEntryModel item = getOrderEntry(cartItemKey);
		if (item == null)
		{
			return null;
		}
		final String productCode = item.getProduct().getCode();
		final long startTime = logFacadeCallStart("GET configuration FOR CART ITEM [PRODUCT_CODE='%s'; CART_ITEM='%s']",
				productCode, cartItemKey);
		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productCode);

		boolean copyRequired = true;
		String configId = getAbstractOrderEntryLinkStrategy().getConfigIdForCartEntry(cartItemKey);
		if (configId == null)
		{
			// make sure that the configuration is in session
			configId = restoreConfigModel(cartItemKey).getId();
			// the just restored configuration is only known here, so we can link it directly as draft config (copyRequired=false)
			copyRequired = false;
		}
		// else: config is already in session, as we draft/copy it anyways, we do not need to load and convert it.

		final ConfigurationData draftConfig = draftConfig(cartItemKey, kbKey, configId, copyRequired,
				getConfigurationAbstractOrderIntegrationStrategy().getExternalConfigurationForAbstractOrderEntry(item));
		updateKBKeyForVariants(draftConfig);
		logFacadeCallDone("GET configuration FOR CART ITEM", startTime);

		getConfigConsistenceChecker().checkConfiguration(draftConfig);

		return draftConfig;
	}

	protected AbstractOrderEntryModel getOrderEntry(final String cartItemKey)
	{
		final AbstractOrderEntryModel item = findCartItemByPK(PK.parse(cartItemKey));
		if (item == null)
		{
			LOG.warn("Probably multi-session issue: Item not found in cart for key: " + cartItemKey);

		}
		return item;
	}

	public void updateKBKeyForVariants(final ConfigurationData draftConfig)
	{
		final ProductModel productModel = getProductService().getProductForCode(draftConfig.getKbKey().getProductCode());
		if (getConfigurationVariantUtil().isCPQNotChangeableVariantProduct(productModel))
		{
			draftConfig.getKbKey().setProductCode(getConfigurationVariantUtil().getBaseProductCode(productModel));
		}
	}


	@Override
	public ConfigurationData draftConfig(final String cartItemHandle, final KBKeyData kbKey, final String configId,
			final boolean copyRequired, final String extConfig)
	{
		String newConfigId = configId;
		if (copyRequired)
		{

			newConfigId = getConfigCopyStrategy().deepCopyConfiguration(configId, kbKey.getProductCode(), extConfig, false);
		}
		final String oldDraft = getAbstractOrderEntryLinkStrategy().getDraftConfigIdForCartEntry(cartItemHandle);
		if (null != oldDraft && !newConfigId.equals(oldDraft))
		{
			getConfigLifecycleStrategy().releaseSession(oldDraft);
		}
		getAbstractOrderEntryLinkStrategy().setDraftConfigIdForCartEntry(cartItemHandle, newConfigId);
		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(newConfigId);
		return convert(kbKey, configModel);
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           injects the cart service for interaction with the cart
	 */
	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           injects the hybris model service
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	/**
	 * @param commerceCartService
	 *           injects the commerce cart service
	 */
	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected ConfigurationOrderEntryProductInfoModelPopulator getConfigInfoPopulator()
	{
		return configInfoPopulator;
	}

	/**
	 * @param configInfoPopulator
	 */
	public void setConfigInfoPopulator(final ConfigurationOrderEntryProductInfoModelPopulator configInfoPopulator)
	{
		this.configInfoPopulator = configInfoPopulator;
	}

	protected ProductConfigurationOrderIntegrationService getConfigurationPricingOrderIntegrationService()
	{
		return configurationPricingOrderIntegrationService;
	}

	/**
	 * @param configurationPricingOrderIntegrationService
	 *           the configurationPricingOrderIntegrationService to set
	 */
	@Required
	public void setConfigurationPricingOrderIntegrationService(
			final ProductConfigurationOrderIntegrationService configurationPricingOrderIntegrationService)
	{
		this.configurationPricingOrderIntegrationService = configurationPricingOrderIntegrationService;
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

	protected ProductConfigurationPricingStrategy getProductConfigurationPricingStrategy()
	{
		return productConfigurationPricingStrategy;
	}

	/**
	 * @param productConfigurationPricingStrategy
	 *           the productConfigurationPricingStrategy to set
	 */
	@Required
	public void setProductConfigurationPricingStrategy(
			final ProductConfigurationPricingStrategy productConfigurationPricingStrategy)
	{
		this.productConfigurationPricingStrategy = productConfigurationPricingStrategy;
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


	protected ConfigurationLifecycleStrategy getConfigLifecycleStrategy()
	{
		return configLifecycleStrategy;
	}


	@Required
	public void setConfigLifecycleStrategy(final ConfigurationLifecycleStrategy configLifecycleStrategy)
	{
		this.configLifecycleStrategy = configLifecycleStrategy;
	}

	protected Converter<CommerceCartModification, CartModificationData> getCartModificationConverter()
	{
		return cartModificationConverter;
	}

	@Required
	public void setCartModificationConverter(
			final Converter<CommerceCartModification, CartModificationData> cartModificationConverter)
	{
		this.cartModificationConverter = cartModificationConverter;
	}

	@Override
	public void removeConfigurationLink(final String productCode)
	{
		getProductLinkStrategy().removeConfigIdForProduct(productCode);
	}

	protected ConfigurationProductLinkStrategy getProductLinkStrategy()
	{
		return configurationProductLinkStrategy;
	}

	@Required
	public void setProductLinkStrategy(final ConfigurationProductLinkStrategy configurationProductLinkStrategy)
	{
		this.configurationProductLinkStrategy = configurationProductLinkStrategy;
	}


	/**
	 * @param configurationAbstractOrderIntegrationStrategy
	 */
	@Required
	public void setConfigurationAbstractOrderIntegrationStrategy(
			final ConfigurationAbstractOrderIntegrationStrategy configurationAbstractOrderIntegrationStrategy)
	{
		this.configurationAbstractOrderIntegrationStrategy = configurationAbstractOrderIntegrationStrategy;

	}

	protected ConfigurationAbstractOrderIntegrationStrategy getConfigurationAbstractOrderIntegrationStrategy()
	{
		return configurationAbstractOrderIntegrationStrategy;
	}


	protected ConfigConsistenceChecker getConfigConsistenceChecker()
	{
		return configConsistenceChecker;
	}

	/**
	 * @param configConsistenceChecker
	 *           injects the consistency checker
	 */
	@Required
	public void setConfigConsistenceChecker(final ConfigConsistenceChecker configConsistenceChecker)
	{
		this.configConsistenceChecker = configConsistenceChecker;
	}

	@Override
	public ConfigurationData configureCartItemOnExistingDraft(final String cartEntryKey)
	{
		final AbstractOrderEntryModel item = getOrderEntry(cartEntryKey);
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
		kbkey.setProductCode(item.getProduct().getCode());
		return convert(kbkey, configModel);

	}

}
