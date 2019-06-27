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
package de.hybris.platform.sap.productconfig.services.tracking;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Records events into the hybris session
 */
public interface TrackingRecorder
{
	/**
	 * Records the event of create configuration and notifies writers
	 *
	 * @param configModel
	 *           The created configuration model
	 *
	 * @param kbKey
	 *           The knowledgebase from which the configuration has been created.
	 */
	void recordCreateConfiguration(ConfigModel configModel, KBKey kbKey);

	/**
	 * Records the event of update configuration and notifies writers
	 *
	 * @param configModel
	 *           The configuration model with the information which characteristic values were altered; not the updated
	 *           configuration.
	 */
	void recordUpdateConfiguration(ConfigModel configModel);

	/**
	 * Records the status of a configuration that is retrieved and notifies writers
	 *
	 * @param configModel
	 *           The configuration model that has been retrieved from the configuration provider
	 */
	void recordConfigurationStatus(ConfigModel configModel);

	/**
	 * Records the event of create configuration from a variant product and notifies writers
	 *
	 * @param configModel
	 *           The created configuration model
	 *
	 * @param baseProduct
	 *           The product code of the base product
	 * @param variantProduct
	 *           The product code of the variant product
	 */
	void recordCreateConfigurationForVariant(ConfigModel configModel, String baseProduct, String variantProduct);

	/**
	 * Records the event of create configuration from an external configuration and notifies writers
	 *
	 * @param configModel
	 *           The created configuration model
	 */
	void recordCreateConfigurationFromExternalSource(ConfigModel configModel);

	/**
	 * Records the event of add-to-cart for a configuration and notifies writers
	 *
	 * @param entry
	 *           The newly created cart entry
	 *
	 * @param parameters
	 *           Contains information about the surrounding cart and the configuration id and belongs to the @param entry
	 */
	void recordAddToCart(AbstractOrderEntryModel entry, CommerceCartParameter parameters);

	/**
	 * Records the event of update cart entry for a configuration and notifies writers
	 *
	 * @param entry
	 *           The updated cart entry
	 *
	 * @param parameters
	 *           Contains information about the surrounding cart and the configuration id and belongs to the @param entry
	 */
	void recordUpdateCartEntry(AbstractOrderEntryModel entry, CommerceCartParameter parameters);

	/**
	 * Records the event of delete cart entry for a configuration and notifies writers
	 *
	 * @param entry
	 *           The deleted cart entry
	 *
	 * @param parameters
	 *           Contains information about the surrounding cart and the configuration id and belongs to the @param entry
	 */
	void recordDeleteCartEntry(AbstractOrderEntryModel entry, CommerceCartParameter parameters);
}
