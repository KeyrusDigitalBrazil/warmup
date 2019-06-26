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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Parser for the file registered in the metadata file under the type rfc_messages
 *
 * @see MessageMappingRulesContainerImpl
 */

public class MessageMappingRulesParserImpl extends DefaultHandler
{

	/**
	 * Represents a tag
	 */
	public static final class TAG
	{
		static final String GLOBAL = "global_config";
		static final String MESSAGE = "message";
		static final String SOURCE = "source";
		static final String DESTINATION = "destination";
		static final String HIDE = "hide";

		private TAG()
		{

		}
	}

	/**
	 * Represents an attribute
	 */
	public static final class ATTRIBUTE
	{
		static final String HIDE_NON_ERROR_MESSAGES = "hide_non_error_messages";
		static final String ID = "id";
		static final String NUMBER = "number";
		static final String MSGV1 = "msgV1";
		static final String MSGV2 = "msgV2";
		static final String MSGV3 = "msgV3";
		static final String MSGV4 = "msgV4";
		static final String SEVERITY = "type";
		static final String RES_KEY_SHORT = "resource_key";
		static final String CALLBACKID = "callbackId";
		/**
		 * Process step (Checkout?)
		 */
		public static final String PROCESSSTEP = "process_step";

		private ATTRIBUTE()
		{

		}
	}

	/**
	 * Are we processing a message?
	 */
	protected boolean inMessage = false;

	/**
	 * Hide info or warning messages?
	 */
	@SuppressWarnings("squid:ClassVariableVisibilityCheck")
	public boolean hideNonErrorMsg = false;
	/**
	 * List of rules as result of the parse process
	 */
	@SuppressWarnings("squid:S2386")
	public List<MessageMappingRule> rulesList = new ArrayList<>();

	/**
	 * Search pattern
	 */
	protected MessageMappingRule.Pattern pattern;
	/**
	 * Result of mapping process
	 */
	protected MessageMappingRule.Result result;

	/**
	 * Error message ID: tag inside a tag
	 */
	protected static final String ERROR_TAG_OUTSIDE_MESAGE_TAG = "Tag not inside tag " + TAG.MESSAGE;

	@Override
	public void startElement(final String nsURI, final String stripName, final String tagName, final Attributes attrs)
			throws SAXException
	{

		if (stripName.equals(TAG.GLOBAL))
		{
			hideNonErrorMsg = Boolean.parseBoolean(attrs.getValue(ATTRIBUTE.HIDE_NON_ERROR_MESSAGES));
		}
		else if (stripName.equals(TAG.MESSAGE))
		{
			inMessage = true;
		}
		else if (stripName.equals(TAG.SOURCE))
		{
			checkInMessageFlag();
			pattern = buildPattern(attrs);

		}
		else if (stripName.equals(TAG.DESTINATION))
		{
			checkInMessageFlag();
			result = buildMapResult(attrs);
		}
		else if (stripName.equals(TAG.HIDE))
		{
			checkInMessageFlag();
			result = buildHideResult();
		}
	}

	/**
	 * Method to check inMessage flag
	 *
	 * @throws SAXException
	 */
	private void checkInMessageFlag() throws SAXException
	{
		if (!inMessage)
		{
			throw new SAXException(ERROR_TAG_OUTSIDE_MESAGE_TAG);
		}
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException
	{
		if (localName.equals(TAG.MESSAGE))
		{
			inMessage = false;
			if (pattern == null)
			{
				throw new SAXException("No pattern in rule");
			}
			if (result == null)
			{
				throw new SAXException("No result in rule " + pattern);
			}
			rulesList.add(buildRule());
		}
	}

	/**
	 * Create a pattern
	 *
	 * @param attrs
	 * @return Search pattern
	 */
	protected MessageMappingRule.Pattern buildPattern(final Attributes attrs)
	{
		return new MessageMappingRule.Pattern(attrs.getValue(ATTRIBUTE.ID), attrs.getValue(ATTRIBUTE.NUMBER),
				attrs.getValue(ATTRIBUTE.SEVERITY), attrs.getValue(ATTRIBUTE.MSGV1), attrs.getValue(ATTRIBUTE.MSGV2),
				attrs.getValue(ATTRIBUTE.MSGV3), attrs.getValue(ATTRIBUTE.MSGV4));

	}

	/**
	 * Create a mapping result expression
	 *
	 * @param attrs
	 * @return Mapping result expression
	 */
	protected MessageMappingRule.Result buildMapResult(final Attributes attrs)
	{
		final String severity = attrs.getValue(ATTRIBUTE.SEVERITY);
		final String callbackId = attrs.getValue(ATTRIBUTE.CALLBACKID);
		final String processStep = attrs.getValue(ATTRIBUTE.PROCESSSTEP);
		final Character charSeverity = severity != null && !severity.isEmpty() ? Character.valueOf(severity.charAt(0)) : null;

		return new MessageMappingRule.Result(charSeverity, attrs.getValue(ATTRIBUTE.RES_KEY_SHORT), callbackId, processStep);
	}

	/**
	 * Create a mapping rule
	 *
	 * @return Mapping rule (pattern, result expression)
	 */
	protected MessageMappingRule buildRule()
	{
		return new MessageMappingRule(pattern, result);
	}

	/**
	 * Create a mapping rule result which states that message is hidden
	 *
	 * @return Result expression
	 */
	protected MessageMappingRule.Result buildHideResult()
	{
		return new MessageMappingRule.Result(Boolean.TRUE);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();

		sb.append(ATTRIBUTE.HIDE_NON_ERROR_MESSAGES).append(" = ").append(hideNonErrorMsg).append('\n');

		for (final MessageMappingRule rule : rulesList)
		{
			sb.append(rule).append('\n');
		}
		return sb.toString();
	}

}
