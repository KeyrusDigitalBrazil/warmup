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
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;


/**
 * Facade containing for integration between the shopping cart and configurable products. <br>
 * Pure configuration related behavior is handled by the {@link ConfigurationFacade}.
 */
public interface ConfigurationCartIntegrationFacade
{

	/**
	 * Adds the current configuration to shopping cart. The configuration is attached to the shopping cart item as
	 * external configuration, which is an XML-String.
	 *
	 * @param configuration
	 *           configuration to add to the shopping cart
	 * @return key/handle to re-identify the item within the session
	 * @throws CommerceCartModificationException
	 *            in case the update of the cart failed
	 */
	String addConfigurationToCart(final ConfigurationData configuration) throws CommerceCartModificationException;

	/**
	 * Adds the current configuration to shopping cart. The configuration is attached to the shopping cart item as
	 * external configuration, which is an XML-String.
	 *
	 * @param configId
	 *           configuration to add to the shopping cart
	 * @param productCode
	 *           code of product to add
	 * @param quantity
	 *           quantity to add
	 * @return CartModificationData
	 * @throws CommerceCartModificationException
	 *            in case the update of the cart failed
	 */
	CartModificationData addProductConfigurationToCart(final String productCode, final Long quantity, final String configId)
			throws CommerceCartModificationException;

	/**
	 * Updates the current configuration in shopping cart.
	 *
	 * @param productCode
	 *           code of product to add
	 * @param configId
	 *           configuration to add to the shopping cart
	 * @return CartModificationData
	 *
	 */
	CartModificationData updateProductConfigurationInCart(final String productCode, final String configId);



	/**
	 * Clears the link from a product to a runtime configuration
	 *
	 * @param productCode
	 *           ID of a product
	 */
	void removeConfigurationLink(final String productCode);

	/**
	 * Checks whether item is in cart
	 *
	 * @param key
	 *           /handle to re-identify the item within the session
	 * @return <code>true</code>, only if the item is in the cart
	 */
	boolean isItemInCartByKey(String key);


	/**
	 * Copies a configuration. The implementation can decide if a deep copy is needed; if not, the input ID is simply
	 * returned.
	 *
	 * @param configId
	 *           ID of existing configuration
	 * @param productCode
	 *           product code of configurable product to be copied
	 * @return ID of new configuration if a deep copy was performed; input otherwise
	 * @deprecated since 18.08 - use
	 *             {@link ConfigurationCopyStrategy#deepCopyConfiguration(String, String, String, boolean)} instead
	 */
	@Deprecated
	String copyConfiguration(String configId, String productCode);




	/**
	 * Resets the configuration to the initial state
	 *
	 * @param configId
	 *           ID of existing configuration
	 */
	void resetConfiguration(String configId);


	/**
	 * Restores a configuration from a cart entry specified by its key. This is needed if there is no SSC session
	 * connected to the cart entry yet.
	 *
	 * @param kbKey
	 *           knowledgebase key
	 * @param cartEntryKey
	 *           cart entry key
	 * @return Configuration runtime object. Null if configuration could not be restored
	 */
	ConfigurationData restoreConfiguration(KBKeyData kbKey, String cartEntryKey);


	/**
	 * Searches the session cart for an entry specified by a primary key. In case nothing is found, null is returned.
	 *
	 * @param cartItemPk
	 *           Entry key
	 * @return Corresponding order entry model
	 * @deprecated since 18.08.0 - please read any cart related data via the CartFacade
	 *             {@link de.hybris.platform.commercefacades.order.CartFacade#getSessionCart()}
	 */
	@Deprecated
	default AbstractOrderEntryModel findItemInCartByPK(final PK cartItemPk)
	{
		return null;
	}


	/**
	 * Returns the runtime configuration for a configurable cart item. May return <code>null</code> in case the runtime
	 * configuration is not available (anymore). <b>Callers should check for <code>null</code> and take appropriate
	 * action.</b>
	 *
	 * @param cartItemKey
	 *           key of the cart item for which the runtime configuration should be retrieved
	 * @return runtime configuration, or <code>null</code> if not available anymore
	 */
	default ConfigurationData configureCartItem(final String cartItemKey)
	{
		return null;
	}


	/**
	 * Creates a draft from the given external configuration for the associated cart item
	 *
	 * @param cartItemHandle
	 *           cart item for which a draft is to be generated
	 * @param kbKey
	 *           knowledgebase for the product
	 * @param configId
	 *           configuration runtime id for the configuration attached to the cart item
	 * @param copyRequired
	 *           should the configuration be copied
	 * @param extConfig
	 *           external configuration associated with the configId
	 * @return draft configuration data
	 */
	ConfigurationData draftConfig(final String cartItemHandle, final KBKeyData kbKey, final String configId,
			final boolean copyRequired, final String extConfig);

	/**
	 * Returns the runtime configuration for a configurable cart item, assuming that a configuration draft already
	 * exists! May return <code>null</code> in case the runtime configuration is not available (anymore). <b>Callers
	 * should check for <code>null</code> and take appropriate action.</b>
	 * 
	 * @param cartEntryKey
	 *           PK of cart entry as String
	 * @return Configuration in DTO representation
	 */
	ConfigurationData configureCartItemOnExistingDraft(String cartEntryKey);

}
