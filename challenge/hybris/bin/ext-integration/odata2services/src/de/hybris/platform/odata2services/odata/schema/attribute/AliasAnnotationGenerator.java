/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.odata.schema.attribute;

import static de.hybris.platform.odata2services.constants.Odata2servicesConstants.ALIAS_ANNOTATION_ATTR_NAME;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.integrationkey.IntegrationKeyMetadataGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.olingo.odata2.api.edm.provider.AnnotationAttribute;
import org.springframework.beans.factory.annotation.Required;

public class AliasAnnotationGenerator
{
	private IntegrationKeyMetadataGenerator integrationKeyMetadataGenerator;

	public AnnotationAttribute generate(final IntegrationObjectItemModel itemModel)
	{
		final String integrationKeyAlias = integrationKeyMetadataGenerator.generateKeyMetadata(itemModel);
		if (StringUtils.isNotEmpty(integrationKeyAlias))
		{
			return new AnnotationAttribute()
					.setName(ALIAS_ANNOTATION_ATTR_NAME)
					.setText(integrationKeyAlias);
		}
		return null;
	}

	@Required
	public void setIntegrationKeyMetadataGenerator(final IntegrationKeyMetadataGenerator integrationKeyMetadataGenerator)
	{
		this.integrationKeyMetadataGenerator = integrationKeyMetadataGenerator;
	}
}
