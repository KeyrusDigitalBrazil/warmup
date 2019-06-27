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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolverTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@UnitTest
public class CustomerCouponCodeValueResolverTest extends AbstractValueResolverTest
{

	private final String FILE_NAME_1 = "Vendor_Field_Name1";
	private final String FILE_NAME_2 = "Vendor_Field_Name2";
	private final String COUPON_CODE_1 = "Coupon_Code_1";
	private final String COUPON_CODE_2 = "Coupon_Code_2";
	@Mock
	private ProductModel product;
	@Mock
	private FieldNameProvider fieldNameProvider;
	@Mock
	private CustomerCouponService customerCouponService;
	@Mock
	private ProductDao productDao;
	@Mock
	private PromotionSourceRuleModel promotionSourceRule1;
	@Mock
	private PromotionSourceRuleModel promotionSourceRule2;

	private CustomerCouponCodeValueResolver valueResolver;
	private String PRODUCT_CODE = "Product_Code";
	private String PROMOTION_CODE_1 = "Promotion_Source_Rule_Code_1";
	private String PROMOTION_CODE_2 = "Promotion_Source_Rule_Code_2";
	private Collection<String> fieldNames;

	@Before
	public void setUp()
	{
		valueResolver = new CustomerCouponCodeValueResolver();
		valueResolver.setFieldNameProvider(fieldNameProvider);
		valueResolver.setSessionService(getSessionService());
		valueResolver.setQualifierProvider(getQualifierProvider());
		valueResolver.setCustomerCouponService(customerCouponService);
		valueResolver.setProductDao(productDao);

		fieldNames = new ArrayList<>();
		fieldNames.add(FILE_NAME_1);
		fieldNames.add(FILE_NAME_2);

	}

	@Test
	public void resolverNoPromotion() throws Exception
	{
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);

		when(product.getCode()).thenReturn(PRODUCT_CODE);
		when(customerCouponService.getPromotionSourceRulesForProduct(PRODUCT_CODE)).thenReturn(Collections.emptyList());
		when(customerCouponService.getPromotionSourcesRuleForProductCategories(product)).thenReturn(Collections.emptyList());
		when(customerCouponService.getExclPromotionSourceRulesForProduct(PRODUCT_CODE)).thenReturn(Collections.emptyList());

		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		verify(getInputDocument(), Mockito.never()).addField(any(IndexedProperty.class), any());
	}

	@Test
	public void resolverProductPromotion() throws Exception
	{
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);

		when(product.getCode()).thenReturn(PRODUCT_CODE);
		when(promotionSourceRule1.getCode()).thenReturn(PROMOTION_CODE_1);
		when(customerCouponService.getPromotionSourceRulesForProduct(PRODUCT_CODE))
				.thenReturn(Collections.singletonList(promotionSourceRule1));
		when(customerCouponService.getPromotionSourcesRuleForProductCategories(product)).thenReturn(Collections.emptyList());
		when(customerCouponService.getExclPromotionSourceRulesForProduct(PRODUCT_CODE)).thenReturn(Collections.emptyList());

		when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(fieldNames);

		when(customerCouponService.getCouponCodeForPromotionSourceRule(promotionSourceRule1.getCode()))
				.thenReturn(Collections.singletonList(COUPON_CODE_1));

		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		verify(getInputDocument()).addField(FILE_NAME_1, Collections.singletonList(COUPON_CODE_1));
		verify(getInputDocument()).addField(FILE_NAME_2, Collections.singletonList(COUPON_CODE_1));
	}

	@Test
	public void resolverProductAndCategoryPromotion() throws Exception
	{
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);

		when(product.getCode()).thenReturn(PRODUCT_CODE);
		when(promotionSourceRule1.getCode()).thenReturn(PROMOTION_CODE_1);
		when(promotionSourceRule2.getCode()).thenReturn(PROMOTION_CODE_2);
		when(customerCouponService.getPromotionSourceRulesForProduct(PRODUCT_CODE))
				.thenReturn(Collections.singletonList(promotionSourceRule1));
		when(customerCouponService.getPromotionSourcesRuleForProductCategories(product))
				.thenReturn(Collections.singletonList(promotionSourceRule2));
		when(customerCouponService.getExclPromotionSourceRulesForProduct(PRODUCT_CODE)).thenReturn(Collections.emptyList());

		when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(fieldNames);

		when(customerCouponService.getCouponCodeForPromotionSourceRule(promotionSourceRule1.getCode()))
				.thenReturn(Collections.singletonList(COUPON_CODE_1));
		when(customerCouponService.getCouponCodeForPromotionSourceRule(promotionSourceRule2.getCode()))
				.thenReturn(Collections.singletonList(COUPON_CODE_2));

		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);
		verify(getInputDocument()).addField(FILE_NAME_1, Collections.singletonList(COUPON_CODE_1));
		verify(getInputDocument()).addField(FILE_NAME_2, Collections.singletonList(COUPON_CODE_1));
		verify(getInputDocument()).addField(FILE_NAME_1, Collections.singletonList(COUPON_CODE_2));
		verify(getInputDocument()).addField(FILE_NAME_2, Collections.singletonList(COUPON_CODE_2));
	}

	@Test
	public void resolverProductCategoryExclPromotion() throws Exception
	{
		final IndexedProperty indexedProperty = getIndexedProperty();
		final Collection<IndexedProperty> indexedProperties = Collections.singletonList(indexedProperty);

		when(product.getCode()).thenReturn(PRODUCT_CODE);
		when(promotionSourceRule2.getCode()).thenReturn(PROMOTION_CODE_1);
		when(customerCouponService.getPromotionSourceRulesForProduct(PRODUCT_CODE))
				.thenReturn(Collections.singletonList(promotionSourceRule1));
		when(customerCouponService.getPromotionSourcesRuleForProductCategories(product))
				.thenReturn(Collections.singletonList(promotionSourceRule2));
		when(customerCouponService.getExclPromotionSourceRulesForProduct(PRODUCT_CODE))
				.thenReturn(Collections.singletonList(promotionSourceRule1));

		when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(fieldNames);

		when(customerCouponService.getCouponCodeForPromotionSourceRule(promotionSourceRule2.getCode()))
				.thenReturn(Collections.singletonList(COUPON_CODE_2));

		valueResolver.resolve(getInputDocument(), getBatchContext(), indexedProperties, product);

		verify(getInputDocument()).addField(FILE_NAME_1, Collections.singletonList(COUPON_CODE_2));
		verify(getInputDocument()).addField(FILE_NAME_2, Collections.singletonList(COUPON_CODE_2));
	}


}
