package com.library;

import com.library.config.HibernateConfig;
import com.library.ui.MainWindow;
import org.hibernate.Session;

public class Main {
    public static void main(String[] args) {
        System.out.println("Initializing Library System...");

        try {
            // 1. Force Hibernate to initialize (triggers Step 6: DB Init)
            Session session = HibernateConfig.getSessionFactory().openSession();
            session.close();
            System.out.println("Database connection established.");

            // 2. Launch TUI
            MainWindow mainWindow = new MainWindow();
            mainWindow.start();

        } catch (Exception e) {
            System.err.println("Critical failure during startup: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 3. Graceful shutdown
            HibernateConfig.shutdown();
        }
    }
}