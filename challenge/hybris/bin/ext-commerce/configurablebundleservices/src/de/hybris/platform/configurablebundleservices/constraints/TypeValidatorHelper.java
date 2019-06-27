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

import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.validation.messages.ResourceBundleProvider;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * A helper class for validators providing some common functionality of error message constructing.
 */
public class TypeValidatorHelper
{
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(TypeValidatorHelper.class.getName());

    /**
     * Pattern to check whether validation message is a string resource id.
     */
    protected static final Pattern MESSAGE_PARAMETER_PATTERN = Pattern.compile("\\{([^\\}]+?)\\}");

    @Resource(name = "i18nService")
    private I18NService i18nService;
    @Resource(name = "resourceBundleProvider")
    private ResourceBundleProvider resourceBundleProvider;

    /**
     * Creates custom error message.
     * <p>
     * Message template is taken from validation context.
     * In general it is the one specified in {@code C.message} field.
     * Strings {string.resource.id} treated as string key of localized string resource.
     * Current add locale is used to resolve value.<br/>
     *
     * To have such string as it is, double the surrounding curly brackets:
     * <code>
     *     {{not.a.resource{0}}}
     * </code>
     * will be show as
     * <code>
     *     {not.a.resource&lt;value_of_first_arg&gt;}
     * </code>
     *
     * </p><p>
     * The resulting string is then passed to {@link MessageFormat#format(String, Object...)} along with {@code args}
     * only if any args provided.
     *
     * </p><p>
     * Please pay attention to syntax inconsistency between parametrized and non-parametrized lines:
     * {@code It''s a parametrized line with arg ''{0}''}
     * but
     * {@code It'a a non-parametrized line}
     * </p>
     *
     * @see BundleTemplateDependsOnAncestorValidator
     * @see MessageFormat#format(String, Object...)
     * @see I18NService
     *
     * @param fieldName name of field where the error occurred
     * @param context validation context
     * @param args optional message arguments
     * @throws IllegalStateException
     *         if the instance has not been initialized by Spring
     *         (see {@link BasicBundleRuleValidator#initialize(Annotation)} as an example of initialization)
     */
    protected void buildErrorMessage(final String fieldName, // NOSONAR
                                     @Nonnull final ConstraintValidatorContext context,
                                     final Object ... args)
    {
        validateParameterNotNull(context, "Validation context can not be null");
        String template = context.getDefaultConstraintMessageTemplate();
        final Matcher matcher = MESSAGE_PARAMETER_PATTERN.matcher(template);
        if (matcher.matches())
        {
            final String resourceId = matcher.group(1);
            template = getLocalizedString(resourceId, getCurrentLocale());
        }
        else
        {
            template = template
                    .replace("^\\{\\{", "{")
                    .replace("\\}\\}$", "}");
        }
        String message;
        // It leads to syntax inconsistency, but we can not avoid it, because not all of the strings
        // pass through this method. So having {#} marker is better that relying on knowledge of
        // how message if processed inside of particular validator.
        if (args.length > 0)
        {
            message = MessageFormat.format(template, args);
        }
        else
        {
            message = template;
        }
        final ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder
                = context.buildConstraintViolationWithTemplate(message);
        context.disableDefaultConstraintViolation();
        constraintViolationBuilder.addConstraintViolation();
    }

    protected Locale getCurrentLocale()
    {
        final I18NService localizationService = getI18nService();
        if (localizationService == null)
        {
            throw new IllegalStateException(
                    "Field " + getClass().getName()
                    + "#i18nService has not been initialized. Probably forgot to autowire the instance?");
        }
        return localizationService.getCurrentLocale();
    }

    protected String getLocalizedString(final String key, final Locale locale)
    {
        final ResourceBundleProvider bundleProvider = getResourceBundleProvider();
        if (bundleProvider == null)
        {
            throw new IllegalStateException(
                    "Field " + getClass().getName()
                    + "#resourceBundleProvider has not been initialized. Probably forgot to autowire the instance?");
        }
        ResourceBundle bundle = bundleProvider.getResourceBundle(locale);
        if (bundle == null)
        {
            bundle = bundleProvider.getResourceBundle(Locale.ENGLISH);
            if (bundle == null)
            {
                LOG.warn("String with id '" + key + "' has no localization for locale " + locale.toString());
                return "#" + key;
            }
        }
        return bundle.getString(key);
    }

    protected I18NService getI18nService()
    {
        return i18nService;
    }

    protected ResourceBundleProvider getResourceBundleProvider()
    {
        return resourceBundleProvider;
    }
}
