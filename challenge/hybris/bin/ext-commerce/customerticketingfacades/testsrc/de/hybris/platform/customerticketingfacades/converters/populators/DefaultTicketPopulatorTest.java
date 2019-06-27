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
package de.hybris.platform.customerticketingfacades.converters.populators;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.comments.model.CommentAttachmentModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketCategory;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.data.TicketEventData;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.ticket.enums.CsTicketCategory;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static org.junit.Assert.assertEquals;


/**
 * Test class for DefaultTicketPopulator.
 */
@IntegrationTest
public class DefaultTicketPopulatorTest extends ServicelayerTest
{
	@InjectMocks
	private DefaultTicketPopulator populator;

	@Resource(name = "ticketEventConverter")
	private Converter<CsTicketEventModel, TicketEventData> ticketEventConverter;

	@Mock
	protected CsTicketModel csTicketModel;

	@Mock
	protected CustomerModel customerModel;

	@Mock
	private TicketService ticketService;

	@Mock
	protected CsTicketEventModel csTicketEventOneModel;

	@Mock
	protected CsTicketEventModel csTicketEventTwoModel;

	@Mock
	private AbstractOrderModel abstractOrderModel;

	protected static final String COMPLETED = "COMPLETED";
	protected static final String OPEN = "OPEN";
	protected static final String INPROCESS = "INPROCESS";

	protected static final String KEY_CLOSED = "Closed";
	protected static final String KEY_NEW = "New";
	protected static final String KEY_OPEN = "Open";

	protected static final String TEXT1 = "TEXT1";
	protected static final String TEXT2 = "TEXT2";

	protected static final String SUBJECT = "headline-SUBJECT";
	protected static final String CUSTOMER_NAME = "customer-name";

	protected final String orderCode = "order-code-123";
	protected final String orderItemType = "Order";
	protected final String customerUid = "customer@test.com";

	protected Map<String, StatusData> statusMap;

	protected Date today;
	protected Date yesterday;

	/**
	 * Test setup.
	 */
	@Before
	public void setup()
	{
		final DateTime fixedDay = new DateTime(2016, 7, 11, 12, 0, 0, 0);
		today = fixedDay.toLocalDate().toDate();
		yesterday = fixedDay.minusDays(1).toDate();

		populator = new DefaultTicketPopulator();
		MockitoAnnotations.initMocks(this);

		populator.setStatusMapping(buildStatusMap());
		populator.setValidTransitions(buildValidTransitions());

		populator.setTicketEventConverter(ticketEventConverter);

		Mockito.when(csTicketModel.getCustomer()).thenReturn(customerModel);
		Mockito.when(customerModel.getUid()).thenReturn(customerUid);

		Mockito.when(csTicketModel.getOrder()).thenReturn(abstractOrderModel);
		Mockito.when(abstractOrderModel.getCode()).thenReturn(orderCode);
		Mockito.when(abstractOrderModel.getItemtype()).thenReturn(orderItemType);
		Mockito.when(abstractOrderModel.getModifiedtime()).thenReturn(today);

		Mockito.when(csTicketEventOneModel.getModifiedtime()).thenReturn(today);
		Mockito.when(csTicketEventTwoModel.getModifiedtime()).thenReturn(yesterday);

		Mockito.when(ticketService.getTicketEventsForCustomerByTicket(csTicketModel))
				.thenReturn(Arrays.asList(csTicketEventOneModel, csTicketEventTwoModel));

		Mockito.when(csTicketEventOneModel.getTicket()).thenReturn(csTicketModel);
		Mockito.when(csTicketEventTwoModel.getTicket()).thenReturn(csTicketModel);
		Mockito.when(customerModel.getName()).thenReturn(CUSTOMER_NAME);
	}

	private Map<StatusData, List<StatusData>> buildValidTransitions()
	{
		final Map<StatusData, List<StatusData>> validTransitions = Maps.newHashMap();

		validTransitions.put(createStatus(OPEN), Collections.singletonList(createStatus(COMPLETED)));
		validTransitions.put(createStatus(INPROCESS), Collections.singletonList(createStatus(COMPLETED)));
		validTransitions.put(createStatus(COMPLETED), Collections.singletonList(createStatus(INPROCESS)));

		return validTransitions;
	}

	protected Map<String, StatusData> buildStatusMap()
	{
		final Map<String, StatusData> statusDataMap = Maps.newHashMap();

		final StatusData openStatus = createStatus(OPEN);
		final StatusData inProgressStatus = createStatus(INPROCESS);
		final StatusData completedStatus = createStatus(COMPLETED);

		statusDataMap.put(KEY_NEW, openStatus);
		statusDataMap.put(KEY_OPEN, inProgressStatus);
		statusDataMap.put(KEY_CLOSED, completedStatus);

		return statusDataMap;
	}

