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
package de.hybris.platform.customercouponservices.daos.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SortData;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.customercouponservices.model.CustomerCouponForPromotionSourceRuleModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.promotionengineservices.model.CatForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for {@link DefaultCustomerCouponDao}
 */
@IntegrationTest
public class DefaultCustomerCouponDaoTest extends ServicelayerTransactionalTest
{
	private static final String COUPON_ID = "GFFC32483";
	private static final String COUPON_NAME = "test";
	private static final int CURRENT_PAGE = 0;
	private static final int PAGE_SIZE = 5;
	private static final String CUSTOMER_UID = "testcustomer";
	private static final String PROMOTION_SOURCE_RULE_CODE_1 = "rule1";
	private static final String PROMOTION_SOURCE_RULE_CODE_2 = "rule2";
	private static final String PROMOTION_SOURCE_RULE_CODE_3 = "rule3";
	private static final String MODEULE_NAME = "primary-kie-module";
	private static final String CATEGORY_CODE = "576";
	private static final String PRODUCT_CODE = "111111";
	private static final String COUPON_CODE = "customerCouponCode1";
	private static final String COUPONID_ASM = "tesinasm";
	private static final String COUPONID2_ASM = "tesinasm2";
	private static final String CUSTOMER_UID_ASM = "testcustomerinasm";

