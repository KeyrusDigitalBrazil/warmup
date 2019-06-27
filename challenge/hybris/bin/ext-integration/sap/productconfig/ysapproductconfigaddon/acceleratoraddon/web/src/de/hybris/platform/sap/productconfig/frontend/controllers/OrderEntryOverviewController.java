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

import de.hybris.platform.sap.productconfig.facades.ConfigurationOrderIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationQuoteIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationSavedCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * Controller for OrderEntry entities (e.g. quote, order history) Configuration Overview integration
 */
@Controller
@RequestMapping()
public class OrderEntryOverviewController extends AbstractConfigurationOverviewController
{

	private static final String LOG_MSG_DISPLAY_OV = "Display configOverview for %s: code='%s' entryNumber='%s'";
	private static final Logger LOGGER = Logger.getLogger(OrderEntryOverviewController.class.getName());

	@Resource(name = "sapProductConfigQuoteIntegrationFacade")
	private ConfigurationQuoteIntegrationFacade configurationQuoteIntegrationFacade;
	@Resource(name = "sapProductConfigOrderIntegrationFacade")
	private ConfigurationOrderIntegrationFacade configurationOrderIntegrationFacade;
	@Resource(name = "sapProductConfigSavedCartIntegrationFacade")
	private ConfigurationSavedCartIntegrationFacade configurationSavedCartIntegrationFacade;


	/**
	 * This method handles the case, when the configuration overview is displayed in the context of a quotation.
	 *
	 * @param quotationId
	 * @param entryNumber
	 * @param model
	 * @param redirectModel
	 * @param request
	 * @return overview page view name
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/my-account/my-quotes/{quoteNumber}/{entryNumber}"
			+ SapproductconfigfrontendWebConstants.GENERIC_CONFIG_DISPLAY + "/"
			+ SapproductconfigfrontendWebConstants.CONFIGURATOR_TYPE, method = RequestMethod.GET)
	public String getQuotationOverview(@PathVariable("quoteNumber") final String quotationId,
			@PathVariable("entryNumber") final int entryNumber, final Model model, final RedirectAttributes redirectModel,
			final HttpServletRequest request) throws BusinessException
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(String.format(LOG_MSG_DISPLAY_OV, "quotation", quotationId, Integer.valueOf(entryNumber)));
		}

		final ConfigurationOverviewData configOverviewData = getConfigurationQuoteIntegrationFacade().getConfiguration(quotationId,
				entryNumber);
		if (configOverviewData == null)
		{
			return getConfigurationErrorHandler().handleErrorFromQuotes(quotationId, entryNumber, redirectModel);
		}
		getAbstractOrderOverview(quotationId, entryNumber, model, request, configOverviewData, OverviewMode.QUOTATION_OVERVIEW);

		return SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME;
	}

	protected void getAbstractOrderOverview(final String code, final int entryNumber, final Model model,
			final HttpServletRequest request, final ConfigurationOverviewData configOverviewData, final OverviewMode overviewMode)
			throws BusinessException
	{
		final ConfigurationOverviewData enrichedOverviewData = enrichOverviewData(code, configOverviewData);
		final UiStatus uiStatus = initializeUIStatusForAbstractOrder(enrichedOverviewData);

		final OverviewUiData overviewUiData = initializeOverviewUiData(enrichedOverviewData.getId(), code, entryNumber,
				enrichedOverviewData.getSourceDocumentId(), overviewMode);
		prepareUiModel(request, model, uiStatus, overviewUiData, enrichedOverviewData);

		recordUiAccessOverview(enrichedOverviewData);
	}

	/**
	 * This method handles the case, when the overview page is displayed in context of an order.
	 *
	 * @param orderCode
	 * @param entryNumber
	 * @param model
	 * @param redirectModel
	 * @param request
	 * @return overview page view name
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/my-account/order/{orderCode}/{entryNumber}"
			+ SapproductconfigfrontendWebConstants.GENERIC_CONFIG_DISPLAY + "/"
			+ SapproductconfigfrontendWebConstants.CONFIGURATOR_TYPE, method = RequestMethod.GET)
	public String getOrderOverview(@PathVariable("orderCode") final String orderCode,
			@PathVariable("entryNumber") final int entryNumber, final Model model, final RedirectAttributes redirectModel,
			final HttpServletRequest request) throws BusinessException
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(String.format(LOG_MSG_DISPLAY_OV, "order", orderCode, Integer.valueOf(entryNumber)));
		}

		final ConfigurationOverviewData configOverviewData = getConfigurationOrderIntegrationFacade().getConfiguration(orderCode,
				entryNumber);
		if (configOverviewData == null)
		{
			return getConfigurationErrorHandler().handleErrorFromOrderHistory(orderCode, entryNumber, redirectModel);
		}
		getAbstractOrderOverview(orderCode, entryNumber, model, request, configOverviewData, OverviewMode.ORDER_OVERVIEW);

		return SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME;
	}

	/**
	 * This method handles the case, when the overview page is displayed in context of a saved cart.
	 *
	 * @param cartCode
	 * @param entryNumber
	 * @param model
	 * @param redirectModel
	 * @param request
	 * @return overviewpage view name
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/my-account/saved-carts/{cartCode}/{entryNumber}"
			+ SapproductconfigfrontendWebConstants.GENERIC_CONFIG_DISPLAY + "/"
			+ SapproductconfigfrontendWebConstants.CONFIGURATOR_TYPE, method = RequestMethod.GET)
	public String getSavedCartOverview(@PathVariable("cartCode") final String cartCode,
			@PathVariable("entryNumber") final int entryNumber, final Model model, final RedirectAttributes redirectModel,
			final HttpServletRequest request) throws BusinessException
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(String.format(LOG_MSG_DISPLAY_OV, "saved cart", cartCode, Integer.valueOf(entryNumber)));
		}

		final ConfigurationOverviewData configOverviewData = getConfigurationSavedCartIntegrationFacade().getConfiguration(cartCode,
				entryNumber);
		if (configOverviewData == null)
		{
			return getConfigurationErrorHandler().handleErrorFromSavedCarts(cartCode, entryNumber, redirectModel);
		}

		getAbstractOrderOverview(cartCode, entryNumber, model, request, configOverviewData, OverviewMode.SAVED_CART_OVERVIEW);

		return SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME;
	}



	/**
	 * @return the configurationSavedCartIntegrationFacade
	 */
	public ConfigurationSavedCartIntegrationFacade getConfigurationSavedCartIntegrationFacade()
	{
		return configurationSavedCartIntegrationFacade;
	}

