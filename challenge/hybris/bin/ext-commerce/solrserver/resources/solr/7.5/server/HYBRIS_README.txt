
hybris Changes
=============================

This is a modified version of Solr.

The following directories were removed:
- docs
- example
- contrib/extraction (due to CVE-2018-1000613, affected libraries are bcmail-jdk15on-1.54.jar, bcpkix-jdk15on-1.54.jar and bcprov-jdk15on-1.54.jar)

The following files/directories were added:
- HYBRIS_README.txt
- contrib/hybris
- server/solr/security.json.example
- server/solr/solr.jks

The following files/directories were modified/replaced:
- bin/solr.cmd:
	- workaround for https://issues.apache.org/jira/browse/SOLR-7283 (lines 19-20)
- bin/solr.in.cmd:
	- authentication and ssl configuration example (lines 152-165)
- bin/solr.in.sh:
	- authentication and ssl configuration example (lines 180-193)
- server/solr/solr.xml
- server/solr/configsets
