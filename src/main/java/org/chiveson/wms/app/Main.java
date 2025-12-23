package org.chiveson.wms.app;

import org.chiveson.wms.service.WMS_Service;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        WMS_Service service = new WMS_Service();
        ConsoleUI ui = new ConsoleUI();

        while (true) {
            ui.menu();
            String cmd = input.nextLine().trim();
            if (cmd.equals("0")) return;

            switch (cmd) {
                case "1" -> ui.createProduct(input, service);
                case "2" -> ui.showAllProducts(input, service);
                case "3" -> ui.receiveProduct(input, service);
                case "4" -> ui.searchById(input, service);
                case "5" -> ui.searchByName(input, service);
                case "6" -> ui.searchBySku(input, service);
                case "7" -> ui.shipProduct(input, service);
                case "8" -> ui.moveProduct(input, service);
                case "9" -> ui.archiveProduct(input, service);
                case "10" -> ui.showLowStockProducts(input, service);
                case "11" -> ui.createLocation(input, service);
                case "12" -> ui.showQuantityInLocation(input, service);
                case "13" -> ui.showTotalQuantity(input, service);
                case "14" -> ui.showStockByProduct(input, service);
                default -> System.out.println("Пока не сделано");
            }
        }
    }
}