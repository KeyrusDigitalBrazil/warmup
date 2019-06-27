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

import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.Address;
import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.County;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.sap.tc.logging.Severity;


/**
 * BO representation of an address
 *
 */

@SuppressWarnings("squid:ClassCyclomaticComplexity")
public class AddressImpl extends BusinessObjectBase implements Address
{
	private static final long serialVersionUID = 1L;
	private static final String INITIAL_STRING = "";
	protected static final Log4JWrapper LOG = Log4JWrapper
			.getInstance(AddressImpl.class.getName());

	/**
	 * Postfix to create X fields names (indicating change flag) from standard field names
	 */
	public static final String X_STR = "_X";

	private String id = INITIAL_STRING;
	private String type = INITIAL_STRING; // 1=Organisation, 2=Person, 3=Contact
	// person
	private String titleKey = INITIAL_STRING;
	private String firstName = INITIAL_STRING;
	private String lastName = INITIAL_STRING;
	private String name1 = INITIAL_STRING;
	private String name2 = INITIAL_STRING;
	private String city = INITIAL_STRING;
	private String district = INITIAL_STRING;
	private String postlCod1 = INITIAL_STRING;
	private String postlCod2 = INITIAL_STRING;
	private String street = INITIAL_STRING;
	private String houseNo = INITIAL_STRING;
	private String country = INITIAL_STRING;
	private String region = INITIAL_STRING;
	private String taxJurCode = INITIAL_STRING;
	private String tel1Numbr = INITIAL_STRING;
	private String tel1Ext = INITIAL_STRING;
	private String faxNumber = INITIAL_STRING;
	private String faxExtens = INITIAL_STRING;
	private String email = INITIAL_STRING;

	private String telmob1 = INITIAL_STRING;
	private String telmob1Seq = INITIAL_STRING;
	private String addressString = INITIAL_STRING;
	private String addressStringC = INITIAL_STRING;
	private String addrnum = INITIAL_STRING;
	private String addrguid = INITIAL_STRING;
	private String addressPartner = INITIAL_STRING;
	private String companyName = INITIAL_STRING;
	boolean firstNameX;
	boolean lastNameX;
	boolean name1X;
	boolean name2X;
	boolean emailX;
	boolean cityX;
	private boolean countryX;
	boolean streetX;
	boolean houseNoX;
	boolean regionX;
	boolean postlCod1X;
	boolean postlCod2X;
	boolean districtX;
	private boolean tel1NumbrX;
	boolean tel1ExtX;
	boolean telmob1X;
	boolean faxNumberX;
	boolean faxExtensX;
	private boolean taxJurCodeX;
	boolean titleKeyX;
	boolean companyNameX;	

	@SuppressWarnings("squid:S1948")
	private List<County> countyList = null;

	private Operation operation=Operation.NONE;


	/**
	 * simple constructor
	 */
	public AddressImpl()
	{
		this.setTechKey(TechKey.generateKey());
	}


	/**
	 * Set the property id
	 *
	 * @param id
	 *           the id
	 */
	@Override
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * Returns the property id
	 *
	 * @return id
	 */
	@Override
	public String getId()
	{
		return id;
	}

	/**
	 * Set the property type
	 *
	 * @param type
	 *           the type
	 */
	@Override
	public void setType(final String type)
	{
		this.type = type;
	}

	/**
	 * Returns the property type
	 *
	 * @return type
	 */
	@Override
	public String getType()
	{
		return type;
	}

	// setter methods
	@Override
	public void setTitleKey(final String titleKey)
	{
		// cut spaces from ui
		final String newValue = titleKey != null ? titleKey.trim() : null;
		final String oldValue = getTitleKey();
		if (!oldValue.equals(titleKey))
		{
			this.titleKeyX = true;
		}
		this.titleKey = titleKey == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setFirstName(final String firstName)
	{
		// cut spaces from ui
		final String newValue = firstName != null ? firstName.trim() : null;
		final String oldValue = getFirstName();
		if (!oldValue.equals(firstName))
		{
			this.firstNameX = true;
		}
		this.firstName = firstName == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setLastName(final String lastName)
	{
		// cut spaces from ui
		final String newValue = lastName != null ? lastName.trim() : null;
		final String oldValue = getLastName();
		if (!oldValue.equals(lastName))
		{
			this.lastNameX = true;
		}
		this.lastName = lastName == null ? INITIAL_STRING : newValue;

	}

	@Override
	public void setName1(final String name1)
	{
		final String newValue = name1 != null ? name1.trim() : null;
		final String oldValue = getName1();
		if (!oldValue.equals(name1))
		{
			this.name1X = true;
		}
		this.name1 = name1 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setName2(final String name2)
	{
		final String newValue = name2 != null ? name2.trim() : null;
		final String oldValue = getName2();
		if (!oldValue.equals(name2))
		{
			this.name2X = true;
		}
		this.name2 = name2 == null ? INITIAL_STRING : newValue;

	}
	
	@Override
	public void setCity(final String city)
	{
		// cut spaces from ui
		final String newValue = city != null ? city.trim() : null;
		final String oldValue = getCity();
		if (!oldValue.equals(city))
		{
			this.cityX = true;
			this.setTaxJurCode("");
		}
		this.city = city == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setDistrict(final String district)
	{
		// cut spaces from ui
		final String newValue = district != null ? district.trim() : null;
		final String oldValue = getDistrict();
		if (!oldValue.equals(district))
		{
			this.districtX = true;
			this.setTaxJurCode("");
		}
		this.district = district == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getDistrictX()
	{
		return districtX;
	}

	@Override
	public void setPostlCod1(final String postlCod1)
	{
		// cut spaces from ui
		final String newValue = postlCod1 != null ? postlCod1.trim() : null;
		final String oldValue = getPostlCod1();
		if (!oldValue.equals(postlCod1))
		{
			this.postlCod1X = true;
			this.setTaxJurCode("");
		}
		this.postlCod1 = postlCod1 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getPostlCod1X()
	{
		return postlCod1X;
	}

	@Override
	public void setPostlCod2(final String postlCod2)
	{
		// cut spaces from ui
		final String newValue = postlCod2 != null ? postlCod2.trim() : null;
		final String oldValue = getPostlCod2();
		if (!oldValue.equals(postlCod2))
		{
			this.postlCod2X = true;
		}
		this.postlCod2 = postlCod2 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setStreet(final String street)
	{
		// cut spaces from ui
		final String newValue = street != null ? street.trim() : null;
		final String oldValue = getStreet();
		if (!oldValue.equals(street))
		{
			this.streetX = true;
		}
		this.street = street == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setHouseNo(final String houseNo)
	{
		// cut spaces from ui
		final String newValue = houseNo != null ? houseNo.trim() : null;
		final String oldValue = getHouseNo();
		if (!oldValue.equals(houseNo))
		{
			this.houseNoX = true;
		}
		this.houseNo = houseNo == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setCountry(final String country)
	{
		// cut spaces from ui
		final String newValue = country != null ? country.trim() : null;
		final String oldValue = getCountry();
		if (!oldValue.equals(country))
		{
			this.countryX = true;
			this.setTaxJurCode("");
		}
		this.country = country == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setRegion(final String region)
	{
		// cut spaces from ui
		final String newValue = region != null ? region.trim() : null;
		final String oldValue = getRegion();
		if (!oldValue.equals(region))
		{
			this.regionX = true;
			this.setTaxJurCode("");
		}
		this.region = region == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setTaxJurCode(final String taxJurCode)
	{
		// cut spaces from ui
		final String newValue = taxJurCode != null ? taxJurCode.trim() : null;
		final String oldValue = getTaxJurCode();
		if (!oldValue.equals(taxJurCode))
		{
			this.taxJurCodeX = true;
		}
		this.taxJurCode = taxJurCode == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setTel1Numbr(final String tel1Numbr)
	{
		// cut spaces from ui
		final String newValue = tel1Numbr != null ? tel1Numbr.trim() : null;
		final String oldValue = getTel1Numbr();
		if (!oldValue.equals(tel1Numbr))
		{
			this.tel1NumbrX = true;
		}
		this.tel1Numbr = tel1Numbr == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setTel1Ext(final String tel1Ext)
	{
		// cut spaces from ui
		final String newValue = tel1Ext != null ? tel1Ext.trim() : null;
		final String oldValue = getTel1Ext();
		if (!oldValue.equals(tel1Ext))
		{
			this.tel1ExtX = true;
		}
		this.tel1Ext = tel1Ext == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setFaxNumber(final String faxNumber)
	{
		// cut spaces from ui
		final String newValue = faxNumber != null ? faxNumber.trim() : null;
		final String oldValue = getFaxNumber();
		if (!oldValue.equals(faxNumber))
		{
			this.faxNumberX = true;
		}
		this.faxNumber = faxNumber == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getFaxNumberX()
	{
		return faxNumberX;
	}

	@Override
	public void setFaxExtens(final String faxExtens)
	{
		// cut spaces from ui
		final String newValue = faxExtens != null ? faxExtens.trim() : null;
		final String oldValue = getFaxExtens();
		if (!oldValue.equals(faxExtens))
		{
			this.faxExtensX = true;
		}
		this.faxExtens = faxExtens == null ? INITIAL_STRING : newValue;
	}

	@Override
	public void setAddressPartner(final String partner)
	{
		addressPartner = partner;
	}

	// getter methods
	@Override
	public String getTitleKey()
	{
		return titleKey;
	}

	@Override
	public String getFirstName()
	{
		return firstName;
	}

	@Override
	public String getLastName()
	{
		return lastName;
	}

	@Override
	public String getName1()
	{
		return name1;
	}

	@Override
	public String getName2()
	{
		return name2;
	}

	@Override
	public String getCity()
	{
		return city;
	}

	@Override
	public String getDistrict()
	{
		return district;
	}

	@Override
	public String getPostlCod1()
	{
		return postlCod1;
	}

	@Override
	public String getPostlCod2()
	{
		return postlCod2;
	}

	@Override
	public String getStreet()
	{
		return street;
	}

	@Override
	public String getHouseNo()
	{
		return houseNo;
	}

	@Override
	public String getCountry()
	{
		return country;
	}

	@Override
	public String getRegion()
	{
		return region;
	}

	@Override
	public String getTaxJurCode()
	{
		return taxJurCode;
	}

	@Override
	public String getTel1Numbr()
	{
		return tel1Numbr;
	}

	@Override
	public String getTel1Ext()
	{
		return tel1Ext;
	}

	@Override
	public String getFaxNumber()
	{
		return faxNumber;
	}

	@Override
	public String getFaxExtens()
	{
		return faxExtens;
	}

	@Override
	public String getAddressPartner()
	{
		return addressPartner;
	}

	/**
	 * Set the property eMail
	 *
	 * @param email
	 *           the email address
	 */
	@Override
	public void setEmail(final String email)
	{
		// cut spaces from ui
		final String newValue = email != null ? email.trim() : null;
		final String oldValue = getEmail();
		if (!oldValue.equals(email))
		{
			this.emailX = true;
		}
		this.email = email == null ? INITIAL_STRING : newValue;
	}

	/**
	 * Returns the property email
	 *
	 * @return email
	 */
	@Override
	public String getEmail()
	{
		return email;
	}

	@Override
	public String getName()
	{
		return lastName.length() > 0 ? lastName : name1;
	}

	@Override
	final public AddressImpl clone()
	{
		try
		{
			final AddressImpl addressClone = (AddressImpl) super.clone();
			addressClone.setCountyList(getClonedCountyList());
			return addressClone;
		}
		catch (final CloneNotSupportedException ex)
		{
			// should not happen, because we are clone able
			throw new ApplicationBaseRuntimeException(
					"Failed to clone Object, check whether Cloneable Interface is still implemented", ex);
		}
	}

	private List<County> getClonedCountyList()
	{
		List<County> clone = null;
		if (countyList != null)
		{
			clone = new ArrayList<County>(this.countyList.size());
			for (final County item : countyList)
			{
				clone.add(item.clone());
			}
		}
		return clone;
	}


	@Override
	public void setOperation(final Operation operation)
	{
		this.operation = operation == null ? Operation.NONE : operation;
	}

	@Override
	public Operation getOperation()
	{
		return operation;
	}

	@Override
	public String getTelmob1()
	{
		return telmob1;
	}

	@Override
	public void setTelmob1(final String telmob1)
	{
		// cut spaces from ui
		final String newValue = telmob1 != null ? telmob1.trim() : null;
		final String oldValue = getTelmob1();
		if (!oldValue.equals(telmob1))
		{
			this.telmob1X = true;
		}
		this.telmob1 = telmob1 == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getTelmob1X()
	{
		return telmob1X;
	}

	@Override
	public boolean getEmailX()
	{
		return emailX;
	}

	@Override
	public String getAddrnum()
	{
		return addrnum;
	}

	@Override
	public void setAddrnum(final String addrnum)
	{
		this.addrnum = addrnum;
	}

	@Override
	public String getAddrguid()
	{
		return addrguid;
	}

	@Override
	public void setAddrguid(final String addrguid)
	{
		this.addrguid = addrguid == null ? INITIAL_STRING : addrguid;
	}

	@Override
	public boolean getLastNameX()
	{
		return lastNameX;
	}

	@Override
	public List<County> getCountyList()
	{
		return countyList;
	}

	@Override
	public void setCountyList(final List<County> countyList)
	{
		this.countyList = countyList;
	}

	@Override
	public boolean getFirstNameX()
	{
		return firstNameX;
	}

	@Override
	public boolean getName1X()
	{
		return name1X;
	}

	@Override
	public boolean getName2X()
	{
		return name2X;
	}

	@Override
	public boolean getCityX()
	{
		return cityX;
	}

	@Override
	public boolean getCountryX()
	{
		return countryX;
	}

	@Override
	public boolean getStreetX()
	{
		return streetX;
	}

	@Override
	public boolean getRegionX()
	{
		return regionX;
	}

	@Override
	public boolean getHouseNoX()
	{
		return houseNoX;
	}

	@Override
	public boolean getTel1NumbrX()
	{
		return tel1NumbrX;
	}

	@Override
	public boolean getTel1ExtX()
	{
		return tel1ExtX;
	}

	@Override
	public boolean getFaxExtensX()
	{
		return faxExtensX;
	}

	@Override
	public boolean getTaxJurCodeX()
	{
		return taxJurCodeX;
	}

	@Override
	public boolean getTitleKeyX()
	{
		return titleKeyX;
	}

	@Override
	public void setTelmob1Seq(final String telmob1_seq)
	{
		this.telmob1Seq = telmob1_seq;
	}

	@Override
	public String getTelmob1Seq()
	{
		return telmob1Seq;
	}

	@Override
	public String getCompanyName()
	{
		return companyName;
	}

	@Override
	public void setCompanyName(final String companyName)
	{
		// cut spaces from ui
		final String newValue = companyName != null ? companyName.trim() : null;
		final String oldValue = getCompanyName();
		if (!oldValue.equals(companyName))
		{
			this.companyNameX = true;
		}
		this.companyName = companyName == null ? INITIAL_STRING : newValue;
	}

	@Override
	public boolean getCompanyNameX()
	{
		return companyNameX;
	}

	@Override
	public String getAddressString()
	{
		return addressString;
	}

	@Override
	public void setAddressString(final String addressString)
	{
		this.addressString = addressString;
	}

	@Override
	public String getAddressStringC()
	{
		return addressStringC;
	}

	@Override
	public void setAddressStringC(final String addressStringC)
	{
		this.addressStringC = addressStringC;
	}

	/**
	 * <b>clearX</b> This method is to clear the values of all the <i>"X"</i> variables
	 */
	@Override
	public void clearX()
	{
		this.firstNameX = false;
		this.lastNameX = false;
		this.emailX = false;
		this.cityX = false;
		this.name1X = false;
		this.name2X = false;
		this.countryX = false;
		this.streetX = false;
		this.houseNoX = false;
		this.regionX = false;
		this.postlCod1X = false;
		this.postlCod2X = false;
		this.districtX = false;
		this.tel1NumbrX = false;
		this.tel1ExtX = false;
		this.telmob1X = false;
		this.faxNumberX = false;
		this.faxExtensX = false;
		this.taxJurCodeX = false;
		this.titleKeyX = false;
		this.companyNameX = false;
	}

	/**
	 * <b>getIs_changed</b> This method will check all the "X" fields and see if the address has changed or not
	 *
	 * @return boolean - changed or not
	 */
	
	@SuppressWarnings({"squid:S1067","squid:MethodCyclomaticComplexity"})
	@Override
	public boolean isChanged()
	{
		boolean changed = false;

		if (this.firstNameX || this.lastNameX || this.emailX || this.cityX
				|| this.countryX || this.streetX || this.name1X || this.name2X
				|| this.houseNoX || this.regionX || this.postlCod1X || this.postlCod2X || this.districtX || this.tel1NumbrX
				|| this.tel1ExtX || this.telmob1X || this.faxNumberX || this.faxExtensX || this.taxJurCodeX
				|| this.titleKeyX || this.companyNameX)
		{
			changed = true;
		}
		return changed;
	}



	@Override
	public void setAllXFields()
	{
		this.firstNameX = true;
		this.lastNameX = true;
		this.emailX = true;
		this.name1X = true;
		this.name2X = true;
		this.cityX = true;
		this.countryX = true;
		this.streetX = true;
		this.houseNoX = true;
		this.regionX = true;
		this.postlCod1X = true;
		this.postlCod2X = true;
		this.districtX = true;
		this.tel1NumbrX = true;
		this.tel1ExtX = true;
		this.telmob1X = true;
		this.faxNumberX = true;
		this.faxExtensX = true;
		this.taxJurCodeX = true;
		this.titleKeyX = true;
		this.companyNameX = true;
	}

	@Override
	public boolean getPostlCod2X()
	{
		return postlCod2X;
	}

	@Override
	// do not remove when cleaning up - we still need a generic get method
	public String get(final String fieldName)
	{
		try
		{
			final Field field = this.getClass().getDeclaredField(fieldName);
			// get the current value
			final Object oldValue = field.get(this);
			return oldValue.toString();
		}
		catch (final IllegalAccessException ex)
		{
					LOG.traceThrowable(Severity.DEBUG, ex.getMessage(), ex);
		}
		catch (final IllegalArgumentException ex)
		{
			       LOG.traceThrowable(Severity.DEBUG, ex.getMessage(), ex);
		}
		catch (final SecurityException ex)
		{
					LOG.traceThrowable(Severity.DEBUG, ex.getMessage(), ex);
		}
		catch (final NoSuchFieldException ex)
		{
					LOG.traceThrowable(Severity.DEBUG, ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings({"squid:S1067","squid:MethodCyclomaticComplexity"})
	@Override
	public boolean isAddressfieldsEqualTo(final Address a)
	{

		return city.equals(a.getCity())
				&& companyName.equals(a.getCompanyName()) && country.equals(a.getCountry())
				&& district.equals(a.getDistrict()) && email.equals(a.getEmail())
				&& faxExtens.equals(a.getFaxExtens()) && faxNumber.equals(a.getFaxNumber())
				&& firstName.equals(a.getFirstName())
				&& houseNo.equals(a.getHouseNo())
				&& lastName.equals(a.getLastName())
				&& name1.equals(a.getName1())
				&& name2.equals(a.getName2())
				&& postlCod1.equals(a.getPostlCod1()) && postlCod2.equals(a.getPostlCod2())
				&& region.equals(a.getRegion())
				&& street.equals(a.getStreet())
				&& tel1Ext.equals(a.getTel1Ext())
				&& tel1Numbr.equals(a.getTel1Numbr()) && telmob1.equals(a.getTelmob1())
				&& telmob1Seq.equals(a.getTelmob1Seq()) && titleKey.equals(a.getTitleKey())

		;
	}

	@Override
	public int compareTo(final Address o)
	{
		return addressStringC.compareTo(o.getAddressStringC());
	}

}