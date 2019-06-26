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
package de.hybris.platform.sap.sapproductavailability.businessobject;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * 
 */
public interface SapProductAvailabilityBO
{

	/**
	 * reads the current stock level for a product + future available quantities
	 * @param product
	 * @param customerId
	 * @param plant
	 * @param requestedQuantity
	 * @return @SapProductAvailability  
	 */
	SapProductAvailability readProductAvailability(final ProductModel product, final String customerId, String plant, final Long requestedQuantity);
	

	/**
	 * Gets the plant from the customer material record. Uses RFC BAPI_CUSTMATINFO_GETDETAILM.
	 * @param material
	 * @param customerId
	 * @return Plant 
	 */
	String readPlantForCustomerMaterial(String material, String customerId);


	
	/**
	 * Returns the plant name given information about a product.
	 * @param product
	 * @param customerId
	 * @return String plant
	 */
	String readPlant(ProductModel product, String customerId);
	
}
