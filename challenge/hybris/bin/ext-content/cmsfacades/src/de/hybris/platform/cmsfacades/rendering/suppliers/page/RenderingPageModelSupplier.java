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
package de.hybris.platform.cmsfacades.rendering.suppliers.page;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;

import java.util.Optional;
import java.util.function.Predicate;


/**
 * Interface responsible for providing {@link AbstractPageModel} and corresponding {@link RestrictionData}
 */
public interface RenderingPageModelSupplier
{
	/**
	 * Predicate to test if a given page type code matches the page supplier.
	 * <p>
	 * Returns <tt>TRUE</tt> if the supplier exists; <tt>FALSE</tt> otherwise.
	 * </p>
	 */
	Predicate<String> getConstrainedBy();

	/**
	 * Returns the page by qualifier.
	 *
	 * @param qualifier the qualifier of the page.
	 * @return {@link Optional} {@link AbstractPageModel}
	 */
	Optional<AbstractPageModel> getPageModel(String qualifier);

	/**
	 * Returns restriction data related to current page.
	 *
	 * @param qualifier the qualifier of the page
	 * @return the {@link Optional} {@link RestrictionData}
	 */
	Optional<RestrictionData> getRestrictionData(String qualifier);
}
