/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.processor;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is a workaround to fix content that is crafted not following the RFC 1341 for multipart requests.
 * It needs to stay in place until SCPI is able to send compliant content using CRLF instead of LF only for blank
 * lines between the HTTP headers and the payload.
 * https://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
 */
public class NewLineSanitizerInputStream extends InputStream
{
	private final InputStream is;
	private int previous;
	private boolean generated;

	public NewLineSanitizerInputStream(final InputStream is)
	{
		this.is = is;
	}

	@Override
	public int read() throws IOException
	{
		if (generated)
		{
			generated = false;
			previous = '\n';
			return '\n';
		}

		int c = is.read();
		if (c == '\n' && previous != '\r')
		{
			// we inject the CR and indicate that the next read() should get a LF without consuming the stream
			// again.
			generated = true;
			c = '\r';
		}
		previous = c;
		return c;
	}
}