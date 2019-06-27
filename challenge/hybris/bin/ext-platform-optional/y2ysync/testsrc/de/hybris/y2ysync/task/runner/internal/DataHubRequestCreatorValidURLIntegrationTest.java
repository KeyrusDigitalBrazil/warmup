/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.y2ysync.task.runner.internal;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.task.TaskService;
import de.hybris.y2ysync.enums.Y2YSyncType;
import de.hybris.y2ysync.model.Y2YStreamConfigurationContainerModel;
import de.hybris.y2ysync.model.Y2YSyncCronJobModel;
import de.hybris.y2ysync.model.Y2YSyncJobModel;
import de.hybris.y2ysync.rest.resources.DataStream;
import de.hybris.y2ysync.rest.resources.Y2YSyncRequest;
import de.hybris.y2ysync.task.dao.Y2YSyncDAO;
import de.hybris.y2ysync.task.internal.SyncTaskFactory;
import de.hybris.y2ysync.task.runner.TaskContext;
import de.hybris.y2ysync.task.runner.Y2YSyncContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


@IntegrationTest
public class DataHubRequestCreatorValidURLIntegrationTest extends ServicelayerBaseTest
{
	private final static String SYNC_EXECUTION_ID = "testExecutionId";
	private final static String HOME_URL = "http://localhost:9001";
	private final static String CONSUME_CHANGES_WEBROOT = "/y2ysync";
	private final static String DATAHUB_URI = "/datahub-webapp/v1/data-feeds/y2ysync";

	private static final String TEST_CONDITION_ID = "TEST_CONDITION_ID";
	private static final String TEST_INSERT_UPDATE_HEADER = "INSERT_UPDATE Product;code[unique=true];name[lang=pl]";
	private static final String TEST_TYPE_CODE = "Product";

	private static final String FEED_NAME = "Y2YSYNC_FEED";
	private static final String POOL_NAME = "Y2YSYNC_POOL";
	private static final String TARGET_SYSTEMS = "";

	private DataHubRequestCreator requestCreator;
	private final RestTemplate restTemplate = getRestTemplate();

	private Y2YSyncContext ctx;

	@Resource
	private ModelService modelService;

	@Resource
	private MediaService mediaService;
	@Resource
	private ChangeDetectionService changeDetectionService;
	@Resource
	private TaskService taskService;
	@Resource
	private TypeService typeService;
	@Resource
	private Y2YSyncDAO y2ySyncDAO;

	@Before
	public void setUp()
	{
		requestCreator = new DataHubRequestCreator()
		{
			@Override
			String getHomeUrl()
			{
				return HOME_URL;
			}

			@Override
			String getY2YSyncWebRoot()
			{
				return HOME_URL + CONSUME_CHANGES_WEBROOT;
			}

			@Override
			protected String getDataHubUserName()
			{
				return "test_admin";
			}

			@Override
			protected String getDataHubPassword()
			{
				return "test_nimda";
			}


		};
		requestCreator.setY2YSyncDAO(y2ySyncDAO);
		requestCreator.setRestTemplate(restTemplate);

		ctx = Y2YSyncContext.builder().withSyncExecutionId(SYNC_EXECUTION_ID).withUri(DATAHUB_URI).withFeed(FEED_NAME)
				.withPool(POOL_NAME).withAutoPublishTargetSystems(TARGET_SYSTEMS).build();

		createExportCronJob();

		modelService.saveAll();
	}

	private Y2YSyncCronJobModel createExportCronJob()
	{
		final Y2YStreamConfigurationContainerModel container = modelService.create(Y2YStreamConfigurationContainerModel.class);
		container.setId("testContainer");
		modelService.save(container);

		final Y2YSyncJobModel syncJob = modelService.create(Y2YSyncJobModel.class);
		syncJob.setCode("testJob");
		syncJob.setSyncType(Y2YSyncType.ZIP);
		syncJob.setStreamConfigurationContainer(container);
		modelService.save(syncJob);

		final Y2YSyncCronJobModel cronJob = modelService.create(Y2YSyncCronJobModel.class);
		cronJob.setCode(SYNC_EXECUTION_ID);
		cronJob.setJob(syncJob);
		modelService.save(cronJob);

		return cronJob;
	}

	@Test
	public void shouldGenerateValidMediaULRsForY2YSyncDataStreams() throws Exception
	{
		final String content = ";KEB;Kebab\n;KIE;Kielbasa";
		final ImportScript script = givenInsertUpdateScript(content);
		final ProcessChangesTask task = givenProcessChangesTaskWith(script);

		//should create media in database
		task.execute();

		requestCreator.sendRequest(ctx);

		final ArgumentCaptor<Y2YSyncRequest> argumentCaptor = ArgumentCaptor.forClass(Y2YSyncRequest.class);
		Mockito.verify(restTemplate, Mockito.times(1)).postForEntity(Mockito.anyString(), argumentCaptor.capture(),
				Mockito.eq(Void.class));

		assertThat(argumentCaptor.getValue()).isNotNull();

		final UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		assertThat(argumentCaptor.getValue().getDataStreams()).flatExtracting(DataStream::getUrls).allMatch(urlValidator::isValid);

	}

	private ImportScript givenInsertUpdateScript(final String rows)
	{
		return new ImportScript(TEST_TYPE_CODE, TEST_INSERT_UPDATE_HEADER, rows, null);
	}

	private ProcessChangesTask givenProcessChangesTaskWith(final ImportScript... scripts)
	{
		final Map<String, Object> context = ImmutableMap.<String, Object> builder()
				.put(SyncTaskFactory.CONDITION_NAME_KEY, TEST_CONDITION_ID) //
				.put(SyncTaskFactory.SYNC_EXECUTION_ID_KEY, SYNC_EXECUTION_ID).put(SyncTaskFactory.SYNC_TYPE_KEY, Y2YSyncType.DATAHUB)
				.put(SyncTaskFactory.MEDIA_PK_KEY, PK.createFixedUUIDPK(1, 1)).build();

		final TaskContext taskContext = new TaskContext(modelService, mediaService, context)
		{
			@Override
			public List<ItemChangeDTO> getChanges()
			{
				return Collections.emptyList();
			}
		};
		return new ProcessChangesTask(modelService, mediaService, changeDetectionService, taskService, typeService, y2ySyncDAO,
				taskContext, ImmutableList.copyOf(scripts));
	}


	private RestTemplate getRestTemplate()
	{
		return Mockito.mock(RestTemplate.class);
	}


}
