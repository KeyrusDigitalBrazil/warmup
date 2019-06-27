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
package com.hybris.ymkt.clickstream;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.eventtracking.model.events.AbstractProductAwareTrackingEvent;
import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.model.events.ProductDetailPageViewEvent;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.clickstream.listeners.ClickStreamListener;


@ManualTest
public class PerformanceIntegrationTest extends ServicelayerBaseTest
{
	static final Random RANDOM = new Random(System.currentTimeMillis());

	@Resource(name = "ymktClickStreamListener")
	ClickStreamListener clickStreamListener;

	protected <T extends AbstractTrackingEvent> T createAbstractTrackingEvent(final Class<T> clazz)
	{
		try
		{
			final T event = clazz.newInstance();
			event.setEventType(clazz.getSimpleName());

			// Event has no seconds
			event.setInteractionTimestamp(Long.toString(System.currentTimeMillis() / 1000));
			event.setPageUrl("https://localhost:9002/yacceleratorsstorefront/electronics/en/Open-Catalogue/Camera/");
			event.setPiwikId(Long.toHexString(RANDOM.nextLong()));
			event.setRefUrl("https://localhost:9002/yacceleratorsstorefront/electronics/en/?site=electronics");
			event.setSessionId(Long.toHexString(RANDOM.nextLong()).toUpperCase(Locale.ENGLISH));
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
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public void disableCertificates() throws Exception
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
		this.disableCertificates();
	}

	@Test
	public void test() throws Exception
	{
		//		System.out.println();
		//		System.out.println();
		//		System.out.println("Pause for measurement tool. Press a key to start.");
		//		System.out.println();
		//		System.out.println();
		//		System.in.read();

		final int threadNumber = 40;
		final int eventsPerThread = 250;
		final int batchSize = 20;

		clickStreamListener.setBatchSize(batchSize);

		final Class<ProductDetailPageViewEvent> clazz = ProductDetailPageViewEvent.class;
		final Runnable run = () -> clickStreamListener.onApplicationEvent(this.createAbstractTrackingEvent(clazz));
		final Callable<Object> call = () -> Stream.generate(() -> run).limit(eventsPerThread).peek(Runnable::run).count();
		final List<Callable<Object>> list = Stream.generate(() -> call).limit(threadNumber).collect(Collectors.toList());

		final long start = System.currentTimeMillis();
		for (final Future<Object> f : Executors.newCachedThreadPool().invokeAll(list))
		{
			f.get();
		}
		System.out.println("Threads : " + threadNumber + "\n" + //
				"Events : " + threadNumber * eventsPerThread + "\n" + //
				"BatchSize : " + batchSize + "\n" + //
				"Total : " + (System.currentTimeMillis() - start) + "ms\n" + //
				"Events/seconds : " + (float) threadNumber * eventsPerThread * 1000 / (System.currentTimeMillis() - start) + "\n");
	}

}
