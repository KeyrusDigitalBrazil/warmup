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
package de.hybris.platform.cmsfacades.common.service;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.List;


/**
 * Service to find {@code ItemModel} from a product catalog.
 */
public interface ProductCatalogItemModelFinder
{
	/**
	 * Gets a list of {@code ProductModel} matching the provided {@code compositeKeys}
	 * @param compositeKeys - the keys containing the product catalog, product version and product uid
	 * @return a list of products
	 */
	List<ProductModel> getProductsForCompositeKeys(final List<String> compositeKeys);

	/**
	 * Gets a {@code ProductModel} matching the provided {@code compositeKey}
	 * @param compositeKey - the key containing the product catalog, product version and product uid
	 * @return a product model
	 * @throws UnknownIdentifierException when the product model matching the given key is not found
	 */
	ProductModel getProductForCompositeKey(final String compositeKey);

	/**
	 * Gets a list of {@code CategoryModel} matching the provided {@code compositeKeys}
	 * @param compositeKeys - the keys containing the product catalog, product version and catalog uid
	 * @return a list of categories
	 */
	List<CategoryModel> getCategoriesForCompositeKeys(final List<String> compositeKeys);

	/**
	 * Gets a {@code CategoryModel} matching the provided {@code compositeKey}
	 * @param compositeKey - the key containing the product catalog, product version and catalog uid
	 * @return a category model
	 * @throws UnknownIdentifierException when the product model matching the given key is not found
	 */
	CategoryModel getCategoryForCompositeKey(final String compositeKey);
}
