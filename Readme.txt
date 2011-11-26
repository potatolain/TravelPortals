TravelPortals Plugin
Version 2.0

Introduction 
------------

Hello, and thank you for downloading my Bukkit plugin.

This mod allows you to make simple portals that can transport you between locations.
You make them by making a border around a 2x1 space with obsidian and a wooden or iron
door. You will require 6 obsidian, a door, and a redstone torch.
It should look like this from above:
 o
oRo     o: stack of 2 obsidian on ground; d: door. 
 d      R: a redstone torch

If you are successful, the game will tell you that you have created a portal, and the inside will 
fill with water. (This would be the nether portal texture, but Minecraft did not like that idea.)


Installation
------------

Simply drop TravelPortals.jar and the TravelPortals folder into your plugins folder, 
restart the server, and you should be set to go!

If you want to configure the plugin or use permissions, you will need to set these
things up. See the next two sections for details on how to do this.

You don't need the source folder; you can just delete it unless you are interested in the source code
to this plugin. 

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
- /portal name [name] sets the name of the portal in front of you.
- /portal warp [name] sets the name of the portal that the portal in front of you warps to.
- /portal hide [name] hides or unhides the portal with the name given.
- /portal info gives basic information about the portal in front of you.
- /portal info [name] gives basic information about the portal in front of you.
- /portal deactivate [name] deactivates a portal. 
  (This is op-only. Breaking them manually is preferred)
- /portal list [page number] gives a list of existing portals. 
- /portal export dumps the current portals to travelportals.txt in the data folder.
  Format: x,y,z,name,destination,hidden


Permissions
-----------

This plugin now supports Bukkit permissions on an optional basis. By default, it 
is turned off. To turn it on, you need to change a configuration option for it.
See the section on configuration for that. The options for it are as follows:

Note: These permissions have changed with version 2.0. The default permissions
limit the user to modifying and using their own created portals. If the user does
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

- travelportals.admin.command.name
- travelportals.admin.command.warp
- travelportals.admin.command.claim
- travelportals.admin.command.hide
- travelportals.admin.portal.use
- travelportals.admin.portal.destroy

- travelportals.admin.command.deactivate
- travelportals.admin.command.export


Support & Bug Reports
---------------------

You have a few options for this. The first is by directly emailing me. My email address is owner@cpprograms.net. 

Your other option is the thread on the Bukkit forums.

Either way, I will try to get back to you as soon as is reasonably possible.

SUBMITTING A SUPPORT REQUEST
----------------------------

If you want help with this plugin, please provide the following information about your
server setup. It will help me figure out what is wrong, and what you or I can do about
it. 

- an EXTREMELY detailed description of the problem (Don't spare any words; I don't mind reading)
- Server's Operating System
- Server's version of Java (See here: www.java.com/en/download/installed.jsp; update and try again if you are out of date)
- Server's CraftBukkit build (go to the console and type /version; provide that.)
- A list of plugins you use. (Particularly if there's a chance it is caused by another plugin)
- Any changes in your setup that preceeded the errors
- Any other details that could be relevant. 


Latest Version
--------------

The latest version of this will always be available at http://www.cpprograms.net.