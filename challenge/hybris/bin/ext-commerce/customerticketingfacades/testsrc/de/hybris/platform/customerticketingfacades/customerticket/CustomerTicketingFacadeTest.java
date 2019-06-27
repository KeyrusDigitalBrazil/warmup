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
package de.hybris.platform.customerticketingfacades.customerticket;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerticketingfacades.data.StatusData;
import de.hybris.platform.customerticketingfacades.data.TicketAssociatedData;
import de.hybris.platform.customerticketingfacades.data.TicketData;
import de.hybris.platform.customerticketingfacades.strategies.TicketAssociationStrategies;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.ticket.enums.CsEventReason;
import de.hybris.platform.ticket.enums.CsInterventionType;
import de.hybris.platform.ticket.enums.CsResolutionType;
import de.hybris.platform.ticket.enums.CsTicketState;
import de.hybris.platform.ticket.events.model.CsCustomerEventModel;
import de.hybris.platform.ticket.events.model.CsTicketResolutionEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.service.TicketAttachmentsService;
import de.hybris.platform.ticket.service.TicketBusinessService;
import de.hybris.platform.ticket.service.TicketException;
import de.hybris.platform.ticket.service.TicketService;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * Test cases for CustomerTicketingFacade class.
 */
@UnitTest
public class CustomerTicketingFacadeTest
{

	@InjectMocks
	private DefaultCustomerTicketingFacade facade;

	@Mock
	private TicketService ticketService;

	@Mock
	private TicketBusinessService ticketBusinessService;

	@Mock
	private TicketAssociationStrategies strategy1;

	@Mock
	private TicketAssociationStrategies strategy2;

	@Mock
	private TicketAttachmentsService ticketAttachmentsService;

	@Mock
	private UserService userService;

	private static final String COMPLETED = "COMPLETED";
	private static final String OPEN = "OPEN";
	private static final String INPROCESS = "INPROCESS";
	private static final String FILE1 = "file1";
	private static final String FILE2 = "file2";
	private static final String MESSAGE = "message";
	private static final String TICKET_ID = "ticket-id";

	private Map<String, StatusData> statusMap;

	/**
	 * Test seutp.
	 */
	@Before
	public void setup()
	{
		facade = new DefaultCustomerTicketingFacade();
		MockitoAnnotations.initMocks(this);

		final List<TicketAssociationStrategies> associationStrategies = Lists.newArrayList(strategy1, strategy2);
		facade.setAssociationStrategies(associationStrategies);
		facade.setStatusMapping(buildStatusMap());
	}

	/**
	 * util method to build status map.
	 *
	 * @return a map of status data
	 */
	private Map<String, StatusData> buildStatusMap()
	{
		final Map<String, StatusData> statusDataMap = Maps.newHashMap();

		final StatusData openStatus = createStatus(OPEN);
		final StatusData inProgressStatus = createStatus(INPROCESS);
		final StatusData completedStatus = createStatus(COMPLETED);

		statusDataMap.put("New", openStatus);
		statusDataMap.put("Open", inProgressStatus);
		statusDataMap.put("Closed", completedStatus);

		return statusDataMap;
	}

