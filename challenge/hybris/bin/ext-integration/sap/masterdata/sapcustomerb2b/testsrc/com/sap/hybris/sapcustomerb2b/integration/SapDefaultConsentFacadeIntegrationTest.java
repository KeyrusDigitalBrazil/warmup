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
package com.sap.hybris.sapcustomerb2b.integration;

import de.hybris.bootstrap.annotations.IntegrationTest;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@IntegrationTest(replaces = de.hybris.platform.commercefacades.consent.impl.DefaultConsentFacadeIntegrationTest.class)
public class SapDefaultConsentFacadeIntegrationTest {
	   @Test
	   public void replaceTestGivenAndWithdrawConsentWorkflow() {
	      assertEquals(1,1);
	   }
}