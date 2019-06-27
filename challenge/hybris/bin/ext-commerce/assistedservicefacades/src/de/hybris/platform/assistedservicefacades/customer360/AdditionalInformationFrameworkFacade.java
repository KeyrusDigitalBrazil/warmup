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
package de.hybris.platform.assistedservicefacades.customer360;

import java.util.List;
import java.util.Map;


/**
 *
 * Interface that holds AIF related functionality e.g. retrieving sections and fragments.
 *
 */
public interface AdditionalInformationFrameworkFacade
{
	/**
	 * Return list of sections , this mainly will be used to getting section meta info
	 *
	 * @return list of sections
	 */
	List<Section> getSections();

	/**
	 * Retrieves full section information based on section Id
	 *
	 * @param sectionId
	 *           the section Id to retrieve
	 * @return the section
	 */
	Section getSection(final String sectionId);

	/**
	 * Retrieves a fragment based on section id and fragment id with provided parameters
	 *
	 * @param sectionId
	 *           the section to fetch the fragment from
	 * @param fragmentId
	 *           the fragment Id
	 * @param parameters
	 *           parameters to be passed
	 * @return fragment along with its data
	 */
	Fragment getFragment(final String sectionId, String fragmentId, Map<String, String> parameters);
}