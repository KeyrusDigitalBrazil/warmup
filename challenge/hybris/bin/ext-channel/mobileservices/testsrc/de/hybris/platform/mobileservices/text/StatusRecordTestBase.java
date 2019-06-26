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
package de.hybris.platform.mobileservices.text;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.mobileservices.enums.MobileMessageError;
import de.hybris.platform.mobileservices.enums.MobileMessageStatus;
import de.hybris.platform.mobileservices.model.text.MobileMessageContextModel;
import de.hybris.platform.mobileservices.text.engine.IncomingSMSMessageDTO;
import de.hybris.platform.mobileservices.text.engine.IncomingSMSMessageGateway;
import de.hybris.platform.mobileservices.text.testimpl.TestStatusRecord;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.TaskEngine;
import de.hybris.platform.task.TaskService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Ignore;


/**
 *
 */
@Ignore
public abstract class StatusRecordTestBase extends ServicelayerTest
{
	//private static final Logger LOG = Logger.getLogger(StatusRecordTestBase.class.getName());

	@Resource
	protected ModelService modelService;
	@Resource
	private IncomingSMSMessageGateway incomingSMSMessageGateway;
	/** The short url service. */
	@Resource
	private TaskService taskService;

	protected final MobileMessageStatus assertSent = MobileMessageStatus.SENT;
	protected final MobileMessageError assertNoError = null;
	protected final Boolean assertIsNotDefaultAction = Boolean.FALSE;
	protected final Boolean assertIsDefaultAction = Boolean.TRUE;

	@Before
	public void removePreviousTestConfig() throws Exception
	{
		JaloSession.getCurrentSession().setUser(UserManager.getInstance().getAdminEmployee());
		importCsv("/testdata/cleanup.csv", "UTF-8");
		TestStatusRecord.reset();

		assertTaskEngineRunning();
	}

	protected MobileMessageContextModel getMessage(final String pk)
	{
		final MobileMessageContextModel ctx = modelService.get(PK.parse(pk));
		modelService.refresh(ctx);
		return ctx;
	}

	protected void startMonitor()
	{
		TestStatusRecord.reset();
	}

	protected MobileMessageStatus endMonitor()
	{
		return TestStatusRecord.waitForAnyMessage(30000);
	}


	protected MobileMessageStatus blockUsingRecord(final PK pk)
	{
		return blockUsingModel(pk.toString());
	}

	protected MobileMessageStatus blockUsingRecord(final String pk)
	{
		for (int i = 0; i < 200; i++)
		{
			final MobileMessageStatus status = TestStatusRecord.query(pk);
			if (status != null
					&& (status.equals(MobileMessageStatus.SENT) || status.equals(MobileMessageStatus.DISCARDED) || status
							.equals(MobileMessageStatus.ERROR)))
			{
				return status;
			}

			try
			{
				Thread.sleep(50);
			}
			catch (final InterruptedException x) 
			{
				//NO SILENTLY SWALLOWED EXCEPTION ALLOWED (if this is too radical, at least LOG the exception)!
				throw new RuntimeException(x);
			}
		}

		//Nothing found, return a value that uniquely indicates this outcome
		return null;

	}

	protected MobileMessageStatus blockUsingModel(final String pk)
	{
		return blockUsingModel(pk, 50);
	}

	protected MobileMessageStatus blockUsingModel(final String pk, final int waitTimeSeconds)
	{
		final int waitTimePerTurnMs = 50;
		final int turns = (waitTimeSeconds * 1000) / waitTimePerTurnMs;

		for (int i = 0; i < turns; i++)
		{
			final MobileMessageContextModel msg = getMessage(pk);
			final MobileMessageStatus status = msg.getStatus();
			if (status != null
					&& (status.equals(MobileMessageStatus.SENT) || status.equals(MobileMessageStatus.DISCARDED) || status
							.equals(MobileMessageStatus.ERROR)))
			{
				return status;
			}
			try
			{
				Thread.sleep(waitTimePerTurnMs);
			}
			catch (final InterruptedException x) 
			{
				//NO SILENTLY SWALLOWED EXCEPTION ALLOWED (if this is too radical, at least LOG the exception)!
				throw new RuntimeException(x);
			}
		}

		//Nothing found, return a value that uniquely indicates this outcome
		return null;

	}

	protected String messageReceived(final String shortcodeCountryIsocode, final String phoneCountryIsocode,
			final String shortcode, final String phone, final String text)
	{
		final IncomingSMSMessageDTO dto = new IncomingSMSMessageDTO();

		dto.setPhoneCountryIsoCode(phoneCountryIsocode);
		dto.setPhoneNumber(phone);

		dto.setShortCodeCountryIsoCode(shortcodeCountryIsocode);
		dto.setShortcode(shortcode);

		dto.setContent(text);

		final PK pk = incomingSMSMessageGateway.messageReceived("testEngine", dto);

		return pk != null ? pk.toString() : null;
	}

	protected boolean isTextServiceIllegalStateReplyFailure(final MobileMessageContextModel message)
	{
		modelService.refresh(message);

		return MessageTestingUtilities.isTextServiceIllegalStateReplyFailure(message);
	}

	protected boolean isTextServiceIllegalStateReplyFailure(final String pk)
	{

		return isTextServiceIllegalStateReplyFailure(getMessage(pk));
	}

	private void assertTaskEngineRunning()
	{
		taskService.getEngine().start();
		final TaskEngine engine = taskService.getEngine();
		// check state
		assertTrue("Task engine is running", engine.isRunning());

	}

	protected MobileMessageContextModel waitForMessageToBeProcessed(final String pk)
	{
		for (int i = 0; i < 1000; i++)
		{
			final MobileMessageContextModel msg = getMessage(pk);
			final MobileMessageStatus status = msg.getStatus();
			if (status != null
					&& (status.equals(MobileMessageStatus.SENT) || status.equals(MobileMessageStatus.DISCARDED) || status
							.equals(MobileMessageStatus.ERROR)))
			{
				return msg;
			}


			try
			{
				Thread.sleep(50);
			}
			catch (final InterruptedException x) 
			{
				//NO SILENTLY SWALLOWED EXCEPTION ALLOWED (if this is too radical, at least LOG the exception)!
				throw new RuntimeException(x);
			}
		}

		//Nothing found, return a value that uniquely indicates this outcome
		return null;
	}

	/**
	 * Checks that the message successfully completes with the required status and error parameters.
	 * @param message
	 * @param expectedStatus the desired status, cannot be null
	 * @param expectedError the expected error code. Null means "no error"
	 * @param isDefaultAction
	 */
	protected void assertMessageSuccessfulyProcessed(final MobileMessageContextModel message,
			final MobileMessageStatus expectedStatus, final MobileMessageError expectedError, final Boolean isDefaultAction)
	{
		final String actionName = (message.getMatchedAction() != null) ? message.getMatchedAction().getCode() : "unknown";
		// Check if the reply couldn't be send because the status was not in PROCESSING
		assertFalse("We are in BAM-390 scenario", isTextServiceIllegalStateReplyFailure(message));
		assertEquals("Expected action " + actionName + " status mismatch", expectedStatus, message.getStatus());
		assertEquals("Expected action " + actionName + " error indicator mismatch", expectedError, message.getMessageError());
		assertEquals("Expected action " + actionName + " isDefaultAction mismatch",
				isDefaultAction, message.getUsingDefaultAction());
	}

	protected void assertMessageSuccessfulyProcessed(final MobileMessageContextModel message,
			final MobileMessageStatus expectedStatus)
	{
		assertMessageSuccessfulyProcessed(message, expectedStatus, null, Boolean.FALSE);
	}

	protected void assertMessageSuccessfulyProcessed(final MobileMessageContextModel message)
	{
		assertMessageSuccessfulyProcessed(message, MobileMessageStatus.SENT, null, Boolean.FALSE);
	}

}
