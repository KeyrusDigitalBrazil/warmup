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
package de.hybris.platform.customercouponservices.solrsearch;

import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Provides a list of customer coupon codes associated with the product and the product category for solr engine
 *
 * @deprecated Since 18.11, use {@link CustomerCouponCodeValueResolver} instead.
 */
@Deprecated
public class CustomerCouponCodeValueProvider extends AbstractPropertyFieldValueProvider implements FieldValueProvider
{
	private FieldNameProvider fieldNameProvider;
	private CustomerCouponService customerCouponService;
	private ProductDao productDao;
	private CategoryDao categoryDao;
	private CategoryService categoryService;

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	protected CustomerCouponService getCustomerCouponService()
	{
		return customerCouponService;
	}

	@Required
	public void setCustomerCouponService(final CustomerCouponService customerCouponService)
	{
		this.customerCouponService = customerCouponService;
	}

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		if (model instanceof ProductModel)
		{
			final ProductModel product = (ProductModel) model;
			final Collection<FieldValue> fieldValues = new ArrayList<>();

			getProductDao().findProductsByCode(product.getCode()).stream()
					.forEach(x -> fieldValues.addAll(createFieldValue(x, indexedProperty)));
			return fieldValues;
		}
		else
		{
			throw new FieldValueProviderException("Cannot get promotion codes of non-product item");
		}
	}

	protected ProductDao getProductDao()
	{
		return productDao;
	}

	@Required
	public void setProductDao(final ProductDao productDao)
	{
		this.productDao = productDao;
	}

	protected CategoryDao getCategoryDao()
	{
		return categoryDao;
	}

	@Required
	public void setCategoryDao(final CategoryDao categoryDao)
	{
		this.categoryDao = categoryDao;
	}

	protected List<FieldValue> createFieldValue(final ProductModel product, final IndexedProperty indexedProperty)
	{
		final List<FieldValue> fieldValues = new ArrayList<>();

		final List<PromotionSourceRuleModel> promotionSourceRuleList = getPromotionSourceRulesForProduct(product);
		if(product instanceof VariantProductModel){
			promotionSourceRuleList.addAll(getPromotionSourceRulesForProduct(((VariantProductModel) product).getBaseProduct()));
		}

		promotionSourceRuleList.stream().forEach(
				x -> addFieldValues(fieldValues, indexedProperty, null,
						(getCustomerCouponService().getCouponCodeForPromotionSourceRule(x.getCode())).toArray()));
		return fieldValues;
	}

	protected List<PromotionSourceRuleModel> getPromotionSourceRulesForProduct(final ProductModel product)
	{
		final List<PromotionSourceRuleModel> promotionSourceRuleList = new ArrayList<>();

		promotionSourceRuleList.addAll(getCustomerCouponService().getPromotionSourceRulesForProduct(product.getCode()));

		getCategoryDao()
				.findCategoriesByCatalogVersionAndProduct(product.getCatalogVersion(), product)
				.stream()
				.forEach(
						category -> {
							final List<CategoryModel> nonSubCategories = getNonSubCategries(category);
							nonSubCategories.forEach(nonSubCategory -> promotionSourceRuleList.addAll(getCustomerCouponService()
									.getPromotionSourceRulesForCategory(nonSubCategory.getCode())));
							final List<CategoryModel> nonSuperCategories = getNonSuperCategries(category);
							nonSuperCategories.forEach(nonSuperCategory -> promotionSourceRuleList.removeAll(getCustomerCouponService()
									.getExclPromotionSourceRulesForCategory(nonSuperCategory.getCode())));
						});

		promotionSourceRuleList.removeAll(getCustomerCouponService().getExclPromotionSourceRulesForProduct(product.getCode()));

		return promotionSourceRuleList;
	}

	protected List<CategoryModel> getNonSuperCategries(final CategoryModel category)
	{
		final List<CategoryModel> nonSuperCategries = new ArrayList<>(Arrays.asList(category));
		nonSuperCategries.addAll(categoryService.getAllSubcategoriesForCategory(category));
		return nonSuperCategries;
	}

	protected List<CategoryModel> getNonSubCategries(final CategoryModel category)
	{
		final List<CategoryModel> nonSubCategories = new ArrayList<>(Arrays.asList(category));
		nonSubCategories.addAll(categoryService.getAllSupercategoriesForCategory(category));
		return nonSubCategories;
	}

	protected void addFieldValues(final List<FieldValue> fieldValues, final IndexedProperty indexedProperty,
			final LanguageModel language, final Object value)
	{
		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty,
				language == null ? null : language.getIsocode());
		for (final String fieldName : fieldNames)
		{
			fieldValues.add(new FieldValue(fieldName, value));
		}
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

}
