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
package de.hybris.platform.commerceservices.organization.services.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.daos.OrgUnitDao;
import de.hybris.platform.commerceservices.organization.services.OrgUnitHierarchyService;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.util.CommerceSearchUtils;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;
import de.hybris.platform.util.Config;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Stopwatch;


/**
 * Default implementation of the {@link OrgUnitHierarchyService} interface.
 */
public class DefaultOrgUnitHierarchyService implements OrgUnitHierarchyService
{
	/**
	 * Character used as a delimiter between unit UIDs in the path.
	 */
	public static final char DELIMITER = '/';

	private static final Logger LOG = LoggerFactory.getLogger(OrgUnitHierarchyService.class);

	private OrgUnitDao orgUnitDao;
	private SessionService sessionService;
	private ModelService modelService;

	/**
	 * {@inheritDoc}
	 *
	 * <br>
	 * <br>
	 * Note: The default implementations of {@link #generateUnitPaths(Class)} and
	 * {@link #saveChangesAndUpdateUnitPath(OrgUnitModel)} are <code>synchronized</code> in order to avoid inconsistent
	 * path values in case two threads try to access one of the methods at the same time.
	 */
	@Override
	synchronized public <T extends OrgUnitModel> void generateUnitPaths(final Class<T> unitType)
	{
		validateParameterNotNullStandardMessage("unitType", unitType);

		LOG.info("Generating path values for {}s.", unitType.getSimpleName());
		final Stopwatch stopWatch = Stopwatch.createStarted();

		final List<OrgUnitModel> rootUnits = getOrgUnitDao().findRootUnits(unitType,
				CommerceSearchUtils.getAllOnOnePagePageableData());

		// Re-use existing transaction or execute logic in a new one
		if (Transaction.current().isRunning())
		{
			for (final OrgUnitModel rootUnit : rootUnits)
			{
				LOG.debug("Processing root unit [{}]", rootUnit.getUid());
				updateUnitPath(rootUnit);
			}
		}
		else
		{
			final Transaction tx = Transaction.current();
			tx.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
			try
			{
				tx.execute(new TransactionBody()
				{
					@Override
					public Object execute() throws Exception
					{
						for (final OrgUnitModel rootUnit : rootUnits)
						{
							LOG.debug("Processing root unit [{}]", rootUnit.getUid());

							updateUnitPath(rootUnit);
						}
						return null;
					}

				});
			}
			catch (final Exception e)
			{
				handleTransactionException(e);
			}
		}

		stopWatch.stop();
		LOG.info("Generating path values for {}s took {} ms.", unitType.getSimpleName(),
				Long.valueOf(stopWatch.elapsed(TimeUnit.MILLISECONDS)));
	}

	/**
	 * {@inheritDoc}
	 *
	 * <br>
	 * <br>
	 * Note: The default implementations of {@link #generateUnitPaths(Class)} and
	 * {@link #saveChangesAndUpdateUnitPath(OrgUnitModel)} are <code>synchronized</code> in order to avoid inconsistent
	 * path values in case two threads try to access one of the methods at the same time.
	 */
	@Override
	synchronized public void saveChangesAndUpdateUnitPath(final OrgUnitModel unit)
	{
		validateParameterNotNullStandardMessage("unit", unit);

		final Transaction tx = Transaction.current();
		tx.setTransactionIsolationLevel(Connection.TRANSACTION_READ_COMMITTED);
		try
		{
			tx.execute(new TransactionBody()
			{
				@Override
				public Object execute() throws Exception
				{
					getModelService().save(unit);

					try
					{
						updateUnitPath(unit);
					}
					catch (final OrgUnitHierarchyException e)
					{
						LOG.error(String.format(
								"Update of path values for the branch of unit [%s] failed. Trying to regenerate the paths for all items of type %s.",
								unit.getUid(), unit.getClass().getSimpleName()), e);

						// re-generate all paths values as a fallback
						generateUnitPaths(unit.getClass());
					}

					return null;
				}
			});
		}
		catch (final Exception e)
		{
			handleTransactionException(e);
		}
	}

