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
package de.hybris.platform.integrationbackoffice.widgets.modals;

import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateIntegrationObjectModalControllerTest {

    private final CreateIntegrationObjectModalController controller = new CreateIntegrationObjectModalController();

    @Mock
    private ReadService readService;

    @Test
    public void testServiceNameValid() {
        assertFalse(controller.isServiceNameValid(""));
        assertFalse(controller.isServiceNameValid("$"));
        assertFalse(controller.isServiceNameValid("/"));
        assertFalse(controller.isServiceNameValid(" "));
        assertTrue(controller.isServiceNameValid("InboundProduct42"));
    }

    @Test
    public void testServiceNameUnique() {
        final List<IntegrationObjectModel> integrationObjectModels = new ArrayList<>();
        final IntegrationObjectModel inboundProduct42 = new IntegrationObjectModel();
        inboundProduct42.setCode("InboundProduct42");
        integrationObjectModels.add(inboundProduct42);
        when(readService.getIntegrationObjectModels()).thenReturn(integrationObjectModels);

        controller.setReadService(readService);

        assertTrue(controller.isServiceNameUnique("OutboundProduct"));
    }

}