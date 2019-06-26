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
package de.hybris.platform.sap.productconfig.runtime.interf.services.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link ConfigurationProductUtil}
 */
public class ConfigurationProductUtilImpl implements ConfigurationProductUtil
{
	private ProductService productService;
	private CatalogVersionService catalogVersionService;

	@Override
	public ProductModel getProductForCurrentCatalog(final String productCode)
	{
		return getProductService().getProductForCode(getCurrentCatalogVersion(), productCode);
	}

	protected CatalogVersionModel getCurrentCatalogVersion()
	{
		final List<CatalogVersionModel> versions = getCatalogVersionService().getSessionCatalogVersions().stream()
				.filter(cV -> isProductCatalogActive(cV)).collect(Collectors.toList());
		ServicesUtil.validateIfSingleResult(versions, "No catalog version found in session context.",
				"There is more than one active catalog version in the session.");
		return versions.iterator().next();
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

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected boolean isProductCatalogActive(final CatalogVersionModel currentCatalogVersion)
	{
		return (currentCatalogVersion.getActive().booleanValue())
				&& !(currentCatalogVersion.getCatalog() instanceof ContentCatalogModel)
				&& !(currentCatalogVersion.getCatalog() instanceof ClassificationSystemModel);
	}

}
