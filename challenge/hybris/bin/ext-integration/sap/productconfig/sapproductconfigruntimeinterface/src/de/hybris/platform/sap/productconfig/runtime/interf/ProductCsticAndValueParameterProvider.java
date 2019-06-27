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
package de.hybris.platform.sap.productconfig.runtime.interf;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;

import java.util.Map;


/**
 * Provides characteristic language independent and language dependent names as well as characteristic values from
 * knowledgebase
 */
public interface ProductCsticAndValueParameterProvider
{

	/**
	 * Retrieves characteristic language independent and language dependent names as well as characteristic values from
	 * knowledgebase.
	 *
	 * @param productCode
	 *           product code for which the data is retrieved
	 * @return Map with characteristic language independent name as a key and CsticParameterWithValues as a value
	 */
	Map<String, CsticParameterWithValues> retrieveProductCsticsAndValuesParameters(final String productCode);
}
