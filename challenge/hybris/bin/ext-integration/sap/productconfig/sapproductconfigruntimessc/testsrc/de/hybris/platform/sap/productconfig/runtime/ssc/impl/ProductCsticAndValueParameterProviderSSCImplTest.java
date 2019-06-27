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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticParameterWithValues;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ValueParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sap.sce.kbrt.imp.kb_cstic_imp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
public class ProductCsticAndValueParameterProviderSSCImplTest
{
	ProductCsticAndValueParameterProviderSSCImpl classUnderTest;

	@Before
	public void setUp()
	{
		classUnderTest = Mockito.spy(new ProductCsticAndValueParameterProviderSSCImpl());
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	public void testProcessCstic_Not_String()
	{
		final kb_cstic_imp cstic = null;

		final Map<String, CsticParameterWithValues> csticsMap = new HashMap<String, CsticParameterWithValues>();

		Mockito.doReturn(false).when(classUnderTest).isStringCstic(cstic);
		Mockito.doReturn("C1").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 1").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn("V11,V12").when(classUnderTest).retrieveCsticDomainAsSting(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(1, csticsMap.size());
		assertEquals("C1", csticsMap.get("C1").getCstic().getCsticName());
		assertEquals("Description 1", csticsMap.get("C1").getCstic().getCsticDescription());
		List<ValueParameter> values = csticsMap.get("C1").getValues();
		assertEquals(2, values.size());
		assertEquals("V11", values.get(0).getValueName());
		assertEquals("V12", values.get(1).getValueName());

		Mockito.doReturn("C2").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 2").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn("V21,V22").when(classUnderTest).retrieveCsticDomainAsSting(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(2, csticsMap.size());
		assertEquals("C2", csticsMap.get("C2").getCstic().getCsticName());
		assertEquals("Description 2", csticsMap.get("C2").getCstic().getCsticDescription());
		values = csticsMap.get("C2").getValues();
		assertEquals(2, values.size());
		assertEquals("V21", values.get(0).getValueName());
		assertEquals("V22", values.get(1).getValueName());

		Mockito.doReturn("C1").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 1").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn("V11,V13").when(classUnderTest).retrieveCsticDomainAsSting(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(2, csticsMap.size());
		assertEquals("C1", csticsMap.get("C1").getCstic().getCsticName());
		assertEquals("Description 1", csticsMap.get("C1").getCstic().getCsticDescription());
		values = csticsMap.get("C1").getValues();
		assertEquals(3, values.size());
		assertEquals("V11", values.get(0).getValueName());
		assertEquals("V12", values.get(1).getValueName());
		assertEquals("V13", values.get(2).getValueName());
	}


	@Test
	@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
	public void testProcessCstic_String()
	{
		final kb_cstic_imp cstic = null;

		final Map<String, CsticParameterWithValues> csticsMap = new HashMap<String, CsticParameterWithValues>();

		Mockito.doReturn(true).when(classUnderTest).isStringCstic(cstic);
		Mockito.doReturn("C1").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 1").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn(Arrays.asList("V11", "V12")).when(classUnderTest).retrieveCsticValues(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(1, csticsMap.size());
		assertEquals("C1", csticsMap.get("C1").getCstic().getCsticName());
		assertEquals("Description 1", csticsMap.get("C1").getCstic().getCsticDescription());
		List<ValueParameter> values = csticsMap.get("C1").getValues();
		assertEquals(2, values.size());
		assertEquals("V11", values.get(0).getValueName());
		assertEquals("V12", values.get(1).getValueName());

		Mockito.doReturn("C2").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 2").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn(Arrays.asList("V21", "V22")).when(classUnderTest).retrieveCsticValues(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(2, csticsMap.size());
		assertEquals("C2", csticsMap.get("C2").getCstic().getCsticName());
		assertEquals("Description 2", csticsMap.get("C2").getCstic().getCsticDescription());
		values = csticsMap.get("C2").getValues();
		assertEquals(2, values.size());
		assertEquals("V21", values.get(0).getValueName());
		assertEquals("V22", values.get(1).getValueName());

		Mockito.doReturn("C1").when(classUnderTest).retrieveCsticName(Mockito.any());
		Mockito.doReturn("Description 1").when(classUnderTest).retrieveCsticDescription(Mockito.any());
		Mockito.doReturn(Arrays.asList("V11", "V13")).when(classUnderTest).retrieveCsticValues(Mockito.any());

		classUnderTest.processCstic(cstic, csticsMap);

		assertEquals(2, csticsMap.size());
		assertEquals("C1", csticsMap.get("C1").getCstic().getCsticName());
		assertEquals("Description 1", csticsMap.get("C1").getCstic().getCsticDescription());
		values = csticsMap.get("C1").getValues();
		assertEquals(3, values.size());
		assertEquals("V11", values.get(0).getValueName());
		assertEquals("V12", values.get(1).getValueName());
		assertEquals("V13", values.get(2).getValueName());
	}

	@Test
	public void testFillValueList()
	{
		final List<ValueParameter> values = createValueParameterList();
		final String domainAsString = "V1,V3";

		classUnderTest.fillValueList(values, domainAsString);

		assertEquals(3, values.size());
		assertEquals("V1", values.get(0).getValueName());
		assertEquals("V2", values.get(1).getValueName());
		assertEquals("V3", values.get(2).getValueName());
	}

	@Test
	public void testIsValueAlreadyExists()
	{
		final List<ValueParameter> values = createValueParameterList();

		assertTrue(classUnderTest.isValueAlreadyExists("V1", values));
		assertTrue(classUnderTest.isValueAlreadyExists("V2", values));
		assertFalse(classUnderTest.isValueAlreadyExists("V3", values));
	}

	private List<ValueParameter> createValueParameterList()
	{
		final List<ValueParameter> values = new ArrayList<ValueParameter>();

		ValueParameter valueParameter = new ValueParameter();
		valueParameter.setValueName("V1");
		valueParameter.setValueDescription("");
		values.add(valueParameter);

		valueParameter = new ValueParameter();
		valueParameter.setValueName("V2");
		valueParameter.setValueDescription("");
		values.add(valueParameter);

		return values;
	}

	public void testRetrieveCsticDomainAsStingNoExceptionForNonBdt()
	{
		final kb_cstic_imp cstic = null;
		Mockito.doReturn(false).when(classUnderTest).hasKbBdtType(Mockito.any());
		assertNull(classUnderTest.retrieveCsticDomainAsSting(cstic));
	}
}
