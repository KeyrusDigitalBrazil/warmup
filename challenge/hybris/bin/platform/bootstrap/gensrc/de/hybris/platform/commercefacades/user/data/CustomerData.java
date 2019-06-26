/*
 * ----------------------------------------------------------------
 * --- WARNING: THIS FILE IS GENERATED AND WILL BE OVERWRITTEN!
 * --- Generated at 26/06/2019 16:56:09
 * ----------------------------------------------------------------
 *
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercefacades.user.data;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import java.util.Date;

public  class CustomerData extends PrincipalData 
{

 

	/** <i>Generated property</i> for <code>CustomerData.defaultBillingAddress</code> property defined at extension <code>commercefacades</code>. */
		
	private AddressData defaultBillingAddress;

	/** <i>Generated property</i> for <code>CustomerData.defaultShippingAddress</code> property defined at extension <code>commercefacades</code>. */
		
	private AddressData defaultShippingAddress;

	/** <i>Generated property</i> for <code>CustomerData.titleCode</code> property defined at extension <code>commercefacades</code>. */
		
	private String titleCode;

	/** <i>Generated property</i> for <code>CustomerData.firstName</code> property defined at extension <code>commercefacades</code>. */
		
	private String firstName;

	/** <i>Generated property</i> for <code>CustomerData.lastName</code> property defined at extension <code>commercefacades</code>. */
		
	private String lastName;

	/** <i>Generated property</i> for <code>CustomerData.currency</code> property defined at extension <code>commercefacades</code>. */
		
	private CurrencyData currency;

	/** <i>Generated property</i> for <code>CustomerData.language</code> property defined at extension <code>commercefacades</code>. */
		
	private LanguageData language;

	/** <i>Generated property</i> for <code>CustomerData.displayUid</code> property defined at extension <code>commercefacades</code>. */
		
	private String displayUid;

	/** <i>Generated property</i> for <code>CustomerData.customerId</code> property defined at extension <code>commercefacades</code>. */
		
	private String customerId;

	/** <i>Generated property</i> for <code>CustomerData.deactivationDate</code> property defined at extension <code>commercefacades</code>. */
		
	private Date deactivationDate;

	/** <i>Generated property</i> for <code>CustomerData.defaultAddress</code> property defined at extension <code>assistedservicefacades</code>. */
		
	private AddressData defaultAddress;

	/** <i>Generated property</i> for <code>CustomerData.latestCartId</code> property defined at extension <code>assistedservicefacades</code>. */
		
	private String latestCartId;

	/** <i>Generated property</i> for <code>CustomerData.hasOrder</code> property defined at extension <code>assistedservicefacades</code>. */
		
	private Boolean hasOrder;

	/** <i>Generated property</i> for <code>CustomerData.profilePicture</code> property defined at extension <code>assistedservicefacades</code>. */
		
	private ImageData profilePicture;
	
	public CustomerData()
	{
		// default constructor
	}
	
		
	
	public void setDefaultBillingAddress(final AddressData defaultBillingAddress)
	{
		this.defaultBillingAddress = defaultBillingAddress;
	}

		
	
	public AddressData getDefaultBillingAddress() 
	{
		return defaultBillingAddress;
	}
	
		
	
	public void setDefaultShippingAddress(final AddressData defaultShippingAddress)
	{
		this.defaultShippingAddress = defaultShippingAddress;
	}

		
	
	public AddressData getDefaultShippingAddress() 
	{
		return defaultShippingAddress;
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
	
		
	
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

		
	
	public String getLastName() 
	{
		return lastName;
	}
	
		
	
	public void setCurrency(final CurrencyData currency)
	{
		this.currency = currency;
	}

		
	
	public CurrencyData getCurrency() 
	{
		return currency;
	}
	
		
	
	public void setLanguage(final LanguageData language)
	{
		this.language = language;
	}

		
	
	public LanguageData getLanguage() 
	{
		return language;
	}
	
		
	
	public void setDisplayUid(final String displayUid)
	{
		this.displayUid = displayUid;
	}

		
	
	public String getDisplayUid() 
	{
		return displayUid;
	}
	
		
	
	public void setCustomerId(final String customerId)
	{
		this.customerId = customerId;
	}

		
	
	public String getCustomerId() 
	{
		return customerId;
	}
	
		
	
	public void setDeactivationDate(final Date deactivationDate)
	{
		this.deactivationDate = deactivationDate;
	}

		
	
	public Date getDeactivationDate() 
	{
		return deactivationDate;
	}
	
		
	
	public void setDefaultAddress(final AddressData defaultAddress)
	{
		this.defaultAddress = defaultAddress;
	}

		
	
	public AddressData getDefaultAddress() 
	{
		return defaultAddress;
	}
	
		
	
	public void setLatestCartId(final String latestCartId)
	{
		this.latestCartId = latestCartId;
	}

		
	
	public String getLatestCartId() 
	{
		return latestCartId;
	}
	
		
	
	public void setHasOrder(final Boolean hasOrder)
	{
		this.hasOrder = hasOrder;
	}

		
	
	public Boolean getHasOrder() 
	{
		return hasOrder;
	}
	
		
	
	public void setProfilePicture(final ImageData profilePicture)
	{
		this.profilePicture = profilePicture;
	}

		
	
	public ImageData getProfilePicture() 
	{
		return profilePicture;
	}
	


}
