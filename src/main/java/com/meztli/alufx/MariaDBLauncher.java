package com.meztli.alufx;

import java.io.File;
import java.io.IOException;

public class MariaDBLauncher {
    private static Process dbProcess;

    public static void launch() {
        // Solo intentar arrancar si estamos en Windows, ya que los ejecutables de
        // MariaDB incluidos son de Windows (.exe)
        String osName = System.getProperty("os.name").toLowerCase();
        if (!osName.contains("win")) {
            System.out.println("ℹ️ Sistema operativo no es Windows. Omitiendo lanzamiento de MariaDB portable.");
            return;
        }

        try {
            File batFile = getFile("scripts/start-mariadb.bat");
            if (!batFile.exists()) {
                System.err.println("❌ Script start-mariadb.bat no encontrado en: " + batFile.getAbsolutePath());
                return;
            }

            System.out.println("Iniciando proceso MariaDB desde: " + batFile.getAbsolutePath());
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", batFile.getAbsolutePath());
            pb.inheritIO();
            dbProcess = pb.start();
            System.out.println("✅ MariaDB lanzado con .bat");

            // Registrar Shutdown Hook para cerrar MariaDB y sus descendientes al salir
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Deteniendo MariaDB y procesos hijos...");
                if (dbProcess != null) {
                    try {
                        dbProcess.descendants().forEach(ProcessHandle::destroyForcibly);
                    } catch (Exception e) {
                        System.err.println("Error destruyendo descendientes: " + e.getMessage());
                    }
                    try {
                        dbProcess.destroyForcibly();
                    } catch (Exception e) {
                        System.err.println("Error destruyendo proceso principal: " + e.getMessage());
                    }
                }
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getFile(String relativePath) {
        try {
            // Intentar buscar relativo al directorio de ejecución del JAR/Class
            String path = MariaDBLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File codeLocation = new File(path);
            File baseDir = codeLocation.isDirectory() ? codeLocation : codeLocation.getParentFile();

            File fileRelative = new File(baseDir, relativePath);
            if (fileRelative.exists()) {
                return fileRelative;
            }
        } catch (Exception e) {
            // Ignorar y caer en el fallback
        }
        // Fallback al directorio de trabajo actual
        return new File(relativePath);
    }
}
