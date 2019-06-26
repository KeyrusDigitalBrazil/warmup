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
package de.hybris.platform.inboundservices.retention;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.retention.AbstractRetentionRulesIntegrationTest;

import java.util.List;

import org.junit.Before;

import com.google.common.collect.Lists;

@IntegrationTest
public class InboundRetentionRulesIntegrationTest extends AbstractRetentionRulesIntegrationTest
{
	private static final List<String> TYPES_TO_CLEANUP = Lists.newArrayList("InboundRequest", "InboundRequestMedia");

	@Before
	public void setUp() throws ImpExException
	{
		importData("/impex/essentialdata-inbound-item-cleanup-jobs.impex", "UTF-8");
	}

	@Override
	protected List<String> getTypesToCleanup()
	{
		return TYPES_TO_CLEANUP;
	}
}
