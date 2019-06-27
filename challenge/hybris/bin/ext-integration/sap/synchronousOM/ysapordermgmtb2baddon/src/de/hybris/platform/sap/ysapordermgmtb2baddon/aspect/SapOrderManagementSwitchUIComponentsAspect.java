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
package de.hybris.platform.sap.ysapordermgmtb2baddon.aspect;


import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Arrays;
import java.util.Map;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;


/**
 *
 * This aspect is used to apply the UI changes when switching between synchronous order management SOM and asynchronous
 * order management AOM.
 *
 */
public class SapOrderManagementSwitchUIComponentsAspect
{

	public static final Logger LOG = Logger.getLogger(SapOrderManagementSwitchUIComponentsAspect.class);
	public static final String SOM_ADDON_PREFIX = "addon:/ysapordermgmtb2baddon/";
	public static final String REDIRECT_PREFIX = "redirect:";
	public static final String FORWARD_PREFIX = "forward:";
	public static final String ADDON_PREFIX = "addon:";
	public static final String REDIRECT_MY_ACCOUNT = "redirect:/my-account";
	public static final String VALIDATIONS_ATTRIBUTE = "validations";
	public static final String SAP_ERROR = "sapError";


	private BaseStoreService baseStoreService;

	private SessionService sessionService;

	private ProductFacade productFacade;


	/**
	 * @return the productFacade
	 */
	public ProductFacade getProductFacade()
	{
		return productFacade;
	}


	/**
	 * @param productFacade
	 *           the productFacade to set
	 */
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}


	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}


	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * Apply the UI changes when switching between synchronous order management SOM and asynchronous order management
	 * AOM. For the SOM scenario add the add-on prefix to the UI component.
	 *
	 * @param pjp
	 * @return the UI component name
	 * @throws Throwable
	 */
	@SuppressWarnings("squid:S00112")
	public Object applyUIChanges(final ProceedingJoinPoint pjp) throws Throwable
	{

		String uiComponent = pjp.proceed().toString();
		
		boolean prodSubsMessage = false;
		boolean isBackendOffline = false;
		final List<CartModificationData> cartModifications = getSessionService().getAttribute(VALIDATIONS_ATTRIBUTE);
		if ("pages/cart/cartPage".equals(uiComponent) && null != cartModifications)
		{
			final Model model = (Model) pjp.getArgs()[0];
			if (!cartModifications.isEmpty())
			{
				model.addAttribute("validationData", cartModifications);
				if (SAP_ERROR.equals(cartModifications.iterator().next().getStatusCode()))
				{
					isBackendOffline = true;
					model.addAttribute("messageFlagError", isBackendOffline);
				}
			}
			pjp.getArgs()[0] = model;
		}
		else if ("fragments/cart/addToCartPopup".equals(uiComponent)
				&& null != cartModifications)
		{
			final Model model = (Model) pjp.getArgs()[1];
			final Map<String, Object> modelAtrributesMap = model.asMap();
			if (!CollectionUtils.isEmpty(modelAtrributesMap))
			{
				final ProductData productData = (ProductData) modelAtrributesMap.get("product");
				final OrderEntryData orderEntry = (OrderEntryData) modelAtrributesMap.get("entry");
				if (orderEntry != null)
				{

					final String code = orderEntry.getProduct().getCode();
					if (!productData.getCode().equals(code))
					{
						prodSubsMessage = true;

					}
					model.addAttribute("messageFlag", prodSubsMessage);
					model.addAttribute("product", productFacade.getProductForCodeAndOptions(code, Arrays.asList(ProductOption.BASIC)));
				}
			}
			model.addAttribute("validationData", cartModifications);
			pjp.getArgs()[1] = model;
		}

		if (isUIChangeRequired(uiComponent))
		{
			final StringBuilder prefix = new StringBuilder(SOM_ADDON_PREFIX);
			prefix.append(uiComponent);
			uiComponent = prefix.toString();

			logInfoMessage(pjp.getSignature().toString(), uiComponent, true);
		}
		else
		{
			logInfoMessage(pjp.getSignature().toString(), uiComponent, false);
		}

		return uiComponent;

	}





	/**
	 * Redirect the request to my account page since the requested page is not supported in the SOM scenario
	 *
	 * @param pjp
	 * @return my account page
	 * @throws Throwable
	 */
	@SuppressWarnings("squid:S00112")
	public Object redirectToMyAccountPage(final ProceedingJoinPoint pjp) throws Throwable
	{
		if (isSapOrderMgmtEnabled())
		{
			final String rediredtMsg = "Not found and the request has been redirected to my account page";
			logInfoMessage(pjp.getSignature().toString(), rediredtMsg, true);
			return REDIRECT_MY_ACCOUNT;
		}
		else
		{
			final String uiComponent = pjp.proceed().toString();
			logInfoMessage(pjp.getSignature().toString(), uiComponent, false);
			return uiComponent;
		}
	}


	/**
	 * Switch the UI component definition from the b2bacceleratoraddon add-on to the ysapordermgmtb2baddon add-on
	 *
	 * @param pjp
	 * @return the UI component name
	 * @throws Throwable
	 */
	@SuppressWarnings("squid:S00112")
	public Object switchAddonUIComponent(final ProceedingJoinPoint pjp) throws Throwable
	{

		String uiComponent = pjp.proceed().toString();

		if (isUIChangeRequired(uiComponent))
		{
			uiComponent = uiComponent.replace("b2bacceleratoraddon", "ysapordermgmtb2baddon");

			logInfoMessage(pjp.getSignature().toString(), uiComponent, true);
		}
		else
		{
			logInfoMessage(pjp.getSignature().toString(), uiComponent, false);
		}

		return uiComponent;

	}


	/**
	 * Log an information message
	 *
	 * @param methodSignature
	 * @param uiComponent
	 * @param somEnabled
	 */
	public static void logInfoMessage(final String methodSignature, final String uiComponent, final boolean somEnabled)
	{
		if (LOG.isInfoEnabled())
		{
			if (somEnabled)
			{
				LOG.info("The synchronous order scenario SOM is active and the intercepted method is [" + methodSignature
						+ "]. The synchronous order UI component is [" + uiComponent + "]");
			}
			else
			{
				LOG.info("The asynchronous order scenario AOM is active and the intercepted method is [" + methodSignature
						+ "]. The asynchronous order UI component is [" + uiComponent + "]");
			}
		}
	}

	/**
	 * Check if synchronous order management SOM is active
	 *
	 * @return true is SOM is active
	 */
	protected boolean isSapOrderMgmtEnabled()
	{
		return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration() != null
				&& getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().isSapordermgmt_enabled();

	}

	/**
	 * @param uiComponent
	 * @return true if the UI component switch is required
	 */
	protected boolean isUIChangeRequired(final String uiComponent)
	{

		return isSapOrderMgmtEnabled() && !uiComponent.contains(REDIRECT_PREFIX) && !uiComponent.contains(FORWARD_PREFIX);

	}


	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}