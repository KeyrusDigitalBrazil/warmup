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
package com.hybris.ymkt.common.odata;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.security.SecureRandom;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.hybris.ymkt.common.http.HttpURLConnectionService;


@UnitTest
public class ODataServiceTest
{
	ODataService oDataService = new ODataService();
	HttpURLConnectionService httpURLConnectionService = new HttpURLConnectionService();


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
		disableCertificates();

		oDataService.setHttpURLConnectionService(httpURLConnectionService);
		oDataService.setRootUrl("");
		oDataService.setUser("");
		oDataService.setPassword("");
	}

	//	@Test
	public void testGetEdm() throws Exception
	{
		oDataService.getEdm();
	}

	@Test
	public void testIsGZIP() throws Exception
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out);
		gzipOutputStream.write(56);
		gzipOutputStream.close();
		final byte[] decompressIfGZIP = oDataService.decompressIfGZIP(out.toByteArray());
		Assert.assertEquals(1, decompressIfGZIP.length);
		Assert.assertEquals(56, decompressIfGZIP[0]);
	}

	//	@Test
	public void testRefreshToken() throws Exception
	{
		oDataService.refreshToken(null);
	}

}
