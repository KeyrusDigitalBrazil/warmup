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
package de.hybris.platform.acceleratorfacades.productcarousel;

import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;


/**
 * Facade to fetch list of products for a given product carousel component.
 */
public interface ProductCarouselFacade
{
	/**
	 * Fetch list of products to be displayed for a given product carousel component.
	 *
	 * @param component
	 *           the product carousel component model
	 * @return a list of {@link ProductModel} for the catalog versions in session
	 */
	List<ProductData> collectProducts(ProductCarouselComponentModel component);

}
