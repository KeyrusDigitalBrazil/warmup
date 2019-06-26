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
package de.hybris.platform.chinesepspwechatpayservices.processors.impl;

import de.hybris.platform.chinesepspwechatpayservices.constants.WeChatPaymentConstants;
import de.hybris.platform.chinesepspwechatpayservices.exception.WeChatPayException;
import de.hybris.platform.chinesepspwechatpayservices.processors.AbstractWeChatPayRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayHttpClient;
import de.hybris.platform.core.model.order.OrderModel;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import groovy.util.Node;
import groovy.util.NodeList;
import groovy.util.XmlParser;


/**
 * Processor to get pre-pay Id from weChat
 */
public class UnifiedOrderRequestProcessor extends AbstractWeChatPayRequestProcessor<String>
{
	public UnifiedOrderRequestProcessor(final WeChatPayConfiguration config, final WeChatPayHttpClient httpClient,
			final String openId, final OrderModel order, final String clientIp, final String baseUrl)
	{
		super(config, httpClient);
		super.setUrl(config.getUnifiedOrderURL());
		super.addParameter("appid", config.getAppId());
		super.addParameter("mch_id", config.getMechId());
		super.addParameter("device_info", "WEB");
		super.addParameter("nonce_str", super.getParams().generateNonce());
		super.addParameter("body", order.getEntries().get(0).getProduct().getName());
		super.addParameter("detail", "");
		super.addParameter("attach", "");
		super.addParameter("out_trade_no", order.getCode());

		if ("true".equals(config.getTestMode()))
		{
			super.addParameter("total_fee", "1");
		}
		else
		{
			super.addParameter("total_fee", String.valueOf(order.getTotalPrice().intValue() * 100));
		}
		super.addParameter("spbill_create_ip", clientIp);
		super.addParameter("time_start", "");
		super.addParameter("time_expire", "");
		super.addParameter("goods_tag", "");

		super.addParameter("notify_url", baseUrl + WeChatPaymentConstants.Controller.NOTIFY_URL);
		super.addParameter("trade_type", "JSAPI");
		super.addParameter("product_id", "");
		super.addParameter("limit_pay", "");
		super.addParameter("openid", openId);
		super.addParameter("sign", super.getParams().generateSignature(super.getConfig().getMechKey()));
	}

	@Override
	public String process()
	{
		String prePayId = "";
		try
		{
			final XmlParser xmlParser = new XmlParser();
			final Node node = xmlParser.parseText(post());
			final NodeList prePayIdList = (NodeList) node.get("prepay_id");
			if (!prePayIdList.isEmpty())
			{
				final Node prePayIdNode = (Node) prePayIdList.get(0);
				prePayId = prePayIdNode.children().get(0).toString();
				return prePayId;
			}
			else
			{
				final NodeList errorMsg = (NodeList) node.get("err_code");
				if (!errorMsg.isEmpty())
				{
					final Node errorNode = (Node) errorMsg.get(0);
					debug("Unify Order Error: " + errorNode.children().get(0).toString());
				}
			}
			throw new WeChatPayException("Unify Order Fail");
		}
		catch (ParserConfigurationException | IOException | SAXException e)
		{
			debug("XML Paser Exception" + e.toString());
			throw new WeChatPayException("XML Paser Exception", e);
		}
	}
}
