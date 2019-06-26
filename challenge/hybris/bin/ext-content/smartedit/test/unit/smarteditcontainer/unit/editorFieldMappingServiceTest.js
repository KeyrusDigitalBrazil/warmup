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
describe('genericEditorMappingService -', function() {

    // --------------------------------------------------------------------------------------
    // Constants
    // --------------------------------------------------------------------------------------
    var GENERIC_EDITOR_FLOAT_PRECISION;

    var WILDCARD = '*';
    var STRUCTURE_TYPE_NAME_1 = 'SOME STRUCTURE TYPE1';
    var COMPONENT_TYPE_NAME = 'SOME COMPONENT TYPE';
    var DISCRIMINATOR = 'SOME DISCRIMINATOR';

    // --------------------------------------------------------------------------------------
    // Variables 
    // --------------------------------------------------------------------------------------
    var editorFieldMappingService;
    var field;
    var componentTypeStructure;

    // --------------------------------------------------------------------------------------
    // Before Each
    // --------------------------------------------------------------------------------------
    beforeEach(angular.mock.module('genericEditorServicesModule'));
    beforeEach(inject(function(_GENERIC_EDITOR_FLOAT_PRECISION_, _editorFieldMappingService_) {
        editorFieldMappingService = _editorFieldMappingService_;
        GENERIC_EDITOR_FLOAT_PRECISION = _GENERIC_EDITOR_FLOAT_PRECISION_;

        spyOn(editorFieldMappingService, '_cleanTemplate').and.callThrough();
        spyOn(editorFieldMappingService, '_exactValueMatchPredicate').and.callThrough();

        // Ensure editor is clean before each test. 
        editorFieldMappingService._editorsFieldMapping = [];
        editorFieldMappingService._fieldsTabsMapping = [];

        field = {
            cmsStructureType: STRUCTURE_TYPE_NAME_1,
            smarteditComponentType: COMPONENT_TYPE_NAME,
            qualifier: DISCRIMINATOR
        };

    }));

    // --------------------------------------------------------------------------------------
    // Tests
    // --------------------------------------------------------------------------------------
    describe('Common Mapping -', function() {
        var PAYLOAD = 'some payload';
        var EXACT_MATCH_PREDICATE, WILDCARD_PREDICATE;
        var collection;

        beforeEach(function() {
            collection = [];
            EXACT_MATCH_PREDICATE = editorFieldMappingService._exactValueMatchPredicate;
            WILDCARD_PREDICATE = editorFieldMappingService._wildcardPredicate;
        });

        it('WHEN addMapping is called THEN it stores the right mapping', function() {
            // GIVEN 

            // WHEN 
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, DISCRIMINATOR, PAYLOAD, collection);

            // THEN 
            expect(collection.length).toBe(1);
            expect(collection[0].structureTypeMatcher).toBe(STRUCTURE_TYPE_NAME_1);
            expect(collection[0].componentTypeMatcher).toBe(COMPONENT_TYPE_NAME);
            expect(collection[0].discriminatorMatcher).toBe(DISCRIMINATOR);
            expect(collection[0].value).toBe(PAYLOAD);
        });

        it('GIVEN a mapping is defined WHEN addMapping is called with the same keys THEN it replaces the original mapping', function() {
            // GIVEN
            var someFunc = function() {};
            var somePayload = 'some other payload';

            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, someFunc, DISCRIMINATOR, PAYLOAD, collection);

            // WHEN 
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, someFunc, DISCRIMINATOR, somePayload, collection);

            // THEN 
            expect(collection.length).toBe(1);
            expect(collection[0].structureTypeMatcher).toBe(STRUCTURE_TYPE_NAME_1);
            expect(collection[0].componentTypeMatcher).toBe(someFunc);
            expect(collection[0].discriminatorMatcher).toBe(DISCRIMINATOR);
            expect(collection[0].value).toBe(somePayload);
        });

        it('GIVEN a mapping is defined WHEN getMapping is called THEN the mapping will be returned if there is a perfect match', function() {
            // GIVEN 
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, DISCRIMINATOR, PAYLOAD, collection);

            // WHEN 
            var mapping = editorFieldMappingService._getMapping(field, componentTypeStructure, collection);

            // THEN 
            expect(mapping).toBe(PAYLOAD);
            expect(EXACT_MATCH_PREDICATE).toHaveBeenCalledWith(STRUCTURE_TYPE_NAME_1, field.cmsStructureType);
            expect(EXACT_MATCH_PREDICATE).toHaveBeenCalledWith(COMPONENT_TYPE_NAME, field.smarteditComponentType);
            expect(EXACT_MATCH_PREDICATE).toHaveBeenCalledWith(DISCRIMINATOR, field.qualifier);
        });

        it('GIVEN a mapping is defined WHEN getMapping is called THEN the mapping will be returned if there is a partial match', function() {
            // GIVEN 
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, WILDCARD, DISCRIMINATOR, PAYLOAD, collection);

            // WHEN 
            var mapping = editorFieldMappingService._getMapping(field, componentTypeStructure, collection);

            // THEN 
            expect(mapping).toBe(PAYLOAD);
            expect(EXACT_MATCH_PREDICATE).toHaveBeenCalledWith(STRUCTURE_TYPE_NAME_1, field.cmsStructureType);
            expect(EXACT_MATCH_PREDICATE).not.toHaveBeenCalledWith(field.smarteditComponentType);
            expect(EXACT_MATCH_PREDICATE).toHaveBeenCalledWith(DISCRIMINATOR, field.qualifier);
        });

        it('GIVEN a mapping is defined WHEN getMapping is called THEN the no mapping will be returned if there is no match', function() {
            // GIVEN 
            var otherDiscriminator = 'some other discriminator';
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, otherDiscriminator, PAYLOAD, collection);

            // WHEN 
            var mapping = editorFieldMappingService._getMapping(field, componentTypeStructure, collection);

            // THEN 
            expect(mapping).toBe(null);
            expect(EXACT_MATCH_PREDICATE).toHaveBeenCalledWith(STRUCTURE_TYPE_NAME_1, field.cmsStructureType);
            expect(EXACT_MATCH_PREDICATE).toHaveBeenCalledWith(COMPONENT_TYPE_NAME, field.smarteditComponentType);
            expect(EXACT_MATCH_PREDICATE).toHaveBeenCalledWith(otherDiscriminator, field.qualifier);
        });

        it('GIVEN a mapping is defined with custom predicates WHEN getMapping is called THEN the predicates will be executed to determine if there is a match', function() {
            // GIVEN 
            var structureTypePredicate = jasmine.createSpy('structureTypePredicate');
            var componentTypePredicate = jasmine.createSpy('componentTypePredicate');
            var discriminatorPredicate = jasmine.createSpy('discriminatorPredicate');

            structureTypePredicate.and.returnValue(true);
            componentTypePredicate.and.returnValue(true);
            discriminatorPredicate.and.returnValue(true);

            editorFieldMappingService._addMapping(structureTypePredicate, componentTypePredicate, discriminatorPredicate, PAYLOAD, collection);

            // WHEN 
            var mapping = editorFieldMappingService._getMapping(field, componentTypeStructure, collection);

            // THEN 
            expect(mapping).toBe(PAYLOAD);
            expect(structureTypePredicate).toHaveBeenCalledWith(field.cmsStructureType, field, componentTypeStructure);
            expect(componentTypePredicate).toHaveBeenCalledWith(field.smarteditComponentType, field, componentTypeStructure);
            expect(discriminatorPredicate).toHaveBeenCalledWith(field.qualifier, field, componentTypeStructure);
        });

        it('GIVEN a mapping is defined with custom predicates WHEN getMapping is called and there is no match THEN then no mapping will be returned', function() {
            // GIVEN 
            var structureTypePredicate = jasmine.createSpy('structureTypePredicate');
            var componentTypePredicate = jasmine.createSpy('componentTypePredicate');
            var discriminatorPredicate = jasmine.createSpy('discriminatorPredicate');

            structureTypePredicate.and.returnValue(true);
            componentTypePredicate.and.returnValue(false);
            discriminatorPredicate.and.returnValue(true);

            editorFieldMappingService._addMapping(structureTypePredicate, componentTypePredicate, discriminatorPredicate, PAYLOAD, collection);

            // WHEN 
            var mapping = editorFieldMappingService._getMapping(field, componentTypeStructure, collection);

            // THEN 
            expect(mapping).toBe(null);
            expect(structureTypePredicate).toHaveBeenCalledWith(field.cmsStructureType, field, componentTypeStructure);
            expect(componentTypePredicate).toHaveBeenCalledWith(field.smarteditComponentType, field, componentTypeStructure);
            expect(discriminatorPredicate).not.toHaveBeenCalled();
        });

        it('GIVEN several mappings are defined WHEN getMapping is called THEN the mapping with the exact match will be returned', function() {
            // GIVEN 
            var value1 = 'some value1';
            var value2 = 'some value2';
            var value3 = 'some value3';
            var value4 = 'some value4';
            editorFieldMappingService._addMapping(WILDCARD, COMPONENT_TYPE_NAME, DISCRIMINATOR, value1, collection);
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, DISCRIMINATOR, value2, collection);
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, WILDCARD, DISCRIMINATOR, value3, collection);
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, WILDCARD, value4, collection);

            // WHEN 
            var mapping = editorFieldMappingService._getMapping(field, componentTypeStructure, collection);

            // THEN 
            expect(mapping).toBe(value2);
        });

        it('GIVEN several mappings are defined WHEN getMapping is called THEN the mapping with the exact match will be returned', function() {
            // GIVEN 
            var value1 = 'some value1';
            var value2 = 'some value2';
            var value3 = 'some value3';
            var value4 = 'some value4';

            var structureTypePredicate = jasmine.createSpy('structureTypePredicate');
            var componentTypePredicate = jasmine.createSpy('componentTypePredicate');
            var discriminatorPredicate = jasmine.createSpy('discriminatorPredicate');

            structureTypePredicate.and.returnValue(true);
            componentTypePredicate.and.returnValue(true);
            discriminatorPredicate.and.returnValue(true);

            editorFieldMappingService._addMapping(WILDCARD, COMPONENT_TYPE_NAME, DISCRIMINATOR, value1, collection);
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, DISCRIMINATOR, value2, collection);
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, WILDCARD, DISCRIMINATOR, value3, collection);
            editorFieldMappingService._addMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, WILDCARD, value4, collection);

            editorFieldMappingService._addMapping(WILDCARD, componentTypePredicate, discriminatorPredicate, value1, collection);
            editorFieldMappingService._addMapping(structureTypePredicate, componentTypePredicate, discriminatorPredicate, value2, collection);
            editorFieldMappingService._addMapping(structureTypePredicate, WILDCARD, discriminatorPredicate, value3, collection);
            editorFieldMappingService._addMapping(structureTypePredicate, componentTypePredicate, WILDCARD, value4, collection);

            // WHEN 
            var mapping = editorFieldMappingService._getMapping(field, componentTypeStructure, collection);

            // THEN 
            expect(mapping).toBe(value2);
        });
    });

    describe('Mapping Predicates -', function() {

        it('GIVEN an exact matching predicate WHEN evaluated with an exact match THEN it returns true', function() {
            // GIVEN 
            var expectedValue = 'some expected value';
            var actualValue = 'some expected value';
            var predicate = editorFieldMappingService._exactValueMatchPredicate;

            // WHEN 
            var result = predicate(expectedValue, actualValue, field, componentTypeStructure);

            // THEN
            expect(result).toBe(true);
        });

        it('GIVEN an exact matching predicate WHEN evaluated with a partial match THEN it returns false', function() {
            // GIVEN 
            var expectedValue = 'some expected value';
            var actualValue = 'other value';
            var predicate = editorFieldMappingService._exactValueMatchPredicate;

            // WHEN 
            var result = predicate(expectedValue, actualValue, field, componentTypeStructure);

            // THEN
            expect(result).toBe(false);
        });
    });

    describe('Field Mapping -', function() {
        it('WHEN addFieldMapping is called THEN it is delegated to _addMapping', function() {
            // GIVEN 
            var value = 'some value';
            var fieldMappingCollection = editorFieldMappingService._editorsFieldMapping;
            spyOn(editorFieldMappingService, '_addMapping');

            // WHEN 
            editorFieldMappingService.addFieldMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, DISCRIMINATOR, value);

            // THEN 
            expect(editorFieldMappingService._addMapping).toHaveBeenCalledWith(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, DISCRIMINATOR, value, fieldMappingCollection);
        });

        it('WHEN getEditorFieldMapping is called THEN it is delegated to _getMapping', function() {
            // GIVEN 
            var expectedMapping = {
                template: 'some template'
            };
            var fieldMappingCollection = editorFieldMappingService._editorsFieldMapping;
            spyOn(editorFieldMappingService, '_getMapping').and.returnValue(expectedMapping);

            // WHEN 
            var result = editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure);

            // THEN 
            expect(result).toBe(expectedMapping);
            expect(editorFieldMappingService._getMapping).toHaveBeenCalledWith(field, componentTypeStructure, fieldMappingCollection);
            expect(editorFieldMappingService._cleanTemplate).toHaveBeenCalledWith(expectedMapping.template);
        });

        it('WHEN _registerDefaultFieldMappings is called THEN all default mappings are added', function() {
            // GIVEN
            expect(editorFieldMappingService._editorsFieldMapping.length).toBe(0);

            // WHEN
            editorFieldMappingService._registerDefaultFieldMappings();

            // THEN
            field.cmsStructureType = 'Boolean';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'booleanWrapperTemplate.html'
            });

            field.cmsStructureType = 'ShortString';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'shortStringTemplate.html'
            });

            field.cmsStructureType = 'LongString';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'longStringTemplate.html'
            });

            field.cmsStructureType = 'RichText';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'richTextTemplate.html'
            });

            field.cmsStructureType = 'Number';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'numberTemplate.html'
            });

            field.cmsStructureType = 'Float';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'floatTemplate.html',
                precision: GENERIC_EDITOR_FLOAT_PRECISION
            });

            field.cmsStructureType = 'Dropdown';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'dropdownTemplate.html'
            });

            field.cmsStructureType = 'DateTime';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'dateTimePickerWrapperTemplate.html'
            });

            field.cmsStructureType = 'Enum';
            expect(editorFieldMappingService.getEditorFieldMapping(field, componentTypeStructure)).toEqual({
                template: 'enumTemplate.html'
            });
        });
    });

    describe('Tab Mapping -', function() {
        it('WHEN addFieldTabMapping is called THEN it is delegated to _addMapping', function() {
            // GIVEN 
            var value = 'some value';
            var fieldTabMappingCollection = editorFieldMappingService._fieldsTabsMapping;
            spyOn(editorFieldMappingService, '_addMapping');

            // WHEN 
            editorFieldMappingService.addFieldTabMapping(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, DISCRIMINATOR, value);

            // THEN 
            expect(editorFieldMappingService._addMapping).toHaveBeenCalledWith(STRUCTURE_TYPE_NAME_1, COMPONENT_TYPE_NAME, DISCRIMINATOR, value, fieldTabMappingCollection);
        });

        it('WHEN getFieldTabMapping is called THEN it is delegated to _getMapping', function() {
            // GIVEN 
            var expectedMapping = 'some mapping';
            var fieldTabMappingCollection = editorFieldMappingService._fieldsTabsMapping;
            spyOn(editorFieldMappingService, '_getMapping').and.returnValue(expectedMapping);

            // WHEN 
            var result = editorFieldMappingService.getFieldTabMapping(field, componentTypeStructure);

            // THEN 
            expect(result).toBe(expectedMapping);
            expect(editorFieldMappingService._getMapping).toHaveBeenCalledWith(field, componentTypeStructure, fieldTabMappingCollection);
        });
    });

});
