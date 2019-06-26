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
package de.hybris.platform.previewwebservices.facades.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.preview.CMSPreviewTicketModel;
import de.hybris.platform.cms2.model.preview.PreviewDataModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.previewwebservices.dto.PreviewTicketWsDTO;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import java.net.MalformedURLException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPreviewFacadeTest
{

	private final String TICKET_ID = "validTicketId";

	@InjectMocks
	private DefaultPreviewFacade facade;

	@Mock
	private CMSPreviewService cmsPreviewService;
	@Mock
	private Converter<CMSPreviewTicketModel, PreviewTicketWsDTO> previewTicketConverter;
	@Mock
	private Converter<PreviewTicketWsDTO, PreviewDataModel> previewTicketReverseConverter;
	@Mock
	private ModelService modelService;

	@Mock
	private PreviewDataModel previewDataModel;
	@Mock
	private CMSPreviewTicketModel cmsPreviewTicketModel;

	private final PreviewTicketWsDTO previewTicketWsDTO1 = new PreviewTicketWsDTO();
	private final PreviewTicketWsDTO previewTicketWsDTO2 = new PreviewTicketWsDTO();

	@Test
	public void testCreatePreviewTicketShouldCallTheConverter() throws MalformedURLException, CMSItemNotFoundException
	{
		// GIVEN
		when(previewTicketReverseConverter.convert(previewTicketWsDTO1)).thenReturn(previewDataModel);
		when(cmsPreviewService.createPreviewTicket(previewDataModel)).thenReturn(cmsPreviewTicketModel);
		when(previewTicketConverter.convert(cmsPreviewTicketModel)).thenReturn(previewTicketWsDTO2);

		// WHEN
		final PreviewTicketWsDTO previewTicket = facade.createPreviewTicket(previewTicketWsDTO1);

		// THEN
		verify(previewTicketReverseConverter).convert(previewTicketWsDTO1);
		verify(previewTicketConverter).convert(cmsPreviewTicketModel);
		assertThat(previewTicket, is(previewTicketWsDTO2));

	}

	@Test
	public void testUpdatePreviewTicketShouldCallTheConverterWhenValid()
	{
		// GIVEN
		when(cmsPreviewService.getPreviewTicket(TICKET_ID)).thenReturn(cmsPreviewTicketModel);
		when(cmsPreviewTicketModel.getPreviewData()).thenReturn(previewDataModel);
		when(previewTicketReverseConverter.convert(previewTicketWsDTO1, previewDataModel)).thenReturn(previewDataModel);
		when(previewTicketConverter.convert(cmsPreviewTicketModel)).thenReturn(previewTicketWsDTO2);

		// WHEN
		final PreviewTicketWsDTO previewTicket = facade.updatePreviewTicket(TICKET_ID, previewTicketWsDTO1);

		// THEN
		verify(cmsPreviewService).getPreviewTicket(TICKET_ID);
		verify(previewTicketReverseConverter).convert(previewTicketWsDTO1, previewDataModel);
		verify(previewTicketConverter).convert(cmsPreviewTicketModel);
		assertThat(previewTicket, is(previewTicketWsDTO2));

	}

	@Test(expected = NotFoundException.class)
	public void testUpdatePreviewTicketWithInvalidTicketIdShouldThrowException()
	{
		// GIVEN
		when(cmsPreviewService.getPreviewTicket(TICKET_ID)).thenReturn(null);

		// WHEN
		facade.updatePreviewTicket(TICKET_ID, previewTicketWsDTO1);

	}

	@Test
	public void testGetPreviewTicketWithValidTicketIdShouldReturnAppropriateData()
	{
		// GIVEN
		when(cmsPreviewService.getPreviewTicket(TICKET_ID)).thenReturn(cmsPreviewTicketModel);
		when(previewTicketConverter.convert(cmsPreviewTicketModel)).thenReturn(previewTicketWsDTO1);

		// WHEN
		final PreviewTicketWsDTO previewTicket = facade.getPreviewTicket(TICKET_ID);

		// THEN
		verify(previewTicketConverter).convert(cmsPreviewTicketModel);
		assertThat(previewTicket, is(previewTicketWsDTO1));

	}

	@Test(expected = NotFoundException.class)
	public void testGetPreviewTicketWithInvalidTicketIdShouldThrowException()
	{
		// GIVEN
		when(cmsPreviewService.getPreviewTicket(TICKET_ID)).thenReturn(null);

		// WHEN
		facade.getPreviewTicket(TICKET_ID);

	}

}
