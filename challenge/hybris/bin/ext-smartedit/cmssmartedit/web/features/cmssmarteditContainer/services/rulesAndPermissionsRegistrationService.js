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
angular.module('rulesAndPermissionsRegistrationModule', [
        'catalogVersionPermissionModule',
        'catalogVersionRestServiceModule',
        'pageServiceModule',
        'pageVersionsModule',
        'cmsSmarteditServicesModule',
        'smarteditServicesModule',
        'cmsitemsRestServiceModule'
    ])

    .run(function(
        catalogService,
        catalogVersionPermissionService,
        catalogVersionRestService,
        pageService,
        permissionService,
        sharedDataService,
        cMSModesService,
        typePermissionsRestService,
        experienceService,
        pageInfoService,
        cmsitemsRestService,
        $q
    ) {

        // ------------------------------------------------------------------------
        // Multiple Permissions Evaluation - Success & Error Functions
        // ------------------------------------------------------------------------

        var onSuccess = function(result) {
            return result.reduce(function(acc, val) {
                return acc && val;
            }, true) === true;
        };

        var onError = function() {
            return false;
        };

        // ------------------------------------------------------------------------
        // Rules
        // ------------------------------------------------------------------------

        permissionService.registerRule({
            names: ['se.write.page', 'se.write.slot', 'se.write.component', 'se.write.to.current.catalog.version'],
            verify: function(permissionNameObjs) {
                var promises = permissionNameObjs.map(function(permissionNameObject) {
                    if (permissionNameObject.context) {
                        return catalogVersionPermissionService.hasWritePermission(
                            permissionNameObject.context.catalogId,
                            permissionNameObject.context.catalogVersion
                        );
                    } else {
                        return catalogVersionPermissionService.hasWritePermissionOnCurrent();
                    }
                });
                return $q.all(promises).then(onSuccess, onError);
            }
        });

        permissionService.registerRule({
            names: ['se.sync.catalog'],
            verify: function(permissionNameObjs) {
                var promises = permissionNameObjs.map(function(permissionNameObject) {
                    if (permissionNameObject.context) {
                        return catalogVersionPermissionService.hasSyncPermission(
                            permissionNameObject.context.catalogId,
                            permissionNameObject.context.catalogVersion,
                            permissionNameObject.context.targetCatalogVersion
                        );
                    } else {
                        return catalogVersionPermissionService.hasSyncPermissionFromCurrentToActiveCatalogVersion();
                    }
                });
                return $q.all(promises).then(onSuccess, onError);
            }
        });

        permissionService.registerRule({
            names: ['se.approval.status.page'],
            verify: function() {
                return pageService.getCurrentPageInfo().then(function(pageInfo) {
                    return pageInfo.approvalStatus === "APPROVED";
                });
            }
        });

        permissionService.registerRule({
            names: ['se.read.page', 'se.read.slot', 'se.read.component', 'se.read.current.catalog.version'],
            verify: function() {
                return catalogVersionPermissionService.hasReadPermissionOnCurrent();
            }
        });

        permissionService.registerRule({
            names: ['se.page.belongs.to.experience'],
            verify: function() {
                return sharedDataService.get('experience').then(function(experience) {
                    return experience.pageContext && experience.pageContext.catalogVersionUuid === experience.catalogDescriptor.catalogVersionUuid;
                });
            }
        });

        /**
         * Show the clone icon:
         * - If a page belonging to an active catalog version is a primary page, whose copyToCatalogsDisabled flag is set to false and has at-least one clonable target.
         * - If a page belonging to a non active catalog version has at-least one clonable target.
         */
        permissionService.registerRule({
            names: ['se.cloneable.page'],
            verify: function() {

                return sharedDataService.get('experience').then(function(experience) {

                    var pageUriContext = {
                        CURRENT_CONTEXT_SITE_ID: experience.pageContext.siteId,
                        CURRENT_CONTEXT_CATALOG: experience.pageContext.catalogId,
                        CURRENT_CONTEXT_CATALOG_VERSION: experience.pageContext.catalogVersion
                    };

                    return pageService.getCurrentPageInfo().then(function(pageInfo) {
                        return catalogVersionRestService.getCloneableTargets(pageUriContext).then(function(targets) {

                            if (experience.pageContext.active) {
                                return targets.versions.length > 0 && pageInfo.defaultPage && !pageInfo.copyToCatalogsDisabled;
                            }

                            return targets.versions.length > 0;

                        });
                    });
                });

            }
        });

        permissionService.registerRule({
            names: ['se.content.catalog.non.active'],
            verify: function() {
                return catalogService.isContentCatalogVersionNonActive();
            }
        });

        permissionService.registerRule({
            names: ['se.not.versioning.perspective'],
            verify: function() {
                return cMSModesService.isVersioningPerspectiveActive().then(function(isActive) {
                    return !isActive;
                });
            }
        });

        permissionService.registerRule({
            names: ['se.version.page.selected'],
            verify: function() {
                return experienceService.getCurrentExperience().then(function(experience) {
                    return !!experience.versionId;
                });
            }
        });

        permissionService.registerRule({
            names: ['se.version.page.not.selected'],
            verify: function() {
                return experienceService.getCurrentExperience().then(function(experience) {
                    return !experience.versionId;
                });
            }
        });


        var registerTypePermissionRuleForTypeCodeFromContext = function(ruleName, verify) {
            permissionService.registerRule({
                names: [ruleName],
                verify: function(permissionNameObjs) {
                    var promises = permissionNameObjs.map(function(permissionNameObject) {
                        return verify([permissionNameObject.context.typeCode]).then(function(UpdatePermission) {
                            return UpdatePermission[permissionNameObject.context.typeCode];
                        });
                    });
                    return $q.all(promises).then(onSuccess, onError);
                }
            });
        };

        // check if the current user has change permission on the type provided part of the permission object
        registerTypePermissionRuleForTypeCodeFromContext('se.has.change.permissions.on.type', typePermissionsRestService.hasUpdatePermissionForTypes.bind(typePermissionsRestService));

        // check if the current user has create permission on the type provided part of the permission object
        registerTypePermissionRuleForTypeCodeFromContext('se.has.create.permissions.on.type', typePermissionsRestService.hasCreatePermissionForTypes.bind(typePermissionsRestService));

        // check if the current user has remove permission on the type provided part of the permission object
        registerTypePermissionRuleForTypeCodeFromContext('se.has.remove.permissions.on.type', typePermissionsRestService.hasDeletePermissionForTypes.bind(typePermissionsRestService));


        var registerTypePermissionRuleOnCurrentPage = function(ruleName, verify) {
            permissionService.registerRule({
                names: [ruleName],
                verify: function() {
                    return pageInfoService.getPageUUID().then(function(pageUUID) {
                        return cmsitemsRestService.getById(pageUUID).then(function(pageInfo) {
                            return verify([pageInfo.typeCode]).then(function(permissionObject) {
                                return permissionObject[pageInfo.typeCode];
                            });
                        });
                    });
                }
            });
        };

        // check if the current user has change permission on the page currently loaded
        registerTypePermissionRuleOnCurrentPage('se.has.change.type.permissions.on.current.page', typePermissionsRestService.hasUpdatePermissionForTypes.bind(typePermissionsRestService));

        // check if the current user has create permission on the page currently loaded
        registerTypePermissionRuleOnCurrentPage('se.has.create.type.permissions.on.current.page', typePermissionsRestService.hasCreatePermissionForTypes.bind(typePermissionsRestService));

        // check if the current user has read permission on the page currently loaded
        registerTypePermissionRuleOnCurrentPage('se.has.read.type.permissions.on.current.page', typePermissionsRestService.hasReadPermissionForTypes.bind(typePermissionsRestService));


        var registerTypePermissionRuleForTypeCode = function(ruleName, itemType, verify) {
            permissionService.registerRule({
                names: [ruleName],
                verify: function() {
                    return verify([itemType]).then(function(UpdatePermission) {
                        return UpdatePermission[itemType];
                    });
                }
            });
        };

        // check if the current user has read/create/remove/change permission on the type CMSVersion type
        registerTypePermissionRuleForTypeCode('se.has.read.permission.on.version.type', 'CMSVersion', typePermissionsRestService.hasReadPermissionForTypes.bind(typePermissionsRestService));
        registerTypePermissionRuleForTypeCode('se.has.create.permission.on.version.type', 'CMSVersion', typePermissionsRestService.hasCreatePermissionForTypes.bind(typePermissionsRestService));
        registerTypePermissionRuleForTypeCode('se.has.remove.permission.on.version.type', 'CMSVersion', typePermissionsRestService.hasDeletePermissionForTypes.bind(typePermissionsRestService));
        registerTypePermissionRuleForTypeCode('se.has.change.permission.on.version.type', 'CMSVersion', typePermissionsRestService.hasUpdatePermissionForTypes.bind(typePermissionsRestService));


        // ------------------------------------------------------------------------
        // Permissions
        // ------------------------------------------------------------------------

        permissionService.registerPermission({
            aliases: ['se.add.component'],
            rules: ['se.write.slot', 'se.write.component', 'se.page.belongs.to.experience', 'se.has.change.type.permissions.on.current.page']
        });

        permissionService.registerPermission({
            aliases: ['se.read.page'],
            rules: ['se.read.page']
        });

        permissionService.registerPermission({
            aliases: ['se.edit.page'],
            rules: ['se.write.page']
        });

        permissionService.registerPermission({
            aliases: ['se.sync.catalog'],
            rules: ['se.sync.catalog']
        });

        permissionService.registerPermission({
            aliases: ['se.sync.slot.context.menu', 'se.sync.page', 'se.sync.slot.indicator'],
            rules: ['se.sync.catalog', 'se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.edit.navigation'],
            rules: ['se.write.component']
        });

        permissionService.registerPermission({
            aliases: ['se.context.menu.remove.component'],
            rules: ['se.write.slot', 'se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.slot.context.menu.shared.icon', 'se.slot.context.menu.unshared.icon'],
            rules: ['se.read.slot']
        });

        permissionService.registerPermission({
            aliases: ['se.slot.context.menu.visibility'],
            rules: ['se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.clone.page'],
            rules: ['se.cloneable.page', 'se.has.create.type.permissions.on.current.page']
        });

        permissionService.registerPermission({
            aliases: ['se.context.menu.edit.component'],
            rules: ['se.write.component', 'se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.context.menu.drag.and.drop.component'],
            rules: ['se.write.slot', 'se.write.component', 'se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.edit.page.link', 'se.delete.page.menu'],
            rules: ['se.write.page', 'se.page.belongs.to.experience', 'se.not.versioning.perspective', 'se.has.change.type.permissions.on.current.page']
        });

        permissionService.registerPermission({
            aliases: ['se.shared.slot.override.options', 'se.revert.to.shared.slot.link'],
            rules: ['se.write.page', 'se.page.belongs.to.experience', 'se.not.versioning.perspective']
        });

        permissionService.registerPermission({
            aliases: ['se.clone.component'],
            rules: ['se.write.component', 'se.page.belongs.to.experience']
        });

        permissionService.registerPermission({
            aliases: ['se.edit.page.type', 'se.delete.page.type', 'se.restore.page.type'],
            rules: ['se.has.change.permissions.on.type']
        });

        permissionService.registerPermission({
            aliases: ['se.clone.page.type'],
            rules: ['se.has.create.permissions.on.type']
        });

        permissionService.registerPermission({
            aliases: ['se.permanently.delete.page.type'],
            rules: ['se.has.remove.permissions.on.type']
        });

        // Version
        permissionService.registerPermission({
            aliases: ['se.version.page'],
            rules: ['se.write.page', 'se.page.belongs.to.experience', 'se.content.catalog.non.active', 'se.has.read.permission.on.version.type', 'se.has.read.type.permissions.on.current.page']
        });

        permissionService.registerPermission({
            aliases: ['se.edit.version.page'],
            rules: ['se.write.to.current.catalog.version', 'se.has.change.permission.on.version.type']
        });

        permissionService.registerPermission({
            aliases: ['se.create.version.page'],
            rules: ['se.version.page.not.selected', 'se.page.belongs.to.experience', 'se.has.create.permission.on.version.type', 'se.has.read.type.permissions.on.current.page']
        });

        var rulesForVersionRollback = ['se.version.page.selected', 'se.page.belongs.to.experience', 'se.has.read.permission.on.version.type', 'se.has.create.permission.on.version.type', 'se.has.change.type.permissions.on.current.page'];
        permissionService.registerPermission({
            aliases: ['se.rollback.version.page'],
            rules: rulesForVersionRollback
        });

        permissionService.registerPermission({
            // the page versions menu button should be visible even if a version is not selected
            aliases: ['se.rollback.version.page.versions.menu'],
            rules: rulesForVersionRollback.filter(function(rule) {
                return rule !== 'se.version.page.selected';
            })
        });

        permissionService.registerPermission({
            aliases: ['se.delete.version.page'],
            rules: ['se.has.remove.permission.on.version.type']
        });
    });
