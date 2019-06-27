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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.acceleratorservices.promotions.dao.PromotionsDao;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.couponservices.dao.CouponDao;
import de.hybris.platform.couponservices.dao.CouponRedemptionDao;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.couponservices.services.impl.DefaultCouponService;
import de.hybris.platform.customercouponservices.CustomerCouponService;
import de.hybris.platform.customercouponservices.daos.CouponNotificationDao;
import de.hybris.platform.customercouponservices.daos.CustomerCouponDao;
import de.hybris.platform.customercouponservices.enums.CouponNotificationStatus;
import de.hybris.platform.customercouponservices.model.CouponNotificationModel;
import de.hybris.platform.customercouponservices.model.CustomerCouponModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.daos.LanguageDao;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link CustomerCouponService}
 */
public class DefaultCustomerCouponService extends DefaultCouponService implements CustomerCouponService
{

	private static final String COUPON_CODE_ERROR_MESSAGE = "coupon code must not be null";
	private CustomerCouponDao customerCouponDao;
	private PromotionsDao promotionsDao;
	private UserService userService;
	private CouponRedemptionDao couponRedemptionDao;
	private LanguageDao languageDao;
	private CouponDao couponDao;
	private CouponNotificationDao couponNotificationDao;
	private CommonI18NService commonI18NService;
	private CategoryService categoryService;

	@SuppressWarnings("deprecation")
	@Override
	public de.hybris.platform.commerceservices.search.pagedata.SearchPageData<CustomerCouponModel> getCustomerCouponsForCustomer(
			final CustomerModel customer, final PageableData pageableData)
	{
		return getCustomerCouponDao().findCustomerCouponsByCustomer(customer, pageableData);
	}

	@Override
	public Optional<CustomerCouponModel> getValidCustomerCouponByCode(final String couponCode)
	{
		validateParameterNotNull(couponCode, COUPON_CODE_ERROR_MESSAGE);
		final Date now = new Date();
		final CustomerCouponModel coupon = getCustomerCouponForCode(couponCode).orElse(null);

		if (coupon != null && coupon.getActive() && validateCouponDate(coupon.getStartDate(), coupon.getEndDate(), now))
		{
			return Optional.of(coupon);
		}
		return Optional.empty();
	}

	protected Boolean validateCouponDate(final Date startDate, final Date endDate, final Date now)
	{
		return startDate != null && endDate != null && endDate.compareTo(now) >= 0;
	}

	@Override
	public void assignCouponToCustomer(final String couponCode, final CustomerModel customer)
	{
		validateParameterNotNull(couponCode, COUPON_CODE_ERROR_MESSAGE);
		final CustomerCouponModel coupon = getCustomerCouponForCode(couponCode).orElse(null);
		if (coupon != null)
		{
			if (coupon.getCustomers() == null)
			{
				coupon.setCustomers(Collections.singleton(customer));
			}
			else
			{
				final Set<CustomerModel> customers = new HashSet<>(coupon.getCustomers());
				customers.add(customer);
				coupon.setCustomers(customers);
			}
			getModelService().save(coupon);
		}
	}

	@Override
	public List<PromotionSourceRuleModel> getPromotionSourceRuleForCouponCode(final String couponCode)
	{
		validateParameterNotNull(couponCode, COUPON_CODE_ERROR_MESSAGE);
		return getCustomerCouponDao().findPromotionSourceRuleByCouponCode(couponCode);
	}

	@Override
	public List<PromotionSourceRuleModel> getPromotionSourceRulesForProduct(final String productCode)
	{
		validateParameterNotNull(productCode, "Product code must not be null");
		return getCustomerCouponDao().findPromotionSourceRuleByProduct(productCode);
	}

	@Override
	public List<PromotionSourceRuleModel> getExclPromotionSourceRulesForProduct(final String productCode)
	{
		validateParameterNotNull(productCode, "Product code must not be null");
		return getCustomerCouponDao().findExclPromotionSourceRuleByProduct(productCode);
	}

	@Override
	public List<PromotionSourceRuleModel> getPromotionSourceRulesForCategory(final String categoryCode)
	{
		validateParameterNotNull(categoryCode, "Category code must not be null");
		return getCustomerCouponDao().findPromotionSourceRuleByCategory(categoryCode);
	}

	@Override
	public List<PromotionSourceRuleModel> getExclPromotionSourceRulesForCategory(final String categoryCode)
	{
		validateParameterNotNull(categoryCode, "Category code must not be null");
		return getCustomerCouponDao().findExclPromotionSourceRuleByCategory(categoryCode);
	}

	@Override
	public List<String> getCouponCodeForPromotionSourceRule(final String code)
	{
		validateParameterNotNull(code, "code code must not be null");
		final List<String> couponList = new ArrayList<>();
		getCustomerCouponDao().findCustomerCouponByPromotionSourceRule(code).stream().forEach(x -> couponList.add(x.getCouponId()));
		return couponList;
	}

	@Override
	public int countProductOrCategoryForPromotionSourceRule(final String code)
	{
		validateParameterNotNull(code, "Code must not be null");
		return getCustomerCouponDao().findCategoryForPromotionSourceRuleByPromotion(code).size()
				+ getCustomerCouponDao().findProductForPromotionSourceRuleByPromotion(code).size();
	}

	@Override
	public void saveCouponNotification(final String couponCode)
	{
		validateParameterNotNull(couponCode, COUPON_CODE_ERROR_MESSAGE);
		final List<CouponNotificationModel> couponNotificationList = getCouponNotificationDao()
				.findCouponNotificationByCouponCode(couponCode);
		final boolean isCouponNotified = couponNotificationList.stream().anyMatch(
				x -> x.getCustomer().getCustomerID().equals(((CustomerModel) getUserService().getCurrentUser()).getCustomerID()));

		if (!isCouponNotified)
		{
			final CouponNotificationModel couponNotification = new CouponNotificationModel();
			couponNotification.setBaseSite(getBaseSiteService().getCurrentBaseSite());
			final CustomerCouponModel customerCoupon = (CustomerCouponModel) getCouponDao().findCouponById(couponCode);
			couponNotification.setCustomerCoupon(customerCoupon);
			final CustomerModel customer = (CustomerModel) getUserService().getCurrentUser();
			couponNotification.setCustomer(customer);
			couponNotification.setLanguage(getCommonI18NService().getCurrentLanguage());

			if (new DateTime(customerCoupon.getStartDate()).isBeforeNow())
			{
				couponNotification.setStatus(CouponNotificationStatus.EFFECTIVESENT);
			}
			if (new DateTime(customerCoupon.getEndDate()).isBeforeNow())
			{
				couponNotification.setStatus(CouponNotificationStatus.EXPIRESENT);
			}

			getModelService().save(couponNotification);
		}
	}

	@Override
	public void removeCouponNotificationByCode(final String couponCode)
	{
		validateParameterNotNull(couponCode, COUPON_CODE_ERROR_MESSAGE);
		final List<CouponNotificationModel> couponNotification = getCouponNotificationDao()
				.findCouponNotificationByCouponCode(couponCode);
		couponNotification.stream()
				.filter(x -> x.getCustomer().getUid().equals(((CustomerModel) getUserService().getCurrentUser()).getUid()))
				.forEach(x -> getModelService().remove(x));
	}

	@Override
	public boolean getCouponNotificationStatus(final String couponCode)
	{
		validateParameterNotNull(couponCode, COUPON_CODE_ERROR_MESSAGE);
		final List<CouponNotificationModel> couponNotifications = getCouponNotificationDao()
				.findCouponNotificationByCouponCode(couponCode);
		final List<CouponNotificationModel> couponNotificationList = couponNotifications.stream()
				.filter(x -> x.getCustomer().getUid().equals(((CustomerModel) getUserService().getCurrentUser()).getUid()))
				.collect(Collectors.toList());

		return CollectionUtils.isNotEmpty(couponNotificationList);
	}

	@Override
	public void removeCouponForCustomer(final String couponCode, final CustomerModel customer)
	{
		validateParameterNotNull(couponCode, COUPON_CODE_ERROR_MESSAGE);
		final CustomerCouponModel coupon = getCustomerCouponForCode(couponCode).orElse(null);
		if (coupon != null)
		{
			final Set<CustomerModel> customers = new HashSet<>(coupon.getCustomers());
			customers.remove(customer);
			coupon.setCustomers(customers);
			getModelService().save(coupon);
		}
	}

	@Override
	public Optional<CustomerCouponModel> getCustomerCouponForCode(final String couponCode)
	{
		validateParameterNotNull(couponCode, COUPON_CODE_ERROR_MESSAGE);
		final Optional<AbstractCouponModel> coupon = getCouponForCode(couponCode);
		if (coupon.isPresent())
		{
			try
			{
				return Optional.of((CustomerCouponModel) (coupon.get()));
			}
			catch (final ClassCastException e)//NOSONAR
			{
				return Optional.empty();
			}
		}
		else
		{
			return Optional.empty();
		}
	}

	@Override
	public List<CouponNotificationModel> getCouponNotificationsForCustomer(final CustomerModel customer)
	{
		return getCouponNotificationDao().findCouponNotificationsForCustomer(customer);
	}

	@Override
	public List<CustomerCouponModel> getEffectiveCustomerCouponsForCustomer(final CustomerModel customer)
	{
		return getCustomerCouponDao().findEffectiveCustomerCouponsByCustomer(customer);
	}

	@Override
	public List<CustomerCouponModel> getAssignableCustomerCoupons(final CustomerModel customer, final String text)
	{
		return getCustomerCouponDao().findAssignableCoupons(customer, text);
	}

	@Override
	public List<CustomerCouponModel> getAssignedCustomerCouponsForCustomer(final CustomerModel customer, final String text)
	{
		return getCustomerCouponDao().findAssignedCouponsByCustomer(customer, text);
	}

	@Override
	public SearchPageData<CustomerCouponModel> getPaginatedCouponsForCustomer(final CustomerModel customer,
			final SearchPageData searchPageData)
	{
		validateParameterNotNull(customer, "Parameter customer must not be null");
		validateParameterNotNull(searchPageData, "Parameter searchPageData must not be null");

		return getCustomerCouponDao().findPaginatedCouponsByCustomer(customer, searchPageData);
	}

	@Override
	public List<PromotionSourceRuleModel> getPromotionSourcesRuleForProductCategories(ProductModel product)
	{
		validateParameterNotNull(product, "product must not be null");

		final List<PromotionSourceRuleModel> promotionSourceRuleList = new ArrayList<>();
		final List<CategoryModel> productCategoriesList = new ArrayList<>();
		productCategoriesList.addAll(product.getCatalogVersion().getRootCategories());
		productCategoriesList.addAll(product.getSupercategories());

		productCategoriesList.forEach(category -> {
			final List<CategoryModel> supAndCurrentCategories = new ArrayList<>();

			supAndCurrentCategories.addAll(getCategoryService().getAllSupercategoriesForCategory(category));
			supAndCurrentCategories.add(category);
			supAndCurrentCategories
					.forEach(supCategory -> promotionSourceRuleList.addAll(getPromotionSourceRulesForCategory(supCategory.getCode())));
			promotionSourceRuleList.removeAll(getExclPromotionSourceRulesForCategory(category.getCode()));

		});

		return promotionSourceRuleList;
	}

	protected CouponRedemptionDao getCouponRedemptionDao()
	{
		return couponRedemptionDao;
	}

	@Required
	public void setCouponRedemptionDao(final CouponRedemptionDao couponRedemptionDao)
	{
		this.couponRedemptionDao = couponRedemptionDao;
	}

	protected CustomerCouponDao getCustomerCouponDao()
	{
		return customerCouponDao;
	}

	@Required
	public void setCustomerCouponDao(final CustomerCouponDao customerCouponDao)
	{
		this.customerCouponDao = customerCouponDao;
	}

	@Required
	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected PromotionsDao getPromotionsDao()
	{
		return promotionsDao;
	}

	@Required
	public void setPromotionsDao(final PromotionsDao promotionsDao)
	{
		this.promotionsDao = promotionsDao;
	}

	protected LanguageDao getLanguageDao()
	{
		return languageDao;
	}

	@Required
	public void setLanguageDao(final LanguageDao languageDao)
	{
		this.languageDao = languageDao;
	}


	protected CouponDao getCouponDao()
	{
		return couponDao;
	}

	@Required
	public void setCouponDao(final CouponDao couponDao)
	{
		this.couponDao = couponDao;
	}

	protected CouponNotificationDao getCouponNotificationDao()
	{
		return couponNotificationDao;
	}

	@Required
	public void setCouponNotificationDao(final CouponNotificationDao couponNotificationDao)
	{
		this.couponNotificationDao = couponNotificationDao;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CategoryService getCategoryService()
	{
		return categoryService;
	}

	@Required
	public void setCategoryService(CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
}
