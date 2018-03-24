# Playing screeps using Kotlin

### Types
Most of the types were generated with ts2kt (0.1.3) from https://github.com/screepers/screeps-typescript-starter
and manually improved

### Deployment

Deployment is automated with gradle. 
Use the 'deploy' task to push to sceeps.com. 
The branch 'kotlin' is used by default, make sure it exists.

Credentials can be provided with a 'gradle.properties' file.
    
    screepsUser=<your-username>
    screepsPassword=<your-password>


### Usage

Call your main function from Main.loop
Have a look at the tutorials in src/main/kotlin screeps.game.tutorials (call the tutorials gameloop() in Main.loop to test them)
