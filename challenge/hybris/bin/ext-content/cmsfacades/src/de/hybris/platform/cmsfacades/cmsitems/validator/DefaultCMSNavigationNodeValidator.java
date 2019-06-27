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
package de.hybris.platform.cmsfacades.cmsitems.validator;

import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;

/**
 * Default implementation of the validator for {@link CMSNavigationNodeModel}
 */
public class DefaultCMSNavigationNodeValidator implements Validator<CMSNavigationNodeModel>
{
    private ValidationErrorsProvider validationErrorsProvider;

    @Override
    public void validate(final CMSNavigationNodeModel validatee)
    {
        List<CMSNavigationEntryModel> associatedEntries = getAssociatedEntries(validatee);

        if (!associatedEntries.isEmpty()) {
            getValidationErrorsProvider().getCurrentValidationErrors().add(newValidationErrorBuilder() //
                    .field(CMSNavigationNodeModel.ENTRIES) //
                    .errorCode(CmsfacadesConstants.INVALID_NAVIGATION_ENTRIES) //
                    .errorArgs(new Object[] { associatedEntries.stream().map(CMSNavigationEntryModel::getName).collect(Collectors.joining(", ")) }) //
                    .build());
        }
    }

    /**
     * Retrieves a list of all entries that are already associated to another model.
     *
     * @param node The CMSNavigationNodeModel to inspect.
     * @return The list of associated nodes.
     */
    protected List<CMSNavigationEntryModel> getAssociatedEntries(final CMSNavigationNodeModel node)
    {
        return Optional.ofNullable(node.getEntries())
                .orElse(Collections.emptyList())
                .stream().filter((entry) -> {
                    CMSNavigationNodeModel navigationNode = entry.getNavigationNode();
                    return Objects.nonNull(navigationNode) && !node.getUid().equals(navigationNode.getUid());
                }).collect(Collectors.toList());
    }

    protected ValidationErrorsProvider getValidationErrorsProvider()
    {
        return validationErrorsProvider;
    }

    @Required
    public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
    {
        this.validationErrorsProvider = validationErrorsProvider;
    }
}
