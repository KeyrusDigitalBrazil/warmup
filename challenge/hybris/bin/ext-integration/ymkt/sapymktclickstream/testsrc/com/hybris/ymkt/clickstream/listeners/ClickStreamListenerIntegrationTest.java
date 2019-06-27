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
package com.hybris.ymkt.clickstream.listeners;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.eventtracking.model.events.AbstractProductAwareTrackingEvent;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.model.events.ProductDetailPageViewEvent;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.common.consent.impl.DefaultYmktConsentService;
import com.hybris.ymkt.common.odata.ODataService;


@IntegrationTest
public class ClickStreamListenerIntegrationTest extends ServicelayerBaseTest
{
	static final Random RANDOM = new Random(System.currentTimeMillis());

	@Resource(name = "ymktClickStreamListener")
	ClickStreamListener clickStreamListener;

	ODataServer oDataServer;

	@Resource(name = "ODataService_CUAN_IMPORT_SRV")
	ODataService oDataService;

	protected static <T extends AbstractTrackingEvent> T createAbstractTrackingEvent(final Class<T> clazz) throws Exception
	{
		final T event = clazz.newInstance();
		event.setEventType(clazz.getSimpleName());
		// Event has no seconds
		event.setInteractionTimestamp(Long.toString(System.currentTimeMillis() / 1000));
		event.setPageUrl("https://localhost:9002/yacceleratorsstorefront/electronics/en/Open-Catalogue/Camera/");
		event.setPiwikId(Integer.toHexString(RANDOM.nextInt()));
		event.setRefUrl("https://localhost:9002/yacceleratorsstorefront/electronics/en/?site=electronics");
		event.setSessionId(Integer.toHexString(RANDOM.nextInt()).toUpperCase(Locale.ENGLISH));
		event.setUserEmail("demo@example.com");
		event.setUserId("");

		if (event instanceof AbstractProductAwareTrackingEvent)
		{
			final AbstractProductAwareTrackingEvent e = (AbstractProductAwareTrackingEvent) event;
			e.setProductId("553637");
			e.setProductName("NV10");
		}

		return event;
	}

	public static void disableCertificates() throws Exception
	{
		final TrustManager[] trustAllCerts =
		{ (TrustManager) Proxy.getProxyClass(X509TrustManager.class.getClassLoader(), X509TrustManager.class)
				.getConstructor(InvocationHandler.class).newInstance((InvocationHandler) (o, m, args) -> null) };

		final SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	@Before
	public void setUp() throws Exception
	{
		this.oDataService.setRootUrl("http://localhost:44300/sap/opu/odata/sap/CUAN_IMPORT_SRV/");
		this.clickStreamListener.setBatchSize(1);
		// Disable cookie consent
		this.clickStreamListener.setYmktConsentService(new DefaultYmktConsentService());
		disableCertificates();

		Thread.sleep(1000);
		this.oDataServer = new ODataServer();
	}

	@After
	public void tearDown() throws Exception
	{
		this.oDataServer.stop();
		Thread.sleep(1000);
	}

	@Test
	public void testProductDetailPageViewEvent() throws Exception
	{
		final ProductDetailPageViewEvent event = createAbstractTrackingEvent(ProductDetailPageViewEvent.class);

		this.clickStreamListener.onApplicationEvent(event);
	}

	@Test
	public void testProductDetailPageViewEventTrackingId() throws Exception
	{
		final ProductDetailPageViewEvent event = createAbstractTrackingEvent(ProductDetailPageViewEvent.class);
		event.setRefUrl(
				"https://localhost:9002/yacceleratorsstorefront/electronics/en/?site=electronics&sap-outbound-id=ABCDEF0123456789");

		this.clickStreamListener.onApplicationEvent(event);
	}

}
