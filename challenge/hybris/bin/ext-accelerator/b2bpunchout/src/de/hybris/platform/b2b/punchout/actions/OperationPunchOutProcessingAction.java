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

import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.b2b.punchout.services.impl.AsymmetricManager;
import de.hybris.platform.b2b.punchout.services.impl.SymmetricManager;

import java.util.Date;

import org.cxml.CXML;
import org.springframework.beans.factory.annotation.Required;


public class OperationPunchOutProcessingAction implements PunchOutProcessingAction<CXML, CXML>
{
	private NewSessionPunchOutProcessingAction newSessionPunchOutProcessingAction;

	private PunchOutSessionService punchoutSessionService;

	@Override
	public void process(final CXML input, final CXML output)
	{
		final PunchOutSession punchoutSession = new PunchOutSession();
		punchoutSession.setTime(new Date());
		
		final String salt = AsymmetricManager.getSalt();
		final String key = SymmetricManager.getKey();
		punchoutSession.setSalt(salt);
		punchoutSession.setKey(key);

		newSessionPunchOutProcessingAction.process(input, punchoutSession);

		punchoutSessionService.activate(punchoutSession);
	}

	public NewSessionPunchOutProcessingAction getNewSessionPunchOutProcessingAction()
	{
		return newSessionPunchOutProcessingAction;
	}

	@Required
	public void setNewSessionPunchOutProcessingAction(final NewSessionPunchOutProcessingAction newSessionPunchOutProcessingAction)
	{
		this.newSessionPunchOutProcessingAction = newSessionPunchOutProcessingAction;
	}

	public PunchOutSessionService getPunchOutSessionService()
	{
		return punchoutSessionService;
	}

	@Required
	public void setPunchOutSessionService(final PunchOutSessionService punchoutSessionService)
	{
		this.punchoutSessionService = punchoutSessionService;
	}
}
