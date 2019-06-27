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
package de.hybris.platform.messagecentercsoccaddon.constants;

public class ErrorConstants
{

	public static final String NOT_EXIST_ERROR = "523";

	public static final String NOT_EXIST_MESSAGE = "The conversation does not exist.";

	public static final String ALREADY_ASSIGNED_ERROR = "524";

	public static final String ALREADY_ASSIGNED_MESSAGE = "The conversation has already been picked.";

	public static final String ALREADY_CLOSED_ERROR = "525";

	public static final String ALREADY_CLOSED_MESSAGE = "The conversation has already been closed.";

	public static final String NO_ACCESS_ERROR = "526";

	public static final String NO_ACCESS_MESSAGE = "You are not authorized to process this conversation.";

	public static final String MESSAGE_SAVE_ERROR = "527";

	public static final String MESSAGE_SAVE_MESSAGE = "Saving error in the database, please contact the administrator for support.";

	public static final String AGENT_CREATE_CONVERSATION_ERROR = "528";

	public static final String AGENT_CREATE_CONVERSATION_MESSAGE = "Customer Support Agent is not allowed to create a conversation.";

	public static final String STATUS_INVALID_ERROR = "529";

	public static final String STATUS_INVALID_MESSAGE = "The status should be open or unassigned.";
	
	private ErrorConstants()
	{
		//empty to avoid instantiating this constant class
	}
}
