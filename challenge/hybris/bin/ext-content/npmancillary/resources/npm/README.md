# NPMANCILLARY EXTENSION - NPM 


##  How to regenerate the _npm-shrinkwrap.json_ file

The _npm-shrinkwrap.json_ should be automatically updated if you perform operations using *npm* command with the _--save_ argument, e.g *npm install bootstrap --save*. For any other case, when you want to regenerate the _npm-shrinkwrap.json_ file, follow these steps: 

1. From the root of the extension, type
```
$ ant npmpackagelock
```
The command above will remove  the old _npm-shrinkwrap.json_ file, then it will reinstall node_modules and generate the _npm-shrinkwrap.json_ file.

2. Commit the changes that were made in _package.json_ (if necessary) and _npm-shrinkwrap.json_.

3. To revert changes made in resources/npm/node folder:

One way of doing it is to rewrite the history. Remember to commit the _npm-shrinkwrap.json_ file and _package.json_ (if necessary) first!
```
$ git reset --hard
```

If there are new files after you get the _'git status'_, then remove those files manually or using the following git command
```
$ git clean -fd 
```

4. After that you need to remove node_modules folder and run
```
$ ant npminstall
```

## Windows Users

If you experience problems during a regular ```ant npminstall```, install NodeJS manually using the [node-v10.7.0-x64.msi] installation file and rewrite the contents of /node/node-v10.7.0-win-x64 with it. 

Normally NodeJS for Windows will install NodeJS under _C:\Program Files\nodejs_.    
