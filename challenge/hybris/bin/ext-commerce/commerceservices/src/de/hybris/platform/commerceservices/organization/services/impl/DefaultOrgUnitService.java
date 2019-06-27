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

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.daos.OrgUnitDao;
import de.hybris.platform.commerceservices.organization.services.OrgUnitHierarchyService;
import de.hybris.platform.commerceservices.organization.services.OrgUnitMemberParameter;
import de.hybris.platform.commerceservices.organization.services.OrgUnitParameter;
import de.hybris.platform.commerceservices.organization.services.OrgUnitService;
import de.hybris.platform.commerceservices.organization.strategies.OrgUnitActivationStrategy;
import de.hybris.platform.commerceservices.organization.strategies.OrgUnitAuthorizationStrategy;
import de.hybris.platform.commerceservices.organization.utils.OrgUtils;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.util.CommerceSearchUtils;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link OrgUnitService} interface.
 */
public class DefaultOrgUnitService implements OrgUnitService
{
	private static final Logger LOG = Logger.getLogger(DefaultOrgUnitService.class);

	private static final String PARAM_UNIT = "orgUnit";
	private static final String PARAM_UNIT_ACTIVE = "active";
	private static final String PARAM_UNIT_MEMBERS = "members";
	private static final String PARAM_UNIT_NAME = "name";
	private static final String PARAM_UNIT_PAGEABLE_DATA = "pageableData";
	private static final String PARAM_UNIT_TYPE = "type";
	private static final String PARAM_UNIT_UID = "uid";
	private static final String PARAM_UNIT_EMPLOYEE = "employee";

	private ModelService modelService;
	private UserService userService;
	private FlexibleSearchService flexibleSearchService;
	private OrgUnitActivationStrategy orgUnitActivationStrategy;
	private OrgUnitDao orgUnitDao;
	private OrgUnitAuthorizationStrategy orgUnitAuthorizationStrategy;
	private OrgUnitHierarchyService orgUnitHierarchyService;
	private ConfigurationService configurationService;

