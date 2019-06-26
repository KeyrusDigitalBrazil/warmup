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
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class AddressImplTest
{
	AddressImpl classUnderTest = new AddressImpl();
	private final String firstName = "Robby";
	private final String value = "value";


	@Test
	public void testClearX()
	{
                classUnderTest.firstNameX = true;
		classUnderTest.clearX();
                assertFalse(classUnderTest.firstNameX);
	}

	@Test
	public void testCompareToNoChangeInAddString()
	{
		final AddressImpl otherAddress = new AddressImpl();
		otherAddress.setFirstName("Hello");
		final int compareTo = classUnderTest.compareTo(otherAddress);
		assertTrue(compareTo == 0);
	}

	@Test
	public void testCompareToChangeInAddString()
	{
		final AddressImpl otherAddress = new AddressImpl();
		otherAddress.setAddressStringC("Hello");
		final int compareTo = classUnderTest.compareTo(otherAddress);
		assertFalse(compareTo == 0);
	}

	@Test
	public void testGetNotExisting()
	{
		final String attribute = classUnderTest.get("DOES_NOT_EXIST");
		assertNull(attribute);
	}

	@Test
	public void testGetExisting()
	{

		classUnderTest.setFirstName(firstName);
		final String attribute = classUnderTest.get("firstName");
		assertEquals(firstName, attribute);
	}

	@Test
	public void testIsEqualTo()
	{
		final AddressImpl otherAddress = new AddressImpl();
		final boolean addressfieldsEqualTo = classUnderTest.isAddressfieldsEqualTo(otherAddress);
		assertTrue(addressfieldsEqualTo);
	}

	@Test
	public void testIsEqualToFirstNameDeviates()
	{
		final AddressImpl otherAddress = new AddressImpl();
		otherAddress.setFirstName(firstName);
		final boolean addressfieldsEqualTo = classUnderTest.isAddressfieldsEqualTo(otherAddress);
		assertFalse(addressfieldsEqualTo);
	}

	@Test
	public void testIsChanged()
	{
		assertFalse(classUnderTest.isChanged());
	}

	@Test
	public void testIsChangedFirstNameDeviates()
	{
		classUnderTest.setFirstName(firstName);
		assertTrue(classUnderTest.isChanged());
	}

	@Test
	public void setAllXFields()
	{
		classUnderTest.setAllXFields();
                assertTrue(classUnderTest.firstNameX);
	}

	@Test
	public void testSetCity()
	{
                assertFalse(classUnderTest.cityX);
		classUnderTest.setCity("City");
                assertTrue(classUnderTest.cityX);
	}

	@Test
	public void testCompanyName()
	{
                assertFalse(classUnderTest.companyNameX);
		classUnderTest.setCompanyName(value);
                assertTrue(classUnderTest.companyNameX);
		assertEquals(value, classUnderTest.getCompanyName());
	}

	@Test
	public void testDistrict()
	{
                assertFalse(classUnderTest.districtX);
		classUnderTest.setDistrict(value);
                assertTrue(classUnderTest.districtX);
		assertEquals(value, classUnderTest.getDistrict());
	}

	@Test
	public void testEmail()
	{
                assertFalse(classUnderTest.emailX);
		classUnderTest.setEmail(value);
                assertTrue(classUnderTest.emailX);
		assertEquals(value, classUnderTest.getEmail());
	}

	@Test
	public void testFaxExtens()
	{
                assertFalse(classUnderTest.faxExtensX);
		classUnderTest.setFaxExtens(value);
                assertTrue(classUnderTest.faxExtensX);
		assertEquals(value, classUnderTest.getFaxExtens());
	}

	@Test
	public void testFaxNumber()
	{
                assertFalse(classUnderTest.faxNumberX);
		classUnderTest.setFaxNumber(value);
                assertTrue(classUnderTest.faxNumberX);
		assertEquals(value, classUnderTest.getFaxNumber());
	}

	@Test
	public void testHouseNo()
	{
                assertFalse(classUnderTest.houseNoX);
		classUnderTest.setHouseNo(value);
                assertTrue(classUnderTest.houseNoX);
		assertEquals(value, classUnderTest.getHouseNo());
	}

	@Test
	public void testLastName()
	{
                assertFalse(classUnderTest.lastNameX);
		classUnderTest.setLastName(value);
                assertTrue(classUnderTest.lastNameX);
		assertEquals(value, classUnderTest.getLastName());
	}

	@Test
	public void testName1()
	{
                assertFalse(classUnderTest.name1X);
		classUnderTest.setName1(value);
                assertTrue(classUnderTest.name1X);
		assertEquals(value, classUnderTest.getName1());
	}

	@Test
	public void testName2()
	{
                assertFalse(classUnderTest.name2X);
		classUnderTest.setName2(value);
                assertTrue(classUnderTest.name2X);
		assertEquals(value, classUnderTest.getName2());
	}

	@Test
	public void testPostlCod1()
	{
                assertFalse(classUnderTest.postlCod1X);
		classUnderTest.setPostlCod1(value);
                assertTrue(classUnderTest.postlCod1X);
		assertEquals(value, classUnderTest.getPostlCod1());
	}

	@Test
	public void testPostlCod2()
	{
                assertFalse(classUnderTest.postlCod2X);
		classUnderTest.setPostlCod2(value);
                assertTrue(classUnderTest.postlCod2X);
		assertEquals(value, classUnderTest.getPostlCod2());
	}

	@Test
	public void testRegion()
	{
                assertFalse(classUnderTest.regionX);
		classUnderTest.setRegion(value);
                assertTrue(classUnderTest.regionX);
		assertEquals(value, classUnderTest.getRegion());
	}

	@Test
	public void testAddressPartner()
	{
		classUnderTest.setAddressPartner(value);
		assertEquals(value, classUnderTest.getAddressPartner());
	}

	@Test
	public void testAddressString()
	{
		classUnderTest.setAddressString(value);
		assertEquals(value, classUnderTest.getAddressString());
	}

	@Test
	public void testAddrguid()
	{
		classUnderTest.setAddrguid(value);
		assertEquals(value, classUnderTest.getAddrguid());
	}

	@Test
	public void testAddrnum()
	{
		classUnderTest.setAddrnum(value);
		assertEquals(value, classUnderTest.getAddrnum());
	}

	@Test
	public void testId()
	{
		classUnderTest.setId(value);
		assertEquals(value, classUnderTest.getId());
	}

	@Test
	public void testStreet()
	{
                assertFalse(classUnderTest.streetX);
		classUnderTest.setStreet(value);
                assertTrue(classUnderTest.streetX);
		assertEquals(value, classUnderTest.getStreet());
	}

	@Test
	public void testTel1Ext()
	{
                assertFalse(classUnderTest.tel1ExtX);
		classUnderTest.setTel1Ext(value);
                assertTrue(classUnderTest.tel1ExtX);
		assertEquals(value, classUnderTest.getTel1Ext());
	}

	@Test
	public void testTelmob1()
	{
                assertFalse(classUnderTest.telmob1X);
		classUnderTest.setTelmob1(value);
                assertTrue(classUnderTest.telmob1X);
		assertEquals(value, classUnderTest.getTelmob1());
	}

	@Test
	public void testTitleKey()
	{
                assertFalse(classUnderTest.titleKeyX);
		classUnderTest.setTitleKey(value);
                assertTrue(classUnderTest.titleKeyX);
		assertEquals(value, classUnderTest.getTitleKey());
	}
}
