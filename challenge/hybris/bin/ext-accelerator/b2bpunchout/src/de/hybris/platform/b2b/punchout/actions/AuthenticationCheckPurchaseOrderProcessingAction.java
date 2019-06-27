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

import de.hybris.platform.core.model.order.CartModel;

import org.cxml.CXML;


/**
 * Verifies the authentication for the given input {@link CXML}.
 */
public class AuthenticationCheckPurchaseOrderProcessingAction implements PunchOutProcessingAction<CXML, CartModel>
{

	private AuthenticationVerifier punchoutAuthenticationVerifier;

	@Override
	public void process(final CXML input, final CartModel output)
	{
		getPunchOutAuthenticationVerifier().verify(input);
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
