Change Log
==========

2.2.7
-----

* Water in portals will no longer flow downward if the block below them is broken. Should prevent portals from being able to create infinite lava in the nether.

2.2.6
-----
* Adds a deleteworld feature

2.2.5
-----

* Adds an import feature (that no one will ever use)
* Fixes documentation for certain commands not showing up in /help

2.2.4
-----

* Fixes the location issue once and for all. (I hope) No more jumping, and no more warping from the wrong spot.

2.2.3
-----

* Fixes an issue with R6 and a plugin base method conflicting with the name of a new function in the Bukkit class it supercedes. 
* Fixes a dumb coding mistake that caused users to teleport from the wrong place in some scenarios. 

2.2.2
-----

* Updates to the new Event system
* Fixes a naming conflict with the general plugin architecture this is based off of.

2.2.1
-----

* Fixes an issue with unpacking the default configuration file. Not an essential update.


2.2
---

### New Features
* Regular polling system thanks to asofold (May result in better performance on servers with a large number of players)
** This must be enabled via a config node!
* Commands to rename worlds, and fix existing worlds which do not have names saved
* Default configuration file is automatically unpacked (Finally)
* You can now claim a portal for someone else
* World names are now shown in the portal info command
* A permission node has been added to start users with 0 permissions. (nopermission)

### Fixes/Other Changes
* Reliance on internal block type has been removed. New users can make portals in air without fear of the consequences on processing power! (If you're really concerned, use asofold's polling)
* Permissions checking has been redone to be slightly less clunky
* The portal export command exports all information that we have again...
* Plugin moved to a unified architecture used for all plugins. This greatly cleans up the code, and fixes a number of small issues, while also making future development easier. All three of my plugins have been moved to this architecture. 

2.1
---

### New Features
* None

### Other Changes
* Importing pre-2.0 portals fixed. Sorry for that one.
* Removed links to a VERY old file format (think hmod era)


2.0
---

### New Features
* Portals now have owners!
* Permissions system upgrade - we now just support SuperPerms/The built in permissions system. 
* Reworked permission system to take owners into account. Users can be limited to their own portals (See the permissions page)
* The chunk you are being teleported into is now preloaded.
* New /portal claim command which allows users to claim portals without owners. (Using the command on a portal you own will set it to have no owner again.)
* New /portal info command, which gives information about a nearby portal. It can also be used with the name of a portal to get information about that portal.

### Other Changes
* Permissions listed in plugin.yml
* Original permissions plugin support removed to clean up code. 
* Changed the names of a few permissions. (Mainly command.dumplist was renamed to command.export. You know, the command you use to export the portals?)
* Various minor bug fixes.
* Namespace change - we're finally using a namespace Bukkit doesn't frown upon again!
* Code is much cleaner than in prior versions.


1.5_1
-----

* Fixed a small world loading issue. If you ever had issues with multiworld worlds not loading right, this build is for you!

1.5
---

* moved TravelPortals.ser to the data folder (finally)
* also moved travelportals.txt to data folder
* more intuitive backup system that should remedy any problems with saves
** new configuration variable; lets you define how many backups of the portals to keep. This should make recovery easy if anything ever goes wrong.
* lots of code fixes - if anyone saw all of the misuse of static in the plugin, it's fixed!
* A minor typo that may not have even been in a released version of the plugin
* Gives a user feedback when just using /portal.

(For older version information, see the old forum post [[http://forums.bukkit.org/threads/tp-travelportals-1-5_1-617-1337.920/|here]].)
