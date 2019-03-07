# Warm Up

This is the Warm Up for a hybris project.

## Setup

Add the following to hosts file:

- 127.0.0.1       apparel-uk.local
- 127.0.0.1       apparel-de.local
- 127.0.0.1       electronics.local

Extract hybris **1811** into a folder.

Create a link between Git folders and hybris:
- ln -s ~/**GIT_REPOSITORY_FOLDER**/config/ ~/**HYBRIS_FOLDER**/config
- ln -s ~/**GIT_REPOSITORY_FOLDER**/src/ ~/**HYBRIS_FOLDER**/bin/custom

## Endpoints

- https://electronics.local:9002/keyruswarmupstorefront/
- https://apparel-uk.local:9002/keyruswarmupstorefront/
- https://apparel-de.local:9002/keyruswarmupstorefront/
