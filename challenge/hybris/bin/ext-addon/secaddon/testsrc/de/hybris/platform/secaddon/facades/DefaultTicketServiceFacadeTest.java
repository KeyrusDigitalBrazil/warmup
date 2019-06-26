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
package de.hybris.platform.secaddon.facades;

import com.hybris.charon.RawResponse;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.storesession.StoreSessionFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.secaddon.builder.TicketDataBuilder;
import de.hybris.platform.secaddon.builder.TranscriptBuilder;
import de.hybris.platform.secaddon.data.TicketData;
import de.hybris.platform.secaddon.data.TicketSecData;
import de.hybris.platform.secaddon.data.Transcript;
import de.hybris.platform.secaddon.services.TicketServiceClientAdapter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;

import java.util.Arrays;
import java.util.List;

import static de.hybris.platform.secaddon.constants.SecaddonConstants.VISIBILITY_PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@UnitTest
public class DefaultTicketServiceFacadeTest
{
    private static final String TICKET_ID = "ticketId";
    private static final String ISO_CODE = "isoCode";
    private static final String PRIVATE = "PRIVATE";
    private static final String DESC = "desc";

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    StoreSessionFacade storeSessionFacade;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    TicketServiceClientAdapter ticketServiceClientAdapter;
    @Mock
    private Converter<TicketData, TicketSecData> secTicketConverter;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomerFacade customerFacade;
    @InjectMocks
    DefaultTicketServiceFacade ticketServiceFacade = new DefaultTicketServiceFacade();

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetTicketDetailsWithPublicTranscriptOnly()
    {
        //given
        given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn(ISO_CODE);
        TicketData ticket = TicketDataBuilder.TicketDataBuilder()
                .withPriorityDescription(DESC)
                .withTranscript(getTranscript())
                .build();
        given(ticketServiceClientAdapter.getTicketDetails(ISO_CODE, TICKET_ID)).willReturn(Observable.just(ticket));
        given(customerFacade.getCurrentCustomer()).willReturn(new CustomerData());
        //when
        TicketData resultTicket = ticketServiceFacade.getTicketDetails(TICKET_ID);

        //then
        assertEquals(2, resultTicket.getTranscript().size());
        assertOnlyPublicTranscript(resultTicket);
    }

    @Test
    public void shouldCreateTicket()
    {

        //given
        given(storeSessionFacade.getCurrentLanguage().getIsocode()).willReturn(ISO_CODE);
        TicketData ticket = TicketDataBuilder.TicketDataBuilder().build();
        TicketSecData ticketSecData = new TicketSecData();
        given(secTicketConverter.convert(ticket)).willReturn(ticketSecData);
        given(ticketServiceClientAdapter.createTicket(ISO_CODE, ticketSecData)).willReturn(Observable.just(mock(RawResponse.class)));

        //when
        ticketServiceFacade.createTicket(ticket);

        //then
        verify(ticketServiceClientAdapter).createTicket(ISO_CODE, ticketSecData);
    }

    private void assertOnlyPublicTranscript(TicketData resultTicket)
    {
        assertTrue(resultTicket.getTranscript()
                .stream()
                .allMatch(transcript -> VISIBILITY_PUBLIC.equals(transcript.getVisibility())));
    }

    protected List<Transcript> getTranscript()
    {
        Transcript transcript1 = TranscriptBuilder.TranscriptBuilder()
                .withDescription("description 1")
                .withVisibility(VISIBILITY_PUBLIC)
                .build();
        Transcript transcript2 = TranscriptBuilder.TranscriptBuilder()
                .withDescription("description 2")
                .withVisibility(VISIBILITY_PUBLIC)
                .build();
        Transcript transcript3 = TranscriptBuilder.TranscriptBuilder()
                .withDescription("description 3")
                .withVisibility(PRIVATE)
                .build();
        return Arrays.asList(transcript1, transcript2, transcript3);
    }
}
