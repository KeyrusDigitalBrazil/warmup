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
package de.hybris.platform.bmecat.parser.taglistener;

/**
 * XMLContentLogger
 * 
 * 
 * @deprecated will be replaced by {@link de.hybris.bootstrap.xml.XMLContentLogger}
 */
@Deprecated
public interface XMLContentLogger
{
	public void setXML(String xml);

	public String getXML();
}
