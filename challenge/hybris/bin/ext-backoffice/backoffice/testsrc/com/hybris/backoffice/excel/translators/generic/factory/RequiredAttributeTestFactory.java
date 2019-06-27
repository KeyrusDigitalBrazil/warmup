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
package com.hybris.backoffice.excel.translators.generic.factory;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.europe1.model.PriceRowModel;

import com.hybris.backoffice.excel.translators.generic.RequiredAttribute;

public class RequiredAttributeTestFactory
{

	public static RequiredAttribute prepareStructureForCatalog()
	{
		final RequiredAttribute catalogOfCatalogVersion = new RequiredAttribute(prepareComposedType(CatalogModel._TYPECODE),
				CatalogVersionModel._TYPECODE, CatalogVersionModel.CATALOG, true, true, false);
		final RequiredAttribute idOfCatalog = new RequiredAttribute(prepareAtomicTypeModel(String.class.getName()),
				CatalogModel._TYPECODE, CatalogModel.ID, true, true, false);
		catalogOfCatalogVersion.addChild(idOfCatalog);
		return catalogOfCatalogVersion;
	}

	public static RequiredAttribute prepareStructureForCatalogVersion()
	{
		final RequiredAttribute catalogVersionOfProduct = new RequiredAttribute(prepareComposedType(CatalogVersionModel._TYPECODE),
				ProductModel._TYPECODE, ProductModel.CATALOGVERSION, false, false, false);
		final RequiredAttribute versionOfCatalogVersion = new RequiredAttribute(prepareAtomicTypeModel(String.class.getName()),
				CatalogVersionModel._TYPECODE, CatalogVersionModel.VERSION, false, false, false);
		final RequiredAttribute catalogOfCatalogVersion = new RequiredAttribute(prepareComposedType(CatalogModel._TYPECODE),
				CatalogVersionModel._TYPECODE, CatalogVersionModel.CATALOG, false, false, false);
		final RequiredAttribute idOfCatalog = new RequiredAttribute(prepareAtomicTypeModel(String.class.getName()),
				CatalogModel._TYPECODE, CatalogModel.ID, false, false, false);
		catalogOfCatalogVersion.addChild(idOfCatalog);
		catalogVersionOfProduct.addChild(versionOfCatalogVersion);
		catalogVersionOfProduct.addChild(catalogOfCatalogVersion);
		return catalogVersionOfProduct;
	}

	public static RequiredAttribute prepareStructureForSupercategories()
	{
		final RequiredAttribute supercategoriesOfProduct = new RequiredAttribute(prepareComposedType(CategoryModel._TYPECODE),
				ProductModel._TYPECODE, ProductModel.SUPERCATEGORIES, false, false, false);
		final RequiredAttribute codeOfCategory = new RequiredAttribute(prepareAtomicTypeModel(String.class.getName()),
				CategoryModel._TYPECODE, CategoryModel.CODE, false, false, false);
		final RequiredAttribute catalogVersionOfCategory = new RequiredAttribute(prepareComposedType(CatalogVersionModel._TYPECODE),
				CategoryModel._TYPECODE, CategoryModel.CATALOGVERSION, false, false, false);
		final RequiredAttribute versionOfCatalogVersion = new RequiredAttribute(prepareAtomicTypeModel(String.class.getName()),
				CatalogVersionModel._TYPECODE, CatalogVersionModel.VERSION, false, false, false);
		final RequiredAttribute catalogOfCatalogVersion = new RequiredAttribute(prepareComposedType(CatalogModel._TYPECODE),
				CatalogVersionModel._TYPECODE, CatalogVersionModel.CATALOG, false, false, false);
		final RequiredAttribute idOfCatalog = new RequiredAttribute(prepareAtomicTypeModel(String.class.getName()),
				CatalogModel._TYPECODE, CatalogModel.ID, false, false, false);

		supercategoriesOfProduct.addChild(codeOfCategory);
		supercategoriesOfProduct.addChild(catalogVersionOfCategory);
		catalogVersionOfCategory.addChild(versionOfCatalogVersion);
		catalogVersionOfCategory.addChild(catalogOfCatalogVersion);
		catalogOfCatalogVersion.addChild(idOfCatalog);
		return supercategoriesOfProduct;
	}

	public static RequiredAttribute prepareStructureForPrices()
	{
		final RequiredAttribute pricesOfProduct = new RequiredAttribute(null, ProductModel._TYPECODE, ProductModel.EUROPE1PRICES,
				true, false, false);
		final RequiredAttribute codeOfUnit = new RequiredAttribute(null, UnitModel._TYPECODE, UnitModel.CODE, true, true, false);
		final RequiredAttribute currencyOfPriceRow = new RequiredAttribute(null, PriceRowModel._TYPECODE, PriceRowModel.CURRENCY,
				true, true, false);
		final RequiredAttribute priceValueOfPriceRow = new RequiredAttribute(null, PriceRowModel._TYPECODE, PriceRowModel.PRICE,
				true, true, false);
		final RequiredAttribute isoCodeOfCurrency = new RequiredAttribute(null, CurrencyModel._TYPECODE, CurrencyModel.ISOCODE,
				true, true, false);
		final RequiredAttribute unitOfPriceRow = new RequiredAttribute(null, PriceRowModel._TYPECODE, PriceRowModel.UNIT, true,
				false, false);

		pricesOfProduct.addChild(priceValueOfPriceRow);
		pricesOfProduct.addChild(unitOfPriceRow);
		pricesOfProduct.addChild(currencyOfPriceRow);
		unitOfPriceRow.addChild(codeOfUnit);
		currencyOfPriceRow.addChild(isoCodeOfCurrency);
		return pricesOfProduct;
	}

	private static ComposedTypeModel prepareComposedType(final String code)
	{
		final ComposedTypeModel composedType = new ComposedTypeModel();
		composedType.setCode(code);
		return composedType;
	}

	private static AtomicTypeModel prepareAtomicTypeModel(final String code)
	{
		final AtomicTypeModel atomicTypeModel = new AtomicTypeModel();
		atomicTypeModel.setCode(code);
		return atomicTypeModel;
	}

}
