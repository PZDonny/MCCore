# **MCCore**

Sequestered Minecraft Server plugin library and MongoDB database connector (when enabled in config).
> Maven dependency at the bottom

**Plugin Dependencies**
- WorldEdit
- WorldGuard

**Commands**
- /cc (Clear your own chat)
- /gms, /gma, /gmc, /gmsp [perm: sq.gamemode] (Changes gamemode for yourself and others)

- /fly (Enable/Disable flight)
> Use "sq.fly.use" as a buyable rank permission

> Use "sq.fly.staff" as a staff permission, and being able to set the flight of others

- /speed [perm: sq.speed] (Change movement speed)

- /ci (Clear your own inventory)
> Use "sq.clearinv" to allow clearing of own inventory

> Use "sq.clearinv.others" to allow clearing of other player's inventories

- /day, /noon, /night, /midnight [perm: sq.daycycle] (Change the time of day)
- /corereload (Reload the plugin's config)

**Maven Dependency**
```
<dependency>
  <groupId>MCCore</groupId>
  <artifactId>mccore</artifactId>
  <version>VERSION</version>
  <scope>provided</scope>
</dependency>
```
