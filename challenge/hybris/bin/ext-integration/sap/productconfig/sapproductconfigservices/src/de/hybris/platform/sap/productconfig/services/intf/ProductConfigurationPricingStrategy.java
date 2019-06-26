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
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Facilitates pricing integration for configurable products
 */
public interface ProductConfigurationPricingStrategy
{

	/**
	 * Updates cart entry's base price from configuration model if a price is available in configuration model. ConfigId
	 * has to be present in current session for given cart entry to retrieve configuration model. The caller has to take
	 * care for triggering recalculate of cart afterwards.
	 *
	 * @param entry
	 *           cart entry
	 * @return true if cart entry has been updated
	 */
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
	 */
	boolean updateCartEntryPrices(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter);

	/**
	 * Indicates whether there is a problem to obtain correct prices for cart/order
	 *
	 * @param configModel
	 *           configuration model that represents the runtime state of the configuration
	 * @return true if no prices can be obtained at the moment
	 */
	boolean isCartPricingErrorPresent(ConfigModel configModel);

}