	protected void updateUnitPath(final OrgUnitModel unit)
	{
		// execute in local view to prevent search restrictions
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final OrgUnitModel parentUnit = getParentUnit(unit);
				final Set<OrgUnitModel> unitsToSave = generatePathForUnit(unit, parentUnit);
				for (final OrgUnitModel unitToSave : unitsToSave)
				{
					try
					{
						getModelService().save(unitToSave);
					}
					catch (final Exception e)
					{
						LOG.error("Error when saving unit:" + unit.getUid(), e);
					}
				}
			}
		});
	}

	protected Set<OrgUnitModel> generatePathForUnit(final OrgUnitModel unit, final OrgUnitModel parentUnit)
	{
		beforeUpdate(unit, parentUnit);

		final Set<OrgUnitModel> unitsToSave = new HashSet<>();
		final StringBuilder pathBuilder = new StringBuilder();

		if (parentUnit != null)
		{
			validatePath(parentUnit);
			pathBuilder.append(parentUnit.getPath());
		}
		pathBuilder.append(DELIMITER).append(unit.getUid());
		final String path = pathBuilder.toString();
		unit.setPath(path);
		unitsToSave.add(unit);

		LOG.debug("Path for unit [{}]: {}", unit.getUid(), path);

		// Recursively execute logic for all immediate children of the same type
		for (final OrgUnitModel child : getChildrenOfSameType(unit))
		{
			try
			{
				unitsToSave.addAll(generatePathForUnit(child, unit));
			}
			catch (final Exception e)
			{
				LOG.error("Error when generating path for child of unit:" + unit.getUid() + ", and child unit:" + child.getUid(), e);
			}
		}

		return unitsToSave;
	}

	protected void beforeUpdate(final OrgUnitModel unit, final OrgUnitModel parentUnit)
	{
		if (!getModelService().isNew(unit))
		{
			// lock the row to ensure consistency
			if (!Config.isHSQLDBUsed())
			{
				getModelService().lock(unit.getPk());
			}

			getModelService().refresh(unit);

			// check whether the parent unit was changed in the meantime
			final OrgUnitModel refreshedParentUnit = getParentUnit(unit);
			if (parentUnit != null && !parentUnit.equals(refreshedParentUnit) || parentUnit == null && refreshedParentUnit != null)
			{
				throw new OrgUnitHierarchyException(String.format("The parent unit of [%s] has been modified", unit.getUid()));
			}
		}
	}

	protected void validatePath(final OrgUnitModel parentUnit)
	{
		if (StringUtils.isEmpty(parentUnit.getPath())
				|| !StringUtils.endsWith(parentUnit.getPath(), DELIMITER + parentUnit.getUid()))
		{
			throw new OrgUnitHierarchyException(
					String.format("The path of parent unit [%s] has not been generated or is corrupt", parentUnit.getUid()));
		}
	}

	protected OrgUnitModel getParentUnit(final OrgUnitModel unit)
	{
		final Set<PrincipalGroupModel> groups = new HashSet<>(
				(unit.getGroups() != null ? unit.getGroups() : Collections.emptySet()));

		CollectionUtils.filter(groups, parent -> parent.getClass().equals(unit.getClass()));

		if (groups.size() > 1)
		{
			throw new OrgUnitHierarchyException(String.format("The unit [%s] has more then one parent", unit.getUid()));
		}

		return CollectionUtils.isEmpty(groups) ? null : (OrgUnitModel) groups.iterator().next();
	}

	protected Set<OrgUnitModel> getChildrenOfSameType(final OrgUnitModel unit)
	{
		if (getModelService().isNew(unit))
		{
			return Collections.emptySet();
		}
		final SearchPageData<OrgUnitModel> searchResult = getOrgUnitDao().findMembersOfType(unit, unit.getClass(),
				CommerceSearchUtils.getAllOnOnePagePageableData());
		return searchResult.getResults().stream().filter(child -> child.getClass().equals(unit.getClass()))
				.collect(Collectors.toSet());
	}

	protected void handleTransactionException(final Exception e)
	{
		LOG.error("Updating of unit paths failed", e);
		if (e.getCause() instanceof OrgUnitHierarchyException)
		{
			throw (OrgUnitHierarchyException) e.getCause();
		}
		throw new OrgUnitHierarchyException("Update of unit paths failed", e.getCause());
	}

	protected OrgUnitDao getOrgUnitDao()
	{
		return orgUnitDao;
	}

	@Required
	public void setOrgUnitDao(final OrgUnitDao orgUnitDao)
	{
		this.orgUnitDao = orgUnitDao;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
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
}
