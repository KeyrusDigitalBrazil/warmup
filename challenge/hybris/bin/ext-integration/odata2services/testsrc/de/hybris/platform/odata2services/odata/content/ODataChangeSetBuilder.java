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

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;

public class ODataChangeSetBuilder
{
	private static final String CHANGE_SET_BOUNDARY = "changeSet-boundary";
	private static final String CHANGE_SET_TEMPLATE = "Content-Type: multipart/mixed; boundary=__BOUNDARY__\n" +
			"\r\n" +
			"__PAYLOAD__\n" +
			"--__BOUNDARY__--\n";

	public static ODataChangeSetBuilder changeSetBuilder()
	{
		return new ODataChangeSetBuilder();
	}

	private final List<String> parts = Lists.newArrayList();
	private String changeSetBoundary = CHANGE_SET_BOUNDARY;
	private String uri = "";

	public ODataChangeSetBuilder withBoundary(final String boundary)
	{
		this.changeSetBoundary = boundary;
		return this;
	}

	public ODataChangeSetBuilder withUri(final String uri)
	{
		this.uri = uri;
		return this;
	}

	public ODataChangeSetBuilder withPart(final Locale locale, final ChangeSetPartContentBuilder builder)
	{
		return withPart(locale, builder.build());
	}

	public ODataChangeSetBuilder withPart(final Locale locale, final String payload)
	{
		return withPart(ODataChangeSetPartBuilder.partBuilder()
				.withContentLanguage(locale)
				.withBody(payload));
	}

	public ODataChangeSetBuilder withPart(final ODataChangeSetPartBuilder builder)
	{
		final String part = builder
				.withContentId(parts.size() + 1)
				.withUri(uri)
				.build();
		parts.add(part);
		return this;
	}

	public String build()
	{
		final String separator = "--"+changeSetBoundary+"\n";
		final String payload = separator+StringUtils.join(parts, separator);
		return CHANGE_SET_TEMPLATE
				.replace("__BOUNDARY__", changeSetBoundary)
				.replace("__PAYLOAD__", payload);
	}
}
