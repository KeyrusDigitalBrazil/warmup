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
/**
 * 
 */
package com.hybris.ymkt.segmentation.dto;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * 
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class SAPInitiativeTest
{

	private static SAPInitiative initiative1 = new SAPInitiative();
	private static SAPInitiative initiative2 = new SAPInitiative();
	private static SAPInitiative initiative3 = new SAPInitiative();

	private static String INITIATIVE_ID = "initiativeId";
	private static String INITIATIVE_MEMBER_COUNT = "1";
	private static String INITIATIVE_NAME = "initiativeName";

	private static int hashCode1;

	@Before
	public void setUp() throws Exception
	{
		initiative1.setId(INITIATIVE_ID);
		initiative1.setMemberCount(INITIATIVE_MEMBER_COUNT);
		initiative1.setName(INITIATIVE_NAME);

		initiative2.setId(INITIATIVE_ID);
		initiative2.setMemberCount(INITIATIVE_MEMBER_COUNT);
		initiative2.setName(INITIATIVE_NAME);

		initiative3.setId("");
		initiative3.setMemberCount("");
		initiative3.setName("");

		hashCode1 = initiative1.hashCode();
	}

	@Test
	public void compareTo_Test()
	{
		Assert.assertEquals(0, initiative1.compareTo(initiative2));
		Assert.assertNotEquals(0, initiative1.compareTo(initiative3));
	}

	@Test
	public void equals_Test()
	{
		Assert.assertEquals(false, initiative1.equals(new Object()));
		Assert.assertEquals(true, initiative1.equals(initiative1));
		Assert.assertEquals(true, initiative1.equals(initiative2));
		Assert.assertEquals(false, initiative1.equals(initiative3));
		Assert.assertEquals(false, initiative1.equals(null));
	}

	@Test
	public void hashcode_Test()
	{
		Assert.assertEquals(hashCode1, initiative1.hashCode());
		Assert.assertEquals(true, initiative1.hashCode() == initiative2.hashCode());
	}

	@Test
	public void toString_test()
	{
		Assert.assertEquals("id: " + INITIATIVE_ID + " name: " + INITIATIVE_NAME + " Member Count: " + INITIATIVE_MEMBER_COUNT,
				initiative1.toString());
	}

}
