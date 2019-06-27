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
package de.hybris.platform.b2bacceleratorfacades.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.company.B2BCommerceUnitFacade;
import de.hybris.platform.b2bapprovalprocessfacades.company.impl.DefaultB2BApproverFacade;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.b2bcommercefacades.company.impl.DefaultB2BUnitFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;


/**
 * @deprecated Since 6.0. Use {@link DefaultB2BUnitFacade} and {@link DefaultB2BApproverFacade} instead.
 */
@Deprecated
public class DefaultB2BCommerceUnitFacade extends DefaultCompanyB2BCommerceFacade implements B2BCommerceUnitFacade
{
	/**
	 * @deprecated Since 6.0. Use {@link DefaultB2BApproverFacade#getPagedApproversForUnit(PageableData, String)}
	 *             instead.
	 */
	@Deprecated
	@Override
	public SearchPageData<CustomerData> getPagedApproversForUnit(final PageableData pageableData, final String unitUid)
	{
		final SearchPageData<B2BCustomerModel> approvers = getB2BApproverService().findPagedApproversForUnitByGroupMembership(
				pageableData, unitUid, B2BConstants.B2BAPPROVERGROUP);
		final SearchPageData<CustomerData> searchPageData = convertPageData(approvers, getB2BUserConverter());
		// update the results with approvers that already have been selected.
		final B2BUnitData unit = this.getUnitForUid(unitUid);
		validateParameterNotNull(unit, String.format("No unit found for uid %s", unitUid));
		for (final CustomerData userData : searchPageData.getResults())
		{
			userData.setSelected(CollectionUtils.find(unit.getApprovers(), new BeanPropertyValueEqualsPredicate(
					B2BCustomerModel.UID, userData.getUid())) != null);
		}

		return searchPageData;
	}

	@Override
	public SearchPageData<CustomerData> getPagedCustomersForUnit(final PageableData pageableData, final String unitUid)
	{
		final SearchPageData<CustomerData> searchPageData = this.getPagedUserDataForUnit(pageableData, unitUid);
		// update the results with users that already have been selected.
		final B2BUnitData unit = this.getUnitForUid(unitUid);
		validateParameterNotNull(unit, String.format("No unit found for uid %s", unitUid));
		for (final CustomerData userData : searchPageData.getResults())
		{
			userData.setSelected(CollectionUtils.find(unit.getCustomers(), new BeanPropertyValueEqualsPredicate(
					B2BCustomerModel.UID, userData.getUid())) != null);
		}

		return searchPageData;
	}

	@Override
	public SearchPageData<CustomerData> getPagedAdministratorsForUnit(final PageableData pageableData, final String unitUid)
	{
		final SearchPageData<CustomerData> searchPageData = this.getPagedUserDataForUnit(pageableData, unitUid);

		// update the results with users that already have been selected.
		final B2BUnitData unit = this.getUnitForUid(unitUid);
		validateParameterNotNull(unit, String.format("No unit found for uid %s", unitUid));
		for (final CustomerData userData : searchPageData.getResults())
		{
			userData.setSelected(CollectionUtils.find(unit.getAdministrators(), new BeanPropertyValueEqualsPredicate(
					B2BCustomerModel.UID, userData.getUid())) != null);
		}

		return searchPageData;
	}

	@Override
	public SearchPageData<CustomerData> getPagedManagersForUnit(final PageableData pageableData, final String unitUid)
	{
		final SearchPageData<CustomerData> searchPageData = this.getPagedUserDataForUnit(pageableData, unitUid);
		// update the results with users that already have been selected.
		final B2BUnitData unit = this.getUnitForUid(unitUid);
		validateParameterNotNull(unit, String.format("No unit found for uid %s", unitUid));
		for (final CustomerData userData : searchPageData.getResults())
		{
			userData.setSelected(CollectionUtils.find(unit.getManagers(), new BeanPropertyValueEqualsPredicate(B2BCustomerModel.UID,
					userData.getUid())) != null);
		}

		return searchPageData;
	}

	@Override
	public void disableUnit(final String uid)
	{
		validateParameterNotNullStandardMessage("unit UID", uid);
		getB2BCommerceUnitService().disableUnit(uid);
	}

	@Override
	public B2BUnitNodeData getParentUnitNode()
	{
		final B2BUnitModel parentUnit = getB2BCommerceUnitService().getParentUnit();
		return (B2BUnitNodeData) CollectionUtils.find(getBranchNodes(),
				new BeanPropertyValueEqualsPredicate("id", parentUnit.getUid()));
	}


