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
package de.hybris.platform.chinesetaxinvoiceaddon.forms;



public class TaxInvoiceForm
{

	private String id;

	private String recipient;

	private String category;

	private String recipientType;

	private boolean invoiceRequired;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getRecipient()
	{
		return recipient;
	}

	public void setRecipient(String recipient)
	{
		this.recipient = recipient;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getRecipientType()
	{
		return recipientType;
	}

	public void setRecipientType(String recipientType)
	{
		this.recipientType = recipientType;
	}

	public boolean isInvoiceRequired()
	{
		return invoiceRequired;
	}

	public void setInvoiceRequired(boolean invoiceRequired)
	{
		this.invoiceRequired = invoiceRequired;
	}


}
