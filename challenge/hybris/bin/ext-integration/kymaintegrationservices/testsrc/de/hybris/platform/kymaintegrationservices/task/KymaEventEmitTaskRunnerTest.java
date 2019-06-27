/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2018 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package de.hybris.platform.kymaintegrationservices.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.apiregistryservices.event.EventExportFailedEvent;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.apiregistryservices.services.EventDlqService;
import de.hybris.platform.kymaintegrationservices.dto.JsonPublishRequestData;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.kymaintegrationservices.utils.RestTemplateWrapper;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.util.Config;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MutableMessage;
import org.springframework.integration.support.MutableMessageHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.UUID;

import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.*;
import static de.hybris.platform.kymaintegrationservices.utils.KymaHttpHelper.getDefaultHeaders;
import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


@IntegrationTest
public class KymaEventEmitTaskRunnerTest extends ServicelayerTest
{
    private static final String TEST_TASK_RUNNER = "kymaEventEmitTestTaskRunner";
    private static final String URL = "https://localhost:8081/v1/events";
    private static final String EXCEPTION_MESSAGE = "msg";

    private KymaEventEmitTaskRunner kymaEventEmitTaskRunner;

    @Mock
    private RestTemplateWrapper restTemplateWrapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MessageChannel messageChannel;

    @Resource
    private DestinationService destinationService;

    @Resource
    private EventDlqService eventDlqService;

    @Resource(name = "kymaExportJacksonObjectMapper")
    private ObjectMapper jacksonObjectMapper;

    @Resource
    private ModelService modelService;

    @Mock
    private TaskService taskService;

    @Resource
    private EventService eventService;

    private EventService eventServiceSpy;

    private PublishRequestData data;
    private MessageHeaders headers;
    private HttpEntity<String> request;
    private TaskModel taskSpy;

    private int maxConsecutiveRetries = Config.getInt(MAX_CONSECUTIVE_RETRIES, 3);
    private int maxRetries = Config.getInt(MAX_RETRIES, 3);

    @Resource
    private Converter<PublishRequestData, JsonPublishRequestData> kymaJsonEventConverter;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        eventServiceSpy = spy(eventService);

        kymaEventEmitTaskRunner = new KymaEventEmitTaskRunner();
        kymaEventEmitTaskRunner.setDestinationService(destinationService);
        kymaEventEmitTaskRunner.setEventChannel(messageChannel);
        kymaEventEmitTaskRunner.setRestTemplate(restTemplateWrapper);
        kymaEventEmitTaskRunner.setEventService(eventServiceSpy);
        kymaEventEmitTaskRunner.setKymaJsonEventConverter(kymaJsonEventConverter);

        importCsv("/test/apiConfigurations.impex", "UTF-8");

        data = new PublishRequestData();
        data.setEventType("type");
        data.setEventTypeVersion("v1");
        data.setData(new HashMap<>());
        data.setEventId(UUID.randomUUID().toString());
        data.setEventTime("2002-10-02T10:00:00-05:00");
        headers = new MutableMessageHeaders(null);
        final TaskModel task = modelService.create(TaskModel.class);
        taskSpy = spy(task);
        taskSpy.setRunnerBean(TEST_TASK_RUNNER);
        taskSpy.setContext(new MutableMessage<>(data, headers));

        when(messageChannel.send(any())).thenReturn(true);
        when(restTemplateWrapper.getUpdatedRestTemplate()).thenReturn(restTemplate);
        doNothing().when(restTemplateWrapper).updateCredentials(any());

        doAnswer(invocationOnMock -> {
            kymaEventEmitTaskRunner.run(taskService, taskSpy);
            return null;
        }).when(taskService).scheduleTask(any());

        final HttpHeaders httpHeaders = getDefaultHeaders();
        final JsonPublishRequestData jsonPublishRequestData = kymaJsonEventConverter.convert(data);
        request = new HttpEntity(jsonPublishRequestData, httpHeaders);
        when(restTemplate.postForEntity(any(String.class), any(), eq(String.class)))
              .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Config.setParameter(EVENTS_SERVICE_ID, "kyma-events");
    }

    protected void runTask()
    {
        kymaEventEmitTaskRunner.run(taskService, taskSpy);
    }

    @Test
    public void sendEvent()
    {
        runTask();
        verify(restTemplate, times(1)).postForEntity(URL, request, String.class);
        assertEquals(0, taskSpy.getRetry());
    }

    /*
     * Testing that 1) all consecutive retries within runner were triggered. 2) RetryLaterException thrown after
     * maxConsecutiveRetries has been reached. 3) event message was sent back to queue channel after maxConsecutiveRetries
     * has been reached.
     */
    @Test(expected = RetryLaterException.class)
    public void retryIsTriggered() throws Exception
    {
        when(restTemplate.postForEntity(URL, request, String.class)).thenThrow(new RestClientException(EXCEPTION_MESSAGE));
        runTask();
        verify(eventServiceSpy, times(1)).publishEvent(any(EventExportFailedEvent.class));
        verify(restTemplate, times(maxConsecutiveRetries + 1)).postForEntity(URL, request, String.class);
        verify(messageChannel, times(1)).send(any());
    }

    @Test
    public void eventMessageSendsSuccessfullyOnLastTry() throws Exception
    {
        final Integer[] currentStep = { -1 };
        final Integer throwNumber = (maxConsecutiveRetries + 1) - 1;

        when(restTemplate.postForEntity(URL, request, String.class)).then(invocationOnMock -> {
            currentStep[0] = currentStep[0] + 1;
            if (currentStep[0] < throwNumber)
                throw new RestClientException(EXCEPTION_MESSAGE);
            return new ResponseEntity<>(HttpStatus.OK);
        });

        // call
        kymaEventEmitTaskRunner.run(taskService, taskSpy);
        verify(messageChannel, never()).send(any());
        verify(eventServiceSpy, never()).publishEvent(any(EventExportFailedEvent.class));

    }
}
