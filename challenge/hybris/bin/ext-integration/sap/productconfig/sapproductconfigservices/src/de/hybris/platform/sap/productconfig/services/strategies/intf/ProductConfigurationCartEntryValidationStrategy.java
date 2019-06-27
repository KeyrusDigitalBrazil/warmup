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
package de.hybris.platform.sap.productconfig.services.strategies.intf;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.sap.productconfig.services.strategies.impl.ProductConfigurationCartEntryValidationStrategyImpl;


/**
 * Validation strategy for configurable cart entries.
 */
public interface ProductConfigurationCartEntryValidationStrategy
{
	/**
	 * Validates a cart entry model with regards to product configuration
	 *
	 * @param cartEntryModel
	 *           Model representation of cart entry
	 * @return Null if no issue occurred. A modification in status
	 *         {@link ProductConfigurationCartEntryValidationStrategyImpl#REVIEW_CONFIGURATION} in case a validation
	 *         error occurred.
	 */
	CommerceCartModification validateConfiguration(final CartEntryModel cartEntryModel);
}
