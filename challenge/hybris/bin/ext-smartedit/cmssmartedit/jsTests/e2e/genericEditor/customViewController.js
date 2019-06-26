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
/* jshint unused:false, undef:false */
angular.module('customViewModule', [
        'templateCacheDecoratorModule',
        'cmssmarteditContainerTemplates',
        'ySelectModule',
        'genericEditorModule',
        'smarteditServicesModule',
        'localizedElementModule',
        'seMediaFieldModule',
        'seMediaContainerFieldModule',
        'genericEditorModalServiceModule'
    ])
    .run(function(editorFieldMappingService, $templateCache) {
        // Register generic editor fields
        editorFieldMappingService.addFieldMapping('Media', null, null, {
            template: 'mediaTemplate.html'
        });

        editorFieldMappingService.addFieldMapping('MediaContainer', null, null, {
            template: 'mediaContainerTemplate.html'
        });

        //use a copy of select2
        $templateCache.put("pagedSelect2/match-multiple.tpl.html", $templateCache.get("select2/match-multiple.tpl.html"));
        $templateCache.put("pagedSelect2/match.tpl.html", $templateCache.get("select2/match.tpl.html"));
        $templateCache.put("pagedSelect2/no-choice.tpl.html", $templateCache.get("select2/no-choice.tpl.html"));
        $templateCache.put("pagedSelect2/select-multiple.tpl.html", $templateCache.get("select2/select-multiple.tpl.html"));
        $templateCache.put("pagedSelect2/select.tpl.html", $templateCache.get("select2/select.tpl.html"));

        //our own flavor of select2 for paging that makes use of yInfiniteScrolling component
        $templateCache.put("pagedSelect2/choices.tpl.html", $templateCache.get("uiSelectPagedChoicesTemplate.html"));

    })
    .controller('customViewController', function(CONTEXT_SITE_ID, genericEditorModalService) {
        this.thesmarteditComponentType = 'TypeWithMedia';
        this.thesmarteditComponentId = 'componentWithMedia';

        this.typeWithMediaContainer = 'TypeWithMediaContainer';
        this.componentWithMediaContainer = 'componentWithMediaContainer';

        this.componentToValidateId = 'componentToValidateId';
        this.componentToValidateType = 'componentToValidateType';

        this.navigationComponent = 'navigationComponent';
        this.navigationComponentType = 'NavigationComponentType';

        this.structureApi = 'cmswebservices/v1/types/:smarteditComponentType';
        this.displaySubmit = true;
        this.displayCancel = true;

        this.structureForBasic = [{
            cmsStructureType: "ShortString",
            qualifier: "name",
            i18nKey: 'type.Item.name.name'
        }, {
            cmsStructureType: "Date",
            qualifier: "creationtime",
            i18nKey: 'type.AbstractItem.creationtime.name',
            editable: false
        }, {
            cmsStructureType: "Date",
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

        this.contentApi = '/cmswebservices/v1/sites/' + CONTEXT_SITE_ID + '/cmsitems';


        this.openCMSLinkComponentEditor = function() {

            var payload = {
                componentId: 'cmsLinkComponentId',
                componentUuid: 'cmsLinkComponentId',
                componentType: 'CMSLinkComponent',
                title: 'type.CMSLinkComponent.name'
            };
            return genericEditorModalService.open(payload);
        };

    });
angular.module('smarteditcontainer').requires.push('customViewModule');
