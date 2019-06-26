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
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractSearchPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.marketplaceaddon.controllers.MarketplaceaddonControllerConstants;
import de.hybris.platform.marketplacefacades.vendor.VendorFacade;
import de.hybris.platform.marketplacefacades.vendor.data.VendorData;
import de.hybris.platform.util.Config;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping(value = "/vendors")
public class VendorIndexPageController extends AbstractSearchPageController
{
	@Resource(name = "vendorFacade")
	private VendorFacade vendorFacade;

	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder simpleBreadcrumbBuilder;

	private static final String VENDOR_INDEX_PAGE = "vendorIndexPage";
	private static final String BREADCRUMBS_ATTR = "breadcrumbs";
	private static final int VENDORS_IN_PAGE = 10;
	private static final String MEDIA_FORMAT_CONFIG = "marketplaceaddon.vendor.indexPage.logo.format";
	private static final String LOGO_FORMAT = "logoFormat";

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String vendorIndexPage(@RequestParam(value = "page", defaultValue = "0") final int page,
			@RequestParam(value = "show", defaultValue = "Page") final ShowMode showMode,
			@RequestParam(value = "sort", required = false) final String sortCode, final Model model)
			throws CMSItemNotFoundException
	{
		final PageableData pageableData = createPageableData(page, VENDORS_IN_PAGE, sortCode, showMode);
		final SearchPageData<VendorData> pagedVendorData = vendorFacade.getPagedIndexVendors(pageableData);
		populateModel(model, pagedVendorData, showMode);

		final String mediaFormat = Config.getParameter(MEDIA_FORMAT_CONFIG);
		model.addAttribute(LOGO_FORMAT, mediaFormat);
		final List<Breadcrumb> breadcrumbs = simpleBreadcrumbBuilder.getBreadcrumbs(null);
		final ContentPageModel vendorIndexPage = getContentPageForLabelOrId(VENDOR_INDEX_PAGE);
		breadcrumbs.add(new Breadcrumb("#", vendorIndexPage.getTitle(), null));
		model.addAttribute(BREADCRUMBS_ATTR, breadcrumbs);

		storeCmsPageInModel(model, vendorIndexPage);
		setUpMetaDataForContentPage(model, vendorIndexPage);
		return MarketplaceaddonControllerConstants.Views.Pages.Vendor.VendorIndexPage;
	}

}



