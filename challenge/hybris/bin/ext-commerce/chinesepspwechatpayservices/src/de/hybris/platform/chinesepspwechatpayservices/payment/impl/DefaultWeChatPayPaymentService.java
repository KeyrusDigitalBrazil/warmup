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
package de.hybris.platform.chinesepspwechatpayservices.payment.impl;

import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepaymentservices.order.service.ChineseOrderService;
import de.hybris.platform.chinesepaymentservices.payment.ChinesePaymentService;
import de.hybris.platform.chinesepspwechatpayservices.dao.WeChatPayOrderDao;
import de.hybris.platform.chinesepspwechatpayservices.data.WeChatPayQueryResult;
import de.hybris.platform.chinesepspwechatpayservices.processors.impl.OrderQueryRequestProcessor;
import de.hybris.platform.chinesepspwechatpayservices.strategies.WeChatPayPaymentInfoStrategy;
import de.hybris.platform.chinesepspwechatpayservices.strategies.WeChatPayPaymentTransactionStrategy;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayConfiguration;
import de.hybris.platform.chinesepspwechatpayservices.wechatpay.WeChatPayHttpClient;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.model.WeChatPayPaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;


/**
 * A default implements of chinese payment service
 */
public class DefaultWeChatPayPaymentService extends DefaultPaymentServiceImpl implements ChinesePaymentService
{
	private static final String WECHAT_LOGO = "/images/theme/wechatpay.png";

	private static final String UNSUPPORTED_MESSAGE = "WeChat Pay does NOT support this operation.";

	private MediaService mediaService;

	private WeChatPayPaymentInfoStrategy weChatPayPaymentInfoStrategy;

	private CommerceCheckoutService commerceCheckoutService;

	private ConfigurationService configurationService;

	private WeChatPayOrderDao orderDao;

	private WeChatPayPaymentTransactionStrategy weChatPayPaymentTransactionStrategy;

	private WeChatPayConfiguration weChatPayConfiguration;

	private WeChatPayHttpClient weChatPayHttpClient;

	private ModelService modelService;

	private ChineseOrderService chineseOrderService;

	@Override
	public String handleAsyncResponse(final HttpServletRequest request, final HttpServletResponse response)
	{
		throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
	}

	@Override
	public String handleSyncResponse(final HttpServletRequest request, final HttpServletResponse response)
	{
		throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
	}

	@Override
	public boolean cancelPayment(final String orderCode)
	{
		return false;
	}

	@Override
	public String getPaymentRequestUrl(final String orderCode)
	{
		return "/checkout/multi/wechat/pay/" + orderCode + "?showwxpaytitle=1";
	}

	@Override
	public void syncPaymentStatus(final String orderCode)
	{
		final OrderQueryRequestProcessor orderQueryRequestProcessor = new OrderQueryRequestProcessor(getWeChatPayHttpClient(),
				orderCode, getWeChatPayConfiguration());
		final Optional<WeChatPayQueryResult> processResult = orderQueryRequestProcessor.process();
		processResult
				.ifPresent(weChatPayQueryResult -> {
					final Optional<OrderModel> orderModel = getOrderDao().findOrderByCode(orderCode);
					orderModel
							.ifPresent(order -> {
								final Optional<WeChatPayPaymentTransactionEntryModel> weChatPayPaymentTransactionEntry = getWeChatPayPaymentTransactionStrategy()
										.saveForStatusCheck(order, weChatPayQueryResult);
								weChatPayPaymentTransactionEntry.ifPresent(entry -> {
									if (TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus()))
									{
										order.setPaymentStatus(PaymentStatus.PAID);
										getModelService().save(order);
									}
								});
							});
				});
	}

	@Override
	public boolean setPaymentInfo(final CartModel cartModel, final ChinesePaymentInfoModel chinesePaymentInfoModel)
	{
		cartModel.setChinesePaymentInfo(chinesePaymentInfoModel);
		getWeChatPayPaymentInfoStrategy().updatePaymentInfoForPayemntMethod(chinesePaymentInfoModel);
		final CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setPaymentInfo(chinesePaymentInfoModel);
		return getCommerceCheckoutService().setPaymentInfo(parameter);
	}

	@Override
	public String getPspLogoUrl()
	{
		final MediaModel weChatLogo = getMediaService().getMedia(WECHAT_LOGO);
		return weChatLogo.getURL();
	}

	@Override
	public void updatePaymentInfoForPlaceOrder(final String orderCode)
	{
		final Optional<OrderModel> optional = getOrderDao().findOrderByCode(orderCode);
		if (optional.isPresent())
		{
			final OrderModel order = optional.get();
			getWeChatPayPaymentInfoStrategy().updatePaymentInfoForPlaceOrder(order);
		}
	}


	@Override
	public Optional<String> getRefundRequestUrl(final String orderCode)
	{
		return Optional.empty();
	}

	/**
	 * class for Chinesepspwechatpayservices payment impl.
	 *
	 * @param orderCode
	 *           Find Order by the orderCode
	 */
	public void createTransactionForNewRequest(final String orderCode)
	{
		final Optional<OrderModel> optional = getOrderDao().findOrderByCode(orderCode);
		if (optional.isPresent())
		{
			final OrderModel order = optional.get();
			weChatPayPaymentTransactionStrategy.createForNewRequest(order);
		}
	}

	public MediaService getMediaService()
	{
		return mediaService;
	}

	@Required
	public void setMediaService(final MediaService mediaService)
	{
		this.mediaService = mediaService;
	}

	public WeChatPayPaymentInfoStrategy getWeChatPayPaymentInfoStrategy()
	{
		return weChatPayPaymentInfoStrategy;
	}

	@Required
	public void setWeChatPayPaymentInfoStrategy(final WeChatPayPaymentInfoStrategy weChatPayPaymentInfoStrategy)
	{
		this.weChatPayPaymentInfoStrategy = weChatPayPaymentInfoStrategy;
	}

	public CommerceCheckoutService getCommerceCheckoutService()
	{
		return commerceCheckoutService;
	}

	@Required
	public void setCommerceCheckoutService(final CommerceCheckoutService commerceCheckoutService)
	{
		this.commerceCheckoutService = commerceCheckoutService;
	}

	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public WeChatPayOrderDao getOrderDao()
	{
		return orderDao;
	}

	@Required
	public void setOrderDao(final WeChatPayOrderDao orderDao)
	{
		this.orderDao = orderDao;
	}

	/**
	 * @return the weChatPayPaymentTransactionStrategy
	 */
	public WeChatPayPaymentTransactionStrategy getWeChatPayPaymentTransactionStrategy()
	{
		return weChatPayPaymentTransactionStrategy;
	}

	/**
	 * @param weChatPayPaymentTransactionStrategy
	 *           the weChatPayPaymentTransactionStrategy to set
	 */
	public void setWeChatPayPaymentTransactionStrategy(
			final WeChatPayPaymentTransactionStrategy weChatPayPaymentTransactionStrategy)
	{
		this.weChatPayPaymentTransactionStrategy = weChatPayPaymentTransactionStrategy;
	}

	protected WeChatPayConfiguration getWeChatPayConfiguration()
	{
		return weChatPayConfiguration;
	}

	@Required
	public void setWeChatPayConfiguration(final WeChatPayConfiguration weChatPayConfiguration)
	{
		this.weChatPayConfiguration = weChatPayConfiguration;
	}

	protected WeChatPayHttpClient getWeChatPayHttpClient()
	{
		return weChatPayHttpClient;
	}

	@Required
	public void setWeChatPayHttpClient(final WeChatPayHttpClient weChatPayHttpClient)
	{
		this.weChatPayHttpClient = weChatPayHttpClient;
	}

	@Override
	protected ModelService getModelService()
	{
		return modelService;
	}

	@Override
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ChineseOrderService getChineseOrderService()
	{
		return chineseOrderService;
	}

	@Required
	public void setChineseOrderService(final ChineseOrderService chineseOrderService)
	{
		this.chineseOrderService = chineseOrderService;
	}



}