	protected StatusData createStatus(final String statusString)
	{
		if (statusMap == null)
		{
			statusMap = Maps.newHashMap();
		}
		if (statusMap.containsKey(statusString))
		{
			return statusMap.get(statusString);
		}

		final StatusData status = new StatusData();
		status.setId(statusString.toUpperCase());

		statusMap.put(statusString, status);

		return status;
	}

	/**
	 * This test should populate ticket model basic data information.
	 */
	@Test
	public void shouldPopulateTicketData()
	{
		Mockito.when(csTicketModel.getCategory()).thenReturn(CsTicketCategory.ENQUIRY);
		Mockito.when(csTicketModel.getState()).thenReturn(CsTicketState.NEW);
		Mockito.when(csTicketModel.getHeadline()).thenReturn(SUBJECT);
		Mockito.when(csTicketModel.getCreationtime()).thenReturn(yesterday);
		Mockito.when(csTicketModel.getModifiedtime()).thenReturn(today);
		Mockito.when(customerModel.getName()).thenReturn(CUSTOMER_NAME);

		final TicketData ticketData = new TicketData();
		populator.populate(csTicketModel, ticketData);

		assertEquals(SUBJECT, ticketData.getSubject());
		assertEquals(OPEN, ticketData.getStatus().getId());
		assertEquals(COMPLETED, ticketData.getAvailableStatusTransitions().get(0).getId());
		assertEquals(yesterday, ticketData.getCreationDate());
		assertEquals(today, ticketData.getLastModificationDate());
		assertEquals(TicketCategory.ENQUIRY, ticketData.getTicketCategory());
	}

	/**
	 * This test should populate the ticket order which is associated to.
	 */
	@Test
	public void shouldPopulateOrder()
	{
		Mockito.when(csTicketModel.getCategory()).thenReturn(CsTicketCategory.ENQUIRY);
		Mockito.when(csTicketModel.getState()).thenReturn(CsTicketState.NEW);
		Mockito.when(csTicketModel.getHeadline()).thenReturn(SUBJECT);
		Mockito.when(csTicketModel.getCreationtime()).thenReturn(yesterday);
		Mockito.when(csTicketModel.getModifiedtime()).thenReturn(today);
		Mockito.when(customerModel.getName()).thenReturn(CUSTOMER_NAME);

		final TicketData ticketData = new TicketData();
		populator.populate(csTicketModel, ticketData);

		assertEquals("Order: order-code-123; Updated: 11-07-16", ticketData.getAssociatedTo());
	}

	/**
	 * This test should populate ticket model's event information.
	 */
	@Test
	public void shouldPopulateTicketEvent()
	{
		final EmployeeModel agent = Mockito.mock(EmployeeModel.class);
		Mockito.when(csTicketModel.getCategory()).thenReturn(CsTicketCategory.ENQUIRY);
		Mockito.when(csTicketModel.getState()).thenReturn(CsTicketState.NEW);
		Mockito.when(csTicketModel.getHeadline()).thenReturn(SUBJECT);
		Mockito.when(csTicketModel.getCreationtime()).thenReturn(yesterday);
		Mockito.when(csTicketModel.getModifiedtime()).thenReturn(today);
		Mockito.when(customerModel.getName()).thenReturn(CUSTOMER_NAME);

		Mockito.when(csTicketEventOneModel.getText()).thenReturn(TEXT1);
		Mockito.when(csTicketEventOneModel.getCreationtime()).thenReturn(today);
		Mockito.when(csTicketEventOneModel.getAuthor()).thenReturn(agent);

		Mockito.when(csTicketEventTwoModel.getEntries()).thenReturn(Collections.emptySet());
		Mockito.when(csTicketEventTwoModel.getText()).thenReturn(TEXT2);
		Mockito.when(csTicketEventTwoModel.getCreationtime()).thenReturn(yesterday);

		final TicketData ticketData = new TicketData();
		populator.populate(csTicketModel, ticketData);

		assertEquals(2, ticketData.getTicketEvents().size());

		assertEquals(TEXT1, ticketData.getTicketEvents().get(0).getText());
		assertEquals(Boolean.TRUE, ticketData.getTicketEvents().get(0).getAddedByAgent());
		assertEquals(TEXT1, ticketData.getTicketEvents().get(0).getText());

		assertEquals(TEXT2, ticketData.getTicketEvents().get(1).getText());


	}

