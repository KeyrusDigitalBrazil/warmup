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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.daos.CategoryDao;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * This ValueProvider will provide a list of promotion codes associated with the product. This implementation uses only
 * the DefaultPromotionGroup.
 */
@UnitTest
public class CustomerCouponCodeValueProviderTest
{
	@Mock
	private CustomerCouponService customerCouponService;
	@Mock
	private FieldNameProvider fieldNameProvider;
	@Mock
	private ProductDao productDao;
	@Mock
	private CategoryDao categoryDao;

	private CustomerCouponCodeValueProvider CustomerCouponCodeValueProvider;

	private IndexConfig indexConfig;

	private IndexedProperty indexedProperty;

	private ProductModel productModel;

	private final String PRODUCT_CODE = "testProduct";
	private final String PROMOTION_SOURCE_RULE = "rule1";
	private final String COUPON_CODE_1 = "coupon_code_1";
	private List<ProductModel> products;
	private ArrayList<String> couponCodes;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		CustomerCouponCodeValueProvider = new CustomerCouponCodeValueProvider();
		CustomerCouponCodeValueProvider.setCustomerCouponService(customerCouponService);
		CustomerCouponCodeValueProvider.setFieldNameProvider(fieldNameProvider);
		products = new ArrayList<>();
		final ProductModel product = new ProductModel();
		products.add(product);
		Mockito.when(productDao.findProductsByCode(Mockito.anyString())).thenReturn(products);

		CustomerCouponCodeValueProvider.setProductDao(productDao);
		indexConfig = new IndexConfig();
		indexedProperty = new IndexedProperty();
		productModel = new ProductModel();
		productModel.setCode(PRODUCT_CODE);
		final List<PromotionSourceRuleModel> promtionSourceRules = new ArrayList<>();
		final PromotionSourceRuleModel promotionSourceRule = new PromotionSourceRuleModel();
		promotionSourceRule.setCode(PROMOTION_SOURCE_RULE);
		promotionSourceRule.setStatus(RuleStatus.PUBLISHED);
		promtionSourceRules.add(promotionSourceRule);
		Mockito.when(customerCouponService.getPromotionSourceRulesForProduct(Mockito.anyString())).thenReturn(promtionSourceRules);
		CustomerCouponCodeValueProvider.setCategoryDao(categoryDao);
		Mockito.when(categoryDao.findCategoriesByCatalogVersionAndProduct(Mockito.any(), Mockito.any()))
				.thenReturn(CollectionUtils.EMPTY_COLLECTION);


		couponCodes = new ArrayList<>();
		couponCodes.add(COUPON_CODE_1);
		Mockito.when(customerCouponService.getCouponCodeForPromotionSourceRule(Mockito.anyString())).thenReturn(couponCodes);
	}

	@Test
	public void testGetFieldValues() throws Exception
	{
		final Collection<String> fieldNames = new ArrayList<>();
		fieldNames.add("customerCouponCode");
		Mockito.when(fieldNameProvider.getFieldNames(Mockito.any(), Mockito.anyString())).thenReturn(fieldNames);

		final Collection<FieldValue> fields = CustomerCouponCodeValueProvider.getFieldValues(indexConfig, indexedProperty,
				productModel);

		Assert.assertEquals("customerCouponCode", fields.iterator().next().getFieldName());
	}
}
