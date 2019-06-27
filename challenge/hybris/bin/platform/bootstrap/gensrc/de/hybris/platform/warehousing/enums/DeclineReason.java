/*
 *  
 * [y] hybris Platform
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.warehousing.enums;

import de.hybris.platform.core.HybrisEnumValue;

/**
 * Generated enum DeclineReason declared at extension warehousing.
 */
@SuppressWarnings("PMD")
public enum DeclineReason implements HybrisEnumValue
{
	/**
	 * Generated enum value for DeclineReason.Damaged declared at extension warehousing.
	 */
	DAMAGED("Damaged"),
	/**
	 * Generated enum value for DeclineReason.OutOfStock declared at extension warehousing.
	 */
	OUTOFSTOCK("OutOfStock"),
	/**
	 * Generated enum value for DeclineReason.StoreClosed declared at extension warehousing.
	 */
	STORECLOSED("StoreClosed"),
	/**
	 * Generated enum value for DeclineReason.TooBusy declared at extension warehousing.
	 */
	TOOBUSY("TooBusy"),
	/**
	 * Generated enum value for DeclineReason.AsnCancellation declared at extension warehousing.
	 */
	ASNCANCELLATION("AsnCancellation"),
	/**
	 * Generated enum value for DeclineReason.Other declared at extension warehousing.
	 */
	OTHER("Other");
	 
	/**<i>Generated model type code constant.</i>*/
	public final static String _TYPECODE = "DeclineReason";
	
	/**<i>Generated simple class name constant.</i>*/
	public final static String SIMPLE_CLASSNAME = "DeclineReason";
	
	/** The code of this enum.*/
	private final String code;
	
	/**
	 * Creates a new enum value for this enum type.
	 *  
	 * @param code the enum value code
	 */
	private DeclineReason(final String code)
	{
		this.code = code.intern();
	}
	
	
	/**
	 * Gets the code of this enum value.
	 *  
	 * @return code of value
	 */
	@Override
	public String getCode()
	{
		return this.code;
	}
	
	/**
	 * Gets the type this enum value belongs to.
	 *  
	 * @return code of type
	 */
	@Override
	public String getType()
	{
		return SIMPLE_CLASSNAME;
	}
	
}
