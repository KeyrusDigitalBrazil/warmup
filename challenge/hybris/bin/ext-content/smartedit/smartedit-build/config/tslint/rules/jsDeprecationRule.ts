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
import * as Lint from "tslint";
import * as utils from "tsutils";
import * as ts from "typescript";

const OPTION_DEPRECATE_FROM_VERSION: any = null;

interface Options {
	deprecateFromMajorVersion: null;
	deprecateFromMinorVersion: null;
}

export class Rule extends Lint.Rules.AbstractRule {

	public static metadata: Lint.IRuleMetadata = {
		ruleName: "jsDeprecation",
		description: "Throws errors when @deprecated annotations are is found",
		rationale: Lint.Utils.dedent`
           Adds deprecation validation. Use @deprecated x.x in your JavaScript JSDocs or NGDocs. (x.x = the version number).`,
		optionsDescription: Lint.Utils.dedent`
            One of the following options may be provided:
            * \`"${OPTION_DEPRECATE_FROM_VERSION}"\` determines from which version deprecation errors should be thrown.
        `,
		options: {
			type: "array",
			items: {
				type: "string",
				enum: [
					OPTION_DEPRECATE_FROM_VERSION
				],
			},
		},
		type: "functionality",
		typescriptOnly: false,
	};

	public apply(sourceFile: ts.SourceFile): Lint.RuleFailure[] {
		return this.applyWithWalker(new DeprecationWalker(sourceFile, this.ruleName, {
			deprecateFromMajorVersion: this.ruleArguments[0], deprecateFromMinorVersion: this.ruleArguments[1]
		}));
	}
}

export interface HybrisVersion {
	majorVersion: number;
	minorVersion: number;
	toString: string;
}

export class DefaultHybrisVersion implements HybrisVersion {
	majorVersion: number;
	minorVersion: number;
	toString: string;

	constructor(version: string) {
		this.toString = version;
		const versionMatches = version.match(/^([0-9]+)(?:.([0-9]+)|)$/);
		this.majorVersion = +versionMatches[1];
		if (!versionMatches[2]) {
			versionMatches[2] = "0";
		}
		this.minorVersion = +versionMatches[2];
	}
}

export class DeprecationWalker extends Lint.AbstractWalker<Options> {
	public walk(sourceFile: ts.SourceFile) {
		// override to prevent displaying WARNING in console output. tslint does not support ruleSeverity: 'info'.
		// tslint:disable-next-line
		console.warn = function() {};
		utils.forEachTokenWithTrivia(sourceFile, (fullText: string, kind: ts.SyntaxKind, range: ts.TextRange, parent: ts.Node) => {
			if (kind === ts.SyntaxKind.MultiLineCommentTrivia) {
				const commentText: string = fullText.slice(range.pos, range.end);
				const deprecatedVersion: HybrisVersion = this.getDeprecatedVersionFromAnnotation(commentText);
				if (deprecatedVersion && this.isVersionDeprecatedFrom(this.options.deprecateFromMajorVersion, this.options.deprecateFromMinorVersion, deprecatedVersion)) {
					const startCodeSnippet: number = parent.getStart(parent.getSourceFile());
					const endCodeSnippet: number = fullText.indexOf("\n", startCodeSnippet);
					const codeSnippet: string = fullText.slice(startCodeSnippet, endCodeSnippet);
					// tslint:disable-next-line
					console.info("INFO: " + parent.getSourceFile().fileName + " : deprecated code since version " + deprecatedVersion.toString + " : " + codeSnippet);
					// this.addFailureAtNode(parent, "deprecated code since version " + deprecatedVersion.toString + " : " + codeSnippet);
				}
			}
		});
	}

	public isVersionDeprecatedFrom(fromMajor: number, fromMinor: number, hybrisVersion: HybrisVersion): boolean {
		if (hybrisVersion.majorVersion < fromMajor) {
			return true;
		}
		if (hybrisVersion.majorVersion === fromMajor) {
			if (hybrisVersion.minorVersion <= fromMinor) {
				return true;
			}
		}
		return false;
	}

	public getDeprecatedVersionFromAnnotation(comment: string): HybrisVersion {
		const deprecatedRegexp = /@deprecated/i;
		const deprecationStart: number = comment.search(deprecatedRegexp);
		if (deprecationStart > -1) {
			const deprecationEnd: number = comment.indexOf("\n", deprecationStart);
			const deprecationSnippet: string = comment.slice(deprecationStart, deprecationEnd);
			const deprecatedVersionRegexp = /^.*\s([0-9\.]+)/i;
			const match = deprecatedVersionRegexp.exec(deprecationSnippet);
			if (match !== null) {
				return new DefaultHybrisVersion(match[1]);
			}
		}
		return null;
	}
}
