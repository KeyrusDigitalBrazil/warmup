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
package de.hybris.platform.cmsfacades.constants;

/**
 * Global class for all Cmsfacades constants. You can add global constants for your extension into this class.
 */
public final class CmsfacadesConstants extends GeneratedCmsfacadesConstants
{
	public static final String EXTENSIONNAME = "cmsfacades";

	public static final String VISITORS_CTX_LOCALES = "VISITORS_CTX_LOCALES";
	public static final String VISITORS_CTX_TARGET_CATALOG_VERSION = "VISITORS_CTX_TARGET_CATALOG_VERSION";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	public static final String DISPLAY_DATETIME_FORMAT = "MM/dd/yy hh:mm:ssa";
	public static final String TYPE_CACHE_EXPIRATION = "cmsfacades.types.cache.expiration";

	public static final String FIELD_REQUIRED = "field.required";
	public static final String FIELD_REQUIRED_L10N = "field.required.l10n";
	public static final String FIELD_ALREADY_EXIST = "field.already.exist";
	public static final String DEFAULT_PAGE_ALREADY_EXIST = "default.page.already.exist";
	public static final String DEFAULT_PAGE_DOES_NOT_EXIST = "default.page.does.not.exist";
	public static final String DEFAULT_PAGE_LABEL_ALREADY_EXIST = "default.page.label.already.exist";
	public static final String DEFAULT_PAGE_HAS_VARIATIONS = "default.page.has.variations";
	public static final String TOP_LEVEL_HOMEPAGE_CANNOT_BE_REMOVED = "top.level.homepage.cannot.be.removed";
	public static final String FIELD_DOES_NOT_EXIST = "field.doesnot.exist";
	public static final String FIELD_NOT_ALLOWED = "field.not.allowed";
	public static final String FIELD_CONTAINS_INVALID_CHARS = "field.contains.invalid.chars";
	public static final String FIELD_LENGTH_EXCEEDED = "field.length.exceeded";
	public static final String FIELD_NOT_INTEGER = "field.integer.invalid";
	public static final String FIELD_NOT_FLOAT = "field.float.invalid";
	public static final String FIELD_NOT_LONG = "field.long.invalid";
	public static final String FIELD_NOT_DOUBLE = "field.double.invalid";
	public static final String FIELD_NOT_POSITIVE_INTEGER = "field.positive.integer.invalid";
	public static final String FIELD_MIN_VIOLATED = "field.min.violated";
	public static final String FIELD_MAX_VIOLATED = "field.max.violated";
	public static final String FIELD_FORMAT_INVALID = "field.format.invalid";
	public static final String FIELD_MEDIA_FORMAT_REQUIRED = "field.media.format.required";
	public static final String FIELD_MEDIA_FORMAT_REQUIRED_L10N = "field.media.format.required.l10n";
	public static final String FIELD_INVALID_UUID_L10N = "field.invalid.uuid";
	public static final String ITEM_WITH_NAME_ALREADY_EXIST = "item.with.name.already.exist";
	public static final String INVALID_TYPECODE_COMBINATION = "invalid.typecode.combination";
	public static final String INVALID_TYPECODE_VALUE = "invalid.typecode.value";

	public static final String INVALID_ROOT_NODE_UID = "invalid.navigation.node.root.uid";
	public static final String INVALID_NAVIGATION_ENTRIES = "invalid.navigation.entries";
	public static final String INVALID_NAVIGATION_NODE_UID = "invalid.navigation.node.uid";
	public static final String INVALID_NAVIGATION_NODE_PARENT_UID = "invalid.navigation.node.parent.uid";

	public static final String INVALID_URL_FORMAT = "url.format.invalid";
	public static final String LINK_ITEMS_EXCEEDED = "link.items.exceeded";
	public static final String LINK_MISSING_ITEMS = "link.items.missing";

	public static final String INVALID_DATE_RANGE = "date.range.invalid";
	public static final String INVALID_MEDIA_CODE = "media.code.invalid";
	public static final String INVALID_MEDIA_CODE_L10N = "media.code.invalid.l10n";
	public static final String INVALID_MEDIA_FORMAT_MEDIA_CODE = "media.format.media.code.invalid";
	public static final String INVALID_MEDIA_FORMAT_MEDIA_CODE_L10N = "media.format.media.code.invalid.l10n";
	public static final String INVALID_SORT_PARAMETER = "sort.parametername.invalid";
	public static final String INVALID_SORT_DIRECTION = "sort.direction.invalid";
	public static final String INVALID_PARAMS_PARAMETER = "params.parametername.invalid";
	public static final String MEDIA_INPUT_STREAM_CLOSED = "media.inputstream.closed";
	public static final String UNAUTHORIZED_SYNCHRONIZATION_READ = "unauthorized.synchronization.read";
	public static final String UNAUTHORIZED_SYNCHRONIZATION_WRITE = "unauthorized.synchronization.write";
	public static final String UNAUTHORIZED_SYNCHRONIZATION_INSUFFICIENT_ACCESS = "unauthorized.synchronization.insufficient.access";
	public static final String ACTIVE_SYNC_JOB_REQUIRED = "active.sync.job.required";

