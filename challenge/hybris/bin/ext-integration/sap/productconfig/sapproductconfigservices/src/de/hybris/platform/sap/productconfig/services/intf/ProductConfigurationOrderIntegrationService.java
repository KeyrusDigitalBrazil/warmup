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
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;


/**
 * Facilitates interaction between configuration, pricing and order entries.
 *
 */
public interface ProductConfigurationOrderIntegrationService
{

	/**
	 * Calculates configuration relevant attributes at cart entry level
	 *
	 * @param model
	 *           Cart Entry
	 * @return attributes relevant for configuration
	 */
	CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(AbstractOrderEntryModel model);

	/**
	 * @deprecated Use
	 *             {@link ConfigurationAbstractOrderEntryLinkStrategy#getConfigurationForAbstractOrderEntry(AbstractOrderEntryModel)}
	 *             instead <br>
	 *             Ensures that configuration is available in session
	 *
	 * @param cartEntryKey
	 *           Key of cart entry, derived from {@link PK}
	 * @param productCode
	 *           Product ID
	 * @param externalConfiguration
	 *           External configuration as XML
	 * @return configuration model
	 */
	@Deprecated
	ConfigModel ensureConfigurationInSession(String cartEntryKey, String productCode, String externalConfiguration);

	/**
	 * Calculates configuration relevant attributes at cart entry level
	 *
	 * @param cartEntryKey
	 *           Key of cart entry, derived from {@link PK}
	 * @param productCode
	 *           Product ID
	 * @param externalConfiguration
	 *           External configuration as XML
	 * @return attributes relevant for configuration
	 */
	CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(String cartEntryKey, String productCode,
			String externalConfiguration);

	/**
	 * Updates cart entry's external configuration from configuration model
	 *
	 * @param parameters
	 *           parameters for cart
	 * @param entry
	 *           cart entry
	 * @return true if cart entry has been updated
	 */
	boolean updateCartEntryExternalConfiguration(final CommerceCartParameter parameters, final AbstractOrderEntryModel entry);

	/**
	 * Updates cart entry's external configuration and creates configuration in current session from external string
	 * representation (which contains the configuration in XML format)
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @param entry
	 *           cart entry
	 * @return true if cart entry has been updated
	 */
	boolean updateCartEntryExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel entry);

	/**
	 * Update the product of the cartItem, if the product is different to the current cart item product
	 *
	 * @param entry
	 *           Entry to change, if necessary
	 * @param product
	 *           cart item product
	 * @param configId
	 *           ID of the current configuration
	 * @return true if the entry was updated
	 */
	boolean updateCartEntryProduct(final AbstractOrderEntryModel entry, final ProductModel product, final String configId);

	/**
	 * Fill the summary map at the order entry with configuration status information
	 *
	 * @param entry
	 *           Entry to be enhanced with additional information
	 */
	void fillSummaryMap(final AbstractOrderEntryModel entry);

	/**
	 * Updates cart entry's base price from configuration model if a price is available in configuration model. ConfigId
	 * has to be present in current session for given cart entry to retrieve configuration model. The caller has to take
	 * care for triggering recalculate of cart afterwards.
	 *
	 * @param entry
	 *           cart entry
	 * @return true if cart entry has been updated
	 *
	 * @deprecated since 18.08 use {@link ProductConfigurationStrategy#updateCartEntryBasePrice(AbstractOrderEntryModel)}
	 *             instead
	 */
	@Deprecated
	boolean updateCartEntryBasePrice(final AbstractOrderEntryModel entry);

	/**
	 * Updates cart entry's base price (in case it deviates from the current configuration price) from configuration
	 * model/pricing service. ConfigId has to be present in current session for given cart entry to retrieve
	 * configuration model. This includes recalculation and saving of the cart if entry prices were updated.
	 *
	 * @param entry
	 *           cart entry
	 * @param calculateCart
	 *           specifies whether cart is calculated on successful update
	 * @param passedParameter
	 *           parameters for recalculation of the cart
	 * @return true if cart entry has been updated
	 *
	 * @deprecated since 18.08 use
	 *             {@link ProductConfigurationStrategy#updateCartEntryPrices(AbstractOrderEntryModel,boolean,CommerceCartParameter)}
	 *             instead
	 */
	@Deprecated
	boolean updateCartEntryPrices(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter);


}
