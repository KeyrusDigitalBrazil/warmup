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
package de.hybris.y2ysync.task.runner;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.deltadetection.impl.InMemoryChangesCollector;
import de.hybris.deltadetection.model.ItemVersionMarkerModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.utils.NeedsTaskEngine;
import de.hybris.deltadetection.Y2YTestDataGenerator;
import de.hybris.y2ysync.model.media.ConsumeMarkerMediaModel;
import de.hybris.y2ysync.task.internal.SyncTaskFactory;

import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import org.springframework.util.SerializationUtils;

import static org.fest.assertions.Assertions.assertThat;


@IntegrationTest
@NeedsTaskEngine
public class ConsumeY2YChangesTaskRunnerTest extends ServicelayerBaseTest
{
	private final static int TITLES_NUMBER = 5;
	private Y2YTestDataGenerator.TitlesFixture titlesFixture;

	@Resource
	private SyncTaskFactory syncTaskFactory;

	@Resource
	private ModelService modelService;

	@Resource
	private TypeService typeService;

	@Resource
	private ChangeDetectionService changeDetectionService;

	@Resource
	private MediaService mediaService;

	@Resource
	private FlexibleSearchService flexibleSearchService;

	private InMemoryChangesCollector changesCollector;
	@Before
	public void prepareTitles()
	{
		final Y2YTestDataGenerator y2YTestDataGenerator = new Y2YTestDataGenerator(modelService, typeService);
		titlesFixture = y2YTestDataGenerator.generateTitles(TITLES_NUMBER);
		changesCollector = new InMemoryChangesCollector();
	}

	private void createConsumeMarkerMedia(final String syncExecutionId, final InMemoryChangesCollector changesCollector )
	{
		final ConsumeMarkerMediaModel media = modelService.create(ConsumeMarkerMediaModel.class);
		media.setCode(UUID.randomUUID().toString());
		media.setSyncExecutionID(syncExecutionId);
		modelService.save(media);
		mediaService.setDataForMedia(media, SerializationUtils.serialize(changesCollector.getChanges()));
		modelService.saveAll();
	}

	@Test
	public void taskShouldConsumeAllChanges() throws InterruptedException {
		// given
		final String syncExecutionId = UUID.randomUUID().toString();

		changeDetectionService.collectChangesForType(titlesFixture.getComposedType(), titlesFixture.getStreamId(),
				changesCollector);
		createConsumeMarkerMedia(syncExecutionId, changesCollector);

		assertItemVersionMarkerNumber(0);

		// when
		syncTaskFactory.runConsumeSyncChangesTask(syncExecutionId);

		Thread.sleep(10_000);
		modelService.detachAll();

		// then
		assertItemVersionMarkerNumber(5);
	}

	private void assertItemVersionMarkerNumber(final int number)
	{
		final List<ItemVersionMarkerModel> afterResult = flexibleSearchService
				.<ItemVersionMarkerModel> search("SELECT {PK} FROM {" + ItemVersionMarkerModel._TYPECODE + "}").getResult();

		assertThat(afterResult).hasSize(number);
	}
}
