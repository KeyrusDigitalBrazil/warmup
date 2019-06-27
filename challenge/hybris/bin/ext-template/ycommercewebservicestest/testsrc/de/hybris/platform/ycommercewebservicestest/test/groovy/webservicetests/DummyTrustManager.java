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
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;


public class DummyTrustManager extends X509ExtendedTrustManager
{
	private static final X509Certificate[] ACCEPTED_ISSUERS = new X509Certificate[0];

	@Override
	public void checkClientTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException
	{
		return;
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] arg0, final String arg1) throws CertificateException
	{
		return;
	}

	@Override
	public X509Certificate[] getAcceptedIssuers()
	{
		return ACCEPTED_ISSUERS;
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] arg0, final String arg1, final Socket arg2) throws CertificateException
	{
		return;
	}

	@Override
	public void checkClientTrusted(final X509Certificate[] arg0, final String arg1, final SSLEngine arg2)
			throws CertificateException
	{
		return;
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] arg0, final String arg1, final Socket arg2) throws CertificateException
	{
		return;
	}

	@Override
	public void checkServerTrusted(final X509Certificate[] arg0, final String arg1, final SSLEngine arg2)
			throws CertificateException
	{
		return;
	}
}
