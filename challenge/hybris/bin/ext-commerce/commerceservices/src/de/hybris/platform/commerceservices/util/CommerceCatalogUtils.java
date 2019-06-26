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
package de.hybris.platform.commerceservices.util;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.commerceservices.externaltax.impl.DefaultTaxCodeStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Some helper methods for the catalog service.
 *
 * @deprecated Since 6.5 will be removed in the effort to remove deprecated code as this Utility Class is not used.
 */
@Deprecated
public final class CommerceCatalogUtils
{
	/**
	 * Private constructor for avoiding instantiation.
	 */
	private CommerceCatalogUtils()
	{
		//empty
	}

	/**
	 * Filters out {@link ClassificationSystemVersionModel} and ContentCatalogModel catalogs
	 *
	 * @deprecated Since 6.5 Will be removed
	 *
	 * @param catalogVersions
	 *           A collection of {@link CatalogVersionModel}s
	 * @return A collection of product catalog versions
	 */
	@Deprecated
	public static Collection<CatalogVersionModel> findProductCatalogVersions(final Collection<CatalogVersionModel> catalogVersions)
	{
		final List<CatalogVersionModel> result = new ArrayList<CatalogVersionModel>(catalogVersions.size());

		for (final CatalogVersionModel catalogVersion : catalogVersions)
		{
			//TODO: find a better way to test for ContentCatalogModel without depending on cms2
			if (!(catalogVersion instanceof ClassificationSystemVersionModel)
					&& !(catalogVersion.getCatalog().getClass().getName().equals("ContentCatalogModel"))) //NOSONAR
			{
				result.add(catalogVersion);
			}
		}

		return result;
	}

	/**
	 * Gets the active Product Catalog
	 *
	 * @param catalogVersions
	 *           A collection of {@link CatalogVersionModel}s
	 * @deprecated Since 6.5. Use {@link DefaultTaxCodeStrategy#getActiveProductCatalogVersion(Collection)} instead.
	 * @return An active product catalog
	 */
	@Deprecated
	public static CatalogVersionModel getActiveProductCatalogVersion(final Collection<CatalogVersionModel> catalogVersions)
	{

		for (final CatalogVersionModel cvm : catalogVersions)
		{
			if (cvm.getCatalog().getClass().isAssignableFrom(CatalogModel.class))
			{
				return cvm;
			}
		}

		return null;
	}
}
