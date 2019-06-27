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

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.processing.model.AbstractRetentionRuleModel;
import de.hybris.platform.processing.model.AfterRetentionCleanupRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

public abstract class AbstractRetentionRulesIntegrationTest extends ServicelayerTest
{
	private static final long ONE_WEEK_IN_SECONDS = 60 * 60 * 24 * 7;
	private static final String EVERY_DAY_AT_MIDNIGHT = "0 0 0 * * ?";
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Test
	public void testRetentionRulesCleanOnlyTypedDataOlderThanAWeek()
	{
		final AbstractRetentionRuleModel cleanupRuleExample = new AbstractRetentionRuleModel();
		cleanupRuleExample.setActionReference("basicRemoveCleanupAction");

		final List<AbstractRetentionRuleModel> cleanupRules = flexibleSearchService.getModelsByExample(cleanupRuleExample);

		assertThat(cleanupRules).hasSize(getTypesToCleanup().size());
		cleanupRules.forEach(rule -> {
			assertThat(rule).hasFieldOrPropertyWithValue("retentionTimeSeconds", ONE_WEEK_IN_SECONDS);
			if( rule instanceof AfterRetentionCleanupRuleModel )
			{
				assertThat(getTypesToCleanup()).contains(((AfterRetentionCleanupRuleModel)rule).getRetirementItemType().getCode());
			}
		});
	}

	@Test
	public void testMediaRetentionCleanupRuleHasNoFilterForRetentionCleanupRuleType()
	{
		final AfterRetentionCleanupRuleModel cleanupRuleExample = new AfterRetentionCleanupRuleModel();
		cleanupRuleExample.setRetirementItemType(composedType(mediaType()));

		final AfterRetentionCleanupRuleModel mediaCleanupRule = assertModelExists(cleanupRuleExample);

		assertThat(mediaCleanupRule.getItemtype()).isEqualTo("AfterRetentionCleanupRule");
		assertThat(mediaCleanupRule.getItemFilterExpression()).isNullOrEmpty();
	}

	protected String mediaType()
	{
		return getTypesToCleanup().stream().filter(s -> s.endsWith("Media")).findFirst().orElse(null);
	}

	protected ComposedTypeModel composedType(final String typeCode)
	{
		final ComposedTypeModel composedTypeModel = new ComposedTypeModel();
		composedTypeModel.setCode(typeCode);
		return assertModelExists(composedTypeModel);
	}

	@Test
	public void testRetentionCronJobsTriggersEveryDayAtMidnight()
	{
		final SearchResult<CronJobModel> cronJobResult = flexibleSearchService.search("SELECT {pk} FROM {CronJob}");
		final List<CronJobModel> cronJobs = cronJobResult.getResult();

		assertThat(cronJobs).hasSize(getTypesToCleanup().size());
		cronJobs.forEach(cronJob -> assertThat(cronJob.getTriggers().get(0).getCronExpression()).isEqualTo(EVERY_DAY_AT_MIDNIGHT));
	}

	protected abstract List<String> getTypesToCleanup();
}
