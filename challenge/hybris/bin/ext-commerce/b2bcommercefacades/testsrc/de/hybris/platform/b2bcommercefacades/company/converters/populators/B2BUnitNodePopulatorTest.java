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
package de.hybris.platform.b2bcommercefacades.company.converters.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class B2BUnitNodePopulatorTest
{
	private static final String TEST_UNIT_UID = "testUnitId";
	private static final String TEST_UNIT_NAME = "testUnitName";
	private static final String TEST_PARENT_UNIT_UID = "testParentUnitId";

	private B2BUnitModel source;
	private B2BUnitModel parentB2bUnitModel;

	private final B2BUnitNodePopulator b2bUnitNodePopulator = new B2BUnitNodePopulator();

	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		b2bUnitNodePopulator.setB2BUnitService(b2BUnitService);

		// Initializing 'source' B2BUnitModel and 'target' B2BUnitNodeData
		source = new B2BUnitModel();

		parentB2bUnitModel = new B2BUnitModel();
		parentB2bUnitModel.setUid(TEST_PARENT_UNIT_UID);

		source.setActive(Boolean.TRUE);
		source.setUid(TEST_UNIT_UID);
		source.setName(TEST_UNIT_NAME);

		final B2BUnitModel b2bUnitModel1 = new B2BUnitModel();
		final B2BUnitModel b2bUnitModel2 = new B2BUnitModel();
		source.setMembers(new HashSet<PrincipalModel>());
		source.getMembers().add(b2bUnitModel1);
		source.getMembers().add(b2bUnitModel2);

	}

	@Test(expected = IllegalStateException.class)
	public void testShouldPopulateB2BUnitNodeData()
	{
		final B2BUnitNodeData target = new B2BUnitNodeData();
		Mockito.when(b2BUnitService.getParent(source)).thenReturn(parentB2bUnitModel);

		b2bUnitNodePopulator.populate(source, target);

		Assert.assertEquals("Unexpected value for name", TEST_UNIT_NAME, target.getName());
		Assert.assertEquals("Unexpected value for id", TEST_UNIT_UID, target.getId());
		Assert.assertEquals("Unexpected value for active", Boolean.TRUE, Boolean.valueOf(target.isActive()));
		Assert.assertEquals("Unexpected value for parent", TEST_PARENT_UNIT_UID, target.getParent());
		Assert.assertEquals("Unexpected number of children", 2, target.getChildren().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullB2BUnitModel()
	{
		b2bUnitNodePopulator.populate(null, new B2BUnitNodeData());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIfPopulateIsCalledWithNullB2BUnitNodeData()
	{
		b2bUnitNodePopulator.populate(source, null);
	}

}
