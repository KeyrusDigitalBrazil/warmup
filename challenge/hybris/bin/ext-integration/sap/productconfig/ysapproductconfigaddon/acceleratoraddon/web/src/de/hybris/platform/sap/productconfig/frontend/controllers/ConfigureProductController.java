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


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.servicelayer.exceptions.BusinessException;


/**
 * Default Controller for the dynamic product configuration content page.
 *
 * @see UpdateConfigureProductController
 */
@Controller
@RequestMapping()
public class ConfigureProductController extends AbstractProductConfigController
{
	private static final Logger LOGGER = Logger.getLogger(ConfigureProductController.class);

	/**
	 * Renders the dynamic product configuration content page for the given product. In case a configuration session
	 * already exists for this product in the user session, it will be restored. Otherwise a new session is initiated, so
	 * that the runtime configuration is populated with the default values.
	 *
	 * @param productCodeEncoded
	 *           coded of the configurable product
	 * @param model
	 *           view model
	 * @param request
	 *           HTTP-Request
	 * @param redirectAttributes
	 *           redirect attributes
	 * @return view name
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/**/{productCode:.*}/configuratorPage/"
			+ SapproductconfigfrontendWebConstants.CONFIGURATOR_TYPE, method =
	{ RequestMethod.GET, RequestMethod.POST })
	public String configureProduct(@PathVariable("productCode") final String productCodeEncoded, final Model model,
			final HttpServletRequest request, final RedirectAttributes redirectAttributes) throws BusinessException
	{
		final String productCode = decodeWithScheme(productCodeEncoded, UTF_8);
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Config GET received for '" + productCode + "' - Current Session: '"
					+ getSessionService().getCurrentSession().getSessionId() + "'");
		}

		final ConfigurationData configData = populateConfigurationModel(request, model, productCode);
		getUiRecorder().recordUiAccess(configData, productCode);
		if (hasProductVariantApplied(productCode, configData))
		{
			return SapproductconfigfrontendWebConstants.CONFIG_PAGE_VIEW_NAME;
		}
		else
		{
			model.asMap().entrySet().stream()
					.forEach(entry -> redirectAttributes.addFlashAttribute(entry.getKey(), entry.getValue()));
			return REDIRECT_PREFIX + ROOT + configData.getKbKey().getProductCode() + SapproductconfigfrontendWebConstants.CONFIG_URL;
		}
	}

	protected boolean hasProductVariantApplied(final String productCode, final ConfigurationData configData)
	{
		return configData.getKbKey().getProductCode().equals(productCode);
	}

	protected ConfigurationData populateConfigurationModel(final HttpServletRequest request, final Model model,
			final String productCode) throws BusinessException
	{
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getBreadcrumbBuilder().getBreadcrumbs(productCode, getCartEntryNumber(productCode)));
		populateCMSAttributes(model);

		if (model.containsAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE))
		{
			return (ConfigurationData) model.asMap().get(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE);
		}


		final KBKeyData kbKey = new KBKeyData();
		kbKey.setProductCode(productCode);
		final ConfigurationData configData;

		UiStatus uiStatus;
		if (!isConfigRemoved(productCode))
		{
			// old ui status
			uiStatus = getSessionAccessFacade().getUiStatusForProduct(productCode);

			configData = reloadConfigurationByKBKey(kbKey, uiStatus);
			if (uiStatus == null)
			{
				uiStatus = getSessionAccessFacade().getUiStatusForProduct(configData.getKbKey().getProductCode());
			}
			configData.setQuantity(uiStatus.getQuantity());
		}
		else
		{
			final long quantity = getQuantity(request);
			configData = loadNewConfiguration(kbKey);
			configData.setQuantity(quantity);

			// new ui status is created by  loadNewConfiguration, product code can change in case of variants
			uiStatus = getSessionAccessFacade().getUiStatusForProduct(configData.getKbKey().getProductCode());
			uiStatus.setQuantity(quantity);
		}

		populateProductData(configData.getKbKey().getProductCode(), model, request);
		getUiStateHandler().compileGroupForDisplay(configData, uiStatus);

		setCartEntryLinks(configData);


		model.addAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, configData);

		final BindingResult errors = getBindingResultForConfigAndSaveUiStatus(configData, uiStatus);
		model.addAttribute(BindingResult.MODEL_KEY_PREFIX + SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, errors);
		getUiStateHandler().countNumberOfUiErrorsPerGroup(configData.getGroups());

		getUiStateHandler().handleConflictSolverMessage(uiStatus, getUiStatusSync().getNumberOfConflicts(configData), model);
		getUiStateHandler().handleProductConfigMessages(configData, model);

		logModelmetaData(configData);

		return configData;
	}





	protected long getQuantity(final HttpServletRequest request)
	{
		final String qtyString = request.getParameter("qty");
		long qty = 1;

		if (StringUtils.isNotEmpty(qtyString))
		{
			qty = Long.parseLong(qtyString);
		}

		return qty;
	}
}
