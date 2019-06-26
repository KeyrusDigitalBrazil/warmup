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
package de.hybris.platform.warehousingfacades;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.warehousingfacades.returns.WarehousingReturnFacade;
import de.hybris.platform.warehousingfacades.util.BaseWarehousingFacadeIntegrationTest;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class WarehousingReturnFacadeIntegrationTest extends BaseWarehousingFacadeIntegrationTest
{
	@Resource
	private WarehousingReturnFacade warehousingReturnFacade;
	protected String code = "";
	protected RefundEntryModel refundEntry;

	@Test
	public void isAcceptGoodsConfirmable_Successfully()
	{
		//when
		refundEntry = createReturnAndReadyToAcceptGoods();
		code = refundEntry.getReturnRequest().getCode();
		//then
		assertTrue(warehousingReturnFacade.isAcceptGoodsConfirmable(code));
	}

	@Test
	public void isAcceptGoodsConfirmable_Fail_ReturnNotInCorrectStatus()
	{
		//when
		final RefundEntryModel refundEntry = createDefaultReturnRequest(createDefaultConsignmentAndOrder());
		modelService.saveAll();
		code = refundEntry.getReturnRequest().getCode();
		//then
		assertFalse(warehousingReturnFacade.isAcceptGoodsConfirmable(code));
	}

	@Test (expected = IllegalStateException.class)
	public void acceptGoods_Fail_ReturnNotInCorrectStatus()
	{
		//given
		final RefundEntryModel refundEntry = createDefaultReturnRequest(createDefaultConsignmentAndOrder());
		modelService.saveAll();
		code = refundEntry.getReturnRequest().getCode();
		//when
		warehousingReturnFacade.acceptGoods(code);
	}
}
