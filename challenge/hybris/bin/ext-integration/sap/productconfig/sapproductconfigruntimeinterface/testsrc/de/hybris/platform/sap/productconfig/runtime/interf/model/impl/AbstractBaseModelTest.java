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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.BaseModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractBaseModelTest
{

	protected void testGenericEqualPart(final BaseModel model1, final BaseModel model2)
	{
		assertTrue(model1.equals(model1));
		assertFalse(model1.equals(null));
		assertFalse(model1.equals(new String("Hello World")));
		assertTrue(model1.equals(model2));
	}

	protected void equalCheck(final Object model1, final Object model2, final String methodName, final Object value,
			final Object tempValue) throws Exception
	{
		assertTrue(model1.equals(model2));

		Class classType = value.getClass();

		if (classType.equals(Boolean.class))
		{
			classType = boolean.class;
		}
		else if (classType.equals(ArrayList.class))
		{
			classType = List.class;
		}
		else if (classType.equals(InstanceModelImpl.class))
		{
			classType = InstanceModel.class;
		}
		else if (classType.equals(PriceModelImpl.class))
		{
			classType = PriceModel.class;
		}
		else if (classType.equals(KBKeyImpl.class))
		{
			classType = KBKey.class;
		}

		final Method method = model1.getClass().getMethod(methodName, classType);

		method.invoke(model2, value);
		assertFalse(model1.equals(model2));

		if (tempValue != null)
		{
			method.invoke(model1, tempValue);
			assertFalse(model1.equals(model2));
		}

		method.invoke(model1, value);
		assertTrue(model1.equals(model2));
	}
}
