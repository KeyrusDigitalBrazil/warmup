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
package de.hybris.platform.customercouponservices.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.core.servicelayer.data.SortData;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Integration test for {@link DefaultCustomerCouponService}
 */
@IntegrationTest
public class DefaultCustomerCouponServiceTest extends ServicelayerTransactionalTest
{
	private static final String COUPON_ID = "TESTID";
	private static final String COUPON_ID_2 = "customerCouponCode1";
	private static final String COUPON_ID_3 = "customerCouponCode3";
	private static final String COUPON_ID_4 = "customerCouponCode4";
	private static final String CUSTOMER_UID = "testcustomer";
	private static final String CUSTOMER2_UID = "testcustomer2";
	private static final String CUSTOMER3_UID = "testcustomer3";
	private static final String COUPON_NAME = "test";
	private static final int CURRENT_PAGE = 0;
	private static final int PAGE_SIZE = 5;
	private static final String NOT_FOUND_COUPON = "NOTFOUND";
	private static final String PROMOTION_SOURCE_RULE_CODE_1 = "rule1";
	private static final String PRODUCT_CODE = "111111";
	private static final String CATEGORY_CODE = "576";
	private static final Integer NUMS = 2;


	@Resource(name = "customerCouponService")
	private DefaultCustomerCouponService customerCouponService;

	@Resource
	protected FlexibleSearchService flexibleSearchService;

	@Resource
	private ModelService modelService;
	private CustomerModel customer;
	private CustomerModel customer2;
	private CustomerModel customer3;
	private CustomerCouponModel coupon;
	private Date startDate;
	private Date endDate;
	private PageableData pageableData;
	private SearchPageData searchPageData;
	private SortData sort;

	@Mock
	private UserService userService;

	@Before
	public void prepare() throws ImpExException
	{
		MockitoAnnotations.initMocks(this);
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

		customer2 = modelService.create(CustomerModel.class);
		customer2.setUid(CUSTOMER2_UID);
		modelService.save(customer2);

		searchPageData = new SearchPageData();
		sort = new SortData();

		final PaginationData pagination = new PaginationData();
		pagination.setCurrentPage(0);
		pagination.setPageSize(10);
		pagination.setNeedsTotal(false);
		searchPageData.setPagination(pagination);

		importCsv("/customercouponservices/test/DefaultCustomerCouponServiceTest.impex", "UTF-8");
	}

	@Test
	public void testGetCustomerCouponsForCustomer()
	{
		final de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> result = customerCouponService
				.getCustomerCouponsForCustomer(customer,
				pageableData);

		Assert.assertEquals(CURRENT_PAGE, result.getPagination().getCurrentPage());
		Assert.assertEquals(PAGE_SIZE, result.getPagination().getPageSize());
		Assert.assertEquals(COUPON_ID, result.getResults().get(0).getCouponId());
	}

	@Test
	public void testGetEffectiveCustomerCouponsForCustomer()
	{
		final List<CustomerCouponModel> result = customerCouponService.getEffectiveCustomerCouponsForCustomer(customer);

		Assert.assertEquals(COUPON_ID, result.get(0).getCouponId());
	}

	@Test
	public void testAssignCouponToCustomer()
	{
		customerCouponService.assignCouponToCustomer(COUPON_ID, customer2);
		final CustomerCouponModel resultCoupon = (CustomerCouponModel) customerCouponService.getCouponForCode(COUPON_ID).orElse(
				null);

		assertThat(resultCoupon.getCustomers(), containsInAnyOrder(customer, customer2));

		customerCouponService.assignCouponToCustomer(COUPON_ID_2, customer2);
		final CustomerCouponModel resultCoupon2 = (CustomerCouponModel) customerCouponService.getCouponForCode(COUPON_ID_2)
				.orElse(null);

		Assert.assertNotNull(resultCoupon2.getCustomers());

	}

	@Test
	public void testGetValidCustomerCouponByCode()
	{
		final CustomerCouponModel result1 = customerCouponService.getValidCustomerCouponByCode(COUPON_ID).orElse(null);
		final CustomerCouponModel result2 = customerCouponService.getValidCustomerCouponByCode(NOT_FOUND_COUPON).orElse(null);

		Assert.assertEquals(COUPON_ID, result1.getCouponId());
		Assert.assertNull(result2);
	}

	@Test
	public void testRemoveCouponForCustomer()
	{
		customerCouponService.removeCouponForCustomer(COUPON_ID, customer);
		final List<CustomerCouponModel> result = customerCouponService.getEffectiveCustomerCouponsForCustomer(customer);

		Assert.assertEquals(CURRENT_PAGE, result.size());
	}

	@Test
	public void testGetPromotionSourceRuleForCouponCode()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponService
				.getPromotionSourceRuleForCouponCode(COUPON_ID_2);

		Assert.assertEquals(promotionSourceRules.get(0).getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testGetPromotionSourceRulesForProduct()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponService
				.getPromotionSourceRulesForProduct(PRODUCT_CODE);

		Assert.assertEquals(promotionSourceRules.get(0).getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testGetPromotionSourceRulesForCategory()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponService
				.getPromotionSourceRulesForCategory(CATEGORY_CODE);

		Assert.assertEquals(promotionSourceRules.get(0).getCode(), PROMOTION_SOURCE_RULE_CODE_1);
	}

	@Test
	public void testGetCouponCodeForPromotionSourceRule()
	{
		final List<String> customerCoupons = customerCouponService
				.getCouponCodeForPromotionSourceRule(PROMOTION_SOURCE_RULE_CODE_1);

		Assert.assertEquals(customerCoupons.get(0), COUPON_ID_2);
	}

	@Test
	public void testCountProductOrCategoryForPromotionSourceRule()
	{
		final Integer lengths = customerCouponService.countProductOrCategoryForPromotionSourceRule(PROMOTION_SOURCE_RULE_CODE_1);

		Assert.assertEquals(lengths, NUMS);
	}


	@Test
	public void testRemoveCouponNotificationByCode()
	{
		customerCouponService.setUserService(userService);
		final CustomerModel customer = new CustomerModel();
		customer.setCustomerID("keenreviewer1@hybris.com");
		Mockito.doReturn(customer).when(userService).getCurrentUser();

		customerCouponService.removeCouponNotificationByCode(COUPON_ID_3);
		final boolean isNotificationOn = customerCouponService.getCouponNotificationStatus(COUPON_ID_3);

		Assert.assertEquals(false, isNotificationOn);
	}

	@Test
	public void testGetCouponNotificationStatus()
	{
		customerCouponService.setUserService(userService);
		final CustomerModel customer = new CustomerModel();

		customer.setCustomerID("keenreviewer2@hybris.com");
		Mockito.doReturn(customer).when(userService).getCurrentUser();

		final boolean isNotificationOn = customerCouponService.getCouponNotificationStatus(COUPON_ID_4);

		Assert.assertEquals(false, isNotificationOn);
	}

	@Test
	public void testGetPaginatedCouponsForCustomer_incorrect_sort()
	{
		sort.setCode("errorsort");
		searchPageData.setSorts(Collections.singletonList(sort));

		final SearchPageData<CustomerCouponModel> result = customerCouponService.getPaginatedCouponsForCustomer(customer,
				searchPageData);

		Assert.assertTrue(CollectionUtils.isEmpty(result.getSorts()));
	}

	@Test
	public void testGetPaginatedCouponsForCustomer_correct_sort()
	{
		sort.setCode("enddate");
		searchPageData.setSorts(Collections.singletonList(sort));

		final SearchPageData<CustomerCouponModel> result = customerCouponService.getPaginatedCouponsForCustomer(customer,
				searchPageData);

		Assert.assertEquals("enddate", result.getSorts().get(0).getCode());
	}

	@Test
	public void testGetExclPromotionSourceRulesForProduct()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponService
				.getExclPromotionSourceRulesForProduct(PRODUCT_CODE);
		Assert.assertEquals(0,promotionSourceRules.size());

	}

	@Test
	public void testGetExclPromotionSourceRulesForCategory()
	{
		final List<PromotionSourceRuleModel> promotionSourceRules = customerCouponService
				.getExclPromotionSourceRulesForCategory(CATEGORY_CODE);
		Assert.assertEquals(0,promotionSourceRules.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPaginatedCouponsForCustomer_null_params()
	{
		customerCouponService.getPaginatedCouponsForCustomer(null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetExclPromotionSourceRulesForProduct_null_params()
	{
		customerCouponService.getExclPromotionSourceRulesForProduct(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetExclPromotionSourceRulesForCategory_null_params()
	{
		customerCouponService.getExclPromotionSourceRulesForCategory(null);
	}

	@Test
	public void testGetCouponNotificationsForCustomer()
	{
		customer3 = modelService.create(CustomerModel.class);
		customer3.setUid(CUSTOMER3_UID);
		modelService.save(customer);
		final List<CouponNotificationModel> result = customerCouponService.getCouponNotificationsForCustomer(customer);
		Assert.assertEquals(0, result.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetPromotionSourcesRuleForProductCategoriesNull()
	{
		customerCouponService.getPromotionSourcesRuleForProductCategories(null);
	}

	@Test
	public void testGetPromotionSourcesRuleForProductCategoriesNon()
	{
		final ProductModel product = getProductByCode(PRODUCT_CODE);

		List<PromotionSourceRuleModel> promotionList = customerCouponService.getPromotionSourcesRuleForProductCategories(product);
		Assert.assertEquals(promotionList.size(), 0);
	}


	protected ProductModel getProductByCode(final String code)
	{
		final ProductModel template = new ProductModel();
		template.setCode(code);
		return flexibleSearchService.getModelByExample(template);
	}

}