	@Resource(name = "customerCouponDao")
	private DefaultCustomerCouponDao customerCouponDao;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource
	private RuleDao ruleDao;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "couponDao")
	private CouponDao couponDao;

	private CustomerCouponModel coupon;
	private CustomerModel customer;
	private Date startDate;
	private Date endDate;
	private PageableData pageableData;

	@Before
	public void prepare() throws ImpExException
	{
		customer = modelService.create(CustomerModel.class);
		customer.setUid(CUSTOMER_UID);
		modelService.save(customer);

		final Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - 2);
		startDate = c.getTime();
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 5);
		endDate = c.getTime();

		pageableData = new PageableData();
		pageableData.setCurrentPage(CURRENT_PAGE);
		pageableData.setPageSize(PAGE_SIZE);

		coupon = modelService.create(CustomerCouponModel.class);
		coupon.setCouponId(COUPON_ID);
		coupon.setName(COUPON_NAME);
		coupon.setActive(Boolean.TRUE);
		coupon.setStartDate(startDate);
		coupon.setEndDate(endDate);
		coupon.setCustomers(Collections.singleton(customer));
		modelService.save(coupon);

		importCsv("/customercouponservices/test/DefaultCustomerCouponDaoTest.impex", "UTF-8");

		final CustomerCouponModel customerCoupon = (CustomerCouponModel) couponDao.findCouponById(COUPONID_ASM);
		customerCoupon.setStartDate(startDate);
		customerCoupon.setEndDate(endDate);
		modelService.save(customerCoupon);

		final CustomerCouponModel customerCoupon2 = (CustomerCouponModel) couponDao.findCouponById(COUPONID2_ASM);
		customerCoupon2.setStartDate(startDate);
		customerCoupon2.setEndDate(endDate);
		modelService.save(customerCoupon2);

	}

	@Test
	public void testFindCustomerCouponsByCustomer()
	{
		final SearchPageData<CustomerCouponModel> result = customerCouponDao.findCustomerCouponsByCustomer(customer, pageableData);

		Assert.assertEquals(result.getPagination().getCurrentPage(), CURRENT_PAGE);
		Assert.assertEquals(result.getPagination().getPageSize(), PAGE_SIZE);
		Assert.assertEquals(result.getResults().get(0).getCouponId(), COUPON_ID);
	}

	@Test
	public void testFindEffectiveCustomerCouponsByCustomer()
	{
		final List<CustomerCouponModel> result = customerCouponDao.findEffectiveCustomerCouponsByCustomer(customer);

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result.get(0).getCouponId(), COUPON_ID);
	}

	@Test
	public void testFindAllCusCouponForSourceRules()
	{
		final PromotionSourceRuleModel rule = ruleDao.findRuleByCode(PROMOTION_SOURCE_RULE_CODE_1);
		final List<CustomerCouponForPromotionSourceRuleModel> result = customerCouponDao.findAllCusCouponForSourceRules(rule);

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testFindAllCusCouponForSourceRules2()
	{
		final PromotionSourceRuleModel rule = ruleDao.findRuleByCode(PROMOTION_SOURCE_RULE_CODE_2);
		final List<CustomerCouponForPromotionSourceRuleModel> result = customerCouponDao.findAllCusCouponForSourceRules(rule,
				MODEULE_NAME);

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void testcCheckCustomerCouponAvailableForCustomer()
	{
		final boolean result = customerCouponDao.checkCustomerCouponAvailableForCustomer(COUPON_ID, customer);

		Assert.assertEquals(result, true);
	}

	@Test
	public void testCountAssignedCouponForCustomer()
	{
		final int result = customerCouponDao.countAssignedCouponForCustomer(COUPON_ID, customer);

		Assert.assertEquals(result, 1);
	}

	@Test
	public void testFindCategoryForPromotionSourceRuleByPromotion()
	{
		final List<CatForPromotionSourceRuleModel> categorys = customerCouponDao
				.findCategoryForPromotionSourceRuleByPromotion(PROMOTION_SOURCE_RULE_CODE_1);

		Assert.assertEquals(categorys.get(0).getCategoryCode(), CATEGORY_CODE);
	}

	@Test
	public void testFindProductForPromotionSourceRuleByPromotion()
	{
		final List<ProductForPromotionSourceRuleModel> products = customerCouponDao
				.findProductForPromotionSourceRuleByPromotion(PROMOTION_SOURCE_RULE_CODE_1);

		Assert.assertEquals(products.get(0).getProductCode(), PRODUCT_CODE);
	}

	@Test
	public void testFindPromotionSourceRuleByCouponCode()
	{
		final List<PromotionSourceRuleModel> promotionSourceRule = customerCouponDao
				.findPromotionSourceRuleByCouponCode(COUPON_CODE);

		Assert.assertEquals(promotionSourceRule.get(0).getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testFindCustomerCouponByPromotionSourceRule()
	{
		final List<CustomerCouponModel> customerCoupons = customerCouponDao
				.findCustomerCouponByPromotionSourceRule(PROMOTION_SOURCE_RULE_CODE_1);

		Assert.assertEquals(customerCoupons.get(0).getCouponId(), COUPON_CODE);
	}

	@Test
	public void testFindPromotionSourceRuleByCategory()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponDao
				.findPromotionSourceRuleByCategory(CATEGORY_CODE);

		Assert.assertEquals(promotionSourceRules.get(0).getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testFindPromotionSourceRuleByProduct()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponDao
				.findPromotionSourceRuleByProduct(PRODUCT_CODE);

		Assert.assertEquals(promotionSourceRules.get(0).getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testFindPromotionSourceRuleByCode()
	{
		final Optional<PromotionSourceRuleModel> promotionSourceRule = customerCouponDao
				.findPromotionSourceRuleByCode(PROMOTION_SOURCE_RULE_CODE_1);

		Assert.assertEquals(promotionSourceRule.get().getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testFindPromotionSourceRuleByCode_QueryResultEmpty()
	{

		final Optional<PromotionSourceRuleModel> promotionSourceRule = customerCouponDao
				.findPromotionSourceRuleByCode(PROMOTION_SOURCE_RULE_CODE_3);
		Assert.assertFalse(promotionSourceRule.isPresent());
	}

	@Test
	public void testFindAssignableCoupons()
	{
		final CustomerModel customer = userService.getUserForUID(CUSTOMER_UID_ASM, CustomerModel.class);
		final List<CustomerCouponModel> result = customerCouponDao.findAssignableCoupons(customer, COUPONID2_ASM);

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(COUPONID2_ASM, result.get(0).getCouponId());
		Assert.assertTrue(CollectionUtils.isEmpty(result.get(0).getCustomers()));
	}

	@Test
	public void testFindAssignedCouponsByCustomer()
	{
		final CustomerModel customer = userService.getUserForUID(CUSTOMER_UID_ASM, CustomerModel.class);
		final List<CustomerCouponModel> result = customerCouponDao.findAssignedCouponsByCustomer(customer, COUPONID_ASM);

		Assert.assertEquals(COUPONID_ASM, result.get(0).getCouponId());
		Assert.assertEquals(CUSTOMER_UID_ASM, result.get(0).getCustomers().iterator().next().getUid());
	}

	@Test
	public void testFindExclPromotionSourceRuleByCategory()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponDao
				.findExclPromotionSourceRuleByCategory(CATEGORY_CODE);

		Assert.assertEquals(promotionSourceRules.get(0).getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testFindExclPromotionSourceRuleByProduct()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponDao
				.findExclPromotionSourceRuleByProduct(PRODUCT_CODE);

		Assert.assertEquals(promotionSourceRules.get(0).getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testFindPaginatedCouponsByCustomer()
	{
		final de.hybris.platform.core.servicelayer.data.SearchPageData param = new de.hybris.platform.core.servicelayer.data.SearchPageData();

		final SortData sort = new SortData();
		sort.setCode("enddate");
		sort.setAsc(true);
		param.setSorts(Collections.singletonList(sort));

		final PaginationData pagination = new PaginationData();
		pagination.setCurrentPage(0);
		pagination.setPageSize(10);
		pagination.setNeedsTotal(true);
		param.setPagination(pagination);

		final de.hybris.platform.core.servicelayer.data.SearchPageData<CustomerCouponModel> result = customerCouponDao
				.findPaginatedCouponsByCustomer(customer, param);

		Assert.assertEquals(COUPON_ID, result.getResults().get(0).getCouponId());
		Assert.assertEquals("enddate", result.getSorts().get(0).getCode());
		Assert.assertTrue(result.getSorts().get(0).isAsc());
		Assert.assertEquals(1, result.getPagination().getTotalNumberOfResults());
	}
}
