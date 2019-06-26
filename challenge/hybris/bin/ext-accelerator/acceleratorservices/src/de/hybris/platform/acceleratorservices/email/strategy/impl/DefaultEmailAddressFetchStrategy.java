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
package de.hybris.platform.acceleratorservices.email.strategy.impl;

import de.hybris.platform.acceleratorservices.email.dao.EmailAddressDao;
import de.hybris.platform.acceleratorservices.email.strategy.EmailAddressFetchStrategy;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.impl.UniqueAttributesInterceptor;
import de.hybris.platform.servicelayer.internal.model.impl.DefaultModelService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.tx.DefaultTransaction;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;
import de.hybris.platform.util.Utilities;

import java.sql.Connection;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default strategy for fetching EmailAddressModel for given address. The implementation executes in a transaction which
 * uses fetch-create if needed-fetch approach which is important in multi threaded environment.
 *
 * @see de.hybris.platform.acceleratorservices.model.email.EmailAddressModel
 */
public class DefaultEmailAddressFetchStrategy implements EmailAddressFetchStrategy
{
	private EmailAddressDao emailAddressDao;
	private ModelService modelService;

	private static final Logger LOG = Logger.getLogger(DefaultEmailAddressFetchStrategy.class);

	@Override
	public EmailAddressModel fetch(final String emailAddress, final String displayName)
	{
		ServicesUtil.validateParameterNotNull(emailAddress, "emailAddress must not be null");
		try
		{
			final Transaction tx = Transaction.current();
			tx.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
			final boolean txRollbackOnlyBefore = tx.isRollbackOnly();

			return (EmailAddressModel) tx.execute(new TransactionBody() // NOSONAR
			{
				@Override
				public Object execute()
				{
					final JaloSession session = JaloSession.getCurrentSession();
					try
					{
						disableNestedTransactions(session.createLocalSessionContext());

						EmailAddressModel emailAddressModel = loadEmailAddressFromDatabase(emailAddress, displayName);
						if (emailAddressModel == null)
						{
							emailAddressModel = handleEmailAddressCreation(emailAddress, displayName, tx, txRollbackOnlyBefore);
						}
						return emailAddressModel;
					}
					finally
					{
						session.removeLocalSessionContext();
					}
				}

			});
		}
		catch (final Exception e)
		{
			throw new SystemException("Could not find email address for email: " + emailAddress + " and name: " + displayName, e);
		}
	}

	protected EmailAddressModel loadEmailAddressFromDatabase(final String emailAddress, final String displayName)
	{
		return getEmailAddressDao().findEmailAddressByEmailAndDisplayName(emailAddress, displayName);
	}

	protected EmailAddressModel handleEmailAddressCreation(final String emailAddress, final String displayName,
			final Transaction tx, final boolean txRollbackOnlyBefore)
	{
		EmailAddressModel emailAddressModel;
		try
		{
			emailAddressModel = getModelService().create(EmailAddressModel.class);
			emailAddressModel.setEmailAddress(emailAddress);
			emailAddressModel.setDisplayName(displayName);
			getModelService().save(emailAddressModel);
		}
		catch (final ModelSavingException e)
		{
			if (isIgnorableConstraintViolationException(e))
			{
				emailAddressModel = loadEmailAddressFromDatabase(emailAddress, displayName);
				if (emailAddressModel == null)
				{
					emailAddressModel = handleMissingEmailAfterInsertConflict(e, emailAddress, displayName);
				}
				// PLA-11093
				// reset rollback-only here in case it had not been set before ( otherwise our successful retry would be lost! )
				if (!txRollbackOnlyBefore && tx.isRollbackOnly())
				{
					((DefaultTransaction) tx).clearRollbackOnly();
				}
			}
			else
			{
				throw new IllegalStateException(ModelSavingException.class.getName() + " recognized as unrecoverable.", e);
			}
		}
		return emailAddressModel;
	}

	protected boolean isIgnorableConstraintViolationException(final Exception exception)
	{
		if (isInsertConditionException(exception))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Could not schedule task! The " + exception.getClass().getSimpleName() + " has occurred with message: '"
						+ exception.getMessage() + "'. Now attempting to run the transaction again!");
			}
			return true;
		}
		else if (exception instanceof RuntimeException)
		{
			throw (RuntimeException) exception;
		}
		else
		{
			throw new SystemException(exception);
		}
	}

	protected boolean isInsertConditionException(final Exception exception)
	{
		return getModelService().isUniqueConstraintErrorAsRootCause(exception)
				|| (Utilities.getRootCauseOfType(exception, InterceptorException.class) != null
						&& (((InterceptorException) Utilities.getRootCauseOfType(exception, InterceptorException.class))
								.getInterceptor() instanceof UniqueAttributesInterceptor));
	}

	protected void disableNestedTransactions(final SessionContext loclCtx)
	{
		loclCtx.setAttribute(SessionContext.TRANSACTION_IN_CREATE_DISABLED, Boolean.TRUE);
		loclCtx.setAttribute(DefaultModelService.ENABLE_TRANSACTIONAL_SAVES, Boolean.FALSE);
	}

	protected EmailAddressDao getEmailAddressDao()
	{
		return emailAddressDao;
	}

	@Required
	public void setEmailAddressDao(final EmailAddressDao emailAddressDao)
	{
		this.emailAddressDao = emailAddressDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Tries to fetch the given email address 10 times with 10 ms interval. It tries to recover from an exception thrown
	 * scenario where multi threads tried to insert the same row to the database.
	 *
	 * @param e
	 * @param emailAddress
	 * @param displayName
	 * @return addr the EmailAddressModel
	 */
	protected EmailAddressModel handleMissingEmailAfterInsertConflict(final ModelSavingException e, final String emailAddress,
			final String displayName)
	{
		for (int retries = 0; retries < 10; retries++)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (final InterruptedException e1)
			{
				Thread.currentThread().interrupt(); // restore flag
				break;
			}
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Retrying to lookup " + emailAddress + "/" + displayName + " - count = " + retries);
			}
			final EmailAddressModel addr = loadEmailAddressFromDatabase(emailAddress, displayName);
			if (addr != null)
			{
				return addr;
			}
		}
		logDebugInfo(e, "Could neither create nor load email address. Special handling was re-trying "
				+ "for 1 second (10 times). Ignorable exception was:");
		throw new IllegalStateException(
				"Could neither create nor load email address, even with special handling " + "(retrying lookup for 1 second).");
	}

	protected void logDebugInfo(final ModelSavingException e, final String message)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(message, e);
		}
	}
}
