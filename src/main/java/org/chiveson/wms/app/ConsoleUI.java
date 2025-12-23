package org.chiveson.wms.app;


import org.chiveson.wms.domain.Location;
import org.chiveson.wms.domain.Product;
import org.chiveson.wms.domain.StockItem;
import org.chiveson.wms.service.WMS_Service;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    Scanner input = new Scanner(System.in);

    public void menu() {
        System.out.println("        Окно управления складом         ");
        System.out.println("________________________________________");
        System.out.println("1. Добавить товар"); //Product || createProduct() + addProduct()
        System.out.println("2. Показать все товары");
        System.out.println("3. Приемка товара на склад"); //Увеличить кол-во товара
        System.out.println("4. Найти товар по ID");
        System.out.println("5. Найти товар по названию");
        System.out.println("6. Найти товар по артикулу");
        System.out.println("7. Отгрузка со склада"); // Уменьшить кол-во товара
        System.out.println("8. Перемещение между ячейками");
        System.out.println("9. Архивировать товар");
        System.out.println("10. Показать товары с низким остатком");
        System.out.println("11. Создать ячейку");
        System.out.println("12. Остаток товара в ячейке");
        System.out.println("13. Общий остаток товара по складу");
        System.out.println("14. Где лежит товар (раскладка по ячейкам)");
        System.out.print("Выберите операцию: ");
    }

    //Добавить товар (Диалог с пользователем)
    public void createProduct(Scanner input, WMS_Service service) {
        System.out.println("---- Добавление товара ----");
        System.out.print("Введите название ");
        String name = input.nextLine().trim();
        System.out.print("Введите артикул ");
        int sku = Integer.parseInt(input.nextLine().trim());
        System.out.print("Введите кол-во. (Может быть 0) ");
        int quantity = Integer.parseInt(input.nextLine().trim());
        if (quantity < 0) {
            System.out.println("Количество товара не может быть меньше нуля");
            System.out.println("Повторите ввод");
            return;
        }
        try {
            Product created = service.createProduct(name, sku);
            System.out.println("Товар создан: " + created);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
        }
    }

    // Вывести список товаров (Диалог с пользователем)
    public void showAllProducts(Scanner input, WMS_Service service) {
        //System.out.println("Как вывести список?");
        //System.out.println("1. Сортировка по остатку"); // Один метод
        //System.out.println("2. Сортировка по ID"); // Второй метод
        List<Product> list = service.getAllProducts();
        if (list.isEmpty()) {
            System.out.println("Товаров нет");
            return;
        }
        for (Product p : list) {
            System.out.println(p);
        }

    }

    // Поиск товара по ID (Диалог с пользователем)
    public void searchById(Scanner input, WMS_Service service) {
        System.out.println("---- Поиск товара по ID ----");
        System.out.println("Введите ID");
        long id = Long.parseLong(input.nextLine().trim());
        Product searched = service.getById(id);
        if (searched == null) {
            System.out.println("Товар не найден");
            return;
        }
        System.out.println(searched);
    }

    // Поиск товара по названию (Диалог с пользователем)
    public void searchByName(Scanner input, WMS_Service service) {
        System.out.println("---- Поиск по названию ----");
        System.out.println("Введите наименование товара");
        String query = input.nextLine().trim(); // Исправить ошибку
        if (query.isBlank()) {
            System.out.println("Товар не найден");
            return;
        }
        List<Product> searched = service.findByName(query);
        if (searched.isEmpty()) {
            System.out.println("Совпадений не найдено");
            return;
        }
        for (Product p : searched) {
            System.out.println(p);
        }
    }

    public void searchBySku(Scanner input, WMS_Service service) {
        System.out.println("---- Поиск по артикулу ----");
        System.out.println("Введите артикул для поиска ");
        int sku = Integer.parseInt(input.nextLine().trim());
        Product searched = service.getBySku(sku);
        if (searched == null) {
            System.out.println("Товар не найден");
            return;
        }
        System.out.println(searched);
    }

    // Увеличить/уменьшить кол-во товара (Диалог с пользователем)
    public void setQuantity(Scanner input, WMS_Service service) {
        System.out.println("---- Управление кол-вом товара");
        System.out.println("Введите ID товара");
        long id = Long.parseLong(input.nextLine().trim());
        System.out.println("Выберите операцию:");
        System.out.println("1. Увеличить количество");
        System.out.println("2. Уменьшить количество");
        // Здесь будут методы из Service + логика
    }

    // Показать товары с низким остатком (Диалог с пользователем)
    public void showLowQuantity(Scanner input, WMS_Service service) {
        System.out.println("---- Установите параметры ----");
        System.out.println("Введите нижний порог");
        int threshold = Integer.parseInt(input.nextLine().trim());
        // Добавить логику, арифметику, учитывание статуса Активен. Так же - сделать if
    }

    //Архивировать товар (Диалог с пользователем)
    public void archiveProduct(Scanner input, WMS_Service service) {
        System.out.println("---- Архивирование товара ----");
        System.out.println("Введите ID товара");
        long id = Long.parseLong(input.nextLine().trim());
        boolean archived = service.archiveProduct(id);
        if (!archived) {
            System.out.println("Товар не найден");
            return;
        }
    }

    // Создание ячейки
    public void createLocation(Scanner input, WMS_Service service) {
        System.out.println("=== Создание ячейки ===");
        System.out.print("Введите номер ячейки в формате А-88-88: ");
        String code = input.nextLine().trim();
        if (code == null || code.isBlank()) {
            System.out.println("Используется дефолтная ячейка");
            return;
        }
        try {
            Location created = service.createLocation(code);
            System.out.println("Ячейка создана: " + created.getCode() + " (id = " + created.getId() + ")");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
        }

    }

    //Приемка товара на склад
    public void receiveProduct(Scanner input, WMS_Service service) {
        System.out.println("=== Приемка товара ===");
        System.out.println("Введите ID товара: ");
        long productId = Long.parseLong(input.nextLine().trim());
        System.out.println("Введите код ячейки: ");
        String code = input.nextLine().trim();
        if (code == null || code.isBlank()) code = "A-00-00";
        System.out.println("Количество: ");
        int quantity = Integer.parseInt(input.nextLine().trim());
        try {
            int newQuantity = service.receiveProductByLocationCode(productId, code, quantity);
            System.out.println("Остаток в ячейке №" + code.trim().toUpperCase() + ": " + newQuantity);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
        }
    }

    //Отгрузка товара
    public void shipProduct (Scanner input, WMS_Service service) {
        System.out.println("Отгрузка товара");
        System.out.println("Введите ID товара");
        long productId = Long.parseLong(input.nextLine().trim());
        System.out.println("Введите код ячейки");
        String code = input.nextLine().trim();
        if (code == null || code.isBlank()) code = "A-00-00";
        System.out.println("Количество: ");
        int quantity = Integer.parseInt(input.nextLine().trim());
        try {
            int newQuantity = service.shipProductByLocationCode(productId, code, quantity);
            System.out.println("Остаток в ячейке №" + code.trim().toUpperCase() + ": " + newQuantity);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
        }
    }

    // Перемещение продукта
    public void moveProduct(Scanner input, WMS_Service service) {
        System.out.println("--- Перемещение продукта ---");
        System.out.println("Введите ID продукта ");
        long productId = Long.parseLong(input.nextLine().trim());
        System.out.println("Введите количество, которое хотите переместить ");
        int quantity = Integer.parseInt(input.nextLine().trim());
        System.out.println("Введите номер ячейки, из которой хотите переместить");
        String fromCode = input.nextLine().trim();

        if (fromCode == null || fromCode.isBlank()) fromCode = "A-00-00";
        System.out.println("Введите номер ячейки, в которую хотите переместить ");
        String toCode = input.nextLine().trim();

        try {
            int newQuantityInTo = service.moveProductByLocationCode(productId, fromCode, toCode, quantity);
            System.out.println("Перемещение выполнено. Остаток в ячейке: " + toCode.trim().trim() + ": " + newQuantityInTo);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка " + e.getMessage());
        }
    }
    public void showLowStockProducts(Scanner input, WMS_Service service) {
        System.out.println("=== Товары с низким остатком ===");
        System.out.print("Введите порог: ");

        int threshold;
        try {
            threshold = Integer.parseInt(input.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите число");
            return;
        }

        try {
            List<Product> low = service.getLowStockProducts(threshold);

            if (low.isEmpty()) {
                System.out.println("Нет товаров с остатком ниже " + threshold);
                return;
            }

            System.out.println("Найдено товаров: " + low.size());
            for (Product p : low) {
                int total = service.getTotalQuantity(p.getId());
                System.out.println(
                        "ID=" + p.getId()
                                + " | " + p.getName()
                                + " | SKU=" + p.getSku()
                                + " | Остаток=" + total
                                + " | Статус=" + p.getStatus()
                );
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    // Остаток товара в конкретной ячейке (по коду ячейки)
    public void showQuantityInLocation(Scanner input, WMS_Service service) {
        System.out.println("=== Остаток товара в ячейке ===");
        System.out.print("Введите ID товара: ");
        long productId;
        try {
            productId = Long.parseLong(input.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
            return;
        }

        System.out.print("Введите код ячейки (пусто = дефолт): ");
        String code = input.nextLine().trim();
        if (code.isBlank()) code = "A-00-00";

        try {
            Location location = service.getLocationByCode(code);
            if (location == null) {
                System.out.println("Ячейка не найдена: " + code);
                return;
            }

            int qty = service.getQuantity(productId, location.getId());
            System.out.println("Остаток товара ID=" + productId + " в ячейке " + location.getCode() + " (id=" + location.getId() + "): " + qty);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Общий остаток товара по складу
    public void showTotalQuantity(Scanner input, WMS_Service service) {
        System.out.println("=== Общий остаток товара по складу ===");
        System.out.print("Введите ID товара: ");
        long productId;
        try {
            productId = Long.parseLong(input.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
            return;
        }

        try {
            int total = service.getTotalQuantity(productId);
            System.out.println("Общий остаток товара ID=" + productId + " на складе: " + total);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Где лежит товар: список ячеек и остатки по каждой
    public void showStockByProduct(Scanner input, WMS_Service service) {
        System.out.println("=== Где лежит товар (раскладка по ячейкам) ===");
        System.out.print("Введите ID товара: ");
        long productId;
        try {
            productId = Long.parseLong(input.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
            return;
        }

        try {
            List<StockItem> items = service.getStockByProduct(productId);
            int total = service.getTotalQuantity(productId);

            System.out.println("Товар ID=" + productId + " лежит в " + items.size() + " ячейках. Общий остаток: " + total);
            for (StockItem item : items) {
                Location loc = service.getLocationById(item.getLocationId());
                String code = (loc == null ? "???" : loc.getCode());
                System.out.println("Ячейка " + code + " (id=" + item.getLocationId() + "): " + item.getQuantity());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}