	/**
	 * @param configurationSavedCartIntegrationFacade
	 *           the configurationSavedCartIntegrationFacade to set
	 */
	public void setConfigurationSavedCartIntegrationFacade(
			final ConfigurationSavedCartIntegrationFacade configurationSavedCartIntegrationFacade)
	{
		this.configurationSavedCartIntegrationFacade = configurationSavedCartIntegrationFacade;
	}

	protected void recordUiAccessOverview(final ConfigurationOverviewData configOverviewData)
	{
		if (configOverviewData.getId() != null)
		{
			getUiRecorder().recordUiAccessOverview(configOverviewData, configOverviewData.getProductCode());
		}
		else
		{
			getUiRecorder().recordUiAccessVariantOverview(configOverviewData.getProductCode());
		}
	}

	protected OverviewUiData initializeOverviewUiData(final String configId, final String code, final int entryNumber,
			final String sourceDocumentId, final OverviewMode overviewMode)
	{
		final OverviewUiData overviewUiData = new OverviewUiData();
		setOverviewMode(configId, overviewMode, overviewUiData);
		overviewUiData.setSourceDocumentId(sourceDocumentId);
		overviewUiData.setAbstractOrderCode(code);
		overviewUiData.setAbstractOrderEntryNumber(Integer.valueOf(entryNumber));
		return overviewUiData;
	}

	protected void setOverviewMode(final String configId, final OverviewMode overviewMode, final OverviewUiData overviewUiData)
	{
		overviewUiData.setOverviewMode(overviewMode);
		if (configId == null)
		{
			if (OverviewMode.QUOTATION_OVERVIEW.equals(overviewMode))
			{
				overviewUiData.setOverviewMode(OverviewMode.QUOTATION_VARIANT_OVERVIEW);
			}
			else if (OverviewMode.ORDER_OVERVIEW.equals(overviewMode))
			{
				overviewUiData.setOverviewMode(OverviewMode.ORDER_VARIANT_OVERVIEW);
			}
			else if (OverviewMode.SAVED_CART_OVERVIEW.equals(overviewMode))
			{
				overviewUiData.setOverviewMode(OverviewMode.SAVED_CART_VARIANT_OVERVIEW);
			}
		}
	}

	protected UiStatus initializeUIStatusForAbstractOrder(final ConfigurationOverviewData overviewData)
	{
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setHideImageGallery(true);

		initializeFilterListsInUiStatus(overviewData, uiStatus);
		return uiStatus;
	}

	protected ConfigurationOverviewData enrichOverviewData(final String code, final ConfigurationOverviewData configOverviewData)
	{

		ConfigurationOverviewData enrichedOverviewData = configOverviewData;
		if (enrichedOverviewData.getId() != null)
		{
			enrichedOverviewData = getConfigurationOverviewFacade().getOverviewForConfiguration(enrichedOverviewData.getId(),
					enrichedOverviewData);
		}
		enrichedOverviewData.setSourceDocumentId(code);
		return enrichedOverviewData;
	}

	protected ConfigurationQuoteIntegrationFacade getConfigurationQuoteIntegrationFacade()
	{
		return configurationQuoteIntegrationFacade;
	}

	/**
	 * @param configurationQuoteIntegrationFacade
	 *           cpq facaded for integration with hybris quotation
	 */
	public void setConfigurationQuoteIntegrationFacade(
			final ConfigurationQuoteIntegrationFacade configurationQuoteIntegrationFacade)
	{
		this.configurationQuoteIntegrationFacade = configurationQuoteIntegrationFacade;
	}

	protected ConfigurationOrderIntegrationFacade getConfigurationOrderIntegrationFacade()
	{
		return configurationOrderIntegrationFacade;
	}

	/**
	 * @param configurationOrderIntegrationFacade
	 *           cpq facaded for integration with hybris order
	 */
	public void setConfigurationOrderIntegrationFacade(
			final ConfigurationOrderIntegrationFacade configurationOrderIntegrationFacade)
	{
		this.configurationOrderIntegrationFacade = configurationOrderIntegrationFacade;
	}

}
