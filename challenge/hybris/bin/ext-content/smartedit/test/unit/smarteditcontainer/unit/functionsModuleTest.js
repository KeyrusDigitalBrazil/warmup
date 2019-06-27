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
/* jshint esversion: 6 */
describe('functionsModule', function() {

    var escapeHtml, getDataFromResponse, getKeyHoldingDataFromResponse, isBlank, hitch, copy, customTimeout, merge, getQueryString, parseQuery, trim, convertToArray, uniqueArray, regExpFactory, generateIdentifier, closeOpenModalsOnBrowserBack, $uibModalStack, isAllTruthy, isAnyTruthy, compareHTMLElementsPosition, deepObjectPropertyDiff, deepIterateOverObjectWith, sanitize;

    beforeEach(angular.mock.module('functionsModule'));

    beforeEach(angular.mock.module('ui.bootstrap', function($provide) {
        $uibModalStack = jasmine.createSpyObj('$uibModalStack', ['getTop', 'dismissAll']);
        $provide.value('$uibModalStack', $uibModalStack);
    }));

    beforeEach(inject(function(_isBlank_, _hitch_, _copy_, _customTimeout_, _merge_, _getQueryString_,
        _parseQuery_, _trim_, _convertToArray_, _uniqueArray_, _regExpFactory_, _generateIdentifier_, _escapeHtml_,
        _closeOpenModalsOnBrowserBack_, _getDataFromResponse_, _getKeyHoldingDataFromResponse_, _isAllTruthy_, _isAnyTruthy_, _compareHTMLElementsPosition_,
        _deepObjectPropertyDiff_, _deepIterateOverObjectWith_, _sanitize_) {

        isBlank = _isBlank_;
        hitch = _hitch_;
        copy = _copy_;
        customTimeout = _customTimeout_;
        merge = _merge_;
        getQueryString = _getQueryString_;
        parseQuery = _parseQuery_;
        trim = _trim_;
        convertToArray = _convertToArray_;
        uniqueArray = _uniqueArray_;
        regExpFactory = _regExpFactory_;
        generateIdentifier = _generateIdentifier_;
        escapeHtml = _escapeHtml_;
        closeOpenModalsOnBrowserBack = _closeOpenModalsOnBrowserBack_;
        getDataFromResponse = _getDataFromResponse_;
        getKeyHoldingDataFromResponse = _getKeyHoldingDataFromResponse_;
        isAllTruthy = _isAllTruthy_;
        isAnyTruthy = _isAnyTruthy_;
        compareHTMLElementsPosition = _compareHTMLElementsPosition_;
        deepObjectPropertyDiff = _deepObjectPropertyDiff_;
        deepIterateOverObjectWith = _deepIterateOverObjectWith_;
        sanitize = _sanitize_;
    }));

    beforeEach(function() {
        // Clock is globally installed somewhere, somehow. We need to uninstall it before
        // installing it for each test in the suite.
        jasmine.clock().uninstall();
        jasmine.clock().install();
    });

    afterEach(function() {
        jasmine.clock().uninstall();
    });

    it('isBlank will return true if a variable is undefined or null or empty', function() {

        expect(isBlank('')).toBe(true);
        expect(isBlank(null)).toBe(true);
        expect(isBlank('null')).toBe(true);
        expect(isBlank(undefined)).toBe(true);
        expect(isBlank('not blank')).toBe(false);

    });

    it('hitch will return a new function that will bind the given scope into a given function', function() {

        var myfunc = function(arg1, arg2) {
            return arg1 + " " + this.message + " " + arg2;
        };

        var object1 = {
            message: '. This is message of object1',
            func: myfunc
        };

        var object2 = {
            message: '. This is message of object2',
        };


        object2.func = hitch(object1, myfunc, "Hello");

        expect(object2.func('. From object2')).toBe('Hello . This is message of object1 . From object2');


    });

    it('copy will do a deep copy of a given object into another object', function() {

        var JSVar = {
            'key1': 'value1',
            'key2': 'value2'
        };

        var newVar = copy(JSVar);

        expect(newVar).not.toBe(JSVar);
        expect(newVar).toEqualData({
            key1: 'value1',
            key2: 'value2'
        });


    });

    it('customTimeout will call a specified function after a specified duration (in ms)', function() {

        var duration = 5000;
        var func = jasmine.createSpy('func').and.returnValue(function() {});

        spyOn(window, 'setTimeout').and.callThrough();

        customTimeout(func, duration);
        expect(func).not.toHaveBeenCalled();

        jasmine.clock().tick(2000);
        expect(func).not.toHaveBeenCalled();

        jasmine.clock().tick(3000);
        expect(func).toHaveBeenCalled();

        expect(window.setTimeout).toHaveBeenCalledWith(jasmine.any(Function), 5000);

    });

    it('merge will call jquery extend and merge two objects into one', function() {

        var extendSpy = spyOn(window.jQuery, 'extend').and.callThrough();

        var source = {
            apple: 0,
            banana: {
                weight: 52,
                price: 100
            },
            cherry: 97
        };

        var dest = {
            banana: {
                price: 200
            },
            durian: 100
        };

        var result = merge(source, dest);

        expect(extendSpy).toHaveBeenCalledWith(source, dest);
        expect(result).toEqualData({
            apple: 0,
            banana: {
                price: 200
            },
            cherry: 97,
            durian: 100
        });

    });

    it('getQueryString will convert given object into query type', function() {

        var sampleObj = {
            key1: 'value1',
            key2: 'value2',
            key3: 'value3',
            key4: 'value4'
        };

        var queryString = getQueryString(sampleObj);

        expect(queryString).toBe('?&key1=value1&key2=value2&key3=value3&key4=value4');

    });

    it('parseQuery will convert give query into an object of params', function() {

        var query = '?abc=abc&def=def&ijk=789';

        var resultObj = parseQuery(query);

        expect(resultObj).toEqualData({
            abc: 'abc',
            def: 'def',
            ijk: '789'
        });

    });

    it('trim function removes space at the beginning and end of a given string', function() {

        var inputString = "  testStringWithSpaces ";

        expect(trim(inputString)).toBe('testStringWithSpaces');

    });

    it('convertToArray will convert a given object to an array', function() {

        var sampleObj = {

            key1: 'value1',
            key2: 'value2',
            key3: 'value3',
            key4: 'value4',

        };

        expect(convertToArray(sampleObj)).toEqualData([{
            key: 'key1',
            value: 'value1'
        }, {
            key: 'key2',
            value: 'value2'
        }, {
            key: 'key3',
            value: 'value3'
        }, {
            key: 'key4',
            value: 'value4'
        }]);

    });

    it('uniqueArray will return an array of unique items from a given pair of input arrays', function() {

        var array1 = ['item1', 'item2', 'item3'];
        var array2 = ['item4', 'item3', 'item6', 'item2'];

        var uniqueArr = uniqueArray(array1, array2);

        expect(uniqueArr).toEqualData(['item1', 'item2', 'item3', 'item4', 'item6']);

    });

    it('regExpFactory will convert a given pattern into a regular expression', function() {

        var pattern1 = '*1234';
        var regExp1 = regExpFactory(pattern1);
        expect(regExp1).toEqualData(/^.*1234$/g);

        var pattern2 = '^((?!Middle).)*$';
        var regExp2 = regExpFactory(pattern2);
        expect(regExp2).toEqualData(/^((?!Middle).)*$/g);

    });

    it('generateIdentifier will generate a unique identifier each time it is called', function() {

        var uniqueKey1 = generateIdentifier();
        var uniqueKey2 = generateIdentifier();

        expect(uniqueKey1).not.toBe(uniqueKey2);

    });

    it('escapeHtml will escape dangerous characters from a given string', function() {

        var string = escapeHtml("hello<button>&'\"");
        expect(string).toBe("hello&lt;button&gt;&amp;&apos;&quot;");

    });

    it('escapeHtml will handle numeric values correctly', function() {

        var string = escapeHtml(123456);
        expect(string).toBe(123456);

    });

    it('closeOpenModalsOnBrowserBack will dismiss open modal windows if open', function() {

        $uibModalStack.getTop.and.returnValue({
            "modal1": "modal1"
        });

        closeOpenModalsOnBrowserBack();
        expect($uibModalStack.getTop).toHaveBeenCalled();
        expect($uibModalStack.dismissAll).toHaveBeenCalled();

    });

    it('closeOpenModalsOnBrowserBack will not dismiss modal windows if no window is open', function() {

        $uibModalStack.getTop.and.returnValue();

        closeOpenModalsOnBrowserBack();
        expect($uibModalStack.getTop).toHaveBeenCalled();
        expect($uibModalStack.dismissAll).not.toHaveBeenCalled();

    });

    it('GIVEN the provided object contains an array WHEN getDataFromResponse is called THEN it returns the array', function() {
        // GIVEN 
        var sampleResponse = {
            somePromise: {},
            otherProperty: 'some property',
            testArray: ['A', 'B', 'C'],
            otherProperty2: {}
        };

        // WHEN
        var result = getDataFromResponse(sampleResponse);

        // THEN
        expect(result).toEqualData(['A', 'B', 'C']);
    });

    it('GIVEN the provided object contains an array WHEN getKeyHoldingDataFromResponse is called THEN it returns the id of the property holding the array', function() {
        // GIVEN 
        var sampleResponse = {
            somePromise: {},
            otherProperty: 'some property',
            testArray: ['A', 'B', 'C'],
            otherProperty2: {}
        };

        // WHEN
        var result = getKeyHoldingDataFromResponse(sampleResponse);

        // THEN
        expect(result).toBe('testArray');
    });

    it('isAllTruthy should return true if each given function returns true', function() {
        var mockData = {
            url: 'http://any_url',
            status: 500
        };

        var fn1 = function(data) {
            return data.url === 'http://any_url';
        };
        var fn2 = function(data, anyParameter) {
            return data.status === 500 && anyParameter === 'test';
        };

        expect(isAllTruthy(fn1, fn2)(mockData, 'test')).toBeTruthy();
    });

    it('isAllTruthy should return false if not all given function return true', function() {
        var mockData = {
            url: 'http://any_url',
            status: 500
        };

        var fn1 = function(data) {
            return data.url === 'http://any_url';
        };
        var fn2 = function(data, anyParameter) {
            return data.status === 404 && anyParameter === 'test';
        };

        expect(isAllTruthy(fn1, fn2)(mockData, 'test')).toBeFalsy();
    });

    it('isAnyTruthy should return true if one of the given function returns true', function() {
        var mockData = {
            url: 'http://any_url',
            status: 500
        };

        var fn1 = function(data) {
            return data.url !== 'http://any_url';
        };
        var fn2 = function(data, anyParameter) {
            return data.status === 500 && anyParameter === 'test';
        };

        expect(isAnyTruthy(fn1, fn2)(mockData, 'test')).toBeTruthy();
    });

    it('compareHTMLElementsPosition using sourceIndex should return a function to compare DOM elements according to their position in the DOM', function() {
        // GIVEN
        var element1 = {
            name: 'element1',
            sourceIndex: 0
        };
        var element2 = {
            name: 'element2',
            sourceIndex: 1
        };
        var element3 = {
            name: 'element3',
            sourceIndex: 2
        };
        var mockElementsUnsorted = [element2, element1, element3];

        // WHEN
        var sortedElements = mockElementsUnsorted.sort(compareHTMLElementsPosition());

        // THEN
        expect(sortedElements).toEqualData([element1, element2, element3]);
    });

    it('compareHTMLElementsPosition using compareDocumentPosition should return a function to compare DOM elements according to their position in the DOM', function() {
        // GIVEN
        var element1 = {
            name: 'element1',
            compareDocumentPosition: function() {
                return 0;
            }
        };
        var element2 = {
            name: 'element2',
            compareDocumentPosition: function(el) {
                return el.name === 'element1' ? 2 : 0;
            }
        };
        var element3 = {
            name: 'element3',
            compareDocumentPosition: function() {
                return -1;
            }
        };
        var mockElementsUnsorted = [element2, element1, element3];

        // WHEN
        var sortedElements = mockElementsUnsorted.sort(compareHTMLElementsPosition());

        // THEN
        expect(sortedElements).toEqualData([element1, element2, element3]);
    });

    it('compareHTMLElementsPosition using compareDocumentPosition and a key should return a function to compare DOM elements according to their position in the DOM', function() {
        // GIVEN
        var element1 = {
            name: 'element1',
            compareDocumentPosition: function() {
                return 0;
            }
        };
        var element2 = {
            name: 'element2',
            compareDocumentPosition: function(el) {
                return el.name === 'element1' ? 2 : 0;
            }
        };
        var element3 = {
            name: 'element3',
            compareDocumentPosition: function() {
                return -1;
            }
        };
        var list = [{
            element: element2
        }, {
            element: element1
        }, {
            element: element3
        }];

        // WHEN
        list.sort(compareHTMLElementsPosition('element'));

        // THEN
        expect(list).toEqualData([{
            element: element1
        }, {
            element: element2
        }, {
            element: element3
        }]);
    });

    it("WHEN deepObjectPropertyDiff is called THEN it returns object that is populated with all the fields that are modified, removed or added by the user", function() {
        // GIVEN
        var firstObject = {
            visible: true,
            restrictions: [],
            content: {
                de: 'aaaa',
                fr: "bbbb"
            },
            position: 2,
            slotId: "id",
            removedField: 1
        };

        var secondObject = {
            visible: false,
            restrictions: ['dddd'],
            content: {
                de: 'aaaa',
                en: "cccc"
            },
            position: 54,
            slotId: "id",
            addedField: 2
        };

        // WHEN
        var result = deepObjectPropertyDiff(firstObject, secondObject);

        // THEN
        var outputObj = {
            content: {
                de: false,
                en: true,
                fr: true
            },
            position: true,
            restrictions: true,
            slotId: false,
            visible: true,
            removedField: true,
            addedField: true
        };
        expect(result).toEqual(outputObj);
    });

    it("WHEN deepIterateOverObjectWith is called THEN it iterates through all the properties of the object and applies the callback for every property value", function() {
        // GIVEN
        var inputObj = {
            visible: true,
            restrictions: [],
            content: {
                de: 'aaaa',
                fr: "bbbb"
            },
            position: 2,
            slotId: "id",
            removedField: 1
        };

        var callback = function(value) {
            return typeof value === "string";
        };

        // WHEN
        var result = deepIterateOverObjectWith(inputObj, callback);

        // THEN
        var outputObj = {
            visible: false,
            restrictions: false,
            content: {
                de: true,
                fr: true
            },
            position: false,
            slotId: true,
            removedField: false
        };

        expect(result).toEqual(outputObj);
    });

    describe("WHEN sanitize is called", function() {
        it("with a parenthesis THEN it gives parenthesis prefixed by a backslash", function() {
            var string = sanitize('(');
            expect(string).toEqual(String.raw `\(`);
        });
        it("with a parenthesis prefixed by a backslash THEN it gives the same", function() {
            var string = sanitize('\(');
            expect(string).toEqual(String.raw `\(`);
        });
        it("with a string containing parantheses THEN it prefixes each parentheses by backslash", function() {
            var string = sanitize('hello(1)');
            expect(string).toEqual(String.raw `hello\(1\)`);
        });
        it("with a string containing parentheses with backslashes THEN it will not prepend the backslash again", function() {
            var string = sanitize('hello\(1\)2');
            expect(string).toEqual(String.raw `hello\(1\)2`);
        });
        it("with complex string THEN it gives the correct result with all parentheses escaped", function() {
            var string = sanitize('fun(\((((crazy))))\)');
            expect(string).toEqual(String.raw `fun\(\(\(\(\(crazy\)\)\)\)\)`);
        });
    });
});
