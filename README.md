# Ball-Game
ball game to play

Game player: Only one
Game world:Ball,obstacles
Glay goal:Click the screen to block the ball
Game rules:Firstly,there is a keyball in the middle,when you click the screen,there will be a block to create to surround the ball,as the same time,the ball will move.Try to create block balls to surround the keyball.
Win and Lose:If the ball move to the edge,you lose.If you surround the keyball,you win.

游戏开始时会产生一个红球和10个阻塞的黑球。当你点击屏幕，就会生成一个阻塞的球，同时红球会向边缘移动一步，当红球到达边缘时，你就输了，当你围住这个红球时，你就赢了。

游戏文件介绍：是用android studio写的一个游戏，生成了一个apk文件，可以下载安装。surroundtheballProject.zip就是这个全部文件了。
Dot是球的类，里面包括了球的状态信息和球的位置信息。StartToPlay类是球的逻辑信息，包括红球的自动移动策略和点击生成障碍等，绘图也是在这里进行的。
