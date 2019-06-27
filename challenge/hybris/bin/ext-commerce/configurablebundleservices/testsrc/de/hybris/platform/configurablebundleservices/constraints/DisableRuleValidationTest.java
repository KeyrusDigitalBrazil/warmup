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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.configurablebundleservices.model.AbstractBundleRuleModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.validation.enums.Severity;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@IntegrationTest
public class DisableRuleValidationTest extends AbstractBundleValidationTest
{

    @Before
    public void setup() throws ImpExException
    {
        super.setup();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldWarnAboutEmptyConditionalProducts() throws ImpExException
    {
        importString(
                "INSERT_UPDATE DisableProductBundleRule;id[unique=true];bundleTemplate(id, version, $catalogversion)[unique=true];targetProducts(code, $catalogversion);$catalogversion;ruleType(code)[default=ANY]\n"
                + ";disable_PRODUCT02_in_ProductComponent1;ProductComponent1:1.0;PRODUCT02\n"
        );
        Set<HybrisConstraintViolation> violations = validate("disable_PRODUCT02_in_ProductComponent1");
        assertThat(violations, hasItem(allOf(
                hasProperty("localizedMessage", is("Each disableRule must at least have one conditional product assigned. Please add one conditional product or remove this disable rule 'disable_PRODUCT02_in_ProductComponent1'.")),
                hasProperty("violationSeverity", is(Severity.WARN))
        )));
    }

    @Test
    public void shouldWarnAboutEmptyTargetProductList() throws ImpExException
    {
        importString(
                "INSERT DisableProductBundleRule;id[unique=true];bundleTemplate(id, version, $catalogversion)[unique=true];conditionalProducts(code, $catalogversion);targetProducts(code, $catalogversion);$catalogversion;ruleType(code)[default=ANY]\n"
                + ";disable_NoTarget_ProductComponent1;ProductComponent1:1.0;PRODUCT01,PRODUCT02\n"
        );
        Set<HybrisConstraintViolation> violations = validate("disable_NoTarget_ProductComponent1");
        assertThat(violations, hasItem(allOf(
                hasProperty("localizedMessage", is("Each disableRule must at least have one target product assigned. Please add one target product or remove this disable rule.")),
                hasProperty("violationSeverity", is(Severity.WARN))
        )));
    }

    @Test
    public void shouldNotDenyForeignersInTargetProducts() throws ImpExException
    {
        importString(
                "INSERT DisableProductBundleRule;id[unique=true];bundleTemplate(id, version, $catalogversion)[unique=true];conditionalProducts(code, $catalogversion);targetProducts(code, $catalogversion);$catalogversion;ruleType(code)[default=ANY]\n"
                + ";disable_Foreign_ProductComponent1;ProductComponent1:1.0;PRODUCT01,PRODUCT02;OTHER01,OTHER02\n"
        );
        Set<HybrisConstraintViolation> violations = validate("disable_Foreign_ProductComponent1");
        assertThat(violations, containsInAnyOrder(
                hasProperty("localizedMessage",
                        is("This disable rule has a target product 'OTHER01' which is not a part of the bundle template. Please either add the product 'OTHER01' to the bundle template list or remove this disable rule.")),
                hasProperty("localizedMessage",
                        is("This disable rule has a target product 'OTHER02' which is not a part of the bundle template. Please either add the product 'OTHER02' to the bundle template list or remove this disable rule."))
        ));
    }

    @Test
    public void shouldWarnAboutStandaloneInTargetProducts() throws ImpExException
    {
        importString(
                "INSERT DisableProductBundleRule;id[unique=true];bundleTemplate(id, version, $catalogversion)[unique=true];conditionalProducts(code, $catalogversion);targetProducts(code, $catalogversion);$catalogversion;ruleType(code)[default=ANY]\n"
                + ";disable_Standalone_ProductComponent1;ProductComponent1:1.0;PRODUCT01,PRODUCT02;STANDALONE01\n"
        );
        Set<HybrisConstraintViolation> violations = validate("disable_Standalone_ProductComponent1");
        assertThat(violations, hasItem(hasProperty("localizedMessage",
                is("This disable rule has a target product 'STANDALONE01' which is not a part of the bundle template. Please either add the product 'STANDALONE01' to the bundle template list or remove this disable rule."))));
    }

    @Test
    public void shouldNotDenyMixedInTargetProducts() throws ImpExException
    {
        importString(
                "INSERT DisableProductBundleRule;id[unique=true];bundleTemplate(id, version, $catalogversion)[unique=true];conditionalProducts(code, $catalogversion);targetProducts(code, $catalogversion);$catalogversion;ruleType(code)[default=ANY]\n"
                + ";disable_Mixed_ProductComponent1;ProductComponent1:1.0;PRODUCT01,PRODUCT02;STANDALONE01,OTHER01\n"
        );
        Set<HybrisConstraintViolation> violations = validate("disable_Mixed_ProductComponent1");
        assertThat(violations, containsInAnyOrder(
                hasProperty("localizedMessage",
                        is("This disable rule has a target product 'STANDALONE01' which is not a part of the bundle template. Please either add the product 'STANDALONE01' to the bundle template list or remove this disable rule.")),
                hasProperty("localizedMessage",
                        is("This disable rule has a target product 'OTHER01' which is not a part of the bundle template. Please either add the product 'OTHER01' to the bundle template list or remove this disable rule."))
        ));
    }

    protected Set<HybrisConstraintViolation> validate(final String ruleId)
    {
        return getValidationService().validate(
                getBundleRule(ruleId),
                Collections.singletonList(getValidationService().getDefaultConstraintGroup())
        );
    }

    protected AbstractBundleRuleModel getBundleRule(final String ruleId)
    {
        final AbstractBundleRuleModel rule = new AbstractBundleRuleModel();
        rule.setId(ruleId);
        rule.setCatalogVersion(getCatalog());
        return flexibleSearchService.getModelByExample(rule);
    }
}
