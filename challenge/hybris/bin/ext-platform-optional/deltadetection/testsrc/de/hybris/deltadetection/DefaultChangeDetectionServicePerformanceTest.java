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
package de.hybris.deltadetection;

import de.hybris.bootstrap.annotations.PerformanceTest;
import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.deltadetection.impl.InMemoryChangesCollector;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import de.hybris.deltadetection.Y2YTestDataGenerator;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;


@PerformanceTest
public class DefaultChangeDetectionServicePerformanceTest extends ServicelayerBaseTest
{
	private final static int TITLES_NUMBER = 1000;
	private Y2YTestDataGenerator.TitlesFixture titlesFixture;

	@Resource
	private ModelService modelService;

	@Resource
	private TypeService typeService;

	@Resource
	private ChangeDetectionService changeDetectionService;

	@Before
	public void prepareTitles()
	{
		final Y2YTestDataGenerator y2YTestDataGenerator = new Y2YTestDataGenerator(modelService, typeService);
		titlesFixture = y2YTestDataGenerator.generateTitles(TITLES_NUMBER);
	}

	@Test
	public void changeConsumptionPerformanceTest()
	{
		final Stopwatch started = Stopwatch.createStarted();
		final InMemoryChangesCollector changesCollector = new InMemoryChangesCollector();

		System.out.println("Starting changes consumption");
		changeDetectionService.collectChangesForType(titlesFixture.getComposedType(), titlesFixture.getStreamId(), changesCollector);
		System.out.println("Changes collected: " + started.elapsed(TimeUnit.MILLISECONDS) + " " + TimeUnit.MILLISECONDS);

		changeDetectionService.consumeChanges(changesCollector.getChanges());
		System.out.println("Changes consumed: " + started.elapsed(TimeUnit.MILLISECONDS) + " " + TimeUnit.MILLISECONDS);
	}

}
