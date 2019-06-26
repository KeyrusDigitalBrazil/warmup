/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.cis.client.rest.core.tax;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import com.hybris.charon.RawResponse;
import com.hybris.cis.api.test.util.TestUtils;
import com.hybris.cis.client.shared.models.CisDecision;
import com.hybris.cis.client.shared.models.CisOrder;
import com.hybris.cis.client.tax.TaxClient;
import com.hybris.cis.client.tax.models.CisTaxDoc;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Validates that the "out-of-the-box" spring configuration will wire in the mock client if mock mode is set.
 */
@ManualTest
public class TaxClientTest extends ServicelayerTest
{
	@Resource
	private TaxClient taxClient;

	private CisOrder usOrder;
	private CisTaxDoc cisTaxDoc;


	@Before
	public void before() throws Exception // NOPMD
	{
		this.usOrder = TestUtils.createSampleOrder();
	}

	@Test
	public void shouldQuoteTax()
	{
		cisTaxDoc = taxClient.quote("test", "single", this.usOrder);
		assertEquals(CisDecision.ACCEPT, cisTaxDoc.getDecision());
		assertNotNull(cisTaxDoc.getId());
	}

	@Test
	public void shouldSubmitTax()
	{
		cisTaxDoc = taxClient.post("test", "single", this.usOrder);
		assertEquals(CisDecision.ACCEPT, cisTaxDoc.getDecision());
	}

	@Test
	public void shouldInvoiceTax()
	{
		cisTaxDoc = taxClient.invoice("test", "single", this.usOrder);
		assertEquals(CisDecision.ACCEPT, cisTaxDoc.getDecision());
	}

	@Test
	public void shouldCancelTax()
	{
		cisTaxDoc = taxClient.post("test", "single", this.usOrder);
		final RawResponse<String> response = taxClient.cancel("test", "single", cisTaxDoc.getId());
		assertEquals(Response.Status.NO_CONTENT, response.status());
	}

}
