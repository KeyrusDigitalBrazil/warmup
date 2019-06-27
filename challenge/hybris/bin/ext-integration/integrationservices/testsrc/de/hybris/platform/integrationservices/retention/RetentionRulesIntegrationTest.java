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
package de.hybris.platform.integrationservices.retention;

import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.processing.model.AbstractRetentionRuleModel;
import de.hybris.platform.processing.model.FlexibleSearchRetentionRuleModel;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

@IntegrationTest
public class RetentionRulesIntegrationTest extends AbstractRetentionRulesIntegrationTest
{
	private static final List<String> TYPES_TO_CLEANUP = Lists.newArrayList("IntegrationApiMedia");

	@Before
	public void setUp() throws ImpExException
	{
		importData("/impex/essentialdata-item-cleanup-jobs.impex", "UTF-8");
	}

	@Override
	protected List<String> getTypesToCleanup()
	{
		return TYPES_TO_CLEANUP;
	}

	@Test
	@Override
	public void testMediaRetentionCleanupRuleHasNoFilterForRetentionCleanupRuleType()
	{
		final AbstractRetentionRuleModel cleanupRuleExample = new FlexibleSearchRetentionRuleModel();
		cleanupRuleExample.setCode("integrationApiMediaCleanupRule");

		final AbstractRetentionRuleModel mediaCleanupRule = assertModelExists(cleanupRuleExample);

		assertThat(mediaCleanupRule.getItemtype()).isEqualTo("FlexibleSearchRetentionRule");
	}
}
