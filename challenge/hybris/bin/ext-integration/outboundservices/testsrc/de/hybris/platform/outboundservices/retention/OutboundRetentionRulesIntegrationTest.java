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
package de.hybris.platform.outboundservices.retention;

import java.util.List;

import org.junit.Before;

import com.google.common.collect.Lists;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.retention.AbstractRetentionRulesIntegrationTest;

@IntegrationTest
public class OutboundRetentionRulesIntegrationTest extends AbstractRetentionRulesIntegrationTest
{
	private static final List<String> TYPES_TO_CLEANUP = Lists.newArrayList("OutboundRequest", "OutboundRequestMedia");

	@Before
	public void setUp() throws ImpExException
	{
		importData("/impex/essentialdata-outbound-item-cleanup-jobs.impex", "UTF-8");
	}

	@Override
	protected List<String> getTypesToCleanup()
	{
		return TYPES_TO_CLEANUP;
	}
}
