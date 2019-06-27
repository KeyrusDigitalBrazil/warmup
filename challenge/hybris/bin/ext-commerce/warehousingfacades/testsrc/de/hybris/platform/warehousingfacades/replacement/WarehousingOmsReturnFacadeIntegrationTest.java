/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousingfacades.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ordermanagementfacades.returns.impl.DefaultOmsReturnFacadeIntegrationTest;
import de.hybris.platform.ordermanagementfacades.returns.OmsReturnFacade;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnEntryData;
import de.hybris.platform.ordermanagementfacades.returns.data.ReturnRequestData;
import de.hybris.platform.warehousing.constants.WarehousingTestConstants;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Re-implements test {@link DefaultOmsReturnFacadeIntegrationTest} to test additional fields when warehousing extensions is present
 */
@IntegrationTest(replaces = DefaultOmsReturnFacadeIntegrationTest.class)
public class WarehousingOmsReturnFacadeIntegrationTest extends DefaultOmsReturnFacadeIntegrationTest
{

	@Resource
	private OmsReturnFacade omsReturnFacade;

	@Before
	public void setup()
	{
		super.setup();
		try
		{
			importCsv("/impex/projectdata-dynamic-business-process-order.impex", WarehousingTestConstants.ENCODING);
			importCsv("/impex/projectdata-dynamic-business-process-consignment.impex", WarehousingTestConstants.ENCODING);
			importCsv("/impex/projectdata-dynamic-business-process-return.impex", WarehousingTestConstants.ENCODING);
			importCsv("/impex/projectdata-dynamic-business-process-sendReturnLabelEmail.impex", WarehousingTestConstants.ENCODING);
		}
		catch (final ImpExException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	@Test
	public void testCreateReturnRequest_ValidQtyReturn_Success()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 0);
		final ReturnEntryData returnEntryData2 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 1);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1, returnEntryData2);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, "O-K2010-C0000-001", Boolean.FALSE);

		//When
		final ReturnRequestData createdReturnRequest = omsReturnFacade.createReturnRequest(returnRequestData);

		//then
		Assert.assertNotNull(createdReturnRequest.getRma());
		Assert.assertEquals(ReturnStatus.APPROVAL_PENDING, createdReturnRequest.getStatus());
	}

	/**
	 * Validates the successful cancellation of return, when warehousing extension is present.
	 */
	@Override
	@Test
	public void testCancelReturnRequest_Success()
	{
		//Given
		final ReturnEntryData returnEntryData1 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 0);
		final ReturnEntryData returnEntryData2 = createReturnEntryData(1L, "HOLD", null, "DamagedInTransit", 1);
		final List<ReturnEntryData> returnEntryDatas = Arrays.asList(returnEntryData1, returnEntryData2);
		final ReturnRequestData returnRequestData = createReturnRequestData(returnEntryDatas, "O-K2010-C0000-001", Boolean.FALSE);
		final ReturnRequestData createdReturnRequest = omsReturnFacade.createReturnRequest(returnRequestData);

		//When
		omsReturnFacade.cancelReturnRequest(
				createCancelReturnRequestData(createdReturnRequest.getCode(), CancelReason.OTHER, "successful test"));
	}
}