	/**
	 * This test should populate ticket model's attachments.
	 */
	@Test
	public void shouldPopulateTicketAttachment()
	{
		final EmployeeModel agent = Mockito.mock(EmployeeModel.class);
		Mockito.when(csTicketModel.getCategory()).thenReturn(CsTicketCategory.ENQUIRY);
		Mockito.when(csTicketModel.getState()).thenReturn(CsTicketState.NEW);
		Mockito.when(csTicketModel.getHeadline()).thenReturn(SUBJECT);
		Mockito.when(csTicketModel.getCreationtime()).thenReturn(yesterday);
		Mockito.when(csTicketModel.getModifiedtime()).thenReturn(today);
		Mockito.when(customerModel.getName()).thenReturn(CUSTOMER_NAME);


		Mockito.when(csTicketEventOneModel.getText()).thenReturn(TEXT1);
		Mockito.when(csTicketEventOneModel.getCreationtime()).thenReturn(today);
		Mockito.when(csTicketEventOneModel.getAuthor()).thenReturn(agent);
		Mockito.when(csTicketEventTwoModel.getEntries()).thenReturn(Collections.emptySet());
		Mockito.when(csTicketEventTwoModel.getText()).thenReturn(TEXT2);
		Mockito.when(csTicketEventTwoModel.getCreationtime()).thenReturn(yesterday);

		final CommentAttachmentModel attachmentModel = Mockito.mock(CommentAttachmentModel.class);
		final MediaModel mediaModel = Mockito.mock(MediaModel.class);

		Mockito.when(csTicketEventOneModel.getAttachments()).thenReturn(Collections.singletonList(attachmentModel));
		Mockito.when(attachmentModel.getItem()).thenReturn(mediaModel);
		Mockito.when(mediaModel.getRealFileName()).thenReturn("real-file-name");
		Mockito.when(mediaModel.getURL()).thenReturn("test-url");

		final TicketData ticketData = new TicketData();
		populator.populate(csTicketModel, ticketData);

		assertEquals(1, ticketData.getTicketEvents().get(0).getAttachments().size());
		assertEquals("real-file-name", ticketData.getTicketEvents().get(0).getAttachments().get(0).getFilename());
		assertEquals("test-url", ticketData.getTicketEvents().get(0).getAttachments().get(0).getURL());
	}

	/**
	 * This test should populate ticket category as null value if there is no match category found in system.
	 */
	@Test
	public void shouldPopulateWhenWrongTicketCategory()
	{
		Mockito.when(csTicketModel.getState()).thenReturn(CsTicketState.NEW);
		Mockito.when(csTicketModel.getHeadline()).thenReturn(SUBJECT);
		Mockito.when(csTicketModel.getCreationtime()).thenReturn(yesterday);
		Mockito.when(csTicketModel.getModifiedtime()).thenReturn(today);

		Mockito.when(csTicketModel.getCategory()).thenReturn(CsTicketCategory.valueOf("wrong-status"));

		Mockito.when(customerModel.getName()).thenReturn(CUSTOMER_NAME);
		final TicketData ticketData = new TicketData();
		populator.populate(csTicketModel, ticketData);

		Assert.assertNull(ticketData.getTicketCategory());
	}

	/**
	 * This test should return the correctly history message.
	 */
	@Test
	public void shouldGetHistoryMessage()
	{
		final EmployeeModel agent = Mockito.mock(EmployeeModel.class);
		Mockito.when(csTicketModel.getCreationtime()).thenReturn(yesterday);
		Mockito.when(csTicketEventOneModel.getText()).thenReturn(TEXT1);
		Mockito.when(csTicketEventOneModel.getCreationtime()).thenReturn(today);
		Mockito.when(csTicketEventOneModel.getAuthor()).thenReturn(agent);
		Mockito.when(csTicketEventTwoModel.getEntries()).thenReturn(Collections.emptySet());
		Mockito.when(csTicketEventTwoModel.getText()).thenReturn(TEXT2);
		Mockito.when(csTicketEventTwoModel.getCreationtime()).thenReturn(yesterday);

		Mockito.when(ticketService.getEventsForTicket(csTicketModel))
				.thenReturn(Lists.newArrayList(csTicketEventOneModel, csTicketEventTwoModel));

		List<TicketEventData> ticketEvents = populator.getTicketEvents(csTicketModel);
		assertEquals(2,ticketEvents.size());
		assertText(ticketEvents,"TEXT1", "TEXT2")	;
		assertDisplayText(ticketEvents,
				"CUSTOMER SERVICE on 11-07-16 12:00 AM\nTEXT1",
				"customer-name on 10-07-16 12:00 PM\nTEXT2")	;

	}

	protected void assertDisplayText(List<TicketEventData> ticketEvents, String ... displayText)
	{
		for(int i=0; i< ticketEvents.size(); i ++){
			assertEquals(ticketEvents.get(i).getDisplayText(),displayText[i]);
		}
	}

	protected void assertText(List<TicketEventData> ticketEvents, String ... text)
	{
		for(int i=0; i< ticketEvents.size(); i ++){
			assertEquals(ticketEvents.get(i).getText(),text[i]);
		}
	}
}
