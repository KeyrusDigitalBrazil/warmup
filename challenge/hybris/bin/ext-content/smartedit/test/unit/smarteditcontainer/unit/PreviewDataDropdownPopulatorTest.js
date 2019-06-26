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
describe('PreviewDataDropdownPopulatorModule', function() {

    var PreviewDatapreviewCatalogDropdownPopulator, PreviewDatalanguageDropdownPopulator, $rootScope, $q;

    var siteService, catalogService, languageService;

    var siteDescriptors = [{
        uid: 'siteId1'
    }, {
        uid: 'siteId2'
    }, {
        uid: 'siteId3'
    }, {
        uid: 'siteId4'
    }];

    var catalogDescriptors = [{
        catalogId: 'myCatalogId1',
        _siteId: 'siteId1',
        name: 'myCatalog1',
        versions: [{
            version: 'myCatalogVersion1'
        }]
    }, {
        catalogId: 'myCatalogId2',
        _siteId: 'siteId2',
        name: 'myCatalog2',
        versions: [{
            version: 'myCatalogVersion2'
        }]
    }, {
        catalogId: 'myCatalogId3',
        _siteId: 'siteId3',
        name: "myCatalog3",
        versions: [{
            version: 'myCatalogVersion3'
        }]
    }, {
        catalogId: 'myCatalogId4',
        _siteId: 'siteId4',
        name: "myCatalog4",
        versions: [{
            version: 'myCatalogVersion4'
        }]
    }, {
        catalogId: 'myCatalogId5',
        _siteId: 'siteId5',
        name: "myCatalog5",
        versions: [{
            version: 'myCatalogVersion5'
        }]
    }];

    var languageDescriptors = [{
        isocode: 'en',
        nativeName: 'English'
    }, {
        isocode: 'hi',
        nativeName: 'Hindi'
    }, {
        isocode: 'te',
        nativeName: 'Telugu'
    }];

    var l10nFilterFunction = function() {
        return 'catalogName';
    };

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(angular.mock.module('previewDataDropdownPopulatorModule', function($provide) {

        siteService = jasmine.createSpyObj('siteService', ['getSites']);
        $provide.value('siteService', siteService);

        catalogService = jasmine.createSpyObj('catalogService', ['getContentCatalogsForSite']);
        $provide.value('catalogService', catalogService);

        languageService = jasmine.createSpyObj('languageService', ['getLanguagesForSite']);
        $provide.value('languageService', languageService);

        $provide.value('l10nFilter', l10nFilterFunction);

    }));

    beforeEach(inject(function(_PreviewDatapreviewCatalogDropdownPopulator_, _PreviewDatalanguageDropdownPopulator_, _$rootScope_, _$q_) {
        PreviewDatapreviewCatalogDropdownPopulator = _PreviewDatapreviewCatalogDropdownPopulator_;
        PreviewDatalanguageDropdownPopulator = _PreviewDatalanguageDropdownPopulator_;
        $rootScope = _$rootScope_;
        $q = _$q_;
    }));

    it('GIVEN siteService returns a resolved promise WHEN PreviewDatapreviewCatalogDropdownPopulator.populate is called THEN it will return a list of catalog ID - catalog versions', function() {

        //GIVEN
        siteService.getSites.and.returnValue($q.when(siteDescriptors));
        catalogService.getContentCatalogsForSite.and.callFake(function(siteId) {
            return $q.when(catalogDescriptors.filter(function(catalogVersionDescriptor) {
                return catalogVersionDescriptor._siteId === siteId;
            }));
        });

        //WHEN
        var catalogsPromise = PreviewDatapreviewCatalogDropdownPopulator.populate({});

        //THEN
        expect(catalogsPromise).toBeResolvedWithData([{
            id: 'siteId1_myCatalogId1_myCatalogVersion1',
            label: 'catalogName - myCatalogVersion1'
        }, {
            id: 'siteId2_myCatalogId2_myCatalogVersion2',
            label: 'catalogName - myCatalogVersion2'
        }, {
            id: 'siteId3_myCatalogId3_myCatalogVersion3',
            label: 'catalogName - myCatalogVersion3'
        }, {
            id: 'siteId4_myCatalogId4_myCatalogVersion4',
            label: 'catalogName - myCatalogVersion4'
        }]);

        expect(siteService.getSites).toHaveBeenCalled();
        expect(catalogService.getContentCatalogsForSite.calls.count()).toBe(4);
        expect(catalogService.getContentCatalogsForSite.calls.argsFor(0)).toEqual(['siteId1']);
        expect(catalogService.getContentCatalogsForSite.calls.argsFor(1)).toEqual(['siteId2']);
        expect(catalogService.getContentCatalogsForSite.calls.argsFor(2)).toEqual(['siteId3']);
        expect(catalogService.getContentCatalogsForSite.calls.argsFor(3)).toEqual(['siteId4']);

    });

    it('GIVEN siteService returns a resolved promise WHEN PreviewDatapreviewCatalogDropdownPopulator.populate is called with a search string THEN it will return a list of catalog ID - catalog versions filtered based on the search string', function() {

        //GIVEN
        siteService.getSites.and.returnValue($q.when(siteDescriptors));
        catalogService.getContentCatalogsForSite.and.callFake(function(siteId) {
            return $q.when(catalogDescriptors.filter(function(catalogVersionDescriptor) {
                return catalogVersionDescriptor._siteId === siteId;
            }));
        });

        var payload = {
            search: 'myCatalogVersion1'
        };

        //WHEN
        var catalogsPromise = PreviewDatapreviewCatalogDropdownPopulator.populate(payload);

        //THEN
        expect(catalogsPromise).toBeResolvedWithData([{
            id: 'siteId1_myCatalogId1_myCatalogVersion1',
            label: 'catalogName - myCatalogVersion1'
        }]);

    });

    it('GIVEN siteService returns a rejected promise WHEN PreviewDatapreviewCatalogDropdownPopulator.populate is called THEN it will return a rejected promise', function() {

        //GIVEN
        siteService.getSites.and.returnValue($q.reject(siteDescriptors));
        catalogService.getContentCatalogsForSite.and.callFake(function(siteId) {
            return $q.when(catalogDescriptors.filter(function(catalogVersionDescriptor) {
                return catalogVersionDescriptor._siteId === siteId;
            }));
        });

        //WHEN
        var catalogsPromise = PreviewDatapreviewCatalogDropdownPopulator.populate({});

        //THEN
        expect(catalogsPromise).toBeRejected();
        expect(siteService.getSites).toHaveBeenCalled();
        expect(catalogService.getContentCatalogsForSite).not.toHaveBeenCalled();

    });

    it('GIVEN a correct siteId WHEN PreviewDatalanguageDropdownPopulator.populate is called THEN populate will return a list of associated languages', function() {

        //GIVEN
        languageService.getLanguagesForSite.and.returnValue($q.when(languageDescriptors));
        spyOn(PreviewDatalanguageDropdownPopulator, '_getLanguageDropdownChoices').and.callThrough();

        var payload = {
            field: {
                qualifier: 'somequalifier',
                dependsOn: 'catalog'
            },
            model: {
                catalog: 'siteId1_myCatalogId1_myCatalogVersion1'
            }
        };

        //WHEN
        var languagesPromise = PreviewDatalanguageDropdownPopulator.populate(payload);

        //THEN
        expect(languagesPromise).toBeResolvedWithData([{
            id: 'en',
            label: 'English'
        }, {
            id: 'hi',
            label: 'Hindi'
        }, {
            id: 'te',
            label: 'Telugu'
        }]);

        expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('siteId1');
        expect(PreviewDatalanguageDropdownPopulator._getLanguageDropdownChoices).toHaveBeenCalledWith('siteId1', undefined);

    });

    it('GIVEN a correct siteId WHEN PreviewDatalanguageDropdownPopulator.populate is called with a search string THEN populate will return a list of associated languages filtered based on the search string', function() {

        //GIVEN
        languageService.getLanguagesForSite.and.returnValue($q.when(languageDescriptors));
        spyOn(PreviewDatalanguageDropdownPopulator, '_getLanguageDropdownChoices').and.callThrough();

        var payload = {
            field: {
                qualifier: 'somequalifier',
                dependsOn: 'catalog'
            },
            model: {
                catalog: 'siteId1_myCatalogId1_myCatalogVersion1'
            },
            search: 'te'
        };

        //WHEN
        var languagesPromise = PreviewDatalanguageDropdownPopulator.populate(payload);

        //THEN
        expect(languagesPromise).toBeResolvedWithData([{
            id: 'te',
            label: 'Telugu'
        }]);
        expect(PreviewDatalanguageDropdownPopulator._getLanguageDropdownChoices).toHaveBeenCalledWith('siteId1', 'te');

    });

    it('GIVEN a wrong siteId WHEN PreviewDatalanguageDropdownPopulator.populate is called THEN populate will return a rejected promise', function() {

        //GIVEN
        languageService.getLanguagesForSite.and.returnValue($q.reject(languageDescriptors));
        spyOn(PreviewDatalanguageDropdownPopulator, '_getLanguageDropdownChoices').and.callThrough();

        var payload = {
            field: {
                qualifier: 'somequalifier',
                dependsOn: 'catalog'
            },
            model: {
                catalog: 'siteIdX_myCatalogId1_myCatalogVersion1'
            }
        };

        //WHEN
        var languagesPromise = PreviewDatalanguageDropdownPopulator.populate(payload);

        //THEN
        expect(languagesPromise).toBeRejected();
        expect(languageService.getLanguagesForSite).toHaveBeenCalledWith('siteIdX');
        expect(PreviewDatalanguageDropdownPopulator._getLanguageDropdownChoices).toHaveBeenCalledWith('siteIdX', undefined);

    });



});
