/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.dto;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for {@link DropdownElement}.
 *
 */
public class DropdownElementTest {
	public static final String ID = "id";
	public static final String LABEL = "label";
	public static final String UPDATED_ID = "newId";
	public static final String UPDATED_LABEL = "newLabel";

	@Test
	public void testConstructor() {
		final DropdownElement element = new DropdownElement(ID, LABEL);
		Assert.assertNotNull("Expected element to be created", element);
		Assert.assertEquals("Expected element to have ID of ", ID, element.getId());
		Assert.assertEquals("Expected element to have label of ", LABEL, element.getLabel());
	}

	@Test
	public void testIDMethods() {
		final DropdownElement element = new DropdownElement(ID, LABEL);
		element.setId(UPDATED_ID);
		Assert.assertEquals("Expected label to have ID of ", UPDATED_ID, element.getId());
	}

	@Test
	public void testLabelMethods() {
		final DropdownElement element = new DropdownElement(ID, LABEL);
		element.setLabel(UPDATED_LABEL);
		Assert.assertEquals("Expected element to have label of ", UPDATED_LABEL, element.getLabel());
	}
}