	@Override
	public List<B2BUnitNodeData> getAllowedParentUnits(final String uid)
	{
		return Converters.convertAll(
				getB2BCommerceUnitService().getAllowedParentUnits(getCompanyB2BCommerceService().getUnitForUid(uid)),
				getUnitTreeConverter());
	}

	/**
	 * @deprecated Since 6.0. Use {@link DefaultB2BApproverFacade#addApproverToUnit(String, String)} instead.
	 */
	@Deprecated
	@Override
	public B2BSelectionData addApproverToUnit(final String unitUid, final String approverUid)
	{
		validateParameterNotNullStandardMessage("unitUid", unitUid);
		validateParameterNotNullStandardMessage("approverUid", approverUid);

		final B2BCustomerModel approver = getB2BApproverService().addApproverToUnit(unitUid, approverUid);
		final B2BSelectionData b2BSelectionData = createB2BSelectionData(approver.getUid(), true, approver.getActive()
				.booleanValue());
		return populateRolesForCustomer(approver, b2BSelectionData);
	}


	/**
	 * @deprecated Since 6.0. Use {@link DefaultB2BApproverFacade#removeApproverFromUnit(String, String)} instead.
	 */
	@Deprecated
	@Override
	public B2BSelectionData removeApproverFromUnit(final String unitUid, final String approverUid)
	{
		validateParameterNotNullStandardMessage("unitUid", unitUid);
		validateParameterNotNullStandardMessage("approverUid", approverUid);

		final B2BCustomerModel approver = getB2BApproverService().removeApproverFromUnit(unitUid, approverUid);
		final B2BSelectionData b2BSelectionData = createB2BSelectionData(approver.getUid(), false, approver.getActive()
				.booleanValue());
		return populateRolesForCustomer(approver, b2BSelectionData);
	}

	@Override
	public List<String> getAllActiveUnitsOfOrganization()
	{
		final Set<B2BUnitModel> units = (Set<B2BUnitModel>) getB2BCommerceUnitService().getAllUnitsOfOrganization();
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
		return convertPageData(customers, getB2BUserConverter());
	}

	@Override
	public void addAddressToUnit(final AddressData addressData, final String unitUid)
	{
		final AddressModel addressModel = this.getModelService().create(AddressModel.class);
		getAddressReversePopulator().populate(addressData, addressModel);

		// Store the address against the unit
		getB2BCommerceUnitService().saveAddressEntry(this.getCompanyB2BCommerceService().getUnitForUid(unitUid), addressModel);

		// Update the address ID in the newly created address
		addressData.setId(addressModel.getPk().toString());
	}


	@Override
	public void removeAddressFromUnit(final String unitUid, final String addressId)
	{
		validateParameterNotNullStandardMessage("unitUid", unitUid);
		validateParameterNotNullStandardMessage("addressId", addressId);
		getB2BCommerceUnitService().removeAddressEntry(unitUid, addressId);

	}

	@Override
	public void editAddressOfUnit(final AddressData updatedAddress, final String uid)
	{
		validateParameterNotNullStandardMessage("unit uid", uid);
		validateParameterNotNullStandardMessage("newAddress", updatedAddress);
		final B2BUnitModel unit = getCompanyB2BCommerceService().getUnitForUid(uid);
		final AddressModel addressModel = getB2BCommerceUnitService().getAddressForCode(unit, updatedAddress.getId());
		validateParameterNotNullStandardMessage(String.format("Address not found for pk %s", updatedAddress.getId()), addressModel);
		getAddressReversePopulator().populate(updatedAddress, addressModel);
		getB2BCommerceUnitService().editAddressEntry(unit, addressModel);
	}

	@Override
	public void enableUnit(final String unitUid)
	{
		validateParameterNotNullStandardMessage("unit UID", unitUid);
		getB2BCommerceUnitService().enableUnit(unitUid);
	}

	@Override
	public void updateOrCreateBusinessUnit(final String originalUid, final B2BUnitData unit)
	{
		B2BUnitModel unitModel = this.getCompanyB2BCommerceService().getUnitForUid(originalUid);
		boolean newModel = false;
		if (unitModel == null)
		{
			newModel = true;
			unitModel = this.getModelService().create(B2BUnitModel.class);
		}

		getB2BUnitReversePopulator().populate(unit, unitModel);

		this.getCompanyB2BCommerceService().saveModel(unitModel);
		// if a new unit is being created update branch is the session
		if (newModel)
		{
			this.getB2BCommerceUnitService().updateBranchInSession();
		}
	}
}