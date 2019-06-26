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
package de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.impl;

import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMapping;

/**
 * Class contains the mapping information for the sales organisation and
 * distribution channel <br>
 *
 * @version 1.0
 */
public class DistChannelMappingImpl implements DistChannelMapping {

	protected String distChannelForConditions;
	protected String distChannelForSalesDocTypes;
	protected String distChannelForCustomerMatirial;
	protected String referencePlant;
	protected String distChainCategory;
	protected String allowedPricingLevel;

	@Override
	public String getDistChannelForConditions() {
		return distChannelForConditions;
	}

	@Override
	public void setDistChannelForConditions(String distChannelForConditions) {
		this.distChannelForConditions = distChannelForConditions;
	}

	@Override
	public String getDistChannelForSalesDocTypes() {
		return distChannelForSalesDocTypes;
	}

	@Override
	public void setDistChannelForSalesDocTypes(String distChannelForSalesDocTypes) {
		this.distChannelForSalesDocTypes = distChannelForSalesDocTypes;
	}

	@Override
	public String getDistChannelForCustomerMatirial() {
		return distChannelForCustomerMatirial;
	}

	@Override
	public void setDistChannelForCustomerMatirial(String distChannelForCustomerMatirial) {
		this.distChannelForCustomerMatirial = distChannelForCustomerMatirial;
	}

	@Override
	public String getReferencePlant() {
		return referencePlant;
	}

	@Override
	public void setReferencePlant(String referencePlant) {
		this.referencePlant = referencePlant;
	}

	@Override
	public String getDistChainCategory() {
		return distChainCategory;
	}

	@Override
	public void setDistChainCategory(String distChainCategory) {
		this.distChainCategory = distChainCategory;
	}

	@Override
	public String getAllowedPricingLevel() {
		return allowedPricingLevel;
	}

	@Override
	public void setAllowedPricingLevel(String allowedPricingLevel) {
		this.allowedPricingLevel = allowedPricingLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allowedPricingLevel == null) ? 0 : allowedPricingLevel.hashCode());
		result = prime * result + ((distChainCategory == null) ? 0 : distChainCategory.hashCode());
		result = prime * result + ((distChannelForConditions == null) ? 0 : distChannelForConditions.hashCode());
		result = prime * result
				+ ((distChannelForCustomerMatirial == null) ? 0 : distChannelForCustomerMatirial.hashCode());
		result = prime * result + ((distChannelForSalesDocTypes == null) ? 0 : distChannelForSalesDocTypes.hashCode());
		result = prime * result + ((referencePlant == null) ? 0 : referencePlant.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("squid:S1067") // Suppressing as splitting it would be
										// more of artificially catering to
										// SONAR
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		DistChannelMappingImpl other = (DistChannelMappingImpl) obj;
		return compareAttributes(allowedPricingLevel, other.allowedPricingLevel)
				&& compareAttributes(distChainCategory, other.distChainCategory)
				&& compareAttributes(distChannelForConditions, other.distChannelForConditions)
				&& compareAttributes(distChannelForCustomerMatirial, other.distChannelForCustomerMatirial)
				&& compareAttributes(distChannelForSalesDocTypes, other.distChannelForSalesDocTypes)
				&& compareAttributes(referencePlant, other.referencePlant);
	}

	private boolean compareAttributes(String thisString, String otherString) {
		if ((thisString != null && thisString.equals(otherString)) || (thisString == null && otherString == null)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "DistChannelMappingImpl [allowedPricingLevel=" + allowedPricingLevel + ", distChainCategory="
				+ distChainCategory + ", distChannelForConditions=" + distChannelForConditions
				+ ", distChannelForCustomerMatirial=" + distChannelForCustomerMatirial
				+ ", distChannelForSalesDocTypes=" + distChannelForSalesDocTypes + ", referencePlant=" + referencePlant
				+ "]";
	}

}
