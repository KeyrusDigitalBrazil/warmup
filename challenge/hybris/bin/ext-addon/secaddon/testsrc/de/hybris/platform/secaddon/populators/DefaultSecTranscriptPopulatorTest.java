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
package de.hybris.platform.secaddon.populators;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.secaddon.constants.SecaddonConstants;
import de.hybris.platform.secaddon.data.Transcript;
import de.hybris.platform.secaddon.data.TranscriptSec;
import de.hybris.platform.secaddon.exception.CustomerMappingException;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;

@UnitTest
public class DefaultSecTranscriptPopulatorTest
{
    @Mock
    private SessionService sessionService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomerFacade customerFacade;
    @InjectMocks
    DefaultSecTranscriptPopulator secTranscriptPopulator = new DefaultSecTranscriptPopulator();

    private static final String CUSTOMER_EMAIL = "customerEmail";
    private static final String CUSTOMER_MAPPED_ID = "customerMappedId";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String DESCRIPTION = "description";

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldPopulateTranscript()
    {
        //given
        Transcript transcript = buildTranscript();
        TranscriptSec transcriptSec = new TranscriptSec();
        given(sessionService.getAttribute(SecaddonConstants.MAPPED_CUSTOMER_ID)).willReturn(CUSTOMER_MAPPED_ID);
        mockCurrentCustomer();

        //when
        secTranscriptPopulator.populate(transcript, transcriptSec);

        //then
        assertOwner(transcriptSec);
        assertEquals(SecaddonConstants.VISIBILITY_PUBLIC, transcriptSec.getVisibility());
        assertEquals(DESCRIPTION, transcriptSec.getDescription());
    }

    @Test(expected = CustomerMappingException.class)
    public void shouldThrowCustomerMappingExceptionWhenNoMappingInSession()
    {

        //given
        Transcript transcript = buildTranscript();
        TranscriptSec transcriptSec = new TranscriptSec();
        mockCurrentCustomer();

        //when
        secTranscriptPopulator.populate(transcript, transcriptSec);
        fail("should throwCustomerMappingException");
    }

    private Transcript buildTranscript()
    {
        Transcript transcript = new Transcript();
        transcript.setDescription(DESCRIPTION);
        return transcript;
    }

    private void mockCurrentCustomer()
    {
        given(customerFacade.getCurrentCustomer().getDisplayUid()).willReturn(CUSTOMER_EMAIL);
        given(customerFacade.getCurrentCustomer().getFirstName()).willReturn(FIRST_NAME);
        given(customerFacade.getCurrentCustomer().getLastName()).willReturn(LAST_NAME);
    }

    private void assertOwner(TranscriptSec transcriptSec)
    {
        assertEquals(CUSTOMER_MAPPED_ID, transcriptSec.getOwner().getOwnerId());
        assertEquals(CUSTOMER_EMAIL, transcriptSec.getOwner().getEmail());
        assertEquals(String.format("%s %s", FIRST_NAME, LAST_NAME), transcriptSec.getOwner().getDisplayName());
        assertTrue(transcriptSec.getOwner().getIsCustomer());
    }
}
