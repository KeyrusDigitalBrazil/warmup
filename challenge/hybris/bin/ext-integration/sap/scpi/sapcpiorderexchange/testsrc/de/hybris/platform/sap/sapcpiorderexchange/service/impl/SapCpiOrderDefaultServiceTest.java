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
package de.hybris.platform.sap.sapcpiorderexchange.service.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderService;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;


@Deprecated
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/sapcpiorderexchange-scpi-spring.xml", "classpath:/sapcpiadapter-charon-spring.xml"})
@UnitTest
public class SapCpiOrderDefaultServiceTest{

    @Resource
    SapCpiOrderService sapCpiOrderService;

    @Test(expected = Throwable.class)
    public void testSapCpiOrderService() throws IOException {

        InputStream inputStream = getClass().getResourceAsStream("/test/sapCpiOrder.json");
        ObjectMapper mapper = new ObjectMapper();

        final SapCpiOrder sapCpiOrder = mapper.readValue(inputStream, SapCpiOrder.class);
        inputStream.close();

        assertTrue("Unable to send the order to SCPI!", sapCpiOrderService.sendOrder(sapCpiOrder).toBlocking().value().isSuccessful());
    }

}
