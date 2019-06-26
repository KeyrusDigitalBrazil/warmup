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
package de.hybris.platform.customercouponaddon.breadcrumb;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.SearchBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.util.XSSFilterUtil;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.ProductSearchPageData;
import de.hybris.platform.customercouponfacades.CustomerCouponFacade;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;


/**
 * Builds customer coupon related breadcrumb
 */
public class CustomerCouponSearchBreadcrumbBuilder extends SearchBreadcrumbBuilder
{	
	private CustomerCouponFacade customerCouponFacade;
	private I18NService i18nService;
	private String parentBreadcrumbResourceKey;
	private String parentBreadcrumbLinkPath;
	private MessageSource messageSource;
	private static final String CUSTOMERCOUPONCODE = "customerCouponCode";
	private static final Logger LOG = Logger.getLogger(CustomerCouponSearchBreadcrumbBuilder.class);

	
	@Override
	public List<Breadcrumb> getBreadcrumbs(final String categoryCode, final ProductSearchPageData<SearchStateData, ProductData> searchPageData)
	{
		final List<Breadcrumb> breadcrumbs = new ArrayList<>();
		final String customerCouponCode = getCustomerCouponCodeByURL(searchPageData.getCurrentQuery().getUrl());

		if (StringUtils.isNotBlank(customerCouponCode))
		{
			final String customerCouponName = getCustomerCouponNameByCode(customerCouponCode);
			if (StringUtils.isNotEmpty(customerCouponName))
			{
				final String name = getMessageSource().getMessage(getParentBreadcrumbResourceKey(), null,
						getI18nService().getCurrentLocale());
				final String breadcrumbLinkPath = getParentBreadcrumbLinkPath();
				final String link = breadcrumbLinkPath != null && !breadcrumbLinkPath.isEmpty() ? breadcrumbLinkPath : "#";
				breadcrumbs.add(new Breadcrumb(link, name, null));
				breadcrumbs.add(new Breadcrumb("#", customerCouponName, null));
				return breadcrumbs;
			}
		}
		return super.getBreadcrumbs(categoryCode, searchPageData);
	}

	/**
	 * Gets customer coupon code by URL
	 * 
	 * @param url
	 *           the url used for getting customer coupon code
	 * @return the customer coupon code
	 */
	protected String getCustomerCouponCodeByURL(final String url)
	{
		final String urlParams = StringUtils.substringAfter(url, "?");
		if (urlParams.contains(CUSTOMERCOUPONCODE))
		{
			final String[] params = urlParams.split("%3A");
			for (int pos = 0; pos < params.length; pos++)
			{
				if (CUSTOMERCOUPONCODE.equals(params[pos]) && pos + 1 < params.length)
				{
					return XSSFilterUtil.filter(params[pos + 1]);
				}
			}
		}
		return StringUtils.EMPTY;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected MessageSource getMessageSource() {
		return messageSource;
	}
	
	@Required
	public void setMessageSource(final MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected CustomerCouponFacade getCustomerCouponFacade() {
		return customerCouponFacade;
	}
	
	@Required
	public void setCustomerCouponFacade(final CustomerCouponFacade customerCouponFacade) {
		this.customerCouponFacade = customerCouponFacade;
	}

	protected String getParentBreadcrumbResourceKey()
	{
		return parentBreadcrumbResourceKey;
	}

	@Required
	public void setParentBreadcrumbResourceKey(final String parentBreadcrumbResourceKey)
	{
		this.parentBreadcrumbResourceKey = parentBreadcrumbResourceKey;
	}

	protected String getParentBreadcrumbLinkPath()
	{
		return parentBreadcrumbLinkPath;
	}

	@Required
	public void setParentBreadcrumbLinkPath(final String parentBreadcrumbLinkPath)
	{
		this.parentBreadcrumbLinkPath = parentBreadcrumbLinkPath;
	}

	/**
	 * Gets customer coupon name by code
	 * 
	 * @param customerCouponCode
	 *           the customer coupon code used for getting coupon name
	 * @return the coupon name
	 */
	protected String getCustomerCouponNameByCode(final String customerCouponCode)
	{
		try
		{
			final CustomerCouponData customerCouponData = getCustomerCouponFacade().getCustomerCouponForCode(customerCouponCode); // throws IllegalArgumentException
			if (customerCouponData != null)
			{
				if (StringUtils.isEmpty(customerCouponData.getName()))
				{
					LOG.info("Customer coupon name is empty, use coupon code instead.");
					return customerCouponCode;
				}
				return customerCouponData.getName();
			}
			else
			{
				LOG.info("Invalid customer coupon code.");
			}
		}
		catch (final IllegalArgumentException e)
		{
			//we do nothing here and just log the information when catching the exception
			LOG.info("Invalid customer coupon code.", e);
		}

		return StringUtils.EMPTY;
	}
}