	public static final String COMPONENT_ALREADY_EXIST_SLOT = "component.already.exist.slot";

	public static final String MEDIA_FORMAT = "mediaFormat";

	/**
	 * @deprecated since 1808 please use {@code Cms2Constants#ROOT}
	 */
	@Deprecated
	public static final String ROOT_NODE_UID = "root";

	public static final String FIELD_REQUIRED_NAVIGATION_NODE_ENTRY = "field.required.navigation.node.entry";
	public static final String FIELD_CIRCULAR_DEPENDENCY_ON_NAVIGATION_NODE_ENTRY = "field.circular.dependency.on.navigation.node.entry";
	public static final String FIELD_NAVIGATION_NODE_ENTRY_CONVERTER_NOT_FOUND = "field.navigation.node.entry.converter.not.found";
	public static final String FIELD_NAVIGATION_NODE_ENTRY_CONVERSION_ERROR = "field.navigation.node.entry.conversion.error";
	public static final String TYPES_INVALID_QUALIFIER_ATTR = "types.invalid.qualifier.attribute";
	public static final String PAGE_DISPLAY_CONDITION_PRIMARY = "page.displaycondition.primary";
	public static final String PAGE_DISPLAY_CONDITION_VARIATION = "page.displaycondition.variation";
	public static final String RESTRICTION_SET_FOR_PRIMARY_PAGE = "restriction.set.primary.page";
	public static final String NO_RESTRICTION_SET_FOR_VARIATION_PAGE = "no.restriction.set.variation.page";

	public static final String PRODUCTS_OR_CATEGORIES_REQUIRED = "select.one.of.products.or.categories";

	public static final String CMSITEMS_INVALID_CONVERSION_ERROR = "cmsitems.invalid.conversion.error";

	public static final String INVALID_PAGE_LABEL_OR_ID = "invalid.content.page.label.or.id";

	public static final String VERSION_DOES_NOT_BELONG_TO_CMS_ITEM = "cmsversion.does.not.belong.to.cmsitem";
	public static final String VERSION_ROLLBACK_DESC = "cmsversion.rollback.description";
	public static final String VERSION_ROLLBACK_LABEL_PREFIX = "auto-version-";
	public static final String VERSION_REMOVE_INVALID_VERSION_UID = "cmsversion.remove.invalid.uid";

	/**
	 * Static Field for the UUID variable name
	 */
	public static final String FIELD_UUID = "uuid";
	public static final String FIELD_UID = "uid";
	public static final String FIELD_CATALOG_VERSION = "catalogVersion";
	public static final String FIELD_CLONE_COMPONENT = "cloneComponent";
	public static final String FIELD_CLONE_COMPONENTS = "cloneComponents";
	public static final String FIELD_CLONEABLE_NAME = "cloneable";
	public static final String FIELD_PAGE_UUID = "pageUuid";
	public static final String FIELD_CONTENT_SLOT_UUID = "contentSlotUuid";
	public static final String FIELD_COMPONENT_UUID = "componentUuid";
	public static final String FIELD_EXTERNAL_NAME = "external";
	public static final String FIELD_LINK_TOGGLE_NAME = "linkToggle";
	public static final String FIELD_URI_CONTEXT = "uriContext";
	public static final String FIELD_URL_LINK_NAME = "urlLink";
	public static final String FIELD_ITEM_UUID = "itemUUID";
	public static final String FIELD_LABEL = "label";
	public static final String FIELD_PAGE_REPLACE = "replace";
	public static final String FIELD_COMPONENTS = "components";


	public static final String SESSION_VALIDATION_ERRORS_OBJ = "SESSION_VALIDATION_ERRORS_OBJ";
	public static final String SESSION_ORIGINAL_ITEM_MODEL = "SESSION_ORIGINAL_ITEM_MODEL";

	public static final String SESSION_CLONE_COMPONENT_CONTEXT = "SESSION_CLONE_COMPONENT_CONTEXT";
	public static final String SESSION_CLONE_COMPONENT_SOURCE_MAP = "SESSION_CLONE_COMPONENT_SOURCE_MAP";
	public static final String SESSION_CLONE_COMPONENT_CLONE_MODEL = "SESSION_CLONE_COMPONENT_CLONE_MODEL";
	public static final String SESSION_CLONE_COMPONENT_SOURCE_ATTRIBUTE = "SESSION_CLONE_COMPONENT_ATTRIBUTE";
	public static final String SESSION_CLONE_COMPONENT_LOCALE = "SESSION_CLONE_COMPONENT_LOCALE";

	/* Clone context */
	public static final String CURRENT_CONTEXT_SITE_ID = "CURRENT_CONTEXT_SITE_ID";
	public static final String CURRENT_CONTEXT_CATALOG = "CURRENT_CONTEXT_CATALOG";
	public static final String CURRENT_CONTEXT_CATALOG_VERSION = "CURRENT_CONTEXT_CATALOG_VERSION";

	/* Restriction context */
	public static final String SESSION_RESTRICTION_CONTEXT_ITEM = "SESSION_RESTRICTION_CONTEXT_ITEM";

	private CmsfacadesConstants()
	{
		//empty to avoid instantiating this constant class
	}

}
