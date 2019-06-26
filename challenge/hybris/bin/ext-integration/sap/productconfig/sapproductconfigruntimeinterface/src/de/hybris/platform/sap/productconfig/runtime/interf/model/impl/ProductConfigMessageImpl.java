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

import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessagePromoType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;

import java.util.Date;
import java.util.Objects;



/**
 * A Message class implementation for CPQ<br>
 * <b>This class is immutable.</b>
 */
public class ProductConfigMessageImpl implements ProductConfigMessage
{

	private String message;
	private String key;
	private ProductConfigMessageSeverity severity;
	private ProductConfigMessageSource source;
	private ProductConfigMessageSourceSubType subType;
	private ProductConfigMessagePromoType promoType;
	private String extendedMessage;
	private Date endDate;

	ProductConfigMessageImpl()
	{

	}


	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public String getMessage()
	{
		return message;
	}


	@Override
	public ProductConfigMessageSource getSource()
	{
		return source;
	}


	@Override
	public ProductConfigMessageSeverity getSeverity()
	{
		return severity;
	}

	@Override
	public ProductConfigMessageSourceSubType getSourceSubType()
	{
		return subType;
	}

	@Override
	public String getExtendedMessage()
	{
		return extendedMessage;
	}

	@Override
	public Date getEndDate()
	{
		return endDate;
	}

	@Override
	public ProductConfigMessagePromoType getPromoType()
	{
		return promoType;
	}


	public ProductConfigMessageSourceSubType getSubType()
	{
		return subType;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(key, message, source);
	}



	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final ProductConfigMessageImpl other = (ProductConfigMessageImpl) obj;
		if (key == null)
		{
			if (other.key != null)
			{
				return false;
			}
		}
		else if (!key.equals(other.key))
		{
			return false;
		}
		if (message == null)
		{
			if (other.message != null)
			{
				return false;
			}
		}
		else if (!message.equals(other.message))
		{
			return false;
		}

		return source == other.source;
	}


	void setMessage(final String message)
	{
		this.message = message;
	}


	void setKey(final String key)
	{
		this.key = key;
	}


	void setSeverity(final ProductConfigMessageSeverity severity)
	{
		this.severity = severity;
	}


	void setSource(final ProductConfigMessageSource source)
	{
		this.source = source;
	}


	void setSubType(final ProductConfigMessageSourceSubType subType)
	{
		this.subType = subType;
	}


	void setPromoType(final ProductConfigMessagePromoType promoType)
	{
		this.promoType = promoType;
	}


	void setExtendedMessage(final String extendedMessage)
	{
		this.extendedMessage = extendedMessage;
	}


	void setEndDate(final Date endDate)
	{
		this.endDate = endDate;
	}

}
