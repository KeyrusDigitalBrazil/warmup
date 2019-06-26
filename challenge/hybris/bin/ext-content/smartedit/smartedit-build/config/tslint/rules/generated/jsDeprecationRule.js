"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var __makeTemplateObject = (this && this.__makeTemplateObject) || function (cooked, raw) {
    if (Object.defineProperty) { Object.defineProperty(cooked, "raw", { value: raw }); } else { cooked.raw = raw; }
    return cooked;
};
exports.__esModule = true;
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
/* tslint:disable:max-classes-per-file */
var Lint = require("tslint");
var utils = require("tsutils");
var ts = require("typescript");
var OPTION_DEPRECATE_FROM_VERSION = null;
var Rule = /** @class */ (function (_super) {
    __extends(Rule, _super);
    function Rule() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    Rule.prototype.apply = function (sourceFile) {
        return this.applyWithWalker(new DeprecationWalker(sourceFile, this.ruleName, {
            deprecateFromMajorVersion: this.ruleArguments[0], deprecateFromMinorVersion: this.ruleArguments[1]
        }));
    };
    Rule.metadata = {
        ruleName: "jsDeprecation",
        description: "Throws errors when @deprecated annotations are is found",
        rationale: Lint.Utils.dedent(templateObject_1 || (templateObject_1 = __makeTemplateObject(["\n           Adds deprecation validation. Use @deprecated x.x in your JavaScript JSDocs or NGDocs. (x.x = the version number)."], ["\n           Adds deprecation validation. Use @deprecated x.x in your JavaScript JSDocs or NGDocs. (x.x = the version number)."]))),
        optionsDescription: Lint.Utils.dedent(templateObject_2 || (templateObject_2 = __makeTemplateObject(["\n            One of the following options may be provided:\n            * `\"", "\"` determines from which version deprecation errors should be thrown.\n        "], ["\n            One of the following options may be provided:\n            * \\`\"", "\"\\` determines from which version deprecation errors should be thrown.\n        "])), OPTION_DEPRECATE_FROM_VERSION),
        options: {
            type: "array",
            items: {
                type: "string",
                "enum": [
                    OPTION_DEPRECATE_FROM_VERSION
                ]
            }
        },
        type: "functionality",
        typescriptOnly: false
    };
    return Rule;
}(Lint.Rules.AbstractRule));
exports.Rule = Rule;
var DefaultHybrisVersion = /** @class */ (function () {
    function DefaultHybrisVersion(version) {
        this.toString = version;
        var versionMatches = version.match(/^([0-9]+)(?:.([0-9]+)|)$/);
        this.majorVersion = +versionMatches[1];
        if (!versionMatches[2]) {
            versionMatches[2] = "0";
        }
        this.minorVersion = +versionMatches[2];
    }
    return DefaultHybrisVersion;
}());
exports.DefaultHybrisVersion = DefaultHybrisVersion;
var DeprecationWalker = /** @class */ (function (_super) {
    __extends(DeprecationWalker, _super);
    function DeprecationWalker() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    DeprecationWalker.prototype.walk = function (sourceFile) {
        var _this = this;
        // override to prevent displaying WARNING in console output. tslint does not support ruleSeverity: 'info'.
        // tslint:disable-next-line
        console.warn = function () { };
        utils.forEachTokenWithTrivia(sourceFile, function (fullText, kind, range, parent) {
            if (kind === ts.SyntaxKind.MultiLineCommentTrivia) {
                var commentText = fullText.slice(range.pos, range.end);
                var deprecatedVersion = _this.getDeprecatedVersionFromAnnotation(commentText);
                if (deprecatedVersion && _this.isVersionDeprecatedFrom(_this.options.deprecateFromMajorVersion, _this.options.deprecateFromMinorVersion, deprecatedVersion)) {
                    var startCodeSnippet = parent.getStart(parent.getSourceFile());
                    var endCodeSnippet = fullText.indexOf("\n", startCodeSnippet);
                    var codeSnippet = fullText.slice(startCodeSnippet, endCodeSnippet);
                    // tslint:disable-next-line
                    console.info("INFO: " + parent.getSourceFile().fileName + " : deprecated code since version " + deprecatedVersion.toString + " : " + codeSnippet);
                    // this.addFailureAtNode(parent, "deprecated code since version " + deprecatedVersion.toString + " : " + codeSnippet);
                }
            }
        });
    };
    DeprecationWalker.prototype.isVersionDeprecatedFrom = function (fromMajor, fromMinor, hybrisVersion) {
        if (hybrisVersion.majorVersion < fromMajor) {
            return true;
        }
        if (hybrisVersion.majorVersion === fromMajor) {
            if (hybrisVersion.minorVersion <= fromMinor) {
                return true;
            }
        }
        return false;
    };
    DeprecationWalker.prototype.getDeprecatedVersionFromAnnotation = function (comment) {
        var deprecatedRegexp = /@deprecated/i;
        var deprecationStart = comment.search(deprecatedRegexp);
        if (deprecationStart > -1) {
            var deprecationEnd = comment.indexOf("\n", deprecationStart);
            var deprecationSnippet = comment.slice(deprecationStart, deprecationEnd);
            var deprecatedVersionRegexp = /^.*\s([0-9\.]+)/i;
            var match = deprecatedVersionRegexp.exec(deprecationSnippet);
            if (match !== null) {
                return new DefaultHybrisVersion(match[1]);
            }
        }
        return null;
    };
    return DeprecationWalker;
}(Lint.AbstractWalker));
exports.DeprecationWalker = DeprecationWalker;
var templateObject_1, templateObject_2;
