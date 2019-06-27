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

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.customercouponfacades.CustomerCouponFacade;
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
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CustomerCouponFacade}
 */
public class DefaultCustomerCouponFacade implements CustomerCouponFacade
{

	private UserService userService;
	private CustomerCouponService customerCouponService;
	private Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter;
	private VoucherFacade voucherFacade;
	private CartFacade cartFacade;
	private CustomerCouponRemovableStrategy customerCouponRemovableStrategy;
	private Converter<SearchPageData<CustomerCouponModel>, CustomerCouponSearchPageData> customerCouponSearchPageDataConverter;


	@Override
	public de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponData> getPagedCouponsData(
			final PageableData pageableData)
	{
		final de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponData> pagedCouponsData = new de.hybris.platform.commerceservices.search.pagedata.SearchPageData<>();
		List<CustomerCouponData> couponsData = new ArrayList<>(0);

		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();

		final de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> pagedCouponModels = getCustomerCouponService()
				.getCustomerCouponsForCustomer(customer, pageableData);
		if (pagedCouponModels != null)
		{
			couponsData = convertCustomerCoupons(pagedCouponModels.getResults());
			pagedCouponsData.setPagination(pagedCouponModels.getPagination());
			pagedCouponsData.setSorts(pagedCouponModels.getSorts());
		}

		pagedCouponsData.setResults(couponsData);

		return pagedCouponsData;
	}

	@Override
	public List<CustomerCouponData> getCouponsData()
	{
		List<CustomerCouponData> couponData = new ArrayList<>(0);

		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();

		final List<CustomerCouponModel> couponModels = getCustomerCouponService().getEffectiveCustomerCouponsForCustomer(customer);
		if (CollectionUtils.isNotEmpty(couponModels))
		{
			couponData = convertCustomerCoupons(couponModels);
		}

		return couponData;
	}

	@Override
	public AssignCouponResult grantCouponAccessForCurrentUser(final String couponCode)
	{
		final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();

		final Optional<CustomerCouponModel> coupon = getCustomerCouponService().getValidCustomerCouponByCode(couponCode);

		if (coupon.isPresent())
		{
			final Collection<CustomerModel> customers = coupon.get().getCustomers();
			if (CollectionUtils.isEmpty(customers) || !customers.contains(customer))
			{
				getCustomerCouponService().assignCouponToCustomer(couponCode, customer);
				return AssignCouponResult.SUCCESS;
			}
			else
			{
				return AssignCouponResult.ASSIGNED;
			}
		}
		return AssignCouponResult.INEXISTENCE;
	}

	@Override
	public void saveCouponNotification(final String couponCode)
	{
		getCustomerCouponService().saveCouponNotification(couponCode);
	}

	@Override
	public void removeCouponNotificationByCode(final String couponCode)
	{
		getCustomerCouponService().removeCouponNotificationByCode(couponCode);
	}

	@Override
	public List<CustomerCouponData> getAssignableCustomerCoupons(final String text)
	{
		return convertCustomerCoupons(
				getCustomerCouponService().getAssignableCustomerCoupons((CustomerModel) getUserService().getCurrentUser(), text));
	}

	@Override
	public List<CustomerCouponData> getAssignedCustomerCoupons(final String text)
	{
		return convertCustomerCoupons(
				getCustomerCouponService().getAssignedCustomerCouponsForCustomer((CustomerModel) getUserService().getCurrentUser(),
						text));
	}

	@Override
	public void releaseCoupon(final String couponCode) throws VoucherOperationException
	{
		if (getCustomerCouponRemovableStrategy().checkRemovable(couponCode))
		{
			getCustomerCouponService().removeCouponForCustomer(couponCode, (CustomerModel) getUserService().getCurrentUser());
			getCustomerCouponService().removeCouponNotificationByCode(couponCode);

			final CartData cart = getCartFacade().getSessionCart();
			if (cart != null && cart.getAppliedVouchers().contains(couponCode))
			{
				getVoucherFacade().releaseVoucher(couponCode);
			}
		}
	}

	@Override
	public CustomerCouponData getCustomerCouponForCode(final String couponCode)
	{
		return getCustomerCouponService().getCustomerCouponForCode(couponCode)
				.map(coupon -> getCustomerCouponConverter().convert(coupon)).orElse(null);
	}

	@Override
	public boolean isCouponOwnedByCurrentUser(final String couponCode)
	{
		return getCustomerCouponService().getCustomerCouponForCode(couponCode)
				.map(c -> c.getCustomers().contains(getUserService().getCurrentUser())).orElse(false);
	}

	@Override
	public CustomerCouponSearchPageData getPaginatedCoupons(final SearchPageData searchPageData)
	{
		return getCustomerCouponSearchPageDataConverter().convert(getCustomerCouponService()
				.getPaginatedCouponsForCustomer((CustomerModel) getUserService().getCurrentUser(), searchPageData));
	}

	@Override
	public CustomerCouponData getValidCouponForCode(final String code)
	{
		return getCustomerCouponService().getValidCustomerCouponByCode(code).map(c -> getCustomerCouponConverter().convert(c))
				.orElse(null);
	}

	protected List<CustomerCouponData> convertCustomerCoupons(final List<CustomerCouponModel> customerCouponModels)
	{
		return Converters.convertAll(customerCouponModels, getCustomerCouponConverter());
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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


	protected Converter<CustomerCouponModel, CustomerCouponData> getCustomerCouponConverter()
	{
		return customerCouponConverter;
	}

	@Required
	public void setCustomerCouponConverter(final Converter<CustomerCouponModel, CustomerCouponData> customerCouponConverter)
	{
		this.customerCouponConverter = customerCouponConverter;
	}

	protected VoucherFacade getVoucherFacade()
	{
		return voucherFacade;
	}

	@Required
	public void setVoucherFacade(final VoucherFacade voucherFacade)
	{
		this.voucherFacade = voucherFacade;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	@Required
	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected CustomerCouponRemovableStrategy getCustomerCouponRemovableStrategy()
	{
		return customerCouponRemovableStrategy;
	}

	@Required
	public void setCustomerCouponRemovableStrategy(final CustomerCouponRemovableStrategy customerCouponRemovableStrategy)
	{
		this.customerCouponRemovableStrategy = customerCouponRemovableStrategy;
	}

	protected Converter<SearchPageData<CustomerCouponModel>, CustomerCouponSearchPageData> getCustomerCouponSearchPageDataConverter()
	{
		return customerCouponSearchPageDataConverter;
	}

	@Required
	public void setCustomerCouponSearchPageDataConverter(
			final Converter<SearchPageData<CustomerCouponModel>, CustomerCouponSearchPageData> customerCouponSearchPageDataConverter)
	{
		this.customerCouponSearchPageDataConverter = customerCouponSearchPageDataConverter;
	}

}
