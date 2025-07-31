package com.meztli.alufx;

import java.io.File;
import java.io.IOException;

public class MariaDBLauncher {
    public static void launch() {
        try {
            File batFile = new File("scripts/start-mariadb.bat");
            if (!batFile.exists()) {
                System.err.println("❌ Script start-mariadb.bat no encontrado");
                return;
            }

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", batFile.getAbsolutePath());
            pb.inheritIO();
            pb.start();
            System.out.println("✅ MariaDB iniciado con .bat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

