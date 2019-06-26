/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.frontend.CPQOverviewActionType;
import de.hybris.platform.sap.productconfig.frontend.FilterData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.services.exceptions.ProductConfigurationAccessException;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


/**
 * Default Controller implementation to be used for the configuration overview page.
 *
 * @see VariantOverviewController
 */
@Controller
@RequestMapping()
public class ConfigurationOverviewController extends AbstractConfigurationOverviewController
{
	private static final String AJAX_VIEW_NAME = SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME
			+ SapproductconfigfrontendWebConstants.AJAX_SUFFIX;

	private static final Logger LOGGER = Logger.getLogger(ConfigurationOverviewController.class.getName());

	/**
	 * Renders the product config overview page.
	 *
	 * @param configIdEncoded
	 *           product code of the configurable product
	 * @param model
	 *           view model
	 * @param request
	 *           http request
	 * @return view name
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/**/{configId:.*}"
			+ SapproductconfigfrontendWebConstants.CONFIG_OVERVIEW_URL, method = RequestMethod.GET)
	public String getConfigurationOverview(@PathVariable("configId") final String configIdEncoded, final Model model,
			final HttpServletRequest request) throws BusinessException
	{
		final String configId = decodeWithScheme(configIdEncoded, UTF_8);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(String.format("Config GET received for '%s' - Current Session: '%s'", configId,
					getSessionService().getCurrentSession().getSessionId()));
		}

		final String cartItemKey = getCartItemByConfigId(configId);
		if (StringUtils.isBlank(cartItemKey))
		{
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug(String.format("No cart item is found for config Id '%s' - Current Session: '%s'", configId,
						getSessionService().getCurrentSession().getSessionId()));
			}

			return REDIRECT_PREFIX + ROOT;
		}

		final String productCode = getProductCodeForCartItem(cartItemKey);

		final ConfigurationOverviewData configOverviewData;
		try
		{
			configOverviewData = populateConfigurationModel(productCode, configId, null);
		}
		catch (final ProductConfigurationAccessException pae)
		{
			LOGGER.warn("Not allowed to read configuration", pae);
			return getConfigurationErrorHandler().handleConfigurationAccessError();
		}

		final UiStatus uiStatus = getOrCreateUiStatusForCartEntry(cartItemKey);
		initializeFilterListsInUiStatus(configOverviewData, uiStatus);
		final OverviewUiData overviewUiData = initializeOverviewUiDataForConfiguration();
		prepareUiModel(request, model, uiStatus, overviewUiData, configOverviewData);
		getUiRecorder().recordUiAccessOverview(configOverviewData, configId);

		return SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME;
	}

	protected UiStatus getOrCreateUiStatusForCartEntry(final String cartItemKey)
	{
		UiStatus uiStatus = getSessionAccessFacade().getUiStatusForCartEntry(cartItemKey);
		if (uiStatus == null)
		{
			uiStatus = new UiStatus();
			uiStatus.setHideImageGallery(true);
			getSessionAccessFacade().setUiStatusForCartEntry(cartItemKey, uiStatus);
		}
		return uiStatus;
	}

	protected OverviewUiData initializeOverviewUiDataForConfiguration()
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		overviewUiData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		return overviewUiData;
	}


	/**
	 * Updates the product config overview page. For example if a filter value was changed.
	 *
	 * @param overviewUIData
	 *           data currently displayed on overview page
	 * @param model
	 *           view model
	 * @param request
	 *           http request
	 * @return view name
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/cpq" + SapproductconfigfrontendWebConstants.CONFIG_OVERVIEW_URL, method = RequestMethod.POST)
	@ResponseBody
	public ModelAndView updateConfiguationOverview(
			@ModelAttribute(SapproductconfigfrontendWebConstants.OVERVIEWUIDATA_ATTRIBUTE) final OverviewUiData overviewUIData,
			final Model model, final HttpServletRequest request) throws BusinessException
	{
		final String productCode = overviewUIData.getProductCode();
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(String.format("Config POST received, current Session: '%s'",
					getSessionService().getCurrentSession().getSessionId()));
		}
		final ModelAndView view;

		final UiStatus uiStatus = getUiStatusForOverview(getCartItemByConfigId(overviewUIData.getConfigId()), overviewUIData);

		if (uiStatus == null)
		{
			cleanUpSessionAttribute(productCode);
			view = getConfigurationErrorHandler().handleErrorForAjaxRequest(request, model);
		}
		else
		{
			final ConfigurationOverviewData configOverviewData = populateConfigurationModel(productCode,
					overviewUIData.getConfigId(), null);
			handleCPQAction(overviewUIData, configOverviewData, uiStatus);
			populateConfigurationModel(productCode, overviewUIData.getConfigId(), configOverviewData);
			prepareUiModel(request, model, uiStatus, overviewUIData, configOverviewData);
			view = new ModelAndView(AJAX_VIEW_NAME);
		}
		return view;
	}

	protected void handleCPQAction(final OverviewUiData overviewUIData, final ConfigurationOverviewData configOverviewData,
			final UiStatus uiStatus) throws BusinessException
	{
		if (overviewUIData.getCpqAction() != null)
		{
			if (CPQOverviewActionType.TOGGLE_IMAGE_GALLERY.equals(overviewUIData.getCpqAction()))
			{
				uiStatus.setHideImageGallery(!uiStatus.isHideImageGallery());
			}
			if (CPQOverviewActionType.APPLY_FILTER.equals(overviewUIData.getCpqAction()))
			{
				updateCsticFilterList(overviewUIData, uiStatus);
				updateAppliedFilters(uiStatus, configOverviewData);

				updateGroupFilterList(overviewUIData, uiStatus);
				updateGroups(uiStatus, configOverviewData);
			}
		}
		setUiStatusForOverviewInSession(uiStatus, getCartItemByConfigId(overviewUIData.getConfigId()), overviewUIData);
	}

	protected void updateGroups(final UiStatus uiStatus, final ConfigurationOverviewData configOverviewData)
	{
		final Set<String> filteredOutGroups = new HashSet<>();

		final List<FilterData> maxFilterDataList = uiStatus.getMaxGroupFilterList();

		for (final FilterData filterData : maxFilterDataList)
		{
			if (filterData.isSelected())
			{
				filteredOutGroups.add(filterData.getKey());
			}
		}
		configOverviewData.setAppliedGroupFilters(filteredOutGroups);
	}

	protected void updateGroupFilterList(final OverviewUiData overviewUIData, final UiStatus uiStatus)
	{
		final List<FilterData> uiFilterDataList = overviewUIData.getGroupFilterList();
		final List<FilterData> maxFilterDataList = uiStatus.getMaxGroupFilterList();

		if (uiFilterDataList != null)
		{
			final HashMap<String, FilterData> maxMap = new HashMap<>();
			for (final FilterData filterData : maxFilterDataList)
			{
				maxMap.put(filterData.getKey(), filterData);
			}
			for (final FilterData filterData : uiFilterDataList)
			{
				maxMap.get(filterData.getKey()).setSelected(filterData.isSelected());
			}
		}
	}

	protected ConfigurationOverviewData populateConfigurationModel(final String productCode, final String configId,
			final ConfigurationOverviewData overview)
	{
		final ConfigurationOverviewData configOverview = getConfigurationOverviewFacade().getOverviewForConfiguration(configId,
				overview);

		if (productCode != null)
		{
			configOverview.setProductCode(productCode);
		}
		return configOverview;
	}

	protected void updateCsticFilterList(final OverviewUiData overviewUIData, final UiStatus uiStatus)
	{
		if (overviewUIData != null)
		{
			final List<FilterData> csticFilterList = overviewUIData.getCsticFilterList();
			uiStatus.setCsticFilterList(csticFilterList);
		}
	}

	protected void updateAppliedFilters(final UiStatus uiStatus, final ConfigurationOverviewData configOverviewData)
	{
		if (configOverviewData == null)
		{
			return;
		}
		final List<FilterEnum> appliedFilters = new ArrayList<>();
		appliedFilters.add(FilterEnum.VISIBLE);

		final List<FilterData> filterDataList = uiStatus.getCsticFilterList();
		for (final FilterData filterdata : filterDataList)
		{
			if (filterdata.isSelected())
			{
				appliedFilters.add(FilterEnum.valueOf(filterdata.getKey()));
			}
		}

		configOverviewData.setAppliedCsticFilters(appliedFilters);
	}
}