	/**
	 * util method for created a status data by given status string
	 *
	 * @param statusString
	 *           status
	 * @return StatusData
	 */
	private StatusData createStatus(final String statusString)
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
	 * Test of
	 * {@link de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade#getAssociatedToObjects()}
	 * <p>
	 * should return the associate object map which contents 2 TicketAssociatedData.
	 */
	@Test
	public void shouldReturnAssociatedObjectMapForCurrentUser()
	{
		final UserModel currentUser = Mockito.mock(UserModel.class);

		Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

		final Map<String, List<TicketAssociatedData>> objectMap1 = Maps.newHashMap();
		final TicketAssociatedData ticketAssociatedData1 = new TicketAssociatedData();
		objectMap1.put("object1", Lists.newArrayList(ticketAssociatedData1));
		final Map<String, List<TicketAssociatedData>> objectMap2 = Maps.newHashMap();
		final TicketAssociatedData ticketAssociatedData2 = new TicketAssociatedData();
		objectMap1.put("object2", Lists.newArrayList(ticketAssociatedData2));

		Mockito.when(strategy1.getObjects(currentUser)).thenReturn(objectMap1);
		Mockito.when(strategy2.getObjects(currentUser)).thenReturn(objectMap2);

		final Map<String, List<TicketAssociatedData>> associatedToObjects = facade.getAssociatedToObjects();

		Assert.assertEquals(ticketAssociatedData1, associatedToObjects.get("object1").get(0));
		Assert.assertEquals(ticketAssociatedData2, associatedToObjects.get("object2").get(0));
	}

	/**
	 * Test of
	 * {@link de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade#getCsStatus(TicketData)}
	 * <p>
	 * should return CsTicketStatus.NEW
	 */
	@Test
	public void shouldReturnCsStatusNew()
	{
		final TicketData ticketData = new TicketData();
		ticketData.setStatus(statusMap.get(OPEN));
		final CsTicketState csStatus = facade.getCsStatus(ticketData);
		Assert.assertEquals(CsTicketState.NEW, csStatus);
	}

	/**
	 * Test of
	 * {@link de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade#getCsStatus(TicketData)}
	 * <p>
	 * should return CsTicketStatus.OPEN
	 */
	@Test
	public void shouldReturnCsStatusOpen()
	{
		final TicketData ticketData = new TicketData();
		ticketData.setStatus(statusMap.get(INPROCESS));
		final CsTicketState csStatus = facade.getCsStatus(ticketData);
		Assert.assertEquals(CsTicketState.OPEN, csStatus);
	}

	/**
	 * Test of
	 * {@link de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade#getCsStatus(TicketData)}
	 * <p>
	 * should return CsTicketStatus.CLOSED
	 */
	@Test
	public void shouldReturnCsStatusClose()
	{
		final TicketData ticketData = new TicketData();
		ticketData.setStatus(statusMap.get(COMPLETED));
		final CsTicketState csStatus = facade.getCsStatus(ticketData);
		Assert.assertEquals(CsTicketState.CLOSED, csStatus);
	}

	/**
	 * Test of
	 * {@link de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade#getCsStatus(TicketData)}
	 * <p>
	 * should return NULL value if no match status found.
	 */
	@Test
	public void shouldReturnNullIfNoMatchCsStatus()
	{
		final TicketData ticketData = new TicketData();
		ticketData.setStatus(createStatus("no-match-status"));
		final CsTicketState csStatus = facade.getCsStatus(ticketData);
		Assert.assertNull(csStatus);
	}

	/**
	 * Test of
	 * {@link de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade#getAttachments(TicketData)}
	 * <p>
	 * should return NULL value if no attachments in ticket data.
	 */
	@Test
	public void shouldReturnNullOfAttachments()
	{
		final TicketData ticketData = new TicketData();
		final List<MediaModel> attachments = facade.getAttachments(ticketData);

		Assert.assertNull(attachments);
	}

	/**
	 * Test of
	 * {@link de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade#getAttachments(TicketData)}
	 * <p>
	 * should return list of attachments which contents 2 media models.
	 */
	@Test
	public void shouldReturnListOfAttachments() throws IOException
	{
		final TicketData ticketData = new TicketData();
		final MultipartFile file1 = Mockito.mock(MultipartFile.class);
		final MultipartFile file2 = Mockito.mock(MultipartFile.class);
		final UserModel customer = Mockito.mock(UserModel.class);
		ticketData.setAttachments(Lists.newArrayList(file1, file2));

		final MediaModel media1 = Mockito.mock(MediaModel.class);
		final MediaModel media2 = Mockito.mock(MediaModel.class);

		final byte[] bytes1 = "test-file-1".getBytes();
		final byte[] bytes2 = "test-file-2".getBytes();

		Mockito.when(ticketAttachmentsService.createAttachment(FILE1, ContentType.APPLICATION_OCTET_STREAM.getMimeType(), bytes1,
				customer)).thenReturn(media1);
		Mockito.when(ticketAttachmentsService.createAttachment(FILE2, ContentType.APPLICATION_OCTET_STREAM.getMimeType(), bytes2,
				customer)).thenReturn(media2);

		Mockito.when(file1.getContentType()).thenReturn(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
		Mockito.when(file1.getOriginalFilename()).thenReturn(FILE1);
		Mockito.when(file1.getBytes()).thenReturn(bytes1);
		Mockito.when(file2.getContentType()).thenReturn(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
		Mockito.when(file2.getOriginalFilename()).thenReturn(FILE2);
		Mockito.when(file2.getBytes()).thenReturn(bytes2);

		Mockito.when(userService.getCurrentUser()).thenReturn(customer);

		final List<MediaModel> attachments = facade.getAttachments(ticketData);

		Assert.assertNotNull(attachments);
		Assert.assertEquals(2, attachments.size());
		Assert.assertEquals(media1, attachments.get(0));
		Assert.assertEquals(media2, attachments.get(1));
	}

	/**
	 * Test of
	 * {@link de.hybris.platform.customerticketingfacades.customerticket.DefaultCustomerTicketingFacade#getAttachments(TicketData)}
	 * <p>
	 * should return NULL value if an IOException occurs
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldReturnNullOfAttachmentsIfIOException() throws IOException
	{
		final TicketData ticketData = new TicketData();
		final MultipartFile file = Mockito.mock(MultipartFile.class);
		final UserModel customer = Mockito.mock(UserModel.class);
		ticketData.setAttachments(Collections.singletonList(file));

		Mockito.when(file.getContentType()).thenReturn(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
		Mockito.when(file.getOriginalFilename()).thenReturn("file1");
		Mockito.when(file.getBytes()).thenThrow(new IOException());

		Mockito.when(userService.getCurrentUser()).thenReturn(customer);

		final List<MediaModel> attachments = facade.getAttachments(ticketData);

		Assert.assertTrue(attachments.isEmpty());
	}

	/**
	 * Test of {@link DefaultCustomerTicketingFacade#updateTicket(TicketData)}
	 * <p>
	 * should invoke
	 * {@link TicketBusinessService#addNoteToTicket(CsTicketModel, CsInterventionType, CsEventReason, String, Collection)}
	 * when ticket is NEW state and in OPEN status.
	 *
	 */
	@Test
	public void shouldUpdateTicketWithNewNote()
	{
		final CsTicketModel ticketModel = Mockito.mock(CsTicketModel.class);

		Mockito.when(ticketModel.getState()).thenReturn(CsTicketState.NEW);
		Mockito.when(ticketModel.getTicketID()).thenReturn(TICKET_ID);

		Mockito.when(ticketService.getTicketForTicketId(TICKET_ID)).thenReturn(ticketModel);
		Mockito.when(ticketBusinessService.addNoteToTicket(ticketModel, CsInterventionType.IM, CsEventReason.UPDATE, MESSAGE, null))
				.thenReturn(Mockito.mock(CsCustomerEventModel.class));

		final TicketData ticketData = new TicketData();
		ticketData.setMessage(MESSAGE);
		ticketData.setStatus(createStatus(OPEN));
		ticketData.setId(TICKET_ID);
		final TicketData resultData = facade.updateTicket(ticketData);

		Assert.assertEquals(ticketData, resultData);
		Mockito.verify(ticketBusinessService).addNoteToTicket(ticketModel, CsInterventionType.IM, CsEventReason.UPDATE, MESSAGE,
				null);
	}

	/**
	 * Test of {@link DefaultCustomerTicketingFacade#updateTicket(TicketData)}
	 * <p>
	 * should invoke
	 * {@link TicketBusinessService#resolveTicket(CsTicketModel, CsInterventionType, CsResolutionType, String, Collection)}
	 * when ticket status transit from OPEN to CLOSE
	 *
	 * @throws TicketException
	 */
	@Test
	public void shouldUpdateTicketStatusFromOpenToClose() throws TicketException
	{
		final CsTicketModel ticketModel = Mockito.mock(CsTicketModel.class);

		Mockito.when(ticketModel.getState()).thenReturn(CsTicketState.OPEN);
		Mockito.when(ticketModel.getTicketID()).thenReturn(TICKET_ID);
		Mockito.when(ticketService.getTicketForTicketId(TICKET_ID)).thenReturn(ticketModel);

		Mockito
				.when(ticketBusinessService.resolveTicket(ticketModel, CsInterventionType.IM, CsResolutionType.CLOSED, MESSAGE, null))
				.thenReturn(Mockito.mock(CsTicketResolutionEventModel.class));

		final TicketData ticketData = new TicketData();
		ticketData.setMessage(MESSAGE);
		ticketData.setStatus(createStatus(COMPLETED));
		ticketData.setId(TICKET_ID);
		final TicketData resultData = facade.updateTicket(ticketData);

		Assert.assertEquals(ticketData, resultData);
		Mockito.verify(ticketBusinessService).resolveTicket(ticketModel, CsInterventionType.IM, CsResolutionType.CLOSED, MESSAGE,
				null);
	}

	/**
	 * Test of {@link DefaultCustomerTicketingFacade#updateTicket(TicketData)}
	 * <p>
	 * should invoke
	 * {@link TicketBusinessService#unResolveTicket(CsTicketModel, CsInterventionType, CsEventReason, String, Collection)}
	 * when ticket status transit from NEW to INPROCESS
	 *
	 * @throws TicketException
	 */
	@Test
	public void shouldUpdateTicketStatusFromOpenToInProgress() throws TicketException
	{
		final CsTicketModel ticketModel = Mockito.mock(CsTicketModel.class);

		Mockito.when(ticketModel.getState()).thenReturn(CsTicketState.NEW);
		Mockito.when(ticketModel.getTicketID()).thenReturn(TICKET_ID);
		Mockito.when(ticketService.getTicketForTicketId(TICKET_ID)).thenReturn(ticketModel);

		Mockito.when(ticketBusinessService.unResolveTicket(ticketModel, CsInterventionType.IM, CsEventReason.UPDATE, MESSAGE, null))
				.thenReturn(Mockito.mock(CsTicketResolutionEventModel.class));

		final TicketData ticketData = new TicketData();
		ticketData.setMessage(MESSAGE);
		ticketData.setStatus(createStatus(INPROCESS));
		ticketData.setId(TICKET_ID);
		final TicketData resultData = facade.updateTicket(ticketData);

		Assert.assertEquals(ticketData, resultData);
		Mockito.verify(ticketBusinessService).unResolveTicket(ticketModel, CsInterventionType.IM, CsEventReason.UPDATE,
				ticketData.getMessage(), null);
	}

}
