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
package de.hybris.platform.marketplaceaddon.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.data.VendorReviewData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.marketplaceaddon.controllers.MarketplaceaddonControllerConstants;
import de.hybris.platform.marketplacefacades.VendorProductSearchFacade;
import de.hybris.platform.marketplacefacades.vendor.CustomerVendorReviewFacade;
import de.hybris.platform.marketplacefacades.vendor.VendorFacade;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.marketplaceservices.model.VendorPageModel;
import de.hybris.platform.marketplaceservices.vendor.VendorCMSPageService;
import de.hybris.platform.marketplaceservices.vendor.VendorService;
import de.hybris.platform.ordersplitting.model.VendorModel;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@Scope("tenant")
@RequestMapping(value = "/**/v")
public class VendorLandingPageController extends AbstractSearchPageController
{
	private static final String VENDOR_CODE_PATH_VARIABLE_PATTERN = "/{vendorCode:.*}";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	private static final String BREADCRUMB_VENDOR_REVIEWS = "text.vendor.reviews.breadcrumd";
	private static final String REVIEWS_PAGE_TITLE = "text.vendor.reviews.page.title";
	private static final int ZEROREVIEW = 0;
	private static final int DEFAULTPAGEINDEX = 0;

	@Resource(name = "vendorService")
	private VendorService vendorService;

	@Resource(name = "vendorCMSPageService")
	private VendorCMSPageService vendorCMSPageService;

	@Resource(name = "vendorProductSearchFacade")
	private VendorProductSearchFacade vendorProductSearchFacade;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder simpleBreadcrumbBuilder;

	@Resource(name = "customerVendorReviewFacade")
	private CustomerVendorReviewFacade customerVendorReviewFacade;

	@Resource(name = "vendorFacade")
	private VendorFacade vendorFacade;
	
	@Resource(name = "commerceCommonI18NService")
	private CommerceCommonI18NService commerceCommonI18NService;

	@RequestMapping(value = VENDOR_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
	public String vendor(@PathVariable("vendorCode") final String vendorCode, final Model model)
	{
		final Optional<VendorModel> vendorModelOptional = getVendorService().getVendorByCode(vendorCode);
		if (!vendorModelOptional.isPresent() || !vendorModelOptional.get().isActive())
		{
			return FORWARD_PREFIX + "/404";
		}

		model.addAttribute("vendorData", vendorProductSearchFacade.getVendorCategories(vendorCode));
		model.addAttribute(CMS_PAGE_TITLE, getPageName(vendorModelOptional.get()));
		final List<Breadcrumb> breadcrumbs = simpleBreadcrumbBuilder.getBreadcrumbs(null);
		final Locale currentLocale = getI18nService().getCurrentLocale();
		breadcrumbs.add(new Breadcrumb("#", vendorModelOptional.get().getName(currentLocale), null));
		model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);
		model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		final Optional<VendorPageModel> vendorPageModel = getVendorCMSPageService().getPageForVendor(vendorModelOptional.get());
		if (vendorPageModel.isPresent())
		{
			storeCmsPageInModel(model, vendorPageModel.get());
			return getViewForPage(vendorPageModel.get());
		}
		else
		{
			return FORWARD_PREFIX + "/404";
		}
	}

	@RequestMapping(value = VENDOR_CODE_PATH_VARIABLE_PATTERN + "/reviews", method = RequestMethod.GET)
	public String reviews(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode,
			@PathVariable("vendorCode") final String vendorCode, final Model model) throws CMSItemNotFoundException
	{
		final Optional<VendorData> vendorDataOptional = getVendorFacade().getVendorByCode(vendorCode);
		if (vendorDataOptional.isPresent())
		{
			final VendorData vendor = vendorDataOptional.get();
			model.addAttribute("vendor", vendor);

			final PageableData pageableData = createPageableData(page, 5, sortCode, showMode);
			final SearchPageData<VendorReviewData> pageData = getCustomerVendorReviewFacade().getPagedReviewsForVendor(vendorCode,
					pageableData);
			
			if(ZEROREVIEW == pageData.getResults().size() && DEFAULTPAGEINDEX != page){
				return REDIRECT_PREFIX +"/"+commerceCommonI18NService.getCurrentLanguage().getIsocode()+ "/v/"+vendorCode+"/reviews";
			}
			
			populateModel(model, pageData, showMode);
			model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

			final List<Breadcrumb> breadcrumbs = getSimpleBreadcrumbBuilder().getBreadcrumbs(null);
			breadcrumbs.add(new Breadcrumb("/v/" + vendorCode, vendor.getName(), null));
			breadcrumbs.add(new Breadcrumb("#", getMessageSource().getMessage(BREADCRUMB_VENDOR_REVIEWS, new Object[] {},
					getI18nService().getCurrentLocale()), null));
			model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);

			final ContentPageModel orderPage = getContentPageForLabelOrId("order");
			storeCmsPageInModel(model, orderPage);
			setUpMetaDataForContentPage(model, orderPage);
			model.addAttribute(
					CMS_PAGE_TITLE,
					getPageTitleResolver().resolveContentPageTitle(
							getMessageSource().getMessage(REVIEWS_PAGE_TITLE, new Object[] {}, getI18nService().getCurrentLocale())));

			return MarketplaceaddonControllerConstants.Views.Pages.Vendor.VendorReviewsPage;
		}

		return FORWARD_PREFIX + "/404";
	}

	protected VendorService getVendorService()
	{
		return vendorService;
	}

	protected VendorCMSPageService getVendorCMSPageService()
	{
		return vendorCMSPageService;
	}

	protected CustomerVendorReviewFacade getCustomerVendorReviewFacade()
	{
		return customerVendorReviewFacade;
	}

	protected VendorFacade getVendorFacade()
	{
		return vendorFacade;
	}

	protected ResourceBreadcrumbBuilder getSimpleBreadcrumbBuilder()
	{
		return simpleBreadcrumbBuilder;
	}

	protected String getPageName(final VendorModel vendorModel)
	{
		final String vendorName = vendorModel.getName();
		return StringUtils.isBlank(vendorName) ? vendorModel.getCode() : vendorName;
	}
}
