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
package de.hybris.platform.cmsfacades.uniqueidentifier.functions;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.EncodedItemComposedKey;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueIdentifierConverter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation for conversion of {@link ProductModel}
 */
public class DefaultProductModelUniqueIdentifierConverter implements UniqueIdentifierConverter<ProductModel>
{
	private ProductService productService;
	private CatalogVersionService catalogVersionService;
	private Converter<ProductModel, ItemData> productModelItemDataConverter;
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	@Override
	public String getItemType()
	{
		return ProductModel._TYPECODE;
	}

	@Override
	public ItemData convert(final ProductModel productModel)
	{
		return getProductModelItemDataConverter().convert(productModel);
	}

	@Override
	public ProductModel convert(final ItemData itemData)
	{
		final EncodedItemComposedKey itemComposedKey = new EncodedItemComposedKey
				.Builder(itemData.getItemId()).encoded().build();

		return getSessionSearchRestrictionsDisabler().execute(findProduct(itemComposedKey));
	}

	protected Supplier<ProductModel> findProduct(final EncodedItemComposedKey itemComposedKey)
	{
		return () -> {
			final CatalogVersionModel catalogVersion = getCatalogVersionService().getCatalogVersion(itemComposedKey.getCatalogId(),
					itemComposedKey.getCatalogVersion());
			return getProductService().getProductForCode(catalogVersion, itemComposedKey.getItemId());
		};
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected Converter<ProductModel, ItemData> getProductModelItemDataConverter()
	{
		return productModelItemDataConverter;
	}

	@Required
	public void setProductModelItemDataConverter(final Converter<ProductModel, ItemData> productModelItemDataConverter)
	{
		this.productModelItemDataConverter = productModelItemDataConverter;
	}

	protected SessionSearchRestrictionsDisabler getSessionSearchRestrictionsDisabler()
	{
		return sessionSearchRestrictionsDisabler;
	}

	@Required
	public void setSessionSearchRestrictionsDisabler(final SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler)
	{
		this.sessionSearchRestrictionsDisabler = sessionSearchRestrictionsDisabler;
	}

}
