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

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.cxml.Credential;
import org.cxml.Header;
import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link AuthenticationVerifier}.
 */
@UnitTest
public class AuthenticationVerifierTest
{

	private CXML requestXML;

	@Before
	public void prepare() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutSetupRequest.xml");
	}

	@Test
	public void shouldRetrieveTheIdentityFromRequest()
	{
		final Header header = (Header) requestXML.getHeaderOrMessageOrRequestOrResponse().get(0);
		final Credential credential = header.getSender().getCredential().iterator().next();

		final String login = extractIdentity(credential);
		assertEquals("sysadmin@ariba.com", login);
	}


	private String extractIdentity(final Credential credential)
	{
		String login = null;
		for (final Object obj : credential.getIdentity().getContent())
		{
			if (obj instanceof String)
			{
				login = (String) obj;
			}
		}
		return login;
	}

}
