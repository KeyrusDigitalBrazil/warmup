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
package de.hybris.platform.timedaccesspromotionengineservices.daos;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.promotionengineservices.dao.PromotionDao;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.timedaccesspromotionengineservices.daos.impl.DefaultFlashBuyDao;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.fest.util.Collections;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link DefaultFlashBuyDaoTest}
 */
@IntegrationTest
public class DefaultFlashBuyDaoTest extends ServicelayerTransactionalTest
{
	private static final String COUPON_ID_1 = "couponId1";
	private static final String COUPON_ID_6 = "couponId6";
	private static final String PROMOTION_CODE = "promotion1";
	private static final String PROMOTION_SOURCE_RULE_CODE_1 = "rule1";
	private static final String PROMOTION_SOURCE_RULE_CODE_6 = "rule6";
	private static final String PROMOTION_SOURCE_RULE_CODE_7 = "rule7";
	private static final String PRODUCT_CODE = "testproduct";
	private static final String PRODUCT_CODE1 = "product1";
	private static final String PRODUCT_CODE2 = "testproduct2";
	private static final String PRODUCT_CODE3 = "testproduct3";
	private static final String CATALOG_ID = "electronicsProductCatalog";
	private static final String CATALOG_VERSION = "Online";
	private static final int PRODUCTS_SIZE = 2;
	private static final int ZERO = 0;
	private static final String FIND_PROMOTIONSOURCERULE_BY_CODE = "select {pr." + PromotionSourceRuleModel.PK + "} from {"
			+ PromotionSourceRuleModel._TYPECODE + " as pr} where  {pr." + PromotionSourceRuleModel.CODE + "} = ?code";

	private AbstractPromotionModel promotion;

	@Resource(name = "flashBuyDao")
	private DefaultFlashBuyDao flashBuyDao;

	@Resource(name = "promotionDao")
	private PromotionDao promotionDao;

	@Resource(name = "couponDao")
	private CouponDao couponDao;

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "productService")
	private ProductService productService;

	@Resource
	private CatalogVersionService catalogVersionService;

	@Before
	public void prepare() throws ImpExException
	{
		importCsv("/timedaccesspromotionengineservices/test/FlashBuyTest.impex", "UTF-8");

		promotion = promotionDao.findPromotionByCode(PROMOTION_CODE);
	}

	@Test
	public void testFindFlashBuyCouponbyPromotionCode()
	{
		final Optional<FlashBuyCouponModel> result = flashBuyDao.findFlashBuyCouponByPromotionCode(PROMOTION_SOURCE_RULE_CODE_1);

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(COUPON_ID_1, result.get().getCouponId());
		Assert.assertEquals(PROMOTION_SOURCE_RULE_CODE_1, result.get().getRule().getCode());
	}

	@Test
	public void testFindProductByPromotion()
	{
		final Optional<ProductModel> result = flashBuyDao.findProductByPromotion(promotion);

		Assert.assertTrue(result.isPresent());
		Assert.assertEquals(PRODUCT_CODE, result.get().getCode());
	}

	@Test
	public void testFindPromotionSourceRuleByProduct()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = flashBuyDao.findPromotionSourceRuleByProduct(PRODUCT_CODE);

		Assert.assertEquals(PROMOTION_SOURCE_RULE_CODE_1, promotionSourceRules.get(0).getCode());
	}

	@Test
	public void testFindPromotionSourceRuleByProduct_unbind_promotionSourceRule()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = flashBuyDao.findPromotionSourceRuleByProduct(PRODUCT_CODE2);

		Assert.assertTrue(Collections.isEmpty(promotionSourceRules));
	}

	@Test
	public void testFindPromotionSourceRuleByProduct_unpublished_promotionSourceRule()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = flashBuyDao.findPromotionSourceRuleByProduct(PRODUCT_CODE3);

		Assert.assertTrue(Collections.isEmpty(promotionSourceRules));
	}

	@Test
	public void testfindProductForPromotionSourceRules()
	{
		final FlashBuyCouponModel flashBuyCoupon = (FlashBuyCouponModel) couponDao.findCouponById("couponId1");
		final List<ProductForPromotionSourceRuleModel> productForPromotionSourceRule = flashBuyDao
				.findProductForPromotionSourceRules(flashBuyCoupon.getRule());
		Assert.assertFalse(Collections.isEmpty(productForPromotionSourceRule));
	}

	/**
	 * expect find one product
	 */
	@Test
	public void testFindAllProductsByPromotionSourceRule_one_products()
	{
		final List<ProductModel> products = flashBuyDao
				.findAllProductsByPromotionSourceRule(findPromotionSourceRule(PROMOTION_SOURCE_RULE_CODE_1));
		Assert.assertEquals(PRODUCT_CODE, products.get(0).getCode());
	}

	/**
	 * expect find two product
	 */
	@Test
	public void testFindAllProductsByPromotionSourceRule_two_products()
	{
		final List<ProductModel> products = flashBuyDao
				.findAllProductsByPromotionSourceRule(findPromotionSourceRule(PROMOTION_SOURCE_RULE_CODE_6));
		Assert.assertEquals(PRODUCTS_SIZE, products.size());
	}

	/**
	 * expect find zero product
	 */
	@Test
	public void testFindAllProductsByPromotionSourceRule_zero_products()
	{
		final List<ProductModel> products = flashBuyDao
				.findAllProductsByPromotionSourceRule(findPromotionSourceRule(PROMOTION_SOURCE_RULE_CODE_7));
		Assert.assertEquals(ZERO, products.size());
	}


	protected PromotionSourceRuleModel findPromotionSourceRule(final String ruleCode)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_PROMOTIONSOURCERULE_BY_CODE);
		query.addQueryParameter("code", ruleCode);
		return flexibleSearchService.searchUnique(query);
	}

	/**
	 * expect find one flashbuycoupon
	 */
	@Test
	public void testFindFlashBuyCouponByProduct_one_flashbuycoupon()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION);
		final List<FlashBuyCouponModel> flashBuyCoupons = flashBuyDao
				.findFlashBuyCouponByProduct(productService.getProductForCode(catalogVersion, PRODUCT_CODE1));

		Assert.assertEquals(COUPON_ID_6, flashBuyCoupons.get(0).getCouponId());
	}

	/**
	 * expect find zero flashbuycoupon
	 */
	@Test
	public void testFindFlashBuyCouponByProduct_zero_flashbuycoupon()
	{
		final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION);
		final List<FlashBuyCouponModel> flashBuyCoupons = flashBuyDao
				.findFlashBuyCouponByProduct(productService.getProductForCode(catalogVersion, PRODUCT_CODE));

		Assert.assertTrue(Collections.isEmpty(flashBuyCoupons));
	}
}
