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
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;

import java.util.Date;


/**
 * Product configuration assignment strategy.
 */
public interface ConfigurationAssignmentResolverStrategy
{

	/**
	 * Returns the object type to which the product configuration assigned.
	 *
	 * @param configId
	 *           Configuration id
	 * @return the object type to which the product configuration assigned
	 */
	ProductConfigurationRelatedObjectType retrieveRelatedObjectType(String configId);

	/**
	 * Returns the object type of the given order
	 *
	 * @param order
	 *           order object
	 * @return the object type
	 */
	ProductConfigurationRelatedObjectType retrieveRelatedObjectType(AbstractOrderModel order);


	/**
	 * Returns the creation date of the associated AbstractOrderEntry
	 *
	 * @param configId
	 *           configuration id
	 * @return creation date if entry exists, otherwise null
	 */
	Date retrieveCreationDateForRelatedEntry(String configId);

	/**
	 * Returns the related Product code
	 *
	 * @param configId
	 *           configuration id
	 * @return the related Product code, if available
	 */
	String retrieveRelatedProductCode(String configId);

}
