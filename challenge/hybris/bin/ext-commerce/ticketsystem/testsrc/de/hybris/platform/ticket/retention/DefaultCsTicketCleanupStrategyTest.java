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
package de.hybris.platform.ticket.retention;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.directpersistence.audit.dao.WriteAuditRecordsDAO;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.ticket.events.model.CsTicketChangeEventEntryModel;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import de.hybris.platform.ticket.retention.impl.DefaultCsTicketCleanupStrategy;
import de.hybris.platform.ticket.service.TicketService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@UnitTest
public class DefaultCsTicketCleanupStrategyTest
{
    @InjectMocks
    private final DefaultCsTicketCleanupStrategy csTicketCleanupStrategy = new DefaultCsTicketCleanupStrategy();

    @Mock
    private ModelService modelService;
    @Mock
    private TicketService ticketService;
    @Mock
    private WriteAuditRecordsDAO writeAuditRecordsDAO;

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldCleanupRelatedObjects()
    {
        final CsTicketModel ticketModel = mock(CsTicketModel.class);

        final CsTicketEventModel csTicketEventModel = mock(CsTicketEventModel.class);
        final List<CsTicketEventModel> csTicketEventModels = Collections.singletonList(csTicketEventModel);
        given(ticketService.getEventsForTicket(ticketModel)).willReturn(csTicketEventModels);
        final PK csTicketEventModelPK = PK.parse("1111");
        given(csTicketEventModel.getPk()).willReturn(csTicketEventModelPK);

        final CsTicketChangeEventEntryModel csTicketChangeEventEntryModel = mock(CsTicketChangeEventEntryModel.class);
        final Set<CsTicketChangeEventEntryModel> csTicketChangeEventEntryModels = new HashSet<>(Collections.singletonList(csTicketChangeEventEntryModel));
        given(csTicketEventModel.getEntries()).willReturn(csTicketChangeEventEntryModels);
        final PK csTicketChangeEventEntryModelPK = PK.parse("2222");
        given(csTicketChangeEventEntryModel.getPk()).willReturn(csTicketChangeEventEntryModelPK);

        csTicketCleanupStrategy.cleanupRelatedObjects(ticketModel);

        verify(modelService).remove(csTicketChangeEventEntryModel);
        verify(modelService).remove(csTicketEventModel);
        verify(writeAuditRecordsDAO).removeAuditRecordsForType(CsTicketEventModel._TYPECODE, csTicketEventModelPK);
        verify(writeAuditRecordsDAO).removeAuditRecordsForType(CsTicketChangeEventEntryModel._TYPECODE, csTicketChangeEventEntryModelPK);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCleanupRelatedObjectsIfInputIsNull()
    {
        csTicketCleanupStrategy.cleanupRelatedObjects(null);
    }
}
