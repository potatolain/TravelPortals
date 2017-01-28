TravelPortals Plugin

Introduction 
------------

This mod allows you to make simple portals that can transport you between locations.
You make them by making a border around a 2x1 space with obsidian and a wooden or iron
door. You will require 6 obsidian, a door, and a redstone torch.

A visual example is shown on this youtube video by MinecraftZero: http://www.youtube.com/watch?v=8H4JjwDvLMU

It should look like this from above:

```
 o
oRo     o: stack of 2 obsidian on ground; d: door. 
 d      R: a redstone torch
```

If you are successful, the game will tell you that you have created a portal, and the inside will 
fill with water. (This would be the nether portal texture, but Minecraft did not like that idea.)


Installation
------------

Simply drop TravelPortals.jar into your plugins folder, restart the server, and you should be 
set to go! (Get the jar on http://dev.bukkit.org/projects/travelportals/)

If you want to configure the plugin or use permissions, you will need to set these
things up. See the next two sections for details on how to do this.


Configuration
-------------

This plugin now supports configuration options. They are in the standard
config.yml format. The options are explained in the file, and it is found in
the TravelPortals folder within the plugins folder. All of the options are explained in
the comments, which are the lines that begin with a # symbol.


Usage
-----

Using this plugin is fairly simple, and works from the /portal command. The usage of
this command is explained in-game by typing /portal help. Instruct users to use this
command using whatever help system you have in place. 

The Commands:
- /portal help - Show info on how to use the plugin.
- /portal name [name] - Sets the name of the portal in front of you.
- /portal warp [name] - Sets the name of the portal that the portal in front of you warps to.
- /portal hide [name] - Hides or unhides the portal with the name given.
- /portal info - Gives basic information about the portal in front of you.
- /portal info [name] - Gives basic information about the portal in front of you.
- /portal deactivate [name] - Deactivates a portal.
  (This is op-only. Breaking them manually is preferred)
- /portal list [page number] - Gives a list of existing portals.
- /portal export - Dumps the current portals to travelportals.txt in the data folder.
  Format: x,y,z,name,destination,hidden
- /portal reimport [file name] - Can import a list of portals dumped with /portal export.
- /portal claim - Claims a portal that does not currently have an owner. It will also
  unclaim an existing portal, if you own it. You can also include a name to claim a
  portal for someone else.
- /portal reload - Run this to make the plugin reload the config and portals from storage
- /portal renameworld [old world name] [new world name] - Run this after renaming a world
  or the portals in that world will not point to the right place.
- /portal deleteworld [old world name] - Run this after deleting a world to clean up any
  remaining portals.
- /portal fixworld [world name] - This will fix any portals that are not linked to a world
  to be linked to the world specified. You most likely do not need to run this command,
  unless you've been running this plugin since before multiworld was supported. If you
  do, you only need to run it once.


Permissions
-----------

This plugin now supports Bukkit permissions on an optional basis. By default, it 
is turned off. To turn it on, you need to change a configuration option for it.
See the section on configuration for that. The options for it are as follows:

Note: These permissions have changed with version 2.0. The default permissions
limit the user to modifying their own created portals. If the user does
not own a portal, this user will need an admin permission (see below) to modify
said portal.

- travelportals.portal.create
- travelportals.portal.destroy
- travelportals.portal.use
- travelportals.command.help
- travelportals.command.hide
- travelportals.command.list
- travelportals.command.name
- travelportals.command.warp
- travelportals.command.info
- travelportals.command.claim

The plugin also has some admin-only stuff, which can be limited using the 
travelportals.admin permission nodes. In addition, users with admin permissions
to commands will be able to use these commands on any portals; not just ones that
they own.

- travelportals.admin.command.reload
- travelportals.admin.command.name
- travelportals.admin.command.warp
- travelportals.admin.command.claim
- travelportals.admin.command.hide
- travelportals.admin.command.renameworld
- travelportals.admin.command.fixworld
- travelportals.admin.portal.use
- travelportals.admin.portal.destroy

- travelportals.admin.command.deactivate
- travelportals.admin.command.export
- travelportals.admin.command.reimport

Finally, some default groups have been implemented to make life easier: 

- travelportals.nopermission    - The user has no permissions at all. You'll probably want to build off of this.
- travelportals.classicuser     - Makes the permissions work like classic TravelPortals
- travelportals.normaluser      - Generic user permissions, as above. Not really needed.
- travelportals.op              - Suggested permissions for ops. (Has admin stuff)
- travelportals.*               - All permissions you could ever want.


Support & Bug Reports
---------------------

If you have problems setting up the plugin then use the comments on the [project page](https://dev.bukkit.org/projects/travelportals) on BukkitDev.

If you want to report a bug or suggest an enhancement then you should open an [issue](https://github.com/cppchriscpp/TravelPortals/issues).

Someone will try to get back to you as soon as is reasonably possible.


Latest Version
--------------

The latest version of this will always be available at https://dev.bukkit.org/projects/travelportals.

Development builds of the current code state can also be found at https://ci.minebench.de/job/TravelPortals/


Building
--------

TravelPortals uses Maven to handle dependencies and can be build with it.

To use it you need Maven 3.3.x and then run `mvn clean package`. Most modern IDEs already have support for maven projects.