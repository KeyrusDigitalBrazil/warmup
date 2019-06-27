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
package de.hybris.platform.chinesepspalipaymock.controller.pages;

import de.hybris.platform.chinesepspalipaymock.controller.AbstractController;
import de.hybris.platform.chinesepspalipaymock.controller.AlipayMockControllerConstants;
import de.hybris.platform.chinesepspalipaymock.service.MockService;
import de.hybris.platform.chinesepspalipaymock.utils.imported.CSRFRequestDataValueProcessor;
import de.hybris.platform.chinesepspalipaymock.utils.imported.XSSFilterUtil;
import de.hybris.platform.chinesepspalipayservices.payment.DefaultAlipayPaymentService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping(value = "/checkout/multi/alipay/mock/gateway.do")
public class AlipayMockController extends AbstractController
{
	private static final String PARAM_SERVICE = "service";
	private static final String BASE_GATE_WAY = "baseGateWay";
	private static final String STOREFRONT = "storefront";
	private static final String TOTAL_FEE = "total_fee";
	private static final String OUT_TRADE_NO = "out_trade_no";
	private static final String NOTIFY_ERROR = "notify_error";
	private static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
	private static final String TRADE_STATUS = "trade_status";
	private static final String PARAM_NOTIFY = "notify";
	private static final String PARAM_ACTION = "action";
	private static final String ERROR_CODE = "error_code";
	private static final String DETAIL_DATA = "detail_data";
	private static final Double DOUBLE_ZERO = 0.00;

	private static final String[] DISALLOWED_FIELDS = new String[] {};

	private static final Logger LOG = Logger.getLogger(AlipayMockController.class);

	@Resource
	private MockService mockService;

