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
angular.module('genericEditorApp', [
        'ySelectModule',
        'genericEditorModule',
        'smarteditServicesModule',
        'localizedElementModule',
        'templateCacheDecoratorModule'
    ])
    .run(function($templateCache) {
        //use a copy of select2
        $templateCache.put("pagedSelect2/match-multiple.tpl.html", $templateCache.get("select2/match-multiple.tpl.html"));
        $templateCache.put("pagedSelect2/match.tpl.html", $templateCache.get("select2/match.tpl.html"));
        $templateCache.put("pagedSelect2/no-choice.tpl.html", $templateCache.get("select2/no-choice.tpl.html"));
        $templateCache.put("pagedSelect2/select-multiple.tpl.html", $templateCache.get("select2/select-multiple.tpl.html"));
        $templateCache.put("pagedSelect2/select.tpl.html", $templateCache.get("select2/select.tpl.html"));

        //our own flavor of select2 for paging that makes use of yInfiniteScrolling component
        $templateCache.put("pagedSelect2/choices.tpl.html", $templateCache.get("uiSelectPagedChoicesTemplate.html"));

    })
    .controller('defaultController', function(restServiceFactory, sharedDataService, editorFieldMappingService, genericEditorTabService) {

        restServiceFactory.setDomain('thedomain');
        sharedDataService.set('experience', {
            siteDescriptor: {
                uid: 'someSiteUid'
            },
            catalogDescriptor: {
                catalogId: 'electronics',
                catalogVersion: 'staged'
            }
        });

        this.thesmarteditComponentType = 'thesmarteditComponentType';
        this.thesmarteditComponentId = 'thesmarteditComponentId';
        this.structureApi = "cmswebservices/v1/types/:smarteditComponentType";
        this.displaySubmit = true;
        this.displayCancel = true;
        this.CONTEXT_CATALOG = "CURRENT_CONTEXT_CATALOG";
        this.CONTEXT_CATALOG_VERSION = "CURRENT_CONTEXT_CATALOG_VERSION";

        this.structureForBasic = [{
            cmsStructureType: "ShortString",
            qualifier: "name",
            i18nKey: 'type.Item.name.name'
        }, {
            cmsStructureType: "DateTime",
            qualifier: "creationtime",
            i18nKey: 'type.AbstractItem.creationtime.name',
            editable: false
        }, {
            cmsStructureType: "DateTime",
            qualifier: "modifiedtime",
            i18nKey: 'type.AbstractItem.modifiedtime.name',
            editable: false
        }];

        this.structureForVisibility = [{
            cmsStructureType: "Boolean",
            qualifier: "visible",
            i18nKey: 'type.AbstractCMSComponent.visible.name'
        }];

        this.structureForAdmin = [{
            cmsStructureType: "ShortString",
            qualifier: "uid",
            i18nKey: 'type.Item.uid.name'
        }, {
            cmsStructureType: "ShortString",
            qualifier: "pk",
            i18nKey: 'type.AbstractItem.pk.name',
            editable: false
        }];

        this.contentApi = '/cmswebservices/v1/catalogs/' + this.CONTEXT_CATALOG + '/versions/' + this.CONTEXT_CATALOG_VERSION + '/items';

        var isMultiTabPredicate = function(structureType, field, componentStructure) {
            return componentStructure.category === 'MULTITAB';
        };

        editorFieldMappingService.addFieldTabMapping(null, null, "visible", "visibility");
        editorFieldMappingService.addFieldTabMapping(null, isMultiTabPredicate, "id", "administration");
        editorFieldMappingService.addFieldTabMapping(null, isMultiTabPredicate, "modifiedtime", "administration");
        editorFieldMappingService.addFieldTabMapping("DateTime", isMultiTabPredicate, "creationtime", "administration");

        genericEditorTabService.configureTab('default', {
            priority: 5
        });
        genericEditorTabService.configureTab('administration', {
            priority: 4
        });
    });
