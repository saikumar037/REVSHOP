package com.revshop;

import com.revshop.config.LoggerConfig;
import com.revshop.menu.MainMenu;
import com.revshop.util.DatabaseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.revshop.util.ConsoleColors.*;

public class MainApplication {
    private static final Logger logger = LogManager.getLogger(MainApplication.class);

    public static void main(String[] args) {

        System.setProperty("org.slf4j.simpleLogger.log.com.zaxxer.hikari", "error");
        System.setProperty("org.slf4j.simpleLogger.log.com.mysql", "error");
        System.setProperty("org.slf4j.simpleLogger.log.com.mysql.cj", "error");
        try {
            // Initialize logger
            LoggerConfig.initialize();

            System.out.println();
            System.out.println(GREEN_BOLD_BRIGHT + "╔══════════════════════════════════════════════════════════════════╗" + RESET);
            System.out.println(GREEN_BOLD_BRIGHT + "║" + RESET + PURPLE_BOLD_BRIGHT + "                     REVSHOP E-COMMERCE APPLICATION               " + RESET + GREEN_BOLD_BRIGHT + "║" + RESET);
            System.out.println(GREEN_BOLD_BRIGHT + "║" + RESET + CYAN_BRIGHT + "                   Console-Based Shopping Platform                " + RESET + GREEN_BOLD_BRIGHT + "║" + RESET);
            System.out.println(GREEN_BOLD_BRIGHT + "╚══════════════════════════════════════════════════════════════════╝" + RESET);
            System.out.println();

            System.out.println(info("Starting RevShop E-Commerce Application..."));

            // Test database connection
            if (DatabaseUtil.testConnection()) {
                System.out.println(success("Database connection established successfully!"));
            } else {
                System.out.println(error("Failed to connect to database!"));
                System.out.println(error("Please check your database configuration in database.properties"));
                return;
            }

            // Start the application
            System.out.println(info("Initializing application..."));
            MainMenu mainMenu = new MainMenu();
            mainMenu.start();

        } catch (Exception e) {
            logger.error("Application error", e);
            System.out.println(error("An error occurred: " + e.getMessage()));
            System.out.println(info("Check logs/revshop.log for details."));
        } finally {
            DatabaseUtil.closeConnection();
            System.out.println();
            System.out.println(info("Application terminated."));
        }
    }
}