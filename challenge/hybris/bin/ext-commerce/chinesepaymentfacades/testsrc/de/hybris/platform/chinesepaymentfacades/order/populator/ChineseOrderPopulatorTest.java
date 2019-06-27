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
package de.hybris.platform.chinesepaymentfacades.order.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.chinesepaymentfacades.payment.data.ChinesePaymentInfoData;
import de.hybris.platform.chinesepaymentservices.checkout.strategies.ChinesePaymentServicesStrategy;
import de.hybris.platform.chinesepaymentservices.enums.ServiceType;
import de.hybris.platform.chinesepaymentservices.model.ChinesePaymentInfoModel;
import de.hybris.platform.chinesepaymentservices.payment.ChinesePaymentService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.PaymentModeService;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ChineseOrderPopulatorTest
{
	private final static String PAYMENT_SERVICE_PROVIDER_SERVICE_SUFFIX = "PaymentService";

	@Mock
	private ChinesePaymentServicesStrategy chinesePaymentServicesStrategy;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private StoreSessionFacade storeSessionFacade;
	@Mock
	private PaymentModeService paymentModeService;
	@Mock
	private ChinesePaymentService chinesePaymentService;

	@Mock
	private OrderModel source;
	@Mock
	private ChinesePaymentInfoModel chinesePaymentInfo;
	@Mock
	private LanguageData languageData;
	@Mock
	private ServiceType serviceType;
	@Mock
	private PaymentModeModel paymentMode;

	private ChineseOrderPopulator populator;
	private OrderData target;

	private final static String CHINESE_PAYMENT_INFO_ID = "0000000001";
	private final static String PAYMENT_PROVIDER = "HybrisPay";
	private final static String ISO = "en";
	private final static String SERVICE_TYPE_CODE = "ExpressPay";
	private final static String PAYMENT_PROVIDER_NAME = PAYMENT_PROVIDER;
	private final static String PSP_LOGO_URL = "/logo/url";

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);

		target = new OrderData();
		populator = new ChineseOrderPopulator();
		populator.setChinesePaymentServicesStrategy(chinesePaymentServicesStrategy);
		populator.setEnumerationService(enumerationService);
		populator.setStoreSessionFacade(storeSessionFacade);
		populator.setPaymentModeService(paymentModeService);


		given(chinesePaymentInfo.getCode()).willReturn(CHINESE_PAYMENT_INFO_ID);
		given(chinesePaymentInfo.getPaymentProvider()).willReturn(PAYMENT_PROVIDER);
		given(storeSessionFacade.getCurrentLanguage()).willReturn(languageData);
		given(languageData.getIsocode()).willReturn(ISO);
		given(chinesePaymentInfo.getServiceType()).willReturn(serviceType);
		given(serviceType.getCode()).willReturn(SERVICE_TYPE_CODE);
		given(enumerationService.getEnumerationName(ServiceType.valueOf(SERVICE_TYPE_CODE), new Locale(ISO))).willReturn(
				SERVICE_TYPE_CODE);
		given(paymentModeService.getPaymentModeForCode(PAYMENT_PROVIDER)).willReturn(paymentMode);
		given(paymentMode.getName()).willReturn(PAYMENT_PROVIDER_NAME);
		given(chinesePaymentServicesStrategy.getPaymentService(PAYMENT_PROVIDER + PAYMENT_SERVICE_PROVIDER_SERVICE_SUFFIX))
				.willReturn(chinesePaymentService);
		given(chinesePaymentService.getPspLogoUrl()).willReturn(PSP_LOGO_URL);
	}

	@Test
	public void testPopulate()
	{
		given(source.getChinesePaymentInfo()).willReturn(chinesePaymentInfo);
		populator.populate(source, target);
		final ChinesePaymentInfoData paymentInfoData = target.getChinesePaymentInfo();

		assertNotNull(paymentInfoData);
		assertEquals(CHINESE_PAYMENT_INFO_ID, paymentInfoData.getId());
		assertEquals(PAYMENT_PROVIDER, paymentInfoData.getPaymentProvider());
		assertEquals(SERVICE_TYPE_CODE, paymentInfoData.getServiceType());
		assertEquals(PSP_LOGO_URL, paymentInfoData.getPaymentProviderLogo());
		assertEquals(PAYMENT_PROVIDER_NAME, paymentInfoData.getPaymentProviderName());
	}
	
	@Test
	public void testPopulate_nopaymenyinfo()
	{
		given(source.getChinesePaymentInfo()).willReturn(null);
		populator.populate(source, target);
		final ChinesePaymentInfoData paymentInfoData = target.getChinesePaymentInfo();

		assertEquals(null, paymentInfoData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulatorWithSourceNull()
	{
		populator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPopulatorWithTargetNull()
	{
		populator.populate(source, null);
	}

}
