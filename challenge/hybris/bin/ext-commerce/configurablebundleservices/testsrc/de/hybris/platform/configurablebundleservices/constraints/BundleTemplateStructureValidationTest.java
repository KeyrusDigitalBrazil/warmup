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
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.validation.exceptions.HybrisConstraintViolation;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@IntegrationTest
public class BundleTemplateStructureValidationTest extends AbstractBundleValidationTest
{

    @Before
    public void setup() throws ImpExException
    {
        super.setup();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldAllowRequiredComponent() throws ImpExException
    {
        importString("REMOVE TypeConstraint;id[unique=true,allownull=true]\n" +
                ";BundleTemplateNonLeafConstraint1\n");
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];requiredBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;NestedGroup2"
        );
    }

    @Test
    public void shouldAllowDependentComponent() throws ImpExException
    {
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];dependentBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                 + ";ProductComponent1;;OptionalComponent"
        );
    }

    @Test
    public void shouldRejectThisInRequiredComponents() throws ImpExException
    {
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("There is a circular dependency, please remove the required bundle template 'ProductComponent1'.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];requiredBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;ProductComponent1"
        );
    }

    @Test
    public void shouldRejectAncestorInRequiredComponents() throws ImpExException
    {
        importString("REMOVE TypeConstraint;id[unique=true,allownull=true]\n" +
                ";BundleTemplateNonLeafConstraint1\n");
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("There is a circular dependency, please remove the required bundle template 'NestedGroup1'.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];requiredBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;NestedGroup1"
        );
    }

    @Test
    public void shouldRejectParentInRequiredComponents() throws ImpExException
    {
        importString("REMOVE TypeConstraint;id[unique=true,allownull=true]\n" +
                ";BundleTemplateNonLeafConstraint1\n");
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("There is a circular dependency, please remove the required bundle template 'NestedComponent1'.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];requiredBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;NestedComponent1"
        );
    }

    @Test
    public void shouldNotRejectForeignersInRequiredComponents() throws ImpExException
    {
        importString("REMOVE TypeConstraint;id[unique=true,allownull=true]\n" +
                ";BundleTemplateNonLeafConstraint1\n");
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];requiredBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;SecondGroup2"
        );
        Set<HybrisConstraintViolation> violations = validate("ProductComponent1", null);
        assertThat(violations, hasItem(hasProperty("localizedMessage",
                is("The required bundle template 'SecondGroup2' is not part of the whole bundle, please remove it."))));

    }

    @Test
    public void shouldNotRejectForeignersInDependentComponents() throws ImpExException
    {
        importString("REMOVE TypeConstraint;id[unique=true,allownull=true]\n" +
                ";BundleTemplateNonLeafConstraint1\n");
        importString(
                "INSERT BundleTemplate;id[unique=true];version[default=1.0][unique=true];dependentBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";NewProductComponent;;SecondGroup2"
        );
        Set<HybrisConstraintViolation> violations = validate("NewProductComponent", null);
        assertThat(violations, hasItem(hasProperty("localizedMessage",
                is("The dependent bundle template 'SecondGroup2' is not part of the whole bundle, please remove it."))));

    }

    @Test
    public void shouldRejectThisInDependingComponents() throws ImpExException
    {
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("This bundle template cannot be dependent on itself. Please remove bundle template 'ProductComponent1'.\n"));
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];dependentBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;ProductComponent1"
        );
    }

    @Test
    public void shouldRejectAncestorInDependingComponents() throws ImpExException
    {
        importString("REMOVE TypeConstraint;id[unique=true,allownull=true]\n" +
                ";BundleTemplateNonLeafConstraint1\n");
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("There is a circular dependency, please remove the dependent bundle template 'NestedGroup1'.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];dependentBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;NestedGroup1"
        );
    }

    @Test
    public void shouldAllowLeafsToHaveSelectionCriteria() throws ImpExException
    {
        importString(
                "UPDATE BundleTemplate;id[unique=true];bundleSelectionCriteria(id,$catalogversion);version[default=1.0][unique=true]\n"
                + ";ProductComponent1;PremiumComponent_PickExactly_2"
        );
    }

    @Test
    public void shouldAllowNonLeafsToHaveNoSelectionCriteria() throws ImpExException
    {
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];parentTemplate(id);childTemplates(id,$catalogversion);version[default=1.0][unique=true];status(id)[default='testBundleStatus'];$catalogversion\n"
                + ";NestedComponent3;NestedGroup1;ProductComponent1\n"
        );
    }

    @Test
    public void shouldDenyNonLeafsToHaveAnyCriteria() throws ImpExException
    {
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("Only Leaf Bundle Template can have selection criteria.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0];bundleSelectionCriteria(id, $catalogversion);$catalogversion\n"
                + ";NestedGroup2;;ProductComponent_PickExactly_2"
        );
    }

    @Test
    public void shouldValidateOnCreate() throws ImpExException
    {
        importString(
                "INSERT_UPDATE BundleTemplate;id[unique=true];parentTemplate(id, $catalogversion);version[default=1.0][unique=true];$catalogversion\n"
                + ";NewComponent;ParentPackage"
        );
        Set<HybrisConstraintViolation> violations = validate("NewComponent", null);
        assertThat(violations, hasItem(hasProperty(FIELD_MESSAGE,
                is("Every bundle template should either have child bundle templates or products assigned. Please add either of them."))));
    }

    @Test
    public void shouldRejectNonLeafInRequiredComponents() throws ImpExException
    {
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("The bundle template 'NestedGroup2' is a non leaf node, please remove it.\n"));
         importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];requiredBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;NestedGroup2"
        );
    }

    @Test
    public void shouldRejectNonLeafInDependingComponents() throws ImpExException
    {
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("The bundle template 'NestedGroup2' is a non leaf node, please remove it.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0][unique=true];dependentBundleTemplates(id,version[default=1.0]);$catalogversion\n"
                + ";ProductComponent1;;NestedGroup2"
        );
    }

    @Test
    public void shouldDenyPriceRulesInNonLeafs() throws ImpExException
    {
        importString(
                "INSERT_UPDATE ChangeProductPriceBundleRule;$catalogversion;id[unique=true];currency(isocode)[default=USD, unique=true];bundleTemplate(id, version[default=1.0], $catalogversion)[unique=true];ruleType(code)[default=ANY];conditionalProducts(code, $catalogversion);targetProducts(code, $catalogversion);price\n"
                + ";;test_price_rule;;NestedGroup1;;PRODUCT01;PRODUCT02;1\n\n"
        );
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("Only leaf bundle templates can have price rules.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0];changeProductPriceBundleRules(id,$catalogversion)\n"
                + ";NestedGroup1;;test_price_rule\n"
        );
    }

    @Test
    public void shouldDenyDisableRulesInNonLeafs() throws ImpExException
    {
        importString(
                "INSERT_UPDATE DisableProductBundleRule;id[unique=true];$catalogversion;bundleTemplate(id,version[default=1.0],$catalogversion);conditionalProducts(code,$catalogversion);targetProducts(code,$catalogversion);ruleType(code)[default=ANY]\n"
                + ";test_disable_rule;;NestedGroup1;PRODUCT01;PRODUCT02\n\n"
        );
        thrown.expect(AssertionError.class);
        thrown.expectMessage(endsWith("Only leaf bundle templates can have disabling rules.\n"));
        importString(
                "UPDATE BundleTemplate;id[unique=true];version[default=1.0];disableProductBundleRules(id,$catalogversion)\n"
                + ";NestedGroup1;;test_disable_rule\n"
        );
    }
}