	@Override
	public void createUnit(final OrgUnitParameter parameter)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT_UID, parameter.getUid());
		validateParameterNotNullStandardMessage(PARAM_UNIT_NAME, parameter.getName());
		validateParameterNotNullStandardMessage(PARAM_UNIT_ACTIVE, parameter.getActive());
		getOrgUnitAuthorizationStrategy().validateCreatePermission(getUserService().getCurrentUser());

		final OrgUnitModel orgUnit = getModelService().create(OrgUnitModel.class);

		// essential attributes
		orgUnit.setUid(parameter.getUid());
		orgUnit.setName(parameter.getName());
		orgUnit.setActive(parameter.getActive());

		if (parameter.getParentUnit() != null)
		{
			setParentUnit(orgUnit, parameter.getParentUnit());
		}

		// optional attributes
		orgUnit.setDescription(parameter.getDescription());
		orgUnit.setLineOfBuisness(parameter.getLineOfBusiness());

		// additional attributes
		orgUnit.setSupplier(parameter.getSupplier() == null ? Boolean.FALSE : parameter.getSupplier());
		orgUnit.setBuyer(parameter.getBuyer() == null ? Boolean.FALSE : parameter.getBuyer());
		orgUnit.setManufacturer(parameter.getManufacturer() == null ? Boolean.FALSE : parameter.getManufacturer());
		orgUnit.setCarrier(parameter.getCarrier() == null ? Boolean.FALSE : parameter.getCarrier());

		final boolean isPathGenerationEnabled = getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true);
		if (isPathGenerationEnabled)
		{
			getOrgUnitHierarchyService().saveChangesAndUpdateUnitPath(orgUnit);
		}
		else
		{
			getModelService().save(orgUnit);
		}
	}

	@Override
	public void updateUnit(final OrgUnitParameter parameter)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT, parameter.getOrgUnit());
		getOrgUnitAuthorizationStrategy().validateEditPermission(getUserService().getCurrentUser());

		final OrgUnitModel orgUnit = parameter.getOrgUnit();
		final boolean isNewParent = setParentUnit(orgUnit, parameter.getParentUnit());

		final boolean isPathGenerationEnabled = getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true);
		if (isNewParent && isPathGenerationEnabled)
		{
			getOrgUnitHierarchyService().saveChangesAndUpdateUnitPath(orgUnit);
		}
		else
		{
			getModelService().save(orgUnit);
		}
	}

	@Override
	public Optional<OrgUnitModel> getUnitForUid(final String uid)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT_UID, uid);
		getOrgUnitAuthorizationStrategy().validateViewPermission(getUserService().getCurrentUser());

		try
		{
			return Optional.ofNullable(getUserService().getUserGroupForUID(uid, OrgUnitModel.class));
		}
		catch (UnknownIdentifierException | ClassMismatchException e)
		{
			LOG.error(e);
		}

		return Optional.empty();
	}

	@Override
	public void activateUnit(final OrgUnitModel orgUnit)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT, orgUnit);
		getOrgUnitAuthorizationStrategy().validateEditPermission(getUserService().getCurrentUser());

		getOrgUnitActivationStrategy().activateUnit(orgUnit);
	}

	@Override
	public void deactivateUnit(final OrgUnitModel orgUnit)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT, orgUnit);
		getOrgUnitAuthorizationStrategy().validateEditPermission(getUserService().getCurrentUser());

		getOrgUnitActivationStrategy().deactivateUnit(orgUnit);
	}

	@Override
	public void addMembers(final OrgUnitMemberParameter parameter)
	{
		addRemoveMembers(parameter, (memberGroups, orgUnit) -> memberGroups.add(orgUnit));
	}

	@Override
	public void removeMembers(final OrgUnitMemberParameter parameter)
	{
		addRemoveMembers(parameter, (memberGroups, orgUnit) -> memberGroups.remove(orgUnit));
	}

	@Override
	public <T extends PrincipalModel> SearchPageData<T> getMembers(final OrgUnitMemberParameter<T> parameter)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT_UID, parameter.getUid());
		validateParameterNotNullStandardMessage(PARAM_UNIT_TYPE, parameter.getType());
		validateParameterNotNullStandardMessage(PARAM_UNIT_PAGEABLE_DATA, parameter.getPageableData());
		getOrgUnitAuthorizationStrategy().validateViewPermission(getUserService().getCurrentUser());

		final Optional<OrgUnitModel> unitOptional = getUnitForUid(parameter.getUid());
		if (unitOptional.isPresent())
		{
			return getOrgUnitDao().findMembersOfType(unitOptional.get(), parameter.getType(), parameter.getPageableData());
		}
		LOG.warn("Unit with uid [" + parameter.getUid() + "] does not exist.");
		return CommerceSearchUtils.createEmptySearchPageData();
	}

	@Override
	public Optional<OrgUnitModel> getParent(final OrgUnitModel orgUnit)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT, orgUnit);
		getOrgUnitAuthorizationStrategy().validateViewPermission(getUserService().getCurrentUser());

		return Optional.ofNullable(
				(OrgUnitModel) CollectionUtils.find(orgUnit.getGroups(), PredicateUtils.instanceofPredicate(OrgUnitModel.class)));
	}

	protected <T extends PrincipalModel> void addRemoveMembers(final OrgUnitMemberParameter<T> parameter,
			final BiConsumer<Set<PrincipalGroupModel>, OrgUnitModel> consumer)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT_UID, parameter.getUid());
		validateParameterNotNullStandardMessage(PARAM_UNIT_MEMBERS, parameter.getMembers());
		getOrgUnitAuthorizationStrategy().validateEditPermission(getUserService().getCurrentUser());

		final Optional<OrgUnitModel> unitOptional = getUnitForUid(parameter.getUid());

		if (unitOptional.isPresent())
		{
			for (final PrincipalModel member : parameter.getMembers())
			{
				final Set<PrincipalGroupModel> memberGroups = new HashSet<>(member.getGroups());
				consumer.accept(memberGroups, unitOptional.get());
				member.setGroups(memberGroups);
				getModelService().save(member);
			}
		}
		else
		{
			LOG.warn("Unit with uid [" + parameter.getUid() + "] does not exist.");
		}
	}

	protected boolean setParentUnit(final OrgUnitModel orgUnit, final OrgUnitModel newParentUnit)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT, orgUnit);

		final OrgUnitModel currentParentUnit = getParent(orgUnit).orElse(null);

		if (currentParentUnit == null && newParentUnit == null)
		{
			return false;
		}
		if (currentParentUnit != null && currentParentUnit.equals(newParentUnit))
		{
			return false;
		}

		getOrgUnitAuthorizationStrategy().validateEditParentPermission(getUserService().getCurrentUser());

		final Set<PrincipalGroupModel> groups = orgUnit.getGroups() == null ? new HashSet<>() : new HashSet<>(orgUnit.getGroups());

		if (currentParentUnit != null)
		{
			groups.remove(currentParentUnit);
		}
		if (newParentUnit != null)
		{
			groups.add(newParentUnit);
		}

		orgUnit.setGroups(groups);
		return true;
	}

	@Override
	public Set<PrincipalGroupModel> getRolesForEmployee(final EmployeeModel employee)
	{
		validateParameterNotNullStandardMessage(PARAM_UNIT_EMPLOYEE, employee);

		if (getModelService().isNew(employee))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("The Employee item has not been persisted, yet. Returning an empty Set of organization roles.");
			}
			return Collections.emptySet();
		}

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT {ug:pk} FROM ");
		sql.append("{ ");
		sql.append("	UserGroup as ug");
		sql.append("	JOIN PrincipalGroupRelation as rel");
		sql.append("	ON {rel:target} = {ug:pk}");
		sql.append("	JOIN Employee as e");
		sql.append("	ON {rel:source} = {e:pk}");
		sql.append("} ");
		sql.append("WHERE {e:pk}=?employee AND {ug:uid} IN (?roles)");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
		query.getQueryParameters().put("employee", employee);
		query.getQueryParameters().put("roles", OrgUtils.getRoleUids());
		final SearchResult<PrincipalGroupModel> result = getFlexibleSearchService().search(query);
		final Set<PrincipalGroupModel> restulSet = new HashSet<PrincipalGroupModel>(result.getResult());

		return restulSet;
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected OrgUnitActivationStrategy getOrgUnitActivationStrategy()
	{
		return orgUnitActivationStrategy;
	}

	@Required
	public void setOrgUnitActivationStrategy(final OrgUnitActivationStrategy orgUnitActivationStrategy)
	{
		this.orgUnitActivationStrategy = orgUnitActivationStrategy;
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

	protected OrgUnitAuthorizationStrategy getOrgUnitAuthorizationStrategy()
	{
		return orgUnitAuthorizationStrategy;
	}

	@Required
	public void setOrgUnitAuthorizationStrategy(final OrgUnitAuthorizationStrategy orgUnitAuthorizationStrategy)
	{
		this.orgUnitAuthorizationStrategy = orgUnitAuthorizationStrategy;
	}

	protected OrgUnitHierarchyService getOrgUnitHierarchyService()
	{
		return orgUnitHierarchyService;
	}

	@Required
	public void setOrgUnitHierarchyService(final OrgUnitHierarchyService orgUnitHierarchyService)
	{
		this.orgUnitHierarchyService = orgUnitHierarchyService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
