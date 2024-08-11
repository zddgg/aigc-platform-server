package space.wenliang.ai.aigcplatformserver.test;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OpenFolder {
    public static void main(String[] args) throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File dirToOpen = null;
        try {
            dirToOpen = new File("C:\\Users\\liuwe\\IdeaProjects\\aigc-platform-server\\project\\text\\斗破苍穹测试\\第1章 陨落的天才");
            desktop.open(dirToOpen);
        } catch (IllegalArgumentException iae) {
            System.out.println("File Not Found");
        }
    }
}