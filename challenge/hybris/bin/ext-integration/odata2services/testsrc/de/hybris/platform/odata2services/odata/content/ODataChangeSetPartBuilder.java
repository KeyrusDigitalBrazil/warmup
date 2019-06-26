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

package de.hybris.platform.odata2services.odata.content;

import de.hybris.platform.integrationservices.util.JsonBuilder;

import java.util.Locale;

public class ODataChangeSetPartBuilder
{
	public static ODataChangeSetPartBuilder partBuilder()
	{
		return new ODataChangeSetPartBuilder();
	}

	private int contentId;
	private Locale contentLanguage;
	private Locale acceptLanguage;
	private String partBody;
	private String uri = "";

	public ODataChangeSetPartBuilder withContentId(final int id)
	{
		contentId = id;
		return this;
	}

	public ODataChangeSetPartBuilder withUri(final String uri)
	{
		this.uri = uri;
		return this;
	}

	public ODataChangeSetPartBuilder withContentLanguage(final Locale locale)
	{
		contentLanguage = locale;
		return this;
	}

	public ODataChangeSetPartBuilder withAcceptLanguage(final Locale locale)
	{
		acceptLanguage = locale;
		return this;
	}

	public ODataChangeSetPartBuilder withBody(final JsonBuilder json)
	{
		return withBody(json.build());
	}

	public ODataChangeSetPartBuilder withBody(final String payload)
	{
		partBody = payload;
		return this;
	}

	public String build()
	{
		return "Content-Type: application/http" + '\n' +
				"Content-Transfer-Encoding: binary" + '\n' +
				"Content-ID: " + contentId + "\n\r\n" +
				"POST " + uri + " HTTP/1.1" + '\n' +
				"Content-Type: application/json" + '\n' +
				"Accept: application/json" + '\n' +
				headerLine("Content-Language", contentLanguage) +
				headerLine("Accept-Language", acceptLanguage) +
				"\r\n" +
				partBody + '\n';
	}

	private String headerLine(final String headerName, final Object headerValue)
	{
		return headerValue != null
				? headerName + ": " + headerValue + '\n'
				: "";
	}
}
