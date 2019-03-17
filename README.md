# Playing screeps using Kotlin

**start here**: https://github.com/exaV/screeps-kotlin-starter

Standalone types as a library are available too: https://github.com/exaV/screeps-kotlin-types

Join the official screeps slack on https://chat.screeps.com/ and join #kotlin for help and general screeps kotlin chat.

## Contribute
If you want to contribute you should definitely checkout out the two repos above and create a PR!

## Using this repo
clone: `git clone --recurse-submodules https://github.com/exaV/screeps-kotlin`


### Deployment

Deployment is automated with gradle. 
Use the 'deploy' task to push to sceeps.com. 
The branch 'kotlin' is used by default, make sure it exists.

Credentials can be provided with a 'gradle.properties' file.
    
    screepsUser=<your-username>
    screepsPassword=<your-password>


### Usage

Call your main function from Main.loop

Have a look at the [tutorials](https://github.com/exaV/screeps-kotlin/tree/master/src/main/kotlin/screeps/game/tutorials). Call the tutorials gameloop() in Main.loop to test them.
