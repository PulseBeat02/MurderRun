[![CodeFactor](https://www.codefactor.io/repository/github/pulsebeat02/murderrun/badge)](https://www.codefactor.io/repository/github/pulsebeat02/murderrun)
[![GitHub Actions](https://github.com/PulseBeat02/MurderRun/actions/workflows/tagged-release.yml/badge.svg)](https://github.com/PulseBeat02/MurderRun/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=PulseBeat02_MurderRun&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=PulseBeat02_MurderRun)

![横幅](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_header.webp)
[![赞助商](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_promo2.webp)](https://bisecthosting.com/pulse)
[![GitHub](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_github.webp)](https://github.com/PulseBeat02/MurderRun)
[![Discord](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_discord.webp)](https://discord.gg/cUMB6kCsh6)
[![Kofi](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_kofi.webp)](https://ko-fi.com/pulsebeat_02)
![描述](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_description.webp)

# 2025年1月更新：连接到插件测试服务器 **`murderrun.mcserver.us`** 与好友体验谋杀逃亡！

灵感源自热门游戏《黎明杀机》，谋杀逃亡是一个围绕杀手与幸存者的高级游戏模式。在荒凉的地图中，幸存者必须找到所有车辆零件并扔回卡车上，而杀手则要在那之前消灭所有人。杀手和幸存者都可以使用100+种不同的道具来获取优势或制造障碍。本游戏基于SSundee在YouTube上的"谋杀逃亡"系列。以下是本插件的更多精彩特性：

- 免费开源
- 100+杀手/幸存者道具
- 简易大厅/竞技场创建与自定义
  - 使用GUI或命令创建/修改竞技场/大厅
- 通过WorldEdit自动重置竞技场
- 自定义物品掉落点
  - 支持自动物品掉落点
  - 无需竞技场设置
- 预制NPC道具商店（可调整价格）
  - 可禁用特定道具
- 游戏创建支持快速加入系统或私人房间
  - 使用GUI或命令创建游戏
- 集成PlaceholderAPI、LibsDisguises、Citizens和WorldEdit
  - 通过PlaceholderAPI实现自定义统计
    - `%%fastest_win_killer%%`, `&&fastest_win_survivor&&`, `%%total_kills%%`, `%%total_deaths%%`,
    `%%total_wins%%`, `%%total_losses%%`, `%%total_games%%`, `%%win_loss_ratio%%`
  - 通过LibsDisguises实现自定义道具
  - 无需PlaceholderAPI和LibsDisguises
- 零依赖/即插即用，放入plugins文件夹即可
- 可定制资源包
  - 自定义物品材质、音效
  - 自定义资源包提供方式
  - 无需服务器托管
    - 自动托管于服务器
    - 提供其他托管选项
  - 资源包缓存加速加载
- 可定制语言文件（修改/重排消息）
  - 使用MiniMessage简化格式
- 多语言支持
  - 感谢 **GTedd** 提供简繁中文翻译！
- 可定制道具属性
  - 禁用道具
  - 修改冷却时间、音效和药水效果
- 可定制游戏属性
  - 设置游戏计时器
  - 设置额外资源包
- 可定制存活/死亡聊天
- 支持多杀手/幸存者
- 通过Hibernate支持数据库（MySQL、SQLite、PostgreSQL、H2）
  - 可定制数据库属性
- 更多功能等你探索...
<br></br>

![游戏截图](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_screenshots.webp)
![幸存者道具](https://raw.githubusercontent.com/PulseBeat02/MurderRun/refs/heads/main/survivor.gif)
![杀手道具](https://raw.githubusercontent.com/PulseBeat02/MurderRun/refs/heads/main/killer.gif)
![道具演示](https://raw.githubusercontent.com/PulseBeat02/MurderRun/refs/heads/main/gadget.gif)
<br></br>

![安装指南](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_installation.webp)
1) 从[最新发布页](https://github.com/PulseBeat02/MurderRun/releases/tag/latest)下载插件
2) 将插件放入服务器的`plugins`文件夹并启动服务器，生成默认配置文件
3) 在`murderrun`文件夹中按需配置插件
4) 输入`/murder gui`打开GUI界面。创建并配置竞技场/大厅。在大厅中使用`/murder npc spawn survivor`和`/murder npc spawn killer`生成幸存者/杀手道具商店
5) 通过`/murder gui`创建新游戏
6) 点击聊天栏中的青色加粗消息邀请玩家。关闭并重新打开菜单更新玩家列表。通过点击玩家头颅切换幸存者/杀手身份（需玩家已加入游戏）
<br></br>

![常见问题](https://www.bisecthosting.com/images/CF/Murder_Run/BH_Murder_Run_faq.webp)
- **配置**：多种配置方式。通过编辑`murderrun/locale/murderrun_XX_XX.properties`文件使用MiniMessage自定义消息格式。在`murderrun/settings/game.properties`中修改游戏属性（道具音效、持续时间、效果等）。在`murderrun/sounds`和`murderrun/textures`文件夹修改资源包内容。默认使用MC Packs托管资源包，可通过`config.yml`配置JSON、数据库、HTTP服务器等选项
<br></br>

- **命令**：使用`/murder help`查看命令列表。支持获取特定道具、管理大厅/竞技场/游戏、生成NPC商店等功能
<br></br>

- **权限**：权限基于命令结构，例如`/murder command gadget retrieve-all`对应`murderrun.command.gadget.retrieve-all`权限。推荐使用LuckPerms管理权限