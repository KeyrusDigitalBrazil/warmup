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
package de.hybris.platform.chinesepaymentfacades.payment.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentfacades.payment.data.ChinesePaymentInfoData;
import de.hybris.platform.chinesepaymentservices.checkout.strategies.ChinesePaymentServicesStrategy;
import de.hybris.platform.chinesepaymentservices.enums.ServiceType;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepaymentservices.payment.ChinesePaymentService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.PaymentModeService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CartChinesePaymentInfoPopulatorTest
{

	private final static String PAYMENT_SERVICE_PROVIDER_SERVICE_SUFFIX = "PaymentService";

	@Mock
	private ChinesePaymentServicesStrategy chinesePaymentServicesStrategy;
	@Mock
	private PaymentModeService paymentModeService;
	@Mock
	private ChinesePaymentService chinesePaymentService;

	@Mock
	private CartModel source;
	@Mock
	private ChinesePaymentInfoModel chinesePaymentInfo;
	@Mock
	private ServiceType serviceType;
	@Mock
	private PaymentModeModel paymentMode;

	private CartChinesePaymentInfoPopulator populator;
	private CartData target;

	private final String CHINESE_PAYMENT_INFO_ID = "0000000001";
	private final String PAYMENT_PROVIDER = "HybrisPay";
	private final String SERVICE_TYPE_CODE = "ExpressPay";
	private final String PAYMENT_PROVIDER_NAME = PAYMENT_PROVIDER;
	private final String PSP_LOGO_URL = "/logo/url";

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		target = new CartData();
		populator = new CartChinesePaymentInfoPopulator();
		populator.setChinesePaymentServicesStrategy(chinesePaymentServicesStrategy);
		populator.setPaymentModeService(paymentModeService);

		given(source.getChinesePaymentInfo()).willReturn(chinesePaymentInfo);
		given(chinesePaymentInfo.getCode()).willReturn(CHINESE_PAYMENT_INFO_ID);
		given(chinesePaymentInfo.getPaymentProvider()).willReturn(PAYMENT_PROVIDER);
		given(chinesePaymentInfo.getServiceType()).willReturn(serviceType);
		given(serviceType.getCode()).willReturn(SERVICE_TYPE_CODE);
		given(paymentModeService.getPaymentModeForCode(PAYMENT_PROVIDER)).willReturn(paymentMode);
		given(paymentMode.getName()).willReturn(PAYMENT_PROVIDER_NAME);
		given(chinesePaymentServicesStrategy.getPaymentService(PAYMENT_PROVIDER + PAYMENT_SERVICE_PROVIDER_SERVICE_SUFFIX))
				.willReturn(chinesePaymentService);
		given(chinesePaymentService.getPspLogoUrl()).willReturn(PSP_LOGO_URL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateSourceNull()
	{
		populator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulateTargetNull()
	{
		populator.populate(source, null);
	}

	@Test
	public void testPopulate()
	{
		populator.populate(source, target);
		final ChinesePaymentInfoData paymentInfoData = target.getChinesePaymentInfo();

		assertNotNull(paymentInfoData);
		assertEquals(CHINESE_PAYMENT_INFO_ID, paymentInfoData.getId());
		assertEquals(PAYMENT_PROVIDER, paymentInfoData.getPaymentProvider());
		assertEquals(SERVICE_TYPE_CODE, paymentInfoData.getServiceType());
		assertEquals(PSP_LOGO_URL, paymentInfoData.getPaymentProviderLogo());
		assertEquals(PAYMENT_PROVIDER_NAME, paymentInfoData.getPaymentProviderName());
	}
}
