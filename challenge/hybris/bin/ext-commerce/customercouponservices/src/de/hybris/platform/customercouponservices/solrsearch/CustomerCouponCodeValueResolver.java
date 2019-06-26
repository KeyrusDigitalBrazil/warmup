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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Resolver a list of customer coupon codes associated with the product and the product category for solr engine
 */
public class CustomerCouponCodeValueResolver extends AbstractValueResolver<ProductModel, Object, Object>
{

	private FieldNameProvider fieldNameProvider;
	private CustomerCouponService customerCouponService;
	private ProductDao productDao;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final ProductModel product,
			final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException
	{

		final List<PromotionSourceRuleModel> promotionSourceRuleList = getPromotionSourceRulesForProduct(product);
		if (product instanceof VariantProductModel)
		{
			promotionSourceRuleList.addAll(getPromotionSourceRulesForProduct(((VariantProductModel) product).getBaseProduct()));
		}

		final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, null);

		for (final String fieldName : fieldNames)
		{
			for (final PromotionSourceRuleModel promotionSourceRule : promotionSourceRuleList)
			{
				document.addField(fieldName,
						getCustomerCouponService().getCouponCodeForPromotionSourceRule(promotionSourceRule.getCode()));
			}
		}

	}

	protected List<PromotionSourceRuleModel> getPromotionSourceRulesForProduct(final ProductModel product)
	{
		final List<PromotionSourceRuleModel> promotionSourceRuleList = new ArrayList<>();
		promotionSourceRuleList.addAll(getCustomerCouponService().getPromotionSourceRulesForProduct(product.getCode()));
		promotionSourceRuleList.addAll(getCustomerCouponService().getPromotionSourcesRuleForProductCategories(product));
		promotionSourceRuleList.removeAll(getCustomerCouponService().getExclPromotionSourceRulesForProduct(product.getCode()));

		return promotionSourceRuleList;
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

	protected CustomerCouponService getCustomerCouponService()
	{
		return customerCouponService;
	}

	@Required
	public void setCustomerCouponService(final CustomerCouponService customerCouponService)
	{
		this.customerCouponService = customerCouponService;
	}

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	@Required
	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}
}
