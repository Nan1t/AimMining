### Aim Mining

The plugin is an aim mining system.
In the config, you can configure the worlds in which this system operates, the starting speed of production and the bonus for a successful crit street (successful aim crit on a block N times in a row).

```yml
mining:
  # A worlds in which this system will works
  worlds:
    - 'world'
  startSpeed: 25.0 # In percents
  critTempBonus: 25.0 # Bonus to mining speed for the current block, subject to a crit
  critStreakBonus: 0.01 # Mining speed bonus for each cri
messages:
  critMessage: '&7Crit Streak: &a${count}&7 (&e+${speed}&7 Скорость добычи)'
  critFailMessage: '&7Crit Streak: &c${count}&7 (&e+${speed}&7 Скорость добычи)'
  uselessTool: '&cЭто не самый подходящий инструмент'

# Is drop the item from the block, or give it to the player
dropItem: false

# Configure drop for each block
drop:
  COAL_ORE:
    drop: COAL # Item material
    amount: 1 # Items amount
    replaceBlock: STONE # The block to replace the fossil with. This line may be missing, in which case the block will simply be deleted
  EMERALD_ORE:
    drop: EMERALD
    amount: 1
    replaceBlock: STONE

# Block blacklist. Blocks in this list will not be mined and will not be damaged by the player
blockBlackList:
  - STONE
  - GRASS
```
