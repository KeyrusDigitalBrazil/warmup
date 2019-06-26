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
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * strategy to handle coping/cloning of product configurations
 */
public interface ConfigurationCopyStrategy
{

	/**
	 * Deep Copies a configuration. The implementation can decide if a deep copy is needed; if not, the input ID is
	 * simply returned.
	 *
	 * @param configId
	 *           ID of existing configuration
	 * @param externalConfiguration
	 *           optional - externalConfiguration, if provided this will be used as source, instead of obtaining the
	 *           external config via the provided configId
	 * @param productCode
	 *           optional - product code of configurable product to be copied
	 * @param force
	 *           if <code>true</code> a deep copy is enforced
	 * @return ID of new configuration if a deep copy was performed; input otherwise
	 */
	String deepCopyConfiguration(final String configId, final String productCode, final String externalConfiguration,
			boolean force);

	/**
	 * Hook to be called after a document with a confguration attached was cloned. It might be necessary to clone the
	 * respective configuration, as well. THis happens typically were the configuration is stored in an external system
	 * and only reference in hybris (CPS). In case the configuration is attached in an extenal seriliazed form, no more
	 * actions are necessary (SSC).
	 *
	 * @param source
	 *           source document
	 * @param target
	 *           target document
	 */
	void finalizeClone(AbstractOrderModel source, AbstractOrderModel target);
}
