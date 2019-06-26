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
package de.hybris.platform.sap.sapproductavailability.backend;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapproductavailability.businessobject.SapProductAvailability;


/**
 * 
 */
public interface SapProductAvailabilityBackend extends de.hybris.platform.sap.core.bol.backend.BackendBusinessObject
{



	/**
	 * Gets the plant from the customer material record. Uses RFC BAPI_CUSTMATINFO_GETDETAILM.
	 * @param material
	 * @param customerId
	 * @return Plant 
	 * @throws BackendException
	 */
	String readPlantForCustomerMaterial(String material, String customerId)
			throws BackendException;


	/**
	 * Reads availability information given the search criteria.
     * (Might return '0 for current date available, 1 for tomorrow').
	 * @param product
	 * @param customerId
	 * @param plant
	 * @param requestedQuantity
	 * @return
	 * @throws BackendException
	 */
	SapProductAvailability readProductAvailability(ProductModel product, String customerId,
			String plant, Long requestedQuantity) throws BackendException;


	/**
	 * Returns the plant name given information about a product.
	 * @param product
	 * @param customerId
	 * @return String plant
	 * @throws BackendException
	 */
	String readPlant(ProductModel product, String customerId)
			throws BackendException;







	



	
}
