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
package de.hybris.y2ysync.task.distributed;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.LogFileModel;
import de.hybris.platform.processing.distributed.DistributedProcessService;
import de.hybris.platform.processing.distributed.defaultimpl.DistributedProcessHelper;
import de.hybris.platform.processing.enums.DistributedProcessState;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.y2ysync.enums.Y2YSyncType;
import de.hybris.y2ysync.model.Y2YColumnDefinitionModel;
import de.hybris.y2ysync.model.Y2YDistributedProcessModel;
import de.hybris.y2ysync.model.Y2YStreamConfigurationContainerModel;
import de.hybris.y2ysync.model.Y2YStreamConfigurationModel;
import de.hybris.y2ysync.model.Y2YSyncCronJobModel;

import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class Y2YSyncDistributedProcessHandlerTest extends ServicelayerBaseTest
{
	public static final String TEST_PROCESS_CODE = "TEST_PROCESS";
	@Resource
	private DistributedProcessService distributedProcessService;
	@Resource
	private ModelService modelService;
	@Resource
	private TypeService typeService;
	@Resource
	private CronJobService cronJobService;
	@Resource
	private MediaService mediaService;

	private Y2YStreamConfigurationContainerModel container;

	@Before
	public void setUp() throws Exception
	{
		IntStream.range(0, 1000).forEach(i -> createTitle("foo_" + i));

		container = modelService.create(Y2YStreamConfigurationContainerModel.class);
		container.setId("TEST_CONTAINER");
		modelService.save(container);

		final ComposedTypeModel title = typeService.getComposedTypeForCode("Title");

		final Y2YStreamConfigurationModel config = modelService.create(Y2YStreamConfigurationModel.class);
		config.setContainer(container);
		config.setStreamId("TITLES_STREAM");
		config.setItemTypeForStream(title);
		config.setAutoGenerateWhereClause(Boolean.TRUE);
		config.setWhereClause("");
		modelService.save(config);

		final Y2YColumnDefinitionModel column = modelService.create(Y2YColumnDefinitionModel.class);
		column.setPosition(Integer.valueOf(0));
		column.setStreamConfiguration(config);
		column.setAttributeDescriptor(typeService.getAttributeDescriptor(title, "code"));
		modelService.save(column);
	}

	private void createTitle(final String code)
	{
		final TitleModel title = modelService.create(TitleModel.class);
		title.setCode(code);

		modelService.save(title);
	}

	@Test
	public void shouldSuccessfullyFinishProcessIntoTheZipResult() throws Exception
	{
		// given
		final Y2YSyncProcessCreationData creationData = Y2YSyncProcessCreationData.builder().withProcessId("TEST_PROCESS")
				.withSyncType(Y2YSyncType.ZIP).withContainer(container).withBatchSize(100).build();

		// when
		distributedProcessService.create(creationData);
		distributedProcessService.start(TEST_PROCESS_CODE);
		final Y2YDistributedProcessModel process = waitForDistributedImpEx(TEST_PROCESS_CODE);

		// then
		assertThat(process.getState()).isEqualTo(DistributedProcessState.SUCCEEDED);
		assertThat(process.getY2ySyncCronJob().getStatus()).isEqualTo(CronJobStatus.FINISHED);
		assertThat(process.getY2ySyncCronJob().getResult()).isEqualTo(CronJobResult.SUCCESS);

		assertLogFilesCreated(process.getY2ySyncCronJob());
	}

	private void assertLogFilesCreated(final Y2YSyncCronJobModel cronJob)
	{
		assertThat(cronJob.getLogFiles()).isNotEmpty();
		int noEmpty = 0;
		for (final LogFileModel logFileModel : cronJob.getLogFiles())
		{

			if (mediaService.getDataFromMedia(logFileModel).length > 0)
			{
				noEmpty++;
			}
		}
		assertThat(noEmpty).isGreaterThan(0);
	}


	private Y2YDistributedProcessModel waitForDistributedImpEx(final String processCode)
	{
		do
		{
			try
			{
                final Y2YDistributedProcessModel process = (Y2YDistributedProcessModel) distributedProcessService.wait(processCode, 5);

				if (DistributedProcessHelper.isFinished(process))
				{
					final Y2YSyncCronJobModel cronJob = process.getY2ySyncCronJob();
					while (!cronJobService.isFinished(cronJob))
					{
						Thread.sleep(1000);
						modelService.refresh(cronJob);
					}
					return process;
				}
			}
			catch (final InterruptedException e)
			{
				throw new SystemException(e);
			}
		}
		while (true);
	}
}
