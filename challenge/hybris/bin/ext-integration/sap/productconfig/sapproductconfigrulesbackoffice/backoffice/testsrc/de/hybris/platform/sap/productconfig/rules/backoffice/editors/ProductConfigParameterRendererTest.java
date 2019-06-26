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
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import static org.junit.Assert.*;

import org.junit.Test;
import org.zkoss.zul.Comboitem;

public class ProductConfigParameterRendererTest
{
	ProductConfigParameterRenderer classUnderTest = new ProductConfigParameterRenderer();
	
	@Test
	public void testRender()
	{
		Comboitem item = new Comboitem();
		Object data = "label";
		classUnderTest.render(item, data, 1);
		
		assertEquals(data, item.getValue());
		assertEquals(data.toString(), item.getLabel());
	}
}
