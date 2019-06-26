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
package de.hybris.platform.webservicescommons.mapping.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.mapping.config.FieldSetLevelMapping;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;


@UnitTest
public class DefaultFieldSetLevelHelperTest
{
	private static final String EXTENDED_LEVEL = "EXTENDED";
	private static final String ADDRESS_BASIC_LEVEL = "firstName,lastName,town";
	private static final String ADDRESS_EXTENDED_LEVEL = "firstName,lastName,line1,line2,town,country";
	private static final String COUNTRY_BASIC_LEVEL = "name";
	private static final String COUNTRY_EXTENDED_LEVEL = "name,isocode";
	private static final String TEST_BASIC_LEVEL = "value1,value2";
	private static final String TEST_DEFAULT_LEVEL = "value1,value2";
	private static final String TEST_FULL_LEVEL = "value1,value2,parentValue1,parentValue2,parentValue3";

	@Mock
	private ApplicationContext ctx;
	private DefaultFieldSetLevelHelper fieldSetLevelHelper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		final Map<String, String> addressLevelMap = new HashMap<String, String>();
		addressLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, ADDRESS_BASIC_LEVEL);
		addressLevelMap.put(EXTENDED_LEVEL, ADDRESS_EXTENDED_LEVEL);
		final FieldSetLevelMapping addressMapping = new FieldSetLevelMapping();
		addressMapping.setDtoClass(AddressData.class);
		addressMapping.setLevelMapping(addressLevelMap);

		final Map<String, String> countryLevelMap = new HashMap<String, String>();
		countryLevelMap.put(FieldSetLevelHelper.BASIC_LEVEL, COUNTRY_BASIC_LEVEL);
		countryLevelMap.put(EXTENDED_LEVEL, COUNTRY_EXTENDED_LEVEL);
		final FieldSetLevelMapping countryMapping = new FieldSetLevelMapping();
		countryMapping.setDtoClass(CountryData.class);
		countryMapping.setLevelMapping(countryLevelMap);

		final Map<String, FieldSetLevelMapping> mapping = new HashMap<String, FieldSetLevelMapping>();
		mapping.put("addressMapping", addressMapping);
		mapping.put("countryMapping", countryMapping);


		Mockito.when(ctx.getBeansOfType(FieldSetLevelMapping.class)).thenReturn(mapping);

		fieldSetLevelHelper = new DefaultFieldSetLevelHelper();
		fieldSetLevelHelper.setApplicationContext(ctx);
	}

	@Test
	public void testLevelMap()
	{
		Assert.assertNotNull(fieldSetLevelHelper.getLevelMap());
		Assert.assertTrue(fieldSetLevelHelper.getLevelMap().containsKey(AddressData.class));
		Assert.assertTrue(fieldSetLevelHelper.getLevelMap().containsKey(CountryData.class));
		Assert.assertEquals(2, fieldSetLevelHelper.getLevelMap().size());

		final Map<String, String> addressLevelMap = fieldSetLevelHelper.getLevelMap().get(AddressData.class);
		Assert.assertNotNull(addressLevelMap);
		Assert.assertEquals(ADDRESS_BASIC_LEVEL, addressLevelMap.get(FieldSetLevelHelper.BASIC_LEVEL));
		Assert.assertEquals(ADDRESS_EXTENDED_LEVEL, addressLevelMap.get(EXTENDED_LEVEL));
		Assert.assertEquals(2, addressLevelMap.size());

		final Map<String, String> countryLevelMap = fieldSetLevelHelper.getLevelMap().get(CountryData.class);
		Assert.assertNotNull(addressLevelMap);
		Assert.assertEquals(COUNTRY_BASIC_LEVEL, countryLevelMap.get(FieldSetLevelHelper.BASIC_LEVEL));
		Assert.assertEquals(COUNTRY_EXTENDED_LEVEL, countryLevelMap.get(EXTENDED_LEVEL));
		Assert.assertEquals(2, countryLevelMap.size());
	}

	@Test
	public void testIsLevelName()
	{
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.BASIC_LEVEL, AddressData.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.DEFAULT_LEVEL, AddressData.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(EXTENDED_LEVEL, AddressData.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.FULL_LEVEL, AddressData.class));

		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.BASIC_LEVEL, TestDTO.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.DEFAULT_LEVEL, TestDTO.class));
		Assert.assertFalse(fieldSetLevelHelper.isLevelName(EXTENDED_LEVEL, TestDTO.class));
		Assert.assertTrue(fieldSetLevelHelper.isLevelName(FieldSetLevelHelper.FULL_LEVEL, TestDTO.class));
	}

	@Test
	public void testGetLevelDefinitionForClass()
	{
		String levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(AddressData.class, FieldSetLevelHelper.BASIC_LEVEL);
		Assert.assertEquals(ADDRESS_BASIC_LEVEL, levelDefinition);
		levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(AddressData.class, EXTENDED_LEVEL);
		Assert.assertEquals(ADDRESS_EXTENDED_LEVEL, levelDefinition);
	}

	@Test
	public void testCreateBasicLevel()
	{
		String levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.BASIC_LEVEL);
		Assert.assertNull(levelDefinition);

		levelDefinition = fieldSetLevelHelper.createBasicLevelDefinition(TestDTO.class);
		Assert.assertEquals(TEST_BASIC_LEVEL, levelDefinition);

		levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.BASIC_LEVEL);
		Assert.assertEquals(TEST_BASIC_LEVEL, levelDefinition);
	}

	@Test
	public void testCreateDefaultLevel()
	{
		String levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
		Assert.assertNull(levelDefinition);

		levelDefinition = fieldSetLevelHelper.createDefaultLevelDefinition(TestDTO.class);
		Assert.assertEquals(TEST_DEFAULT_LEVEL, levelDefinition);

		levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);
		Assert.assertEquals(TEST_DEFAULT_LEVEL, levelDefinition);
	}

	@Test
	public void testCreateFullLevel()
	{
		String levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.FULL_LEVEL);
		Assert.assertNull(levelDefinition);

		levelDefinition = fieldSetLevelHelper.createFullLevelDefinition(TestDTO.class);
		Assert.assertEquals(TEST_FULL_LEVEL, levelDefinition);

		levelDefinition = fieldSetLevelHelper.getLevelDefinitionForClass(TestDTO.class, FieldSetLevelHelper.FULL_LEVEL);
		Assert.assertEquals(TEST_FULL_LEVEL, levelDefinition);
	}

	@SuppressWarnings("unused")
	private static class TestParentDTO
	{
		private String parentValue1;
		private int parentValue2;
		private Map<String, String> parentValue3;
	}

	@SuppressWarnings("unused")
	private static class TestDTO extends TestParentDTO
	{
		private static String STATIC_FIELD = "static";
		private final Boolean wrapperType = Boolean.FALSE;
		private String value1;
		private String value2;
	}

	@SuppressWarnings("unused")
	private static class CountryData
	{

		private String isocode;
		private String name;

		public CountryData()
		{
			// default constructor
		}


		public void setIsocode(final String isocode)
		{
			this.isocode = isocode;
		}


		public String getIsocode()
		{
			return isocode;
		}


		public void setName(final String name)
		{
			this.name = name;
		}


		public String getName()
		{
			return name;
		}
	}

	@SuppressWarnings("unused")
	private static class AddressData
	{

		private String lastName;
		private CountryData country;
		private String town;
		private String companyName;
		private String postalCode;
		private String title;
		private String titleCode;
		private String firstName;
		private String formattedAddress;
		private String phone;
		private boolean visibleInAddressBook;
		private boolean shippingAddress;
		private String id;
		private boolean billingAddress;
		private String line2;
		private String line1;
		private String email;
		private boolean defaultAddress;

		public AddressData()
		{
			// default constructor
		}


		public void setLastName(final String lastName)
		{
			this.lastName = lastName;
		}


		public String getLastName()
		{
			return lastName;
		}


		public void setCountry(final CountryData country)
		{
			this.country = country;
		}


		public CountryData getCountry()
		{
			return country;
		}


		public void setTown(final String town)
		{
			this.town = town;
		}


		public String getTown()
		{
			return town;
		}


		public void setCompanyName(final String companyName)
		{
			this.companyName = companyName;
		}


		public String getCompanyName()
		{
			return companyName;
		}


		public void setPostalCode(final String postalCode)
		{
			this.postalCode = postalCode;
		}


		public String getPostalCode()
		{
			return postalCode;
		}


		public void setTitle(final String title)
		{
			this.title = title;
		}


		public String getTitle()
		{
			return title;
		}


		public void setTitleCode(final String titleCode)
		{
			this.titleCode = titleCode;
		}


		public String getTitleCode()
		{
			return titleCode;
		}


		public void setFirstName(final String firstName)
		{
			this.firstName = firstName;
		}


		public String getFirstName()
		{
			return firstName;
		}


		public void setFormattedAddress(final String formattedAddress)
		{
			this.formattedAddress = formattedAddress;
		}


		public String getFormattedAddress()
		{
			return formattedAddress;
		}


		public void setPhone(final String phone)
		{
			this.phone = phone;
		}


		public String getPhone()
		{
			return phone;
		}


		public void setVisibleInAddressBook(final boolean visibleInAddressBook)
		{
			this.visibleInAddressBook = visibleInAddressBook;
		}


		public boolean isVisibleInAddressBook()
		{
			return visibleInAddressBook;
		}


		public void setShippingAddress(final boolean shippingAddress)
		{
			this.shippingAddress = shippingAddress;
		}


		public boolean isShippingAddress()
		{
			return shippingAddress;
		}


		public void setId(final String id)
		{
			this.id = id;
		}


		public String getId()
		{
			return id;
		}


		public void setBillingAddress(final boolean billingAddress)
		{
			this.billingAddress = billingAddress;
		}


		public boolean isBillingAddress()
		{
			return billingAddress;
		}

		public void setLine2(final String line2)
		{
			this.line2 = line2;
		}


		public String getLine2()
		{
			return line2;
		}


		public void setLine1(final String line1)
		{
			this.line1 = line1;
		}


		public String getLine1()
		{
			return line1;
		}


		public void setEmail(final String email)
		{
			this.email = email;
		}


		public String getEmail()
		{
			return email;
		}


		public void setDefaultAddress(final boolean defaultAddress)
		{
			this.defaultAddress = defaultAddress;
		}


		public boolean isDefaultAddress()
		{
			return defaultAddress;
		}
	}
}
