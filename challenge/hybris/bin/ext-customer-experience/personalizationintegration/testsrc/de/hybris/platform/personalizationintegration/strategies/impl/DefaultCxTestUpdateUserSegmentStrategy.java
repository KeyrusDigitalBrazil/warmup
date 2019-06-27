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
package de.hybris.platform.personalizationintegration.strategies.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.personalizationintegration.mapping.MappingData;
import de.hybris.platform.personalizationintegration.service.CxIntegrationMappingService;
import de.hybris.platform.personalizationservices.CxCalculationContext;
import de.hybris.platform.personalizationservices.model.CxSegmentModel;
import de.hybris.platform.personalizationservices.model.CxUserToSegmentModel;
import de.hybris.platform.personalizationservices.segment.CxSegmentService;
import de.hybris.platform.personalizationservices.strategies.UpdateUserSegmentStrategy;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


public class DefaultCxTestUpdateUserSegmentStrategy implements UpdateUserSegmentStrategy
{

	private ModelService modelService;
	private CxSegmentService cxSegmentService;
	private CxIntegrationMappingService cxIntegrationMappingService;
	private List<String> externalIds;


	@Override
	public void updateUserSegments(final UserModel user)
	{
		final Set<CxSegmentModel> cxSegments = findSegmentsFromProfile(externalIds);
		setSegmentsOnUser(user, cxSegments);
	}

	@Override
	public void updateUserSegments(UserModel user, CxCalculationContext context)
	{
		updateUserSegments(user);
	}


	protected Set<CxSegmentModel> findSegmentsFromProfile(final List<String> externalIds)
	{
		final Optional<MappingData> profile = cxIntegrationMappingService.mapExternalData(externalIds,
				"testConverterName");

		if (!profile.isPresent())
		{
			return Collections.emptySet();
		}
		else
		{
			final Set<CxSegmentModel> cxSegments = profile.get().getSegments().stream()//
					.map(s -> s.getCode()).distinct()//
					.map(c -> cxSegmentService.getSegment(c))//
					.filter(s -> s.isPresent()) //
					.map(s -> s.get()) //
					.collect(Collectors.toSet());
			return cxSegments;
		}
	}

	protected void setSegmentsOnUser(final UserModel user, final Set<CxSegmentModel> segments)
	{
		final Collection<CxUserToSegmentModel> previousUserToSegments = cxSegmentService.getUserToSegmentForUser(user);

		cxSegmentService.removeUserToSegments(previousUserToSegments);

		final List<CxUserToSegmentModel> userToSegments = segments.stream().map(s -> createUserToSegment(user, s)).collect(Collectors.toList());

		cxSegmentService.saveUserToSegments(userToSegments);
	}

	protected CxUserToSegmentModel createUserToSegment(final UserModel user, final CxSegmentModel segment){
		final CxUserToSegmentModel uts = new CxUserToSegmentModel();
		uts.setSegment(segment);
		uts.setUser(user);
		uts.setAffinity(BigDecimal.ONE);
		return uts;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CxSegmentService getCxSegmentService()
	{
		return cxSegmentService;
	}

	@Required
	public void setCxSegmentService(final CxSegmentService cxSegmentService)
	{
		this.cxSegmentService = cxSegmentService;
	}

	/**
	 * @return the cxIntegrationMappingService
	 */
	public CxIntegrationMappingService getCxIntegrationMappingService()
	{
		return cxIntegrationMappingService;
	}

	/**
	 * @param cxIntegrationMappingService
	 *           the cxIntegrationMappingService to set
	 */
	public void setCxIntegrationMappingService(final CxIntegrationMappingService cxIntegrationMappingService)
	{
		this.cxIntegrationMappingService = cxIntegrationMappingService;
	}


	/**
	 * @return the externalIds
	 */
	public List<String> getExternalIds()
	{
		return externalIds;
	}


	/**
	 * @param externalIds
	 *           the externalIds to set
	 */
	public void setExternalIds(final List<String> externalIds)
	{
		this.externalIds = externalIds;
	}


}
