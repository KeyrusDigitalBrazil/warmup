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
package de.hybris.platform.marketplaceservices.strategies;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.validation.coverage.CoverageInfo;

public interface AutoApproveProductStrategy {
	/**
	 * Auto approve the Variant and Apparel product, no validation
	 *
	 * @param processedItem
	 *           given product
	 * @return true to apply validation on product, false not apply 
	 * 			 validation on Variant and Apparel product
	 */
	CoverageInfo autoApproveVariantAndApparelProduct(ProductModel product);
}
