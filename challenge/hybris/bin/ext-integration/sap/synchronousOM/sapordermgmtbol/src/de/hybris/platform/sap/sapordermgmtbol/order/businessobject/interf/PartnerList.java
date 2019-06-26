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
package de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf;

import de.hybris.platform.sap.core.common.TechKey;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;



/**
 * Represents the backend's view of a PartnerList.
 *
 */
public interface PartnerList extends Iterable<Entry<String, PartnerListEntry>>, Cloneable, Serializable
{

	/**
	 * Creates an empty <code>PartnerListEntryData</code> for the basket.
	 *
	 * @return PartnerListEntry which can added to the partnerList
	 */
	PartnerListEntry createPartnerListEntry();

	/**
	 * Creates a initialized <code>PartnerListEntryData</code> for the basket.
	 *
	 * @param partnerGUID
	 *           techkey of the business partner
	 * @param partnerId
	 *           id of the business partner
	 * @return PartnerListEntry which can added to the partnerList
	 */
	PartnerListEntry createPartnerListEntry(TechKey partnerGUID, String partnerId);

	/**
	 * Get the business partner for the given role
	 *
	 * @param bpRole
	 *           String the role of the business partner
	 * @return PartnerListEntryData of the business partner, with the given role or null if not found
	 */
	PartnerListEntry getPartnerData(String bpRole);

	/**
	 * Convenience method to Get the soldto business partner, or null, if not available
	 *
	 * @return PartnerListEntryData of the soldTo business partner, or null if not found
	 */
	PartnerListEntry getSoldToData();

	/**
	 * Convenience method to Get the contact business partner, or null, if not available
	 *
	 * @return PartnerListEntryData of the contact business partner, or null if not found
	 */
	PartnerListEntry getContactData();

	/**
	 * Convenience method to Get the contact business partner, or null, if not available
	 *
	 * @return PartnerListEntryData of the contac business partner, or null if not found
	 */
	PartnerListEntry getSoldFromData();

	/**
	 * Convenience method to Get the reseller business partner, or null, if not available
	 *
	 * @return PartnerListEntryData of the reseller business partner, or null if not found
	 */
	PartnerListEntry getResellerData();

	/**
	 * Convenience method to Get the payer business partner, or null, if not available
	 *
	 * @return PartnerListEntryData of the payer business partner, or null if not found
	 */
	PartnerListEntry getPayerData();

	/**
	 * Set the business partner for the given role
	 *
	 * @param bpRole
	 *           String the role of the business partner
	 * @param entry
	 *           the PartnerListEntry for the business partner
	 */
	void setPartnerData(String bpRole, PartnerListEntry entry);

	/**
	 * Convenience method to set the soldto business partner
	 *
	 * @param entry
	 *           of the soldTo business partner
	 */
	void setSoldToData(PartnerListEntry entry);

	/**
	 * Convenience method to set the contact business partner
	 *
	 * @param entry
	 *           of the contact business partner
	 */
	void setContactData(PartnerListEntry entry);

	/**
	 * Convenience method to set the soldfrom business partner
	 *
	 * @param entry
	 *           of the soldfrom business partner
	 */
	void setSoldFromData(PartnerListEntry entry);

	/**
	 * Convenience method to set the payer business partner
	 *
	 * @param entry
	 *           of the payer business partner
	 */
	void setPayerData(PartnerListEntry entry);

	/**
	 * remove the business partner for the given role
	 *
	 * @param bpRole
	 *           String the role of the business partner
	 */
	void removePartnerData(String bpRole);

	/**
	 * remove the business partner with the given GUID
	 *
	 * @param bpTechKey
	 *           TechKey of the Partner, to dlete
	 */
	void removePartnerData(TechKey bpTechKey);

	/**
	 * clear the business partner list
	 */
	void clearList();

	/**
	 *
	 * @return clone
	 * @see Object#clone
	 */

	@SuppressWarnings("squid:S1161")
	PartnerList clone();

	/**
	 * get the business partner list
	 *
	 * @return MashMap list of the business partners
	 */
	Map<String, PartnerListEntry> getList();

	/**
	 * Get a string representation of this partnerList which is unique for these partners
	 *
	 * @return string
	 */
	String getAllToString();

}