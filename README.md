# Together-Forever
Together Forever is a Team API that allows you to sync stuff like advancements or gamestages between the players of your team. By default Advancement Syncing and GameStages Syncing is enabled. If the players of the team were offline when the trigger for a sync happen, that sync will occur when they join to the server.
All the types of sync can be disabled in the config

Teams are managed by [FTB Utilities](https://www.curseforge.com/minecraft/mc-mods/ftb-utilities) (and FTB Library). Use FTB's own team system (`/ftb team ...` or the FTB Utilities team GUI) to create teams, invite players and manage membership. Together Forever reads the team you belong to from FTB and syncs progress between the members.

## Commands
+ `/tofe help` - Lists the available commands
+ `/tofe forcesync` - Forces the sync of team information from another online team member

You can also use `/together <sub_command>` or `/togetherforever <sub_command>`