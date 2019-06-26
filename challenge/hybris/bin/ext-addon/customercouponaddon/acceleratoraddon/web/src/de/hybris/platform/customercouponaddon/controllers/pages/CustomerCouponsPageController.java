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
package de.hybris.platform.customercouponaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.customercouponaddon.controllers.CustomercouponaddonControllerConstants;
import de.hybris.platform.customercouponfacades.CustomerCouponFacade;
import de.hybris.platform.customercouponfacades.customercoupon.data.CustomerCouponData;
import de.hybris.platform.customercouponfacades.emums.AssignCouponResult;
import de.hybris.platform.customercouponfacades.strategies.CustomerNotificationPreferenceCheckStrategy;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for my coupons.
 */
@Controller
@Scope("tenant")
@RequestMapping("/my-account")
public class CustomerCouponsPageController extends AbstractSearchPageController
{

	private static final String PAGE_LABEL = "my-coupons";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	private static final String BREADCRUMB_MY_COUPONS_KEY = "text.account.coupons.breadcrumb";
	private static final String SUCCESS = "text.coupon.check.success";
	private static final String EXIST = "text.coupon.check.exist";
	private static final String ERROR = "text.coupon.check.error";
	protected static final String PRODUCT_GRID_PAGE = "category/productGridPage";

	@Resource(name = "accountBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;

	@Resource(name = "customerCouponFacade")
	private CustomerCouponFacade customerCouponFacade;

	@Resource(name = "customerNotificationPreferenceCheckStrategy")
	private CustomerNotificationPreferenceCheckStrategy customerNotificationPreferenceCheckStrategy;

	@RequireHardLogIn
	@RequestMapping(value = "/coupons", method = RequestMethod.GET)
	public String coupons(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model) throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, 9, sortCode, showMode);
		final SearchPageData<CustomerCouponData> pageData = customerCouponFacade.getPagedCouponsData(pageableData);
		if (pageData.getPagination() == null)
		{
			pageData.setPagination(createEmptyPagination());
		}

		populateModel(model, pageData, showMode);
		storeCmsPageInModel(model, getContentPageForLabelOrId(PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PAGE_LABEL));

		model.addAttribute(BREADCRUMBS_ATTR, accountBreadcrumbBuilder.getBreadcrumbs(BREADCRUMB_MY_COUPONS_KEY));
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

		return getViewForPage(model);
	}

	@RequireHardLogIn
	@RequestMapping(value = "/coupon/claim/{code}", method = RequestMethod.GET)
	public String claim(@PathVariable("code") final String code, final RedirectAttributes redirectModel)
	{
		addFlashMessage(customerCouponFacade.grantCouponAccessForCurrentUser(code), redirectModel);

		return REDIRECT_PREFIX + CustomercouponaddonControllerConstants.Views.Pages.COUPONS.ConponsPage;
	}

	@RequireHardLogIn
	@RequestMapping(value = "/coupon/notification/{code}", method = RequestMethod.GET)
	@ResponseBody
	public String setNotification(@RequestParam("isNotificationOn") final boolean isNotificationOn,
			@PathVariable("code") final String code, final Model model, final RedirectAttributes redirectModel)
	{
		if (isNotificationOn)
		{
			customerCouponFacade.removeCouponNotificationByCode(code);
		}
		else
		{
			customerCouponFacade.saveCouponNotification(code);
			customerNotificationPreferenceCheckStrategy.checkCustomerNotificationPreference();

		}
		return "success";
	}

	protected void addFlashMessage(final AssignCouponResult result, final RedirectAttributes redirectModel)
	{
		if (result == AssignCouponResult.SUCCESS)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER, SUCCESS, null);
		}
		else if (result == AssignCouponResult.ASSIGNED)
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.INFO_MESSAGES_HOLDER, EXIST, null);
		}
		else
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, ERROR, null);
		}
	}

}
