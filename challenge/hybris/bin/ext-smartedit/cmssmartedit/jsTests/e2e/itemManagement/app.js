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
angular.module('restrictionsManagementApp', ['templateCacheDecoratorModule', 'itemManagementModule', 'restrictionsServiceModule', 'cmsitemsRestServiceModule'])

    .controller('testController', function($timeout, restrictionsService, cmsitemsRestService) {

        var restrictions = {
            'add': {
                uid: "addId",
                name: "restriction to add",
                typeCode: "CMSTimeRestriction",
                description: "some time restriction description"
            },
            'edit': {
                uid: "editId",
                name: "editing restriction",
                typeCode: "CMSTimeRestriction",
                description: "some time restriction description"
            },
            'create': {
                typeCode: "CMSTimeRestriction"
            }
        };

        this.visible = true;
        this.uriContext = {
            siteUID: 'mySite',
            catalogId: 'myCatalog',
            catalogVersion: 'myCatalogVersion'
        };

        // gets replaced/overridden by the child component
        this.submit = function submit() {};

        // gets replaced/overridden by the child component
        this.isDirty = function submit() {};

        this.submit = function submit() {
            this.submitCallback().then(function(result) {
                this.result = result;
            }.bind(this));
        }.bind(this);


        this.modeChanged = function(newMode) {
            this.mode = newMode;
            this.restriction = restrictions[newMode];
            this.type = this.restriction.typeCode;
            this.visible = false;
            this.result = null;
            this.structureApi = restrictionsService.getStructureApiUri(this.mode);
            this.contentApi = cmsitemsRestService.get({});
            $timeout(function() {
                this.visible = true;
            }.bind(this));
        };

        this.submitEnabled = function submitEnabled() {
            return this.mode === 'add' || this.isDirty();
        };

        // initialize
        this.modeChanged('add');
    });

angular.module('cmssmarteditContainer').requires.push('restrictionsManagementApp');
