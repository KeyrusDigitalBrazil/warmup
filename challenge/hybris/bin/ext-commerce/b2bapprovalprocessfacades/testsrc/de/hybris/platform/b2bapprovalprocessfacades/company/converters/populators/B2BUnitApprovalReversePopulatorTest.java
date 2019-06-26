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
package de.hybris.platform.b2bapprovalprocessfacades.company.converters.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BUnitApprovalReversePopulatorTest
{
	private B2BUnitApprovalReversePopulator b2BUnitApprovalReversePopulator;
	private B2BUnitData source;
	private B2BUnitModel target;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		source = mock(B2BUnitData.class);
		target = new B2BUnitModel();

		b2BUnitApprovalReversePopulator = new B2BUnitApprovalReversePopulator();
	}

	@Test
	public void shouldPopulate()
	{
		final String apprProceCode = "apprProceCode";
		given(source.getApprovalProcessCode()).willReturn(apprProceCode);
		b2BUnitApprovalReversePopulator.populate(source, target);

		Assert.assertEquals("source and target ApprovalProcessCode should match", apprProceCode, target.getApprovalProcessCode());
	}
}