	@Resource(name = "alipayPaymentService")
	private DefaultAlipayPaymentService defaultAlipayPaymentService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@InitBinder
	public void initBinder(final WebDataBinder binder)
	{
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	/**
	 * Opens alipay mock landing page
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @return page the alipay mock landing page
	 * @throws UnsupportedEncodingException
	 *            throw UnsupportedEncodingException when request parameters contain unsupported encoding chars
	 * @throws AlipayMockException
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String doGetGateWay(final Model model, final HttpServletRequest request)
			throws UnsupportedEncodingException
	{
		final Map<String, String[]> requestParamMap = request.getParameterMap();
		if (requestParamMap == null)
		{
			return AlipayMockControllerConstants.Pages.AlipayMockPage;
		}
		model.addAttribute(BASE_GATE_WAY, request.getRequestURL().toString());

		final Map<String, String> clearParams = removeUselessValue(requestParamMap);
		this.setCSRFToken(clearParams, request);

		final String service = request.getParameter(PARAM_SERVICE);
		if (service == null)
		{
			return AlipayMockControllerConstants.Pages.AlipayMockPage;
		}
		XSSFilterUtil.filter(service);

		final boolean signIsValid = isSignValid(clearParams);
		model.addAttribute("signIsValid", Boolean.valueOf(signIsValid));

		// TOMCAT does not accept "^" in URL parameter, hence encode it here
		final String detailData = clearParams.get(DETAIL_DATA);
		if (StringUtils.isNotEmpty(detailData) && isValidDetailData(detailData))
		{
			model.addAttribute(DETAIL_DATA, detailData);
			clearParams.put(DETAIL_DATA, URLEncoder.encode(detailData, "UTF-8"));
		}

		if(isValidClearParams(clearParams)){
			model.addAttribute("params", clearParams);
		}
		
		final String tradeNo = clearParams.get(OUT_TRADE_NO);
		if (StringUtils.isNotEmpty(tradeNo) && isValidNumeric(tradeNo))
		{
			model.addAttribute(OUT_TRADE_NO, tradeNo);
		}
		model.addAttribute(TOTAL_FEE, String.valueOf(NumberUtils.toDouble(clearParams.get(TOTAL_FEE), DOUBLE_ZERO)));
		model.addAttribute(STOREFRONT, (StringUtils.substringBetween(request.getContextPath(), "/")));

		if ("refund_fastpay_by_platform_pwd".equals(service))
		{
			return AlipayMockControllerConstants.Pages.AlipayRefundPage;
		}

		return AlipayMockControllerConstants.Pages.AlipayMockPage;
	}

	/**
	 * Opens alipay refund landing page
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @return alipay refund landing page
	 */
	@RequestMapping(value = "/refund", method = RequestMethod.GET)
	public String view(final Model model, final HttpServletRequest request)
	{
		final Map<String, String[]> requestParamMap = request.getParameterMap();
		if (requestParamMap == null)
		{
			return AlipayMockControllerConstants.Pages.AlipayRefundTestPage;
		}
		model.addAttribute(BASE_GATE_WAY, request.getRequestURL().toString());

		model.addAttribute(STOREFRONT, (StringUtils.substringBetween(request.getContextPath(), "/")));

		final Map<String, String> clearParams = removeUselessValue(requestParamMap);
		setCSRFToken(clearParams, request);

		model.addAttribute("baseSites", baseSiteService.getAllBaseSites());
		return AlipayMockControllerConstants.Pages.AlipayRefundTestPage;
	}

	/**
	 * Handles refunding process
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @param response
	 *           the http response
	 * @param orderCode
	 *           order code
	 * @param baseSite
	 *           base site name
	 * @return refund result
	 * @throws IOException
	 *            throw IOException when refund request failed
	 *
	 */
	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public @ResponseBody String doRefundRequest(final Model model, final HttpServletRequest request,
			final HttpServletResponse response, final String orderCode, final String baseSite)
	{
		if (baseSite != null)
		{
			baseSiteService.setCurrentBaseSite(baseSite, false);
		}

		if (orderCode == null || orderCode.isEmpty())
		{
			return "Error : Please input order #";
		}
		else
		{
			try
			{
				final Optional<String> refundUrl = defaultAlipayPaymentService.getRefundRequestUrl(orderCode);
				if (refundUrl.isPresent())
				{
					return "redirect:" + refundUrl.get();
				}
			}
			catch (final ModelNotFoundException e) //NOSONAR
			{
				return "Error : Fail to generate refund URL due to invalid order code";
			}
		}
		return "Error : Fail to generate refund URL due to invalid order code";
	}


	/**
	 * Handles verification
	 *
	 * @param response
	 *           the http response
	 * @throws IOException
	 *            throw IOException when outputting failed
	 */
	@RequestMapping(value = "/notify.verify")
	public void doNotifyVerify(final HttpServletResponse response) throws IOException
	{
		response.getWriter().print("true");
	}


	/**
	 * Handles direct pay
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @param response
	 *           the http response
	 * @throws IOException
	 *            throw IOException when handling direct pay failed
	 */
	@RequestMapping(value = "/directpay", method = RequestMethod.POST)
	public void doPostDirectPay(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		doDirectPay(request, response);
	}

	/**
	 * Gets refund result
	 *
	 * @param model
	 *           session content information
	 * @param request
	 *           the http request
	 * @param response
	 *           the http response
	 * @return result of refund
	 * @throws IOException
	 *            throw IOException when refunding failed
	 */
	@RequestMapping(value = "/doRefund", method = RequestMethod.GET)
	public @ResponseBody String doRefund(final Model model, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		final Map<String, String[]> requestParamMap = request.getParameterMap();
		if (requestParamMap == null)
		{
			return "";
		}
		final Map<String, String> requestType = createRequestTypeMap(requestParamMap);
		final Map<String, String> clearParams = removeUselessValue(requestParamMap);
		final String errorCode = XSSFilterUtil.filter(requestType.get(ERROR_CODE));
		this.setCSRFToken(clearParams, request);
		final Map<String, String> notify = mockService.getRefundNotifyParams(clearParams, errorCode);
		mockService.handleRefundRequest(notify);

		final String action = request.getParameter(PARAM_ACTION);
		if (action == null)
		{
			return "";
		}
		String resultMessage = "";
		if (PARAM_NOTIFY.equals(action))
		{
			resultMessage = "Refund Finished!";
		}
		return resultMessage;
	}

	/**
	 * Gets direct pay result
	 *
	 * @param request
	 *           the http request
	 * @param response
	 *           the http response
	 * @return result of direct pay
	 * @throws IOException
	 *            throw IOException when direct pay failed
	 */
	@RequestMapping(value = "/directpay", method = RequestMethod.GET)
	public @ResponseBody String doGetDirectPay(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		doDirectPay(request, response);
		final String action = request.getParameter(PARAM_ACTION);
		if (action == null)
		{
			return "";
		}
		String resultMessage = "";
		if (PARAM_NOTIFY.equals(action))
		{
			resultMessage = "DirectPay Success!";
		}
		else if (NOTIFY_ERROR.equals(action))
		{
			resultMessage = "DirectPay Fails!";
		}
		return resultMessage;
	}

	protected void doDirectPay(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		final Map<String, String[]> requestParamMap = request.getParameterMap();

		if (requestParamMap == null)
		{
			return;
		}
		final Map<String, String> requestType = createRequestTypeMap(requestParamMap);
		final Map<String, String> clearParams = removeUselessValue(requestParamMap);
		this.setCSRFToken(clearParams, request);


		final String sign = mockService.getSign(clearParams);
		final boolean signIsValid = sign.equals(clearParams.get("sign"));
		if (signIsValid)
		{
			final String service = request.getParameter(PARAM_SERVICE);
			if (service != null)
			{
				XSSFilterUtil.filter(service);
				if ("create_direct_pay_by_user".equals(service))
				{
					handleDirectPayRequest(request, response, clearParams, signIsValid, requestType);
				}
			}
		}
	}

	protected boolean isSignValid(final Map<String, String> requestMap)
	{
		final String generateSign = mockService.getSign(requestMap);
		return generateSign.equals(requestMap.get("sign"));
	}


	protected Map<String, String> createRequestTypeMap(final Map<String, String[]> params)
	{
		final Map<String, String> requestType = new HashMap<>();
		requestType.put(PARAM_ACTION, params.get(PARAM_ACTION)[0]);
		requestType.put(TRADE_STATUS, params.get(TRADE_STATUS)[0]);
		requestType.put(ERROR_CODE, params.get(ERROR_CODE)[0]);
		return requestType;
	}

	protected Map<String, String> removeUselessValue(final Map<String, String[]> params)
	{
		final Map<String, String> clearMap = new HashMap<>();
		for (final Entry<String, String[]> entry : params.entrySet()) // NOSONAR
		{
			final String key = entry.getKey();
			if (PARAM_ACTION.equalsIgnoreCase(key) || TRADE_STATUS.equalsIgnoreCase(key) || ERROR_CODE.equalsIgnoreCase(key))
			{
				continue;
			}

			final String value = entry.getValue()[0];
			clearMap.put(key, value);
		}
		return clearMap;
	}

	protected void handleDirectPayRequest(final HttpServletRequest request, final HttpServletResponse response,
			final Map<String, String> params,
			final boolean signIsValid, final Map<String, String> requestType)
			throws IOException
	{
		final String tradeStatus = XSSFilterUtil.filter(requestType.get(TRADE_STATUS));
		final String errorCode = XSSFilterUtil.filter(requestType.get(ERROR_CODE));
		final String action = XSSFilterUtil.filter(requestType.get(PARAM_ACTION));
		LOG.info("Payment request");


		if (PARAM_NOTIFY.equalsIgnoreCase(action))
		{
			notify(params, tradeStatus);
		}
		else if (NOTIFY_ERROR.equalsIgnoreCase(action))
		{
			notifyError(params, errorCode);
		}
		else if ("return".equalsIgnoreCase(action))
		{
			returnResponse(request, response, params, tradeStatus);
		}

		else if (tradeStatus == null && signIsValid)
		{
			final String defaultTradeStatus = Registry.getMasterTenant().getConfig().getString("alipay.mock.default.trade.status",
					WAIT_BUYER_PAY);
			notify(params, WAIT_BUYER_PAY);
			if (!WAIT_BUYER_PAY.equals(defaultTradeStatus))
			{
				notify(params, defaultTradeStatus);
			}
			if ("TRADE_SUCCESS".equals(defaultTradeStatus))
			{
				returnResponse(request, response, params, tradeStatus);
			}
		}
	}

	protected void notify(final Map<String, String> params, final String tradeStatus)
	{
		final Map<String, String> notify = mockService.getNotifyParams(params, tradeStatus);
		mockService.handleNotifyRequest(notify);
	}

	protected void notifyError(final Map<String, String> params, final String errorCode)
	{
		final Map<String, String> notify = mockService.getNotifyErrorParams(params, errorCode);
		mockService.handleNotifyErrorRequest(notify);
	}

	protected void returnResponse(final HttpServletRequest request, final HttpServletResponse response,
			final Map<String, String> params, final String tradeStatus)
			throws IOException
	{
		mockService.stripOffCSRFToken(params);
		final String returnUrl = getReturnShopUrl(params, tradeStatus);
		if (isValidReturnURL(request, returnUrl))
		{
			response.sendRedirect(returnUrl);
		}
	}

	protected String getReturnShopUrl(final Map<String, String> params, final String tradeStatus)
	{
		Assert.notNull(params, "params cannot be null");
		final String baseUrl = params.get("return_url");
		Assert.notNull(baseUrl, "Parameter return_url cannot be null");

		final Map<String, String> notify = mockService.getReturnParams(params, tradeStatus);
		return baseUrl + "?" + mockService.createLinkString(notify);
	}

	protected void setCSRFToken(final Map<String, String> params, final HttpServletRequest request)
	{
		final CSRFRequestDataValueProcessor proc = new CSRFRequestDataValueProcessor();
		final Map<String, String> csrfHiddenField = proc.getExtraHiddenFields(request);
		params.putAll(csrfHiddenField);
	}

	protected boolean isValidReturnURL(final HttpServletRequest request, final String returnUrl)
	{
		try
		{
			final URI currentUri = new URI(request.getRequestURL().toString());
			final URI returnUri = new URI(StringUtils.substringBefore(returnUrl, "?"));

			return currentUri.getScheme().equals(returnUri.getScheme()) && currentUri.getHost().equals(returnUri.getHost())
					&& currentUri.getPort() == returnUri.getPort();
		}
		catch (final URISyntaxException e)
		{
			LOG.error("Paramter return URL is not valid.", e);
			return false;
		}
	}

	protected boolean isValidClearParams(final Map clearParams)
	{
		final String paramsWhiteList = "seller_email,refund_date,CSRFToken,subject,sign,notify_url,payment_type,it_bpay,out_trade_no,"
				+ "paymethod,partner,error_notify_url,total_fee,return_url,sign_type,_input_charset,service,batch_no,batch_num,seller_user_id,detail_data";
		final Iterator<Entry<String, String>> it = clearParams.entrySet().iterator();
		while (it.hasNext())
		{
			final Entry<String,String> entry = it.next();
			final String key = entry.getKey();
			if (paramsWhiteList.indexOf(key) == -1)
			{
				return false;
			}
		}
		return true;
	}

	protected boolean isValidDetailData(final String detail)
	{
		final String[] detailArray = StringUtils.split(detail, "^");
		final String alipayCode = detailArray[0];
		final String refundPrice = detailArray[1];

		return isValidNumeric(alipayCode) && isValidNumeric(refundPrice);
	}

	protected boolean isValidNumeric(final String paramValue)
	{
		final Pattern pattern = Pattern.compile("^[0-9]+(.[0-9]+)?$");
		final Matcher isNum = pattern.matcher(paramValue);
		return isNum.matches();
	}
}
