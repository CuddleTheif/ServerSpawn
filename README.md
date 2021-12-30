# ServerSpawn
Spigot plugin for creating different kinds of server spawns 

# Spawn Types
This plugin allows the server to set a few spawns that do different things.\
They are set in the config or with the /setspawn command

## Server Spawn
This is the spawn point players go to when they enter a server after being offline for a set amount of time (time can be 0 so they always go to this point on entering server)

## Command Spawn
This is the spawn point players go to when they use the /spawn command

## First Spawn
This is the spawn point players go to when they enter the server for the very first time

## Respawn
This is the spawn point players go to when they die. Can be set to override the player's bed respawn or not

## Void
This is the spawn point (set per world) the player goes to when they take damage from the void in the set world.

# Commands

## /spawn
Teleports the player to the set Command Spawn. If no Command spawn is set teleports them to the Server Spawn. If neither are set teleports them to the Respawn. If that's not set teleports them to their bed spawn.

## /setspawn [type]
Set's the spawn type to the location you are currently standing at (with rotation).\
Possible types (If no type given defaults to setting server spawn)
- command
- first
- respawn
- server
- void (requires a world name as well to set the void spawn for. /setspawn void [world])  


## /ssreload
Reloads all the data from the config file

# Config

See the default config for an example

## tp-text
The text that displays when a player teleports to the spawn set to /spawn\
If none set, doesn't send a message

## spawn-text
The text that displays when a player starts the /spawn command before they actually teleport\
If none set, doesn't send a message

## no-move
The text sent when a player moves after they use the /spawn command but before they teleport\
If none set, movement doesn't stop teleporting. If set to "" move still stops teleporting but no message is sent

## spawn-time
The time it takes from when the player uses the /spawn command to when they actually teleport in seconds

## server-time
The time a player must be offline for them to use the server spawn instead of where they left when they enter the server in seconds

## spawn-cooldown-perm
The permission for players who ignore the spawn-time and teleport instantly when using the /spawn command

## respawn-bed-override
If the respawn should override player's bed spawns when they die. If not respawn will only apply if they don't have a bed set

## server
The spawn point a player goes to when they enter the server after they've been offline for the amount of time set at [server-time](#server-time)

## command
The spawn point a player goes to when they use the /spawn command

## first
The spawn point a new player that has never been on the server goes to when they join

## respawn
The spawn point a player goes to when they die. (If [respawn-bed-override](#respawn-bed-override) is false they only go there if they don't have a bed set)

## void
A list of locations set by the name of the world they apply to.\
Where the player gets teleported to when they get hurt by the void in the world.
