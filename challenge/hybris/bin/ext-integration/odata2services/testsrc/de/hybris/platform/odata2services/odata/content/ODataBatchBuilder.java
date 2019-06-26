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

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class ODataBatchBuilder
{
	private static final String BATCH_TEMPLATE = "__PAYLOAD__\n" +
			"--__BOUNDARY__--\n";

	public static final String BATCH_BOUNDARY = "batch-boundary";

	public static ODataBatchBuilder batchBuilder()
	{
		return new ODataBatchBuilder();
	}

	private final List<String> changeSets = Lists.newArrayList();
	private String batchBoundary = BATCH_BOUNDARY;

	public ODataBatchBuilder withBoundary(final String boundary)
	{
		this.batchBoundary = boundary;
		return this;
	}

	public ODataBatchBuilder withChangeSet(final ODataChangeSetBuilder changeSetBuilder)
	{
		this.changeSets.add(changeSetBuilder.build());
		return this;
	}

	public String build()
	{
		final String separator = "\n--"+batchBoundary+"\n";
		final String payload = separator + StringUtils.join(changeSets, separator);
		return BATCH_TEMPLATE
				.replace("__PAYLOAD__", payload)
				.replace("__BOUNDARY__", batchBoundary);
	}
}
