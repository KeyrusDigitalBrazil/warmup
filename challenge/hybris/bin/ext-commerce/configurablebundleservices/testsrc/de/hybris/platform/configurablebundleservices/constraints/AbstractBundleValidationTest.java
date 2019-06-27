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

package de.hybris.platform.configurablebundleservices.constraints;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import de.hybris.platform.impex.constants.ImpExConstants;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.util.Config;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import de.hybris.platform.validation.model.constraints.ConstraintGroupModel;
import de.hybris.platform.validation.model.constraints.jsr303.AbstractConstraintTest;
import de.hybris.platform.validation.services.ValidationService;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;

public class AbstractBundleValidationTest extends AbstractConstraintTest
{
    private static final Logger LOG = Logger.getLogger(AbstractBundleValidationTest.class);

    protected static final String FIELD_MESSAGE = "localizedMessage";
    protected static final String FIELD_PROPERTY = "property";
    protected static final String FIELD_SEVERITY = "violationSeverity";

    @Resource
    private ValidationService validationService;
    @Resource
    private CatalogVersionService catalogVersionService;

    public void setup() throws ImpExException
    {
        LOG.debug("Preparing test data");
        final String legacyModeBackup = Config.getParameter(ImpExConstants.Params.LEGACY_MODE_KEY);
        Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, "true");
        try
        {
            importCsv("/impex/bundleconstraints.impex", "UTF-8");
            importCsv("/configurablebundleservices/test/cartRegistration.impex", "utf-8");
            importCsv("/configurablebundleservices/test/nestedBundleTemplates.impex", "UTF-8");
        }
        finally
        {
            Config.setParameter(ImpExConstants.Params.LEGACY_MODE_KEY, legacyModeBackup);
        }
        validationService.reloadValidationEngine();
    }

    /**
     * Validate bundle template against given validation group.
     *
     * @param bundleTemplateId bundle template id
     * @param constraintGroupId id of validator group to use
     * @return collection of validation violations
     */
    protected Set<HybrisConstraintViolation> validate(final String bundleTemplateId, final String constraintGroupId)
    {
        return getValidationService().validate(
            getBundleTemplate(bundleTemplateId),
            Collections.singletonList(constraintGroupId == null
                ? getValidationService().getDefaultConstraintGroup()
                : getGroup(constraintGroupId))
        );
    }

    /**
     * Get bundle template by ID.
     *
     * @param templateId id
     * @return bundle template
     */
    protected BundleTemplateModel getBundleTemplate(final String templateId)
    {
        final BundleTemplateModel exampleModel = new BundleTemplateModel();
        exampleModel.setId(templateId);
        exampleModel.setCatalogVersion(getCatalog());
        return flexibleSearchService.getModelByExample(exampleModel);
    }

    protected CatalogVersionModel getCatalog()
    {
        return catalogVersionService.getCatalogVersion("testCatalog", "Online");
    }

    protected ConstraintGroupModel getGroup(final String id)
    {
        final ConstraintGroupModel sample = new ConstraintGroupModel();
        sample.setId(id);
        return flexibleSearchService.getModelByExample(sample);
    }

    /**
     * Import given string as ImpEx.
     *
     * @param impex impex script body
     */
    protected void importString(final String impex) throws ImpExException
    {
        importStream(IOUtils.toInputStream(
            "$catalog-id=testCatalog\n"
                + "$catalog-version=Online\n"
                + "$approved=approvalstatus(code)[default='approved']\n"
                + "$catalogversion=catalogversion(catalog(id[default=$catalog-id]),version[default=$catalog-version])[unique=true,default=$catalog-id:$catalog-version]\n"
                + impex,
            StandardCharsets.UTF_8), "UTF-8", "");
    }

    protected ValidationService getValidationService()
    {
        return validationService;
    }

    protected CatalogVersionService getCatalogVersionService()
    {
        return catalogVersionService;
    }
}
