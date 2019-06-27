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
package de.hybris.platform.marketplaceservices.dataimport.batch.translator;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;


/**
 * Category translator for marketplace, validate the category must be assigned to the vendor
 */
public class MarketplaceCategoryTranslator extends AbstractValueTranslator
{
	private static final String VENDOR_SERVICE = "vendorService";
	private static final String CATEGORY_SERVICE = "categoryService";
	private static final String CATEGORYVERSION_SERVICE = "catalogVersionService";

	private VendorService vendorService;
	private CategoryService categoryService;
	private CatalogVersionService catalogVersionService;

	@Override
	public Object importValue(final String paramString, final Item paramItem)
	{
		if (StringUtils.isBlank(paramString))
		{
			throw new IllegalArgumentException("Category code is missing");
		}

		final String vendorCode = this.getColumnDescriptor().getDescriptorData().getModifier("vendor");
		final Optional<VendorModel> vendorOptional = getVendorService().getVendorByCode(vendorCode);
		if (!vendorOptional.isPresent())
		{
			throw new IllegalArgumentException("Invalid vendor code: " + vendorCode);
		}

		final String catalogId = this.getColumnDescriptor().getDescriptorData().getModifier("globalCatalogId");
		final String catalogVersionName = this.getColumnDescriptor().getDescriptorData().getModifier("globalCatalogVersion");
		final CatalogVersionModel catalogVersion = this.getCatalogVersionService().getCatalogVersion(catalogId, catalogVersionName);

		final Set<CategoryModel> categories = Arrays.stream(StringUtils.split(paramString, ","))
				.map(code -> categoryService.getCategoryForCode(catalogVersion, code)).collect(Collectors.toSet());

		if (categories.stream().allMatch(c -> isCategoryValidForVendor(c, vendorOptional.get())))
		{
			return categories;
		}
		else
		{
			throw new IllegalArgumentException("Invalid categories: " + paramString
					+ ", make sure every category is assigned to the vendor " + vendorCode);
		}
	}

	protected boolean isCategoryValidForVendor(final CategoryModel category, final VendorModel vendor)
	{
		validateParameterNotNull(category, "Category must not be null");
		validateParameterNotNull(vendor, "Vendor must not be null");

		final Collection<CategoryModel> vendorCategories = vendor.getCategories();
		return vendorCategories != null
				&& (vendorCategories.contains(category) || categoryService.getAllSupercategoriesForCategory(category).stream()
						.anyMatch(vendorCategories::contains));
	}

	@Override
	public String exportValue(final Object paramObject)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void init(final StandardColumnDescriptor descriptor)
	{
		super.init(descriptor);
		setVendorService((VendorService) Registry.getApplicationContext().getBean(VENDOR_SERVICE));
		setCategoryService((CategoryService) Registry.getApplicationContext().getBean(CATEGORY_SERVICE));
		setCatalogVersionService((CatalogVersionService) Registry.getApplicationContext().getBean(CATEGORYVERSION_SERVICE));
	}

	public CategoryService getCategoryService()
	{
		return categoryService;
	}

	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	public VendorService getVendorService()
	{
		return vendorService;
	}

	public void setVendorService(final VendorService vendorService)
	{
		this.vendorService = vendorService;
	}

	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}
}
