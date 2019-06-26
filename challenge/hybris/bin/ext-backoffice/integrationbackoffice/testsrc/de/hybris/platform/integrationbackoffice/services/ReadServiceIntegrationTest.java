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
package de.hybris.platform.integrationbackoffice.services;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.util.AppendSpringConfiguration;
import org.junit.Test;

import javax.annotation.Resource;

import static junit.framework.TestCase.assertTrue;

@IntegrationTest
@AppendSpringConfiguration("classpath:/test/integrationbackoffice-test-spring.xml")
public class ReadServiceIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private ReadService readService;

    @Test
    public void testFlexibleTypes() {
        assertTrue(readService.isCollectionType("CollectionType"));
        assertTrue(readService.isComposedType("ComposedType"));
        assertTrue(readService.isEnumerationMetaType("EnumerationMetaType"));
        assertTrue(readService.isAtomicType("AtomicType"));
        assertTrue(readService.isMapType("MapType"));
    }

}
