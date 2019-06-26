package de.hybris.platform.kymaintegrationservices.task;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.apiregistryservices.event.EventExportFailedEvent;
import de.hybris.platform.apiregistryservices.model.ConsumedCertificateCredentialModel;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.kymaintegrationservices.dto.JsonPublishRequestData;
import de.hybris.platform.kymaintegrationservices.dto.PublishRequestData;
import de.hybris.platform.kymaintegrationservices.utils.RestTemplateWrapper;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskService;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.logging.HybrisLogListener;
import de.hybris.platform.util.logging.HybrisLogger;
import de.hybris.platform.util.logging.HybrisLoggingEvent;
import org.apache.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.support.MutableMessage;
import org.springframework.integration.support.MutableMessageHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static de.hybris.platform.kymaintegrationservices.utils.KymaEventExportUtils.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
public class KymaEventEmitTaskRunnerUnitTest extends HybrisJUnit4Test
{
    private static final String TEST_TASK_RUNNER = "kymaEventEmitTestTaskRunner";
    private static final String URL = "https://localhost:8081/v1/events";
    private static final String EXCEPTION_MESSAGE = "msg";

    @InjectMocks
    private KymaEventEmitTaskRunner kymaEventEmitTaskRunner;

    @Mock
    private RestTemplateWrapper restTemplateWrapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MessageChannel messageChannel;

    @Mock
    private DestinationService destinationService;

    @Mock
    private TaskService taskService;

    private EventService eventServiceSpy;

    private PublishRequestData data;
    private MessageHeaders headers;
    private TaskModel taskSpy;

    private int maxConsecutiveRetries;
    private int maxRetries;
    private TestListener listener;

    @Mock
    private Converter<PublishRequestData, JsonPublishRequestData> kymaJsonEventConverter;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        eventServiceSpy = spy(EventService.class);

        kymaEventEmitTaskRunner = new KymaEventEmitTaskRunner();
        kymaEventEmitTaskRunner.setDestinationService(destinationService);
        kymaEventEmitTaskRunner.setEventChannel(messageChannel);
        kymaEventEmitTaskRunner.setRestTemplate(restTemplateWrapper);
        kymaEventEmitTaskRunner.setEventService(eventServiceSpy);
        kymaEventEmitTaskRunner.setKymaJsonEventConverter(kymaJsonEventConverter);

        maxConsecutiveRetries = 3;
        Config.setParameter(MAX_CONSECUTIVE_RETRIES, String.valueOf(maxConsecutiveRetries));
        maxRetries = 3;
        Config.setParameter(MAX_RETRIES, String.valueOf(maxRetries));

        ConsumedDestinationModel consumedDestinationModel = mock(ConsumedDestinationModel.class);
        when(consumedDestinationModel.getId()).thenReturn("");
        when(consumedDestinationModel.getUrl()).thenReturn(URL);
        when(consumedDestinationModel.getCredential()).thenReturn(new ConsumedCertificateCredentialModel());
        doReturn(consumedDestinationModel).when(destinationService).getDestinationById(any());

        data = new PublishRequestData();
        data.setEventType("type");
        data.setEventTypeVersion("v1");
        data.setData(new HashMap<>());
        data.setEventId(UUID.randomUUID().toString());
        data.setEventTime("2002-10-02T10:00:00-05:00");

        headers = new MutableMessageHeaders(null);
        taskSpy = spy(TaskModel.class);
        taskSpy.setRunnerBean(TEST_TASK_RUNNER);
        taskSpy.setContext(new MutableMessage<>(data, headers));

        when(messageChannel.send(any())).thenReturn(true);
        when(messageChannel.send(any())).thenReturn(true);
        when(restTemplateWrapper.getUpdatedRestTemplate()).thenReturn(restTemplate);
        doNothing().when(restTemplateWrapper).updateCredentials(any());

        JsonPublishRequestData convertedData = new JsonPublishRequestData();
        convertedData.setEventId(UUID.randomUUID().toString());
        when(kymaJsonEventConverter.convert(data)).thenReturn(convertedData);

        doAnswer(invocationOnMock -> {
            kymaEventEmitTaskRunner.run(taskService, taskSpy);
            return null;
        }).when(taskService).scheduleTask(any());

        Config.setParameter(EVENTS_SERVICE_ID, "kyma-events");

        listener = new TestListener();
        HybrisLogger.addListener(listener);
    }

    @After
    public void tearDown() throws Exception
    {
        HybrisLogger.removeListener(listener);
    }

    /*
        Testing that
         1) all consecutive retries within runner were triggered.
         2) N-1 retries of Task were triggered and no retry exception was thrown
        */
    @Test
    public void eventMessageSendsSuccessfullyOnLastTry()
    {
        final Integer[] currentStep = { -1 };
        final Integer throwNumber = maxConsecutiveRetries;

        when(restTemplate.postForEntity(eq(URL), any(), any())).then(invocationOnMock -> {
            currentStep[0] = currentStep[0] + 1;
            if (currentStep[0] < throwNumber)
                throw new RestClientException(EXCEPTION_MESSAGE);
            return new ResponseEntity<>(HttpStatus.OK);
        });

        // call
        kymaEventEmitTaskRunner.run(taskService, taskSpy);
        assertTrue(listener.getEvents().stream().map(e -> (String) e.getMessage())
              .anyMatch(o -> o.contains("Can not send event to the kyma, event type")));
        assertTrue(listener.getEvents().stream().map(e -> (String) e.getMessage())
              .anyMatch(o -> o.contains("Starting consecutive retries to send event to the kyma, event type")));
        assertTrue(listener.getEvents().stream().map(e -> (String) e.getMessage())
              .noneMatch(o -> o.contains("Maximum of retries is reached, putting Event back to queue")));
        assertTrue(listener.getEvents().stream().map(e -> (String) e.getMessage())
              .noneMatch(o -> o.contains("Maximum of retries is not reached, triggering retry")));

        // we didn't resend event
        verify(messageChannel, never()).send(any());
    }

    /*
    Testing that
     1) all consecutive retries within runner were triggered.
     2) N retries of Task were triggered and retry exception was thrown
    */
    @Test(expected = RetryLaterException.class)
    public void retryIsTriggered() throws Exception
    {
        when(restTemplate.postForEntity(eq(URL), any(), any())).thenThrow(new RestClientException(EXCEPTION_MESSAGE));
        when(taskSpy.getRetry()).thenReturn(0);
        // call
        kymaEventEmitTaskRunner.run(taskService, taskSpy);

        assertTrue(listener.getEvents().stream().map(e -> (String) e.getMessage())
              .anyMatch(o -> o.contains("Can not send event to the kyma, event type")));
        assertTrue(listener.getEvents().stream().map(e -> (String) e.getMessage())
              .anyMatch(o -> o.contains("Starting consecutive retries to send event to the kyma, event type")));
        assertTrue(listener.getEvents().stream().map(e -> (String) e.getMessage())
              .anyMatch(o -> o.contains("Maximum of retries is not reached, triggering retry")));

        verify(eventServiceSpy, times(1)).publishEvent(any(EventExportFailedEvent.class));
    }

    public static class TestListener implements HybrisLogListener
    {
        public List<HybrisLoggingEvent> events = new ArrayList<>();

        public List<HybrisLoggingEvent> getEvents()
        {
            return events;
        }

        @Override
        public boolean isEnabledFor(final Level level)
        {
            return true;
        }

        @Override
        public void log(final HybrisLoggingEvent event)
        {
            this.events.add(event);
        }
    }
}
