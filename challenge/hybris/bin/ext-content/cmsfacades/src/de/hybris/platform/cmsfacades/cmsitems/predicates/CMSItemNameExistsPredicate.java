/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.cmsitems.predicates;

import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Predicate to test if a given cms item name maps to an existing cms item.
 * <p>
 * Returns <tt>TRUE</tt> if the cms item exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class CMSItemNameExistsPredicate implements Predicate<CMSItemModel>
{
	private static final String ABSTRACT = "Abstract";

	private CMSAdminItemService cmsAdminItemService;
	private CMSAdminSiteService cmsAdminSiteService;
	private TypeService typeService;
	private Predicate<Object> cloneContextSameAsActiveCatalogVersionPredicate;
	private List<Predicate<CMSItemModel>> filters;

	/**
	 * Suppress sonar warning (squid:S1166 | Exception handlers should preserve the original exception) : The exception
	 * is correctly handled in the catch clause.
	 */
	@SuppressWarnings("squid:S1166")
	@Override
	public boolean test(final CMSItemModel cmsItemModel)
	{
		if (cmsItemModel == null)
		{
			return false;
		}
		boolean result = false;
		try
		{
			if (getCloneContextSameAsActiveCatalogVersionPredicate().test(cmsItemModel))
			{
				final String typeCode = findAbstractParentTypeCode(cmsItemModel);
				final List<CMSItemModel> results = findCMSItemByTypeCodeAndName(cmsItemModel.getName(), typeCode);
				switch (results.size())
				{
					case 0:
						result = false;
						break;
					case 1:
						result = !results.get(0).getUid().equals(cmsItemModel.getUid());
						break;
					default:
						result = true;
						break;
				}
			}
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			result = false;
		}
		return result;
	}

	/**
	 * Find all {@code CMSItemModel} having the same name as the provided {@name} value. The result list is filter by
	 * applying all the predicates specified by {@code #getFilters()}.
	 *
	 * @param name
	 *           - the name of the item
	 * @param typeCode
	 *           - the typecode of the item where the lookup takes place
	 * @return a list of items containing the same name
	 */
	protected List<CMSItemModel> findCMSItemByTypeCodeAndName(final String name, final String typeCode)
	{
		final SearchResult<CMSItemModel> searchResult = getCmsAdminItemService()
				.findByTypeCodeAndName(getCmsAdminSiteService().getActiveCatalogVersion(), typeCode, name);

		/*
		 * Create a predicate for all the predicates defined in getFilters(). If any of the predicate in getFilters()
		 * evaluates to FALSE, the CMSItemModel will be removed from the method return value
		 */
		final Predicate<CMSItemModel> predicate = getFilters().stream().reduce(Predicate::or).orElse(value -> true);
		return searchResult.getResult().stream()
				.filter(predicate)
				.collect(Collectors.toList());
	}

	/**
	 * Find the abstract parent typecode for a given item model
	 *
	 * @param cmsItemModel
	 *           - the item model for which to find the abstract parent typecode
	 * @return the abstract parent typecode
	 */
	protected String findAbstractParentTypeCode(final CMSItemModel cmsItemModel)
	{
		String typeCode = cmsItemModel.getItemtype();
		final boolean isAbstractType = cmsItemModel.getItemtype().startsWith(ABSTRACT);
		if (!isAbstractType)
		{
			typeCode = getTypeService().getComposedTypeForCode(cmsItemModel.getItemtype()).getAllSuperTypes().stream()
					.filter(type -> type.getCode().startsWith(ABSTRACT)) //
					.map(ComposedTypeModel::getCode).findFirst() //
					.orElse(cmsItemModel.getItemtype());
		}
		return typeCode;
	}

	protected CMSAdminItemService getCmsAdminItemService()
	{
		return cmsAdminItemService;
	}

	@Required
	public void setCmsAdminItemService(final CMSAdminItemService cmsAdminItemService)
	{
		this.cmsAdminItemService = cmsAdminItemService;
	}

	protected CMSAdminSiteService getCmsAdminSiteService()
	{
		return cmsAdminSiteService;
	}

	@Required
	public void setCmsAdminSiteService(final CMSAdminSiteService cmsAdminSiteService)
	{
		this.cmsAdminSiteService = cmsAdminSiteService;
	}

	protected Predicate<Object> getCloneContextSameAsActiveCatalogVersionPredicate()
	{
		return cloneContextSameAsActiveCatalogVersionPredicate;
	}

	@Required
	public void setCloneContextSameAsActiveCatalogVersionPredicate(
			final Predicate<Object> cloneContextSameAsActiveCatalogVersionPredicate)
	{
		this.cloneContextSameAsActiveCatalogVersionPredicate = cloneContextSameAsActiveCatalogVersionPredicate;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected List<Predicate<CMSItemModel>> getFilters()
	{
		return filters;
	}

	@Required
	public void setFilters(final List<Predicate<CMSItemModel>> filters)
	{
		this.filters = filters;
	}

}
