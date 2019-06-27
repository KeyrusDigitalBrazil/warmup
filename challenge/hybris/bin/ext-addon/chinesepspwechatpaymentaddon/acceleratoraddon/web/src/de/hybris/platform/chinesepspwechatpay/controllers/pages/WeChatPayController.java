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
/**
 *
 */
package de.hybris.platform.chinesepspwechatpay.controllers.pages;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.chinesepspwechatpay.constants.ControllerConstants;
import de.hybris.platform.chinesepspwechatpayservices.exception.WeChatPayException;
import de.hybris.platform.chinesepspwechatpayservices.order.WeChatPayOrderService;
import de.hybris.platform.chinesepspwechatpayservices.payment.impl.DefaultWeChatPayPaymentService;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.OpenIdRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.StartPaymentRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.UnifiedOrderRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.UserCodeRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayHttpClient;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@Scope("tenant")
@RequestMapping("/checkout/multi/wechat")
public class WeChatPayController extends AbstractPageController
{
	private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";
	private static final String MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL = "multiStepCheckoutSummary";

	@Value("#{ weChatPayConfiguration.timeout }")
	private Integer timeout;

	@Resource(name = "weChatPayConfiguration")
	private WeChatPayConfiguration weChatPayConfiguration;

	@Resource(name = "weChatPayHttpClient")
	private WeChatPayHttpClient weChatPayHttpClient;

	@Resource(name = "weChatPayOrderService")
	private WeChatPayOrderService weChatPayOrderService;

	@Resource(name = "wechatpayPaymentService")
	private DefaultWeChatPayPaymentService weChatPayPaymentService;

	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@RequestMapping(value = "/pay/" + ORDER_CODE_PATH_VARIABLE_PATTERN)
	public String process(@PathVariable final String orderCode, @RequestParam(value = "code", required = false) final String code,
			final HttpServletRequest request, final HttpServletResponse response, final Model model) throws CMSItemNotFoundException
	{
		try
		{
			if (StringUtils.isEmpty(code))
			{
				new UserCodeRequestProcessor(weChatPayConfiguration, request, response).process();
				return null;
			}

			final Optional<OrderModel> optional = weChatPayOrderService.getOrderByCode(orderCode);
			if (!optional.isPresent())
			{
				throw new WeChatPayException("Can't find order for code:" + orderCode);
			}

			final String openId = new OpenIdRequestProcessor(weChatPayConfiguration, weChatPayHttpClient, code).process();
			final String baseUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getCurrentBaseSite(), true, "");
			final String prepayId = new UnifiedOrderRequestProcessor(weChatPayConfiguration, weChatPayHttpClient, openId,
					optional.get(), request.getRemoteAddr(), baseUrl).process();
			request.setAttribute("paymentData", new StartPaymentRequestProcessor(weChatPayConfiguration, prepayId).process());
			request.setAttribute("orderCode", orderCode);
			model.addAttribute("weChatPayTimeout", timeout);

			final ContentPageModel multiCheckoutSummaryPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
			storeCmsPageInModel(model, multiCheckoutSummaryPage);
			setUpMetaDataForContentPage(model, multiCheckoutSummaryPage);

			return ControllerConstants.Views.Pages.Checkout.WeChatPayPage;
		}
		catch (final WeChatPayException e)//NOSONAR
		{
			return REDIRECT_PREFIX + "/checkout/multi/summary/checkPaymentResult/" + orderCode;
		}
	}

	@RequestMapping(value = "/startPay")
	public void paySuccess(@RequestParam(value = "orderCode", required = false) final String code)
	{
		weChatPayPaymentService.createTransactionForNewRequest(code);
	}
}
