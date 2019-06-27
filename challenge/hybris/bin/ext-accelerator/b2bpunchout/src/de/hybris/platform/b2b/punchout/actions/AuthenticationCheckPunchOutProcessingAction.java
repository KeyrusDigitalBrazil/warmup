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
package de.hybris.platform.b2b.punchout.actions;

import org.cxml.CXML;


/**
 * This class will perform an authentication coming from the punch out system. The shared secret will be compared with
 * the configured shared secret. No cXML will be created, the only purpose is to authenticate the user.
 */
public class AuthenticationCheckPunchOutProcessingAction implements PunchOutProcessingAction<CXML, CXML>
{
	private AuthenticationVerifier punchoutAuthenticationVerifier;

	@Override
	public void process(final CXML input, final CXML output)
	{
		punchoutAuthenticationVerifier.verify(input);
	}

	public AuthenticationVerifier getPunchOutAuthenticationVerifier()
	{
		return punchoutAuthenticationVerifier;
	}

	public void setPunchOutAuthenticationVerifier(final AuthenticationVerifier punchoutAuthenticationVerifier)
	{
		this.punchoutAuthenticationVerifier = punchoutAuthenticationVerifier;
	}

}
