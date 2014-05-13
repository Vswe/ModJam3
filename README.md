ModJam3
=======

Setup Workspace
---

```sh
git clone [git-repo-url] MoarSigns
```

Download [CodeChickenCore-1.7.2-1.0.0-dev][1], [NotEnoughItems-1.7.2-1.0.1-dev][1] & [Waila-1.5.2a_1.7.2][2]

Add a folder called `libs`, in the folder put the two downloaded `.jar` files.

If you don't have [Gradle][3] installed on your computer you can use `gradlew` or `gradlew.bat` instead

For help setting up a ForgeGradle workspace, go to this [forum page][4].

##### Installing for Intellij IDEA
```sh
gradle setupDecompWorkspace idea
```

##### Installing for Eclipse
```sh
gradle setupDecompWorkspace eclipse
```

[1]:http://www.chickenbones.craftsaddle.org/Files/New_Versions/links.php
[2]:http://www.minecraftforum.net/topic/1846244-172-
[3]:http://www.gradle.org/
[4]:http://www.minecraftforge.net/forum/index.php/topic,14048.0.html