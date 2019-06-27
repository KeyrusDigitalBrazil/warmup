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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.messagemapping;

import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRule;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping.MessageMappingRulesContainerImpl.Key;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;


/**
 *
 */
public interface MessageMappingRulesLoader
{

	/**
	 * @return Map of message mapping rules
	 * @throws SAXException
	 * @throws IOException
	 */
	@SuppressWarnings("squid:S1160")
	Map<Key, List<MessageMappingRule>> loadRules() throws SAXException, IOException;

	/**
	 * @return true if non error messages should be hidden
	 */
	boolean isHideNonErrorMsg();

}
