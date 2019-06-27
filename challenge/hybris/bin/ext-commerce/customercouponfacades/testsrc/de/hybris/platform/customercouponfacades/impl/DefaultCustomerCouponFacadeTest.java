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
package de.hybris.platform.customercouponfacades.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.search.ProductSearchService;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.PaginationData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponSearchPageData;
import de.hybris.platform.customercouponfacades.emums.AssignCouponResult;
import de.hybris.platform.customercouponfacades.strategies.CustomerCouponRemovableStrategy;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * Unit test for {@link DefaultCustomerCouponFacade}
 */
@UnitTest
public class DefaultCustomerCouponFacadeTest
{
	private static final String COUPON_ID = "testid";
	private static final String SEARCH_TEXT = "search-text";
	private static final String SAVE_SUCCESS = "save_success";
	private static final String REMOVE_SUCCESS = "remove_success";

	@Spy
	private final DefaultCustomerCouponFacade facade = new DefaultCustomerCouponFacade();

	private Collection<CustomerModel> customers;

	@Mock
	private UserService userService;
	@Mock
	private CustomerCouponService customerCouponService;
	@Mock
	private Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter;
	@Mock
	private Converter<SearchPageData<CustomerCouponModel>, CustomerCouponSearchPageData> customerCouponSearchPageDataConverter;
	@Mock
	private CustomerModel customer;
	@Mock
	private PageableData pageableData;
	@Mock
	private de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> pagedCouponModels;
	@Mock
	private SearchPageData searchPageData;
	@Mock
	private SearchPageData<CustomerCouponModel> couponSearchPageData;
	@Mock
	private CustomerCouponModel couponModel;
	@Mock
	private List<CustomerCouponModel> couponModels;
	@Mock
	private List<CustomerCouponData> coupons;
	@Mock
	private CustomerCouponData coupon;
	@Mock
	private PaginationData pagination;
	@Mock
	private List<SortData> sorts;
	@Mock
	private Converter<ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>, ProductCategorySearchPageData<SearchStateData, ProductData, CategoryData>> productCategorySearchPageConverter;
	@Mock
	private ProductSearchService<SolrSearchQueryData, SearchResultValueData, ProductCategorySearchPageData<SolrSearchQueryData, SearchResultValueData, CategoryModel>> productSearchService;
	@Mock
	private CustomerCouponSearchPageData customerCouponSearchPageData;
	@Mock
	private CustomerCouponRemovableStrategy customerCouponRemovableStrategy;
	@Mock
	private CartData cart;
	@Mock
	private CartFacade cartFacade;
	@Mock
	private VoucherFacade voucherFacade;
	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		facade.setUserService(userService);
		facade.setCustomerCouponService(customerCouponService);
		facade.setCustomerCouponConverter(customerCouponConverter);
		facade.setCustomerCouponSearchPageDataConverter(customerCouponSearchPageDataConverter);
		facade.setCustomerCouponRemovableStrategy(customerCouponRemovableStrategy);
		facade.setCartFacade(cartFacade);
		facade.setVoucherFacade(voucherFacade);


		customers = new ArrayList<>(0);
		customers.add(customer);

		Mockito.when(userService.getCurrentUser()).thenReturn(customer);
		Mockito.when(customerCouponService.getCustomerCouponsForCustomer(customer, pageableData)).thenReturn(pagedCouponModels);
		Mockito.when(customerCouponService.getEffectiveCustomerCouponsForCustomer(customer)).thenReturn(couponModels);
		Mockito.when(pagedCouponModels.getResults()).thenReturn(couponModels);
		Mockito.doReturn(coupons).when(facade).convertCustomerCoupons(couponModels);
		Mockito.when(pagedCouponModels.getPagination()).thenReturn(pagination);
		Mockito.when(pagedCouponModels.getSorts()).thenReturn(sorts);
		Mockito.when(couponModel.getCustomers()).thenReturn(customers);
		Mockito.when(customerCouponService.getPaginatedCouponsForCustomer(customer, searchPageData)).thenReturn(
				couponSearchPageData);

	}

	@Test
	public void testGetPagedCouponsData()
	{
		final de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponData> result = facade
				.getPagedCouponsData(pageableData);

		Assert.assertEquals(coupons, result.getResults());
		Assert.assertEquals(pagination, result.getPagination());
		Assert.assertEquals(sorts, result.getSorts());
	}

	@Test
	public void testGetPagedCouponsData_PagedCouponModelNull()
	{
		Mockito.when(customerCouponService.getCustomerCouponsForCustomer(customer, pageableData)).thenReturn(null);
		final de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponData> result = facade
				.getPagedCouponsData(pageableData);
		Assert.assertEquals(0, result.getResults().size());
		Assert.assertNull(result.getSorts());
		Assert.assertNull(result.getPagination());
	}

	@Test
	public void testGetCouponsData()
	{
		final List<CustomerCouponData> result = facade.getCouponsData();

		Assert.assertEquals(coupons, result);
	}

	@Test
	public void testGetCouponsData_ResultEmpty()
	{
		Mockito.when(customerCouponService.getEffectiveCustomerCouponsForCustomer(customer)).thenReturn(Collections.emptyList());
		final List<CustomerCouponData> result = facade.getCouponsData();

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testGrantCouponAccessForCurrentUserSuccess()
	{
		couponModel = new CustomerCouponModel();
		couponModel.setCouponId(COUPON_ID);
		final Optional<CustomerCouponModel> optional = Optional.of(couponModel);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(Mockito.anyString())).thenReturn(optional);
		final AssignCouponResult result = facade.grantCouponAccessForCurrentUser(COUPON_ID);
		Assert.assertEquals(AssignCouponResult.SUCCESS, result);
	}

	@Test
	public void testGrantCouponAccessForCurrentUserExist()
	{
		couponModel = new CustomerCouponModel();
		couponModel.setCouponId(COUPON_ID);
		couponModel.setCustomers(Collections.singleton(customer));
		final Optional<CustomerCouponModel> optional = Optional.of(couponModel);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(Mockito.anyString())).thenReturn(optional);
		final AssignCouponResult result = facade.grantCouponAccessForCurrentUser(COUPON_ID);
		Assert.assertEquals(AssignCouponResult.ASSIGNED, result);
	}

	@Test
	public void testGrantCouponAccessForCurrentUserError()
	{
		final Optional<CustomerCouponModel> optional = Optional.empty();
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(Mockito.anyString())).thenReturn(optional);
		final AssignCouponResult result = facade.grantCouponAccessForCurrentUser(COUPON_ID);
		Assert.assertEquals(AssignCouponResult.INEXISTENCE, result);
	}

	@Test
	public void testGetAssignableCustomerCoupons()
	{
		Mockito.when(customerCouponService.getAssignableCustomerCoupons(customer, SEARCH_TEXT)).thenReturn(couponModels);
		final List<CustomerCouponData> result = facade.getAssignableCustomerCoupons(SEARCH_TEXT);

		Assert.assertEquals(coupons, result);
	}

	@Test
	public void testGetAssignedCustomerCoupons()
	{
		Mockito.when(customerCouponService.getAssignedCustomerCouponsForCustomer(customer, SEARCH_TEXT)).thenReturn(couponModels);
		final List<CustomerCouponData> result = facade.getAssignedCustomerCoupons(SEARCH_TEXT);

		Assert.assertEquals(coupons, result);
	}

	@Test
	public void testGetCustomerCouponForCode()
	{
		Mockito.when(customerCouponService.getCustomerCouponForCode(COUPON_ID)).thenReturn(Optional.of(couponModel));
		Mockito.when(customerCouponConverter.convert(couponModel)).thenReturn(coupon);

		final CustomerCouponData result = facade.getCustomerCouponForCode(COUPON_ID);
		Assert.assertEquals(coupon, result);
	}

	@Test
	public void testIsCouponOwnedByCurrentUser()
	{
		Mockito.when(customerCouponService.getCustomerCouponForCode(COUPON_ID)).thenReturn(Optional.of(couponModel));

		final boolean result = facade.isCouponOwnedByCurrentUser(COUPON_ID);
		Assert.assertTrue(result);
	}

	@Test
	public void testIsCouponOwnedByCurrentUser_otherwise()
	{
		Mockito.when(customerCouponService.getCustomerCouponForCode(COUPON_ID)).thenReturn(Optional.of(couponModel));
		customers.remove(customer);

		final boolean result = facade.isCouponOwnedByCurrentUser(COUPON_ID);
		Assert.assertFalse(result);
	}

	@Test
	public void testGetPaginatedCoupons()
	{
		Mockito.when(customerCouponSearchPageDataConverter.convert(couponSearchPageData)).thenReturn(
				customerCouponSearchPageData);
		final CustomerCouponSearchPageData result = facade.getPaginatedCoupons(searchPageData);
		Assert.assertEquals(customerCouponSearchPageData, result);
	}

	@Test
	public void testGetValidCouponForCode()
	{
		final CustomerCouponModel model = new CustomerCouponModel();
		model.setCouponId(COUPON_ID);
		model.setCustomers(Collections.singleton(customer));
		final Optional<CustomerCouponModel> optional = Optional.of(model);
		Mockito.when(customerCouponService.getValidCustomerCouponByCode(Mockito.anyString())).thenReturn(optional);
		Mockito.when(customerCouponConverter.convert(model)).thenReturn(coupon);
		final CustomerCouponData result = facade.getValidCouponForCode(COUPON_ID);
		Assert.assertEquals(coupon, result);

	}

	@Test
	public void testNotification(){

		doNothing().when(customerCouponService).saveCouponNotification(COUPON_ID);
		doNothing().when(customerCouponService).removeCouponNotificationByCode(COUPON_ID);

		facade.saveCouponNotification(COUPON_ID);
		facade.removeCouponNotificationByCode(COUPON_ID);

		verify(customerCouponService, times(1)).saveCouponNotification(COUPON_ID);
		verify(customerCouponService, times(1)).removeCouponNotificationByCode(COUPON_ID);

	}

	@Test
	public void testReleaseCoupon() throws VoucherOperationException
	{
		Mockito.when(customerCouponRemovableStrategy.checkRemovable(Mockito.anyString())).thenReturn(true);
		doNothing().when(customerCouponService).removeCouponForCustomer(COUPON_ID, customer);
		doNothing().when(customerCouponService).removeCouponNotificationByCode(COUPON_ID);
		Mockito.when(cartFacade.getSessionCart()).thenReturn(cart);
		final List appliedVouchers = Collections.singletonList(cart);
		Mockito.when(cart.getAppliedVouchers()).thenReturn(appliedVouchers);
		doNothing().when(voucherFacade).releaseVoucher(COUPON_ID);
		
		facade.releaseCoupon(COUPON_ID);
		
		Mockito.when(customerCouponRemovableStrategy.checkRemovable(Mockito.anyString())).thenReturn(true);
		facade.releaseCoupon(COUPON_ID);
	}

}
