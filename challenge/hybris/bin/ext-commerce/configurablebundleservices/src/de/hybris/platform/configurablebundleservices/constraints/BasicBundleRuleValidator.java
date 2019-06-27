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

import de.hybris.platform.configurablebundleservices.model.AbstractBundleRuleModel;
import de.hybris.platform.configurablebundleservices.model.BundleTemplateModel;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;

import javax.annotation.Nonnull;
import javax.validation.ConstraintValidator;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

/**
 * Base class for {@link AbstractBundleRuleModel} validators.
 * @param <A>
 */
public abstract class BasicBundleRuleValidator<A extends Annotation>
        extends TypeValidatorHelper
        implements ConstraintValidator<A, AbstractBundleRuleModel>
{
    private static final Logger LOG = Logger.getLogger(BasicBundleRuleValidator.class);

    @Override
    public void initialize(final A a)
    {
        // Autowire beans of TypeValidatorHelper
        SpringContextProvider.getContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    /**
     * Gets parent bundle template of given rule.
     * <p>
     *     If there is an error, writes details to log and returns {@code null}.
     * </p>
     *
     * @param rule rule
     * @return parent template
     */
    protected BundleTemplateModel getBundleTemplate(@Nonnull final AbstractBundleRuleModel rule)
    {
        try
        {
            final PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(rule.getClass(), "bundleTemplate");
            return (BundleTemplateModel) descriptor.getReadMethod().invoke(rule);
        }
        catch (final BeansException | InvocationTargetException | IllegalAccessException e)
        {
            LOG.error("Error getting bundleTemplate of " + rule.getClass().getName(), e);
        }
        catch (final ClassCastException e)
        {
            LOG.error("Return type of " + rule.getClass().getName()
                    + "#bundleTemplate is not correct. BundleTemplateModel is expected.", e);
        }
        return null;
    }

}
