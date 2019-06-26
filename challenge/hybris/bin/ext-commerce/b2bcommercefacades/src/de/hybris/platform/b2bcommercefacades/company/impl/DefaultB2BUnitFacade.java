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
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.util.CommerceUtils;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.organization.services.OrgUnitHierarchyService;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BUnitFacade}
 */
public class DefaultB2BUnitFacade implements B2BUnitFacade
{

	private ModelService modelService;
	private SessionService sessionService;
	private UserService userService;

	private B2BUnitService<B2BUnitModel, UserModel> b2BUnitService;
	private B2BCommerceUnitService b2BCommerceUnitService;
	private Converter<B2BUnitModel, B2BUnitData> b2BUnitConverter;
	private Converter<B2BUnitModel, B2BUnitNodeData> unitTreeConverter;
	private Converter<B2BCustomerModel, CustomerData> b2BUserConverter;
	private Converter<AddressData, AddressModel> addressReverseConverter;
	private Converter<B2BUnitData, B2BUnitModel> b2BUnitReverseConverter;
	private OrgUnitHierarchyService orgUnitHierarchyService;
	private ConfigurationService configurationService;

	@Override
	public SearchPageData<CustomerData> getPagedCustomersForUnit(final PageableData pageableData, final String unitUid)
	{
		// update the results with users that already have been selected.
		final B2BUnitData unit = this.getValidUnitForUid(unitUid);
		return this.getPagedUsersForUnit(pageableData, unitUid, unit.getCustomers());
	}

	@Override
	public SearchPageData<CustomerData> getPagedAdministratorsForUnit(final PageableData pageableData, final String unitUid)
	{
		// update the results with users that already have been selected.
		final B2BUnitData unit = this.getValidUnitForUid(unitUid);
		return this.getPagedUsersForUnit(pageableData, unitUid, unit.getAdministrators());
	}

	@Override
	public SearchPageData<CustomerData> getPagedManagersForUnit(final PageableData pageableData, final String unitUid)
	{
		// update the results with users that already have been selected.
		final B2BUnitData unit = this.getValidUnitForUid(unitUid);
		return this.getPagedUsersForUnit(pageableData, unitUid, unit.getManagers());
	}

	protected SearchPageData<CustomerData> getPagedUsersForUnit(final PageableData pageableData, final String unitUid,
			final Collection<?> unitCollection)
	{
		final SearchPageData<CustomerData> searchPageData = this.getPagedUserDataForUnit(pageableData, unitUid);

		for (final CustomerData userData : searchPageData.getResults())
		{
			userData.setSelected(CollectionUtils.find(unitCollection,
					new BeanPropertyValueEqualsPredicate(B2BCustomerModel.UID, userData.getUid())) != null);
		}

		return searchPageData;
	}

	@Override
	public void disableUnit(final String unitUid)
	{
		validateParameterNotNullStandardMessage("B2BUnit", unitUid);
		final B2BUnitModel unit = getB2BUnitService().getUnitForUid(unitUid);
		getB2BUnitService().disableBranch(unit);
	}

	@Override
	public void enableUnit(final String unitUid)
	{
		validateParameterNotNullStandardMessage("B2BUnit", unitUid);
		final B2BUnitModel unit = getB2BUnitService().getUnitForUid(unitUid);
		getB2BUnitService().enableUnit(unit);
	}

	@Override
	public B2BUnitData getParentUnit()
	{
		final B2BUnitModel parentUnit = getB2BCommerceUnitService().getParentUnit();
		return parentUnit == null ? null : this.getB2BUnitConverter().convert(parentUnit);
	}

	@Override
	public B2BUnitNodeData getParentUnitNode()
	{
		final B2BCustomerModel currentUser = (B2BCustomerModel) this.getUserService().getCurrentUser();
		final B2BUnitModel parentUnit = getB2BUnitService().getParent(currentUser);
		return (B2BUnitNodeData) CollectionUtils.find(getBranchNodes(),
				new BeanPropertyValueEqualsPredicate("id", parentUnit.getUid()));
	}

	@Override
	public List<B2BUnitNodeData> getAllowedParentUnits(final String uid)
	{
		return Converters.convertAll(getB2BCommerceUnitService().getAllowedParentUnits(getB2BUnitService().getUnitForUid(uid)),
				getUnitTreeConverter());
	}

	@Override
	public List<String> getAllActiveUnitsOfOrganization()
	{
		final B2BCustomerModel currentUser = (B2BCustomerModel) this.getUserService().getCurrentUser();
		final Set<B2BUnitModel> units = getB2BUnitService().getAllUnitsOfOrganization(currentUser);
		final List<String> b2BUnitList = new ArrayList<String>(units.size());
		for (final B2BUnitModel b2BUnitModel : units)
		{
			if (Boolean.TRUE.equals(b2BUnitModel.getActive()))
			{
				b2BUnitList.add(b2BUnitModel.getUid());
			}
		}
		return b2BUnitList;
	}

	@Override
	public SearchPageData<CustomerData> getPagedUserDataForUnit(final PageableData pageableData, final String unit)
	{
		final SearchPageData<B2BCustomerModel> customers = getB2BCommerceUnitService().getPagedUsersForUnit(pageableData, unit);
		return CommerceUtils.convertPageData(customers, getB2BUserConverter());
	}

	@Override
	public void addAddressToUnit(final AddressData newAddress, final String unitUid)
	{
		final AddressModel addressModel = this.getModelService().create(AddressModel.class);
		this.getAddressReverseConverter().convert(newAddress, addressModel);

		// Store the address against the unit
		final B2BUnitModel unitModel = this.getB2BUnitService().getUnitForUid(unitUid);
		getB2BCommerceUnitService().saveAddressEntry(unitModel, addressModel);

		// Update the address ID in the newly created address
		newAddress.setId(addressModel.getPk().toString());
	}

	@Override
	public void removeAddressFromUnit(final String unitUid, final String addressId)
	{
		validateParameterNotNullStandardMessage("unitUid", unitUid);
		validateParameterNotNullStandardMessage("addressId", addressId);
		getB2BCommerceUnitService().removeAddressEntry(unitUid, addressId);
	}

	@Override
	public void editAddressOfUnit(final AddressData newAddress, final String unitUid)
	{
		validateParameterNotNullStandardMessage("unit Uid", unitUid);
		validateParameterNotNullStandardMessage("address Id", newAddress);
		final B2BUnitModel unitModel = getB2BUnitService().getUnitForUid(unitUid);
		final AddressModel addressModel = getB2BCommerceUnitService().getAddressForCode(unitModel, newAddress.getId());
		addressModel.setRegion(null);
		validateParameterNotNullStandardMessage(String.format("Address not found for pk %s", newAddress.getId()), addressModel);
		getAddressReverseConverter().convert(newAddress, addressModel);
		getB2BCommerceUnitService().editAddressEntry(unitModel, addressModel);
	}

	@Override
	public void updateOrCreateBusinessUnit(final String originalUid, final B2BUnitData unit)
	{
		B2BUnitModel unitModel = this.getB2BUnitService().getUnitForUid(originalUid);
		B2BUnitModel parentUnitBefore = null;
		B2BUnitModel parentUnitAfter = null;
		boolean newModel = false;
		if (unitModel == null)
		{
			newModel = true;
			unitModel = getModelService().create(B2BUnitModel.class);
		}
		else
		{
			parentUnitBefore = getB2BCommerceUnitService().getParentUnit(unitModel);
		}

		getB2BUnitReverseConverter().convert(unit, unitModel);
		parentUnitAfter = getB2BCommerceUnitService().getParentUnit(unitModel);
		newModel = newModel || !Objects.equals(parentUnitBefore, parentUnitAfter);

		final boolean isPathGenerationEnabled = getConfigurationService().getConfiguration()
				.getBoolean(CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED, true);
		if (newModel && isPathGenerationEnabled)
		{
			getOrgUnitHierarchyService().saveChangesAndUpdateUnitPath(unitModel);
		}
		else
		{
			getModelService().save(unitModel);
		}

		// if a new unit is being created update branch in the session
		if (newModel)
		{
			final B2BCustomerModel currentUser = (B2BCustomerModel) getUserService().getCurrentUser();
			getB2BUnitService().updateBranchInSession(getSessionService().getCurrentSession(), currentUser);
		}
	}

	@Override
	public List<B2BUnitNodeData> getBranchNodes()
	{
		return Converters.convertAll(getB2BCommerceUnitService().getBranch(), getUnitTreeConverter());
	}

	@Override
	public B2BUnitData getUnitForUid(final String uid)
	{
		final B2BUnitModel unitModel = getB2BUnitService().getUnitForUid(uid);
		return unitModel == null ? null : this.getB2BUnitConverter().convert(unitModel);
	}

	protected B2BUnitData getValidUnitForUid(final String uid)
	{
		final B2BUnitData unit = getUnitForUid(uid);
		validateParameterNotNull(unit, String.format("No unit found for uid %s", uid));
		return unit;
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

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
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

	protected B2BUnitService<B2BUnitModel, UserModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2BUnitService = b2bUnitService;
	}

	protected B2BCommerceUnitService getB2BCommerceUnitService()
	{
		return b2BCommerceUnitService;
	}

	@Required
	public void setB2BCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		this.b2BCommerceUnitService = b2bCommerceUnitService;
	}

	protected Converter<B2BUnitModel, B2BUnitData> getB2BUnitConverter()
	{
		return b2BUnitConverter;
	}

	@Required
	public void setB2BUnitConverter(final Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter)
	{
		this.b2BUnitConverter = b2bUnitConverter;
	}

	protected Converter<B2BUnitModel, B2BUnitNodeData> getUnitTreeConverter()
	{
		return unitTreeConverter;
	}

	@Required
	public void setUnitTreeConverter(final Converter<B2BUnitModel, B2BUnitNodeData> unitTreeConverter)
	{
		this.unitTreeConverter = unitTreeConverter;
	}

	protected Converter<B2BCustomerModel, CustomerData> getB2BUserConverter()
	{
		return b2BUserConverter;
	}

	@Required
	public void setB2BUserConverter(final Converter<B2BCustomerModel, CustomerData> b2bUserConverter)
	{
		this.b2BUserConverter = b2bUserConverter;
	}

	protected Converter<AddressData, AddressModel> getAddressReverseConverter()
	{
		return addressReverseConverter;
	}

	@Required
	public void setAddressReverseConverter(final Converter<AddressData, AddressModel> addressReverseConverter)
	{
		this.addressReverseConverter = addressReverseConverter;
	}

	protected Converter<B2BUnitData, B2BUnitModel> getB2BUnitReverseConverter()
	{
		return b2BUnitReverseConverter;
	}

	@Required
	public void setB2BUnitReverseConverter(final Converter<B2BUnitData, B2BUnitModel> b2BUnitReverseConverter)
	{
		this.b2BUnitReverseConverter = b2BUnitReverseConverter;
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
