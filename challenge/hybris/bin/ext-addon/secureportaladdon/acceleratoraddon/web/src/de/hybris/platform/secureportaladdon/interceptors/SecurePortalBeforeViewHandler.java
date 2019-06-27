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
package de.hybris.platform.secureportaladdon.interceptors;

import static de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController.CMS_PAGE_MODEL;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController.CMS_PAGE_TITLE;

import de.hybris.platform.acceleratorservices.storefront.data.MetaElementData;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.secureportaladdon.constants.SecureportaladdonWebConstants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;


/**
 *
 * Implementation of BeforeViewHandler.
 *
 */
public class SecurePortalBeforeViewHandler implements BeforeViewHandler
{
	private static final String SIDE_CONTENT = "SideContent";
	private static final String ENABLE_REGISTRATION = "enableRegistration";
	private CMSSiteService cmsSiteService;
	private CMSPageService cmsPageService;
	private PageTitleResolver pageTitleResolver;
	private Map<String, Map<String, String>> spViewMap;

	public static final String VIEW_NAME_MAP_KEY = "viewName";
	public static final String CMS_PAGE_ID_MAP_KEY = "cmsPageId";
	public static final String SLOTS_MAP_KEY = "slots";

	@Required
	public void setSpViewMap(final Map<String, Map<String, String>> spViewMap)
	{
		this.spViewMap = spViewMap;
	}

	@Required
	public void setPageTitleResolver(final PageTitleResolver pageTitleResolver)
	{
		this.pageTitleResolver = pageTitleResolver;
	}

	@Required
	public void setCmsPageService(final CMSPageService cmsPageService)
	{
		this.cmsPageService = cmsPageService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	protected boolean isSiteSecured()
	{
		final CMSSiteModel site = cmsSiteService.getCurrentSite();
		return site.isRequiresAuthentication();
	}

	/** Adds model flag to switch registration link in the Login View */
	@Override
	public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView)
			throws Exception
	{
		final ModelMap model = modelAndView.getModelMap();
		final String viewName = modelAndView.getViewName();

		model.addAttribute(ENABLE_REGISTRATION, cmsSiteService.getCurrentSite().isEnableRegistration());

		if (isSiteSecured() && spViewMap.containsKey(viewName))
		{
			model.remove(CMS_PAGE_MODEL);

			final ContentPageModel scpPageModel = cmsPageService.getPageForLabelOrId(spViewMap.get(viewName)
					.get(CMS_PAGE_ID_MAP_KEY));
			model.addAttribute(CMS_PAGE_MODEL, scpPageModel);
			model.addAttribute(CMS_PAGE_TITLE, pageTitleResolver.resolveContentPageTitle(scpPageModel.getTitle()));
			replaceModelMetaData(model, scpPageModel.getKeywords(), scpPageModel.getDescription());
			replaceSideContentSlotComponents(scpPageModel, model);

			modelAndView
					.setViewName(SecureportaladdonWebConstants.VIEW_PAGE_PREFIX + spViewMap.get(viewName).get(VIEW_NAME_MAP_KEY));
		}
	}

	protected void replaceSideContentSlotComponents(final ContentPageModel contentPageModel, final ModelMap model)
	{
		final HashMap<String, ContentSlotModel> slots = (HashMap<String, ContentSlotModel>) model.get(SLOTS_MAP_KEY);
		if (slots != null && slots.get(SIDE_CONTENT) != null)
		{
			slots.get(SIDE_CONTENT).setCmsComponents(
					getContentSlotsForPageAsMap(contentPageModel).get(SIDE_CONTENT).getCmsComponents());
		}

	}

	protected Map<String, ContentSlotModel> getContentSlotsForPageAsMap(final AbstractPageModel page)
	{
		if (page == null)
		{
			return Collections.emptyMap();
		}

		final Collection<ContentSlotData> slotModels = cmsPageService.getContentSlotsForPage(page);

		final HashMap<String, ContentSlotModel> slots = new HashMap<String, ContentSlotModel>(slotModels.size());
		for (final ContentSlotData contentSlot : slotModels)
		{
			slots.put(contentSlot.getPosition(), contentSlot.getContentSlot());
		}

		return slots;
	}

	protected void replaceModelMetaData(final ModelMap model, final String metaKeywords, final String metaDescription)
	{
		final List<MetaElementData> metadata = new LinkedList<MetaElementData>();
		metadata.add(createMetaElement("keywords", metaKeywords));
		metadata.add(createMetaElement("description", metaDescription));
		model.remove("metatags");
		model.addAttribute("metatags", metadata);
	}

	protected MetaElementData createMetaElement(final String name, final String content)
	{
		final MetaElementData element = new MetaElementData();
		element.setName(name);
		element.setContent(content);
		return element;
	}
}
