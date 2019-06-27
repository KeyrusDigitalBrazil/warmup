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
package de.hybris.platform.b2b.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.company.B2BGroupCycleValidator;
import de.hybris.platform.b2b.dao.PagedB2BCustomerDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.util.Assert;


/**
 * Default implementation of {@link B2BCommerceUnitService}
 */
public class DefaultB2BCommerceUnitService implements B2BCommerceUnitService
{
	private UserService userService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private B2BGroupCycleValidator b2BGroupCycleValidator;
	private SessionService sessionService;
	private ModelService modelService;
	private SearchRestrictionService searchRestrictionService;
	private PagedB2BCustomerDao<B2BCustomerModel> pagedB2BCustomerDao;
	private B2BCustomerService b2bCustomerService;

	@Override
	public Collection<? extends B2BUnitModel> getOrganization()
	{
		return getB2BUnitService().getBranch(getRootUnit());
	}

	@Override
	public Collection<? extends B2BUnitModel> getBranch()
	{
		return getB2BUnitService().getBranch(getParentUnit());
	}

	@Override
	public <T extends B2BUnitModel> T getRootUnit()
	{
		return (T) getB2BUnitService().getRootUnit(getB2BUnitService().getParent(getCurrentUser()));
	}


	public <T extends B2BCustomerModel> T getCurrentUser()
	{
		return (T) getUserService().getCurrentUser();
	}

	@Override
	public <T extends B2BUnitModel> T getParentUnit()
	{
		return (T) getB2BUnitService().getParent(getCurrentUser());
	}

	@Override
	public Collection<? extends B2BUnitModel> getAllUnitsOfOrganization()
	{
		return getB2BUnitService().getAllUnitsOfOrganization(getCurrentUser());
	}

	@Override
	public void setParentUnit(final B2BUnitModel unitModel, final B2BUnitModel parentUnit)
	{
		getB2BUnitService().addMember(parentUnit, unitModel);
	}

	@Override
	public Collection<? extends B2BUnitModel> getAllowedParentUnits(final B2BUnitModel unit)
	{
		Assert.notNull(unit, "Unit can not be null!");
		final B2BUnitModel sessionUnitParent = getParentUnit();

		final Set<B2BUnitModel> branch = getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getB2BUnitService().getBranch(sessionUnitParent);
			}
		});

		final Set<B2BUnitModel> allowedUnits = new HashSet<B2BUnitModel>(CollectionUtils.select(branch, new Predicate()
		{
			@Override
			public boolean evaluate(final Object object)
			{
				final PrincipalGroupModel principalGroup = (PrincipalGroupModel) object;
				return getB2BGroupCycleValidator().validateGroups(unit, principalGroup);
			}
		}));

		final B2BUnitModel parentUnit = getParentUnit(unit);
		if (parentUnit != null)
		{
			allowedUnits.add(parentUnit);
		}
		return allowedUnits;
	}

	@Override
	public void updateBranchInSession()
	{
		getB2BUnitService().updateBranchInSession(getSessionService().getCurrentSession(), getCurrentUser());
	}

	@Override
	public B2BUnitModel getUnitForUid(final String unitUid)
	{
		return getB2BUnitService().getUnitForUid(unitUid);
	}

	@Override
	public void disableUnit(final String uid)
	{
		validateParameterNotNullStandardMessage("uid", uid);
		getB2BUnitService().disableBranch(getUnitForUid(uid));
	}

	@Override
	public void enableUnit(final String unit)
	{
		getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				final B2BUnitModel unitModel = getUnitForUid(unit);
				validateParameterNotNullStandardMessage("B2BUnit", unit);
				unitModel.setActive(Boolean.TRUE);
				getModelService().save(unitModel);
			}
		});
	}

	@Override
	public SearchPageData<B2BCustomerModel> getPagedUsersForUnit(final PageableData pageableData, final String unit)
	{
		return getPagedB2BCustomerDao().findPagedCustomersForUnit(pageableData, unit);
	}

	@Override
	public void saveAddressEntry(final B2BUnitModel unitModel, final AddressModel addressModel)
	{
		final Collection<AddressModel> addresses = new ArrayList<AddressModel>(unitModel.getAddresses());
		addressModel.setOwner(unitModel);
		addresses.add(addressModel);
		unitModel.setAddresses(addresses);
		getModelService().save(unitModel);
	}

	@Override
	public void removeAddressEntry(final String unitUid, final String addressId)
	{
		final B2BUnitModel unit = getUnitForUid(unitUid);
		validateParameterNotNullStandardMessage("B2BUnit", unit);
		final Collection<AddressModel> addresses = new ArrayList<AddressModel>(unit.getAddresses());
		for (final AddressModel addressModel : addresses)
		{
			if (addressModel.getPk().getLongValueAsString().equals(addressId))
			{
				addresses.remove(addressModel);
				unit.setAddresses(addresses);
				getModelService().remove(addressModel);
				break;
			}
		}
	}

	@Override
	public AddressModel getAddressForCode(final B2BUnitModel unit, final String id)
	{
		for (final AddressModel addressModel : unit.getAddresses())
		{
			if (addressModel.getPk().getLongValueAsString().equals(id))
			{
				return addressModel;
			}
		}
		return null;
	}

	@Override
	public void editAddressEntry(final B2BUnitModel unitModel, final AddressModel addressModel)
	{
		getModelService().save(addressModel);
	}

	@Override
	public <T extends B2BUnitModel> T getParentUnit(final B2BUnitModel unit)
	{
		return (T) getB2BUnitService().getParent(unit);
	}

	public UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		b2BUnitService = b2bUnitService;
	}

	public B2BGroupCycleValidator getB2BGroupCycleValidator()
	{
		return b2BGroupCycleValidator;
	}

	public void setB2BGroupCycleValidator(final B2BGroupCycleValidator b2bGroupCycleValidator)
	{
		b2BGroupCycleValidator = b2bGroupCycleValidator;
	}

	public SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	public PagedB2BCustomerDao<B2BCustomerModel> getPagedB2BCustomerDao()
	{
		return pagedB2BCustomerDao;
	}

	public void setPagedB2BCustomerDao(final PagedB2BCustomerDao<B2BCustomerModel> pagedB2BCustomerDao)
	{
		this.pagedB2BCustomerDao = pagedB2BCustomerDao;
	}

	public B2BCustomerService getB2bCustomerService()
	{
		return b2bCustomerService;
	}

	public void setB2bCustomerService(final B2BCustomerService b2bCustomerService)
	{
		this.b2bCustomerService = b2bCustomerService;
	}
}
