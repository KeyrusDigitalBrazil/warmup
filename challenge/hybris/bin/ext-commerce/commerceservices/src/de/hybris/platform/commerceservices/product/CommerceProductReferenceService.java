/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commerceservices.product;

import de.hybris.platform.commerceservices.product.data.ReferenceData;

import java.util.List;

/**
 * Defines an API for product reference
 *
 * @param <TYPE>
 *           the product reference type
 * @param <TARGET>
 *           the target product
 */
public interface CommerceProductReferenceService<TYPE, TARGET>
{
	/**
	 * @deprecated Since 5.0. Use getProductReferencesForCode(final String code, final List<TYPE> referenceTypes, final Integer
	 *             limit); instead.
	 * 
	 * @param code
	 *           the product code
	 * @param referenceType
	 *           the product reference type
	 * @param limit
	 *           maximum number of references to retrieve. If null, all available references will be retrieved.
	 * @return a collection product references
	 */
	@Deprecated
	List<ReferenceData<TYPE, TARGET>> getProductReferencesForCode(final String code, final TYPE referenceType, final Integer limit);

	/**
	 * Retrieves product references for a given product and product reference type.
	 * 
	 * @param code
	 *           the product code
	 * @param referenceTypes
	 *           the product reference types to return
	 * @param limit
	 *           maximum number of references to retrieve. If null, all available references will be retrieved.
	 * @return a collection product references.
	 */
	List<ReferenceData<TYPE, TARGET>> getProductReferencesForCode(final String code, final List<TYPE> referenceTypes,
			final Integer limit);
}
