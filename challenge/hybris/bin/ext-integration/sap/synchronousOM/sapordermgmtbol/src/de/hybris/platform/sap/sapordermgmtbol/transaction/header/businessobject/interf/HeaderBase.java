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
package de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf;

import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.Text;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ConnectedDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.util.interf.DocumentType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * Represents the backend's view of the header of a sales document.
 *
 * @version 1.0
 */
public interface HeaderBase extends SimpleHeader
{

	/**
	 * Constant defining a document that is in the completion status OPEN.
	 */
	String DOCUMENT_COMPLETION_STATUS_OPEN = "open";

	/**
	 * Constant defining a document that is in the completion status INPROCESS.
	 */
	String DOCUMENT_COMPLETION_STATUS_INPROCESS = "inprocess";

	/**
	 * Constant defining a document that is in the completion status COMPLETED.
	 */
	String DOCUMENT_COMPLETION_STATUS_COMPLETED = "completed";

	/**
	 * Adds a <code>ConnectedDocument</code> to the predecessor list. A <code>null</code> reference passed to this
	 * function will be ignored, in this case nothing will happen.
	 *
	 * @param predecessorData
	 *           ConnectedDocument to be added to the predecessor list
	 */
	void addPredecessor(ConnectedDocument predecessorData);

	/**
	 * Adds a <code>ConnectedDocument</code> to the successor list. A <code>null</code> reference passed to this function
	 * will be ignored, in this case nothing will happen.
	 *
	 * @param successorData
	 *           ConnectedDocument to be added to the successor list
	 */
	void addSuccessor(ConnectedDocument successorData);

	/**
	 * Instead of a shallow-copy this returns a deep-copy of this <tt>HeaderBaseImpl</tt> instance.
	 *
	 * @return a deep-copy of this HeaderBase
	 */
	@SuppressWarnings("squid:S1161")
	Object clone();


	/**
	 * Creates a text object. This method is used by back end objects to get instances of the text object.
	 *
	 *
	 * @return newly created text object
	 */
	Text createText();

	/**
	 * create a <code>ConnectedDocument</code> object.
	 *
	 * @return new ConnectedDocument Instance
	 */
	ConnectedDocument createConnectedDocument();

	/**
	 * Get the date, the document was changed the last time.
	 *
	 * @return the date, the document was changed the last time
	 */
	Date getChangedAt();

	/**
	 * Get the date, the document was created.
	 *
	 * @return date, the document was created
	 */
	Date getCreatedAt();

	/**
	 * Get the currency used for this document.
	 *
	 * @return the currency used for this document
	 */
	String getCurrency();

	/**
	 * Returns the delivery priority key
	 *
	 * @return String
	 */
	String getDeliveryPriority();

	/**
	 * Get the distribution channel.
	 *
	 * @return distribution channel
	 */
	String getDisChannel();

	/**
	 * Get the devision.
	 *
	 * @return the devision
	 */
	String getDivision();

	/**
	 * Returns the type of the document, the header belongs to.
	 *
	 * @return the document type.
	 */
	DocumentType getDocumentType();

	/**
	 * Get the price for the freight of the order.
	 *
	 * @return the price for the freight of the
	 */
	BigDecimal getFreightValue();

	/**
	 * Get the price including all taxes but not the freight.
	 *
	 * @return the value
	 */
	BigDecimal getGrossValue();

	/**
	 * Get the net price
	 *
	 * @return the price
	 */
	BigDecimal getNetValue();

	/**
	 * Get the net price without freight.
	 *
	 * @return the price
	 */
	BigDecimal getNetValueWOFreight();

	/**
	 * Returns the partner id for the given partner function.
	 *
	 * @param partnerFunction
	 *           partnerFucntion to be checked
	 * @return partner id
	 */
	String getPartnerId(String partnerFunction);

	/**
	 * Returns the partner key for the given partner function.
	 *
	 * @param partnerFunction
	 *           partnerFucntion to be checked
	 * @return partner GUID
	 */
	TechKey getPartnerKey(String partnerFunction);

	/**
	 * Get the business partner list
	 *
	 * @return PartnerList list of business partners
	 */
	PartnerList getPartnerList();

	/**
	 * Get the date the order was created from the customer's point of view.
	 *
	 * @return the posting date
	 */
	Date getPostingDate();

	/**
	 * Get the predecessor list
	 *
	 * @return list of all predecessor documents
	 */
	List<ConnectedDocument> getPredecessorList();

	/**
	 * Get the process type of the document.
	 *
	 * @return process type
	 */
	String getProcessType();

	/**
	 * Get the description of the process type
	 *
	 * @return description of the process type
	 */
	String getProcessTypeDesc();

	/**
	 * Get the external purchase order number.
	 *
	 * @return the purchase order number
	 */
	String getPurchaseOrderExt();

	/**
	 * Get the requested delivery date.
	 *
	 * @return the request delivery date
	 */
	Date getReqDeliveryDate();

	/**
	 * Get the number of the sales document the header belongs to.
	 *
	 * @return the number of the sales document
	 */

	String getSalesDocNumber();

	/**
	 * Get the origin of the sales document.
	 *
	 * @return the origin of the sales document
	 */
	String getSalesDocumentsOrigin();

	/**
	 * Get the sales office.
	 *
	 * @return the sales office
	 */
	String getSalesOffice();

	/**
	 * Get the sales organization for the document.
	 *
	 * @return the sales organization
	 */
	String getSalesOrg();

	/**
	 * Get the shipping conditions for the document.
	 *
	 * @return the shipping conditions
	 */
	String getShipCond();

	/**
	 * Get the successor list
	 *
	 * @return list of all successor documents
	 */
	List<ConnectedDocument> getSuccessorList();

	/**
	 * Get the taxes that have to be paid for the document.
	 *
	 * @return the taxes
	 */
	BigDecimal getTaxValue();

	/**
	 * @return total discount, which is used for strike through prices
	 */
	BigDecimal getTotalDiscount();

	/**
	 * Get the date, the document is valid from.
	 *
	 * @return date, the document is valid from
	 */
	Date getValidFrom();

	/**
	 * Get the date, the document is valid to.
	 *
	 * @return date, the document is valid to
	 */
	Date getValidTo();

	/**
	 * Check whether or not the document is changeable.
	 *
	 * @return <code>true</code> if the document is changeable, otherwise <code>false</code>.
	 */
	boolean isChangeable();

	/**
	 * Set whether or not the document is changeable using a String parameter.
	 *
	 * @param changeable
	 *           <code>" "</code> or <code>""</code> indicates that the document is changeable, all other values that it
	 *           is changeable.
	 */
	void setChangeable(boolean changeable);

	/**
	 * Set the date, the document was changed the last time.
	 *
	 * @param changedAt
	 *           the date, the document was changed the last time
	 */
	void setChangedAt(Date changedAt);

	/**
	 * Set the date, document was created.
	 *
	 * @param createdAt
	 *           the date to be set
	 */
	void setCreatedAt(Date createdAt);

	/**
	 * Set the currency used for the document the header belongs to.
	 *
	 * @param currency
	 *           the currency to be set
	 */
	void setCurrency(String currency);

	/**
	 * Sets the delivery priority key.
	 *
	 * @param deliveryPriority
	 *           key
	 */
	void setDeliveryPriority(String deliveryPriority);

	/**
	 * Set the distribution channel.
	 *
	 * @param disChannel
	 *           the distribution channel to be set
	 */
	void setDisChannel(String disChannel);

	/**
	 * Set the devision.
	 *
	 * @param division
	 *           the devision to be set
	 */
	void setDivision(String division);

	/**
	 * Set the document type.
	 *
	 * @param docType
	 *           document type
	 */
	void setDocumentType(DocumentType docType);

	/**
	 * Set the price for the freight of the order.
	 *
	 * @param freightValue
	 *           the price for the freight
	 */
	void setFreightValue(BigDecimal freightValue);

	/**
	 * Set the price including all taxes but not the freight.
	 *
	 * @param grossValue
	 *           the price to be set
	 */
	void setGrossValue(BigDecimal grossValue);

	/**
	 * Set the net price
	 *
	 * @param netValue
	 *           the price to be set
	 */
	void setNetValue(BigDecimal netValue);

	/**
	 * Set the net price without freight.
	 *
	 * @param netValueWOFreight
	 *           the price to be set
	 */
	void setNetValueWOFreight(BigDecimal netValueWOFreight);

	/**
	 * Sets the business partner list.
	 *
	 * @param list
	 *           list of business partners
	 */
	void setPartnerList(PartnerList list);

	/**
	 * Set the date the order was created from the customer's point of view.
	 *
	 * @param postingDate
	 *           the date to be set
	 */
	void setPostingDate(Date postingDate);

	/**
	 * Set the the process type of the document.
	 *
	 * @param processType
	 *           the process type to be set
	 */
	void setProcessType(String processType);

	/**
	 * Sets the description of the process type
	 *
	 * @param processTypeDesc
	 *           The processTypeDesc to set
	 */

	void setProcessTypeDesc(String processTypeDesc);

	/**
	 * Set the external purchase order number.
	 *
	 * @param purchaseOrderExt
	 *           the number to be set
	 */
	void setPurchaseOrderExt(String purchaseOrderExt);

	/**
	 * Set the requested delivery date.
	 *
	 * @param reqDeliveryDate
	 *           the requested delivery date to be set.
	 */
	void setReqDeliveryDate(Date reqDeliveryDate);

	/**
	 * Set the number of the sales document the header belongs to.
	 *
	 * @param salesDocNumber
	 *           the number of the sales document
	 */
	void setSalesDocNumber(String salesDocNumber);

	/**
	 * Set the origin of the sales document.
	 *
	 * @param salesDocOrigin
	 *           the origin to be set
	 */
	void setSalesDocumentsOrigin(String salesDocOrigin);

	/**
	 * Set the sales office.
	 *
	 * @param salesOffice
	 *           the sales office
	 */
	void setSalesOffice(String salesOffice);

	/**
	 * Set the sales organization for the document.
	 *
	 * @param salesOrg
	 *           the sales organization.
	 */
	void setSalesOrg(String salesOrg);

	/**
	 * Set the shipping conditions for the document.
	 *
	 * @param shipCond
	 *           the shipping conditions
	 */
	void setShipCond(String shipCond);

	/**
	 * Set the taxes that have to be paid for the document.
	 *
	 * @param taxValue
	 *           the taxes to be set
	 */
	void setTaxValue(BigDecimal taxValue);

	/**
	 * sets the total discount, which is used for strike through prices
	 *
	 * @param totalDiscount
	 *           totalDiscount as BigDecimal
	 */
	void setTotalDiscount(BigDecimal totalDiscount);

	/**
	 * Get the gross price without freight.
	 *
	 * @return the gross price without freight
	 */
	BigDecimal getGrossValueWOFreight();

	/**
	 * Set the gross value without freight.
	 *
	 * @param grossValueWOFreight
	 *           the price to be set
	 */
	void setGrossValueWOFreight(BigDecimal grossValueWOFreight);

	/**
	 * Sets the date, the document is valid from.
	 *
	 * @param validFrom
	 *           the validFrom date to be set.
	 */
	void setValidFrom(Date validFrom);

	/**
	 * Sets the date, the document is valid to.
	 *
	 * @param validTo
	 *           the validTo date to be set.
	 */
	void setValidTo(Date validTo);



}