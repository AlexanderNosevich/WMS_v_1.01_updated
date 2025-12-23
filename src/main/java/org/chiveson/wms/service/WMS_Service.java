package org.chiveson.wms.service;

import org.chiveson.wms.domain.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WMS_Service {

    private static final long DEFAULT_LOCATION_ID = 1L;
    private static final String DEFAULT_LOCATION_CODE = "A-00-00";

    private long nextProductId = 1;
    private long nextLocationId = DEFAULT_LOCATION_ID + 1;

    private final Map<Long, Product> products = new HashMap<>();
    private final Map<Long, Location> locations = new HashMap<>();
    private final Map<StockKey, StockItem> stock = new HashMap<>();

    public WMS_Service() {
        locations.put(DEFAULT_LOCATION_ID, new Location(DEFAULT_LOCATION_ID, DEFAULT_LOCATION_CODE));
    }

    // -------------------- Заготовки --------------------

    private static String normalizeLocationCode(String code) {
        if (code == null) return null;
        return code.trim().toUpperCase();
    }

    private Product requireActiveProduct(long productId) {
        if (productId <= 0) {
            throw new IllegalArgumentException("Некорректный ID товара: " + productId);
        }
        Product p = products.get(productId);
        if (p == null) {
            throw new IllegalArgumentException("Товар не найден");
        }
        if (p.getStatus() == Status.ARCHIVED) {
            throw new IllegalArgumentException("Это архивный товар");
        }
        return p;
    }

    private Location requireLocationById(long locationId) {
        if (locationId <= 0) {
            throw new IllegalArgumentException("Некорректный ID ячейки: " + locationId);
        }
        Location loc = locations.get(locationId);
        if (loc == null) {
            throw new IllegalArgumentException("Ячейки не существует");
        }
        return loc;
    }

    private Location requireLocationByCode(String code) {
        String normalized = normalizeLocationCode(code);
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException("Пустой код ячейки");
        }
        Location loc = getLocationByCode(normalized);
        if (loc == null) {
            throw new IllegalArgumentException("Ячейка не найдена: " + normalized);
        }
        return loc;
    }

    // -------------------- Продукты --------------------

    public void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product = null");
        products.put(product.getId(), product);
    }

    public Product createProduct(String name, int sku) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        if (sku <= 0) {
            throw new IllegalArgumentException("Артикул должен быть положительным");
        }
        if (existsBySku(sku)) {
            throw new IllegalArgumentException("Товар с таким артикулом уже существует");
        }

        long id = nextProductId++;
        Product product = new Product(id, name.trim(), Status.ACTIVE, sku);
        products.put(id, product);
        return product;
    }

    private boolean existsBySku(int sku) {
        return getBySku(sku) != null;
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Product getById(long id) {
        return products.get(id);
    }

    public List<Product> findByName(String query) {
        List<Product> result = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return result;
        }
        String q = query.trim().toLowerCase();

        for (Product product : products.values()) {
            String n = product.getName();
            if (n != null && n.toLowerCase().contains(q)) {
                result.add(product);
            }
        }
        return result;
    }

    public Product getBySku(int sku) {
        for (Product product : products.values()) {
            if (product.getSku() == sku) {
                return product;
            }
        }
        return null;
    }

    public boolean archiveProduct(long productId) {
        Product product = products.get(productId);
        if (product == null) return false;

        product.setStatus(Status.ARCHIVED);

        // Чистим остатки архивного товара
        stock.entrySet().removeIf(e -> e.getKey().productId() == productId);

        return true;
    }

    // -------------------- Stock (receive/ship/move) --------------------

    public int receive(long productId, long locationId, int quantity) {
        requireLocationById(locationId);
        requireActiveProduct(productId);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество приемки должно быть больше 0");
        }

        StockKey key = new StockKey(productId, locationId);
        StockItem item = stock.get(key);

        if (item == null) {
            item = new StockItem(productId, locationId, 0);
            stock.put(key, item);
        }

        int newQty = item.getQuantity() + quantity;
        item.setQuantity(newQty);
        return newQty;
    }

    public int ship(long productId, long locationId, int quantity) {
        requireLocationById(locationId);
        requireActiveProduct(productId);

        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество отгрузки должно быть больше 0");
        }

        StockKey key = new StockKey(productId, locationId);
        StockItem item = stock.get(key);

        if (item == null) {
            throw new IllegalArgumentException("Нечего отгружать");
        }

        int newQty = item.getQuantity() - quantity;
        if (newQty < 0) {
            throw new IllegalArgumentException("Недостаточно товара. Доступно: " + item.getQuantity());
        }

        if (newQty == 0) {
            stock.remove(key);
        } else {
            item.setQuantity(newQty);
        }

        return newQty;
    }

    public void move(long productId, long fromLocationId, long toLocationId, int quantity) {
        requireLocationById(fromLocationId);
        requireLocationById(toLocationId);
        requireActiveProduct(productId);

        if (fromLocationId == toLocationId) {
            throw new IllegalArgumentException("Нельзя переместить в эту же ячейку");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество перемещения должно быть больше 0");
        }

        ship(productId, fromLocationId, quantity);
        receive(productId, toLocationId, quantity);
    }

    // -------------------- остатки --------------------

    public int getQuantity(long productId, long locationId) {
        requireLocationById(locationId);
        requireActiveProduct(productId);

        StockKey key = new StockKey(productId, locationId);
        StockItem item = stock.get(key);
        return item == null ? 0 : item.getQuantity();
    }

    public int getTotalQuantity(long productId) {
        requireActiveProduct(productId);

        int total = 0;
        for (StockItem item : stock.values()) {
            if (item.getProductId() == productId) {
                total += item.getQuantity();
            }
        }
        return total;
    }

    public List<StockItem> getStockByProduct(long productId) {
        requireActiveProduct(productId);

        List<StockItem> items = new ArrayList<>();
        for (StockItem item : stock.values()) {
            if (item.getProductId() == productId) {
                items.add(item);
            }
        }

        if (items.isEmpty()) {
            throw new IllegalArgumentException("По товару нет остатков ни в одной ячейке");
        }
        return items;
    }

    public List<Product> getLowStockProducts(int threshold) {
        if (threshold <= 0) {
            throw new IllegalArgumentException("Порог должен быть >= 1");
        }

        List<Product> low = new ArrayList<>();
        for (Product product : products.values()) {
            if (product.getStatus() == Status.ARCHIVED) continue;

            int total = getTotalQuantity(product.getId());
            if (total < threshold) {
                low.add(product);
            }
        }
        return low;
    }

    // -------------------- Ячейки --------------------

    public Location createLocation(String code) {
        String normalized = normalizeLocationCode(code);
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException("Ячейка должна иметь код");
        }

        // Проверка уникальности кода
        for (Location loc : locations.values()) {
            String existing = normalizeLocationCode(loc.getCode());
            if (normalized.equals(existing)) {
                throw new IllegalArgumentException("Такая ячейка уже существует: " + normalized);
            }
        }

        long id = nextLocationId++;
        Location location = new Location(id, normalized);
        locations.put(id, location);
        return location;
    }

    public Location getLocationById(long locationId) {
        return locations.get(locationId);
    }

    public Location getLocationByCode(String code) {
        String normalized = normalizeLocationCode(code);
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException("Пустой код ячейки");
        }

        for (Location loc : locations.values()) {
            String existing = normalizeLocationCode(loc.getCode());
            if (normalized.equals(existing)) {
                return loc;
            }
        }
        return null;
    }

    public List<Location> getAllLocations() {
        return new ArrayList<>(locations.values());
    }



    public int receiveProductByLocationCode(long productId, String code, int quantity) {
        Location loc = requireLocationByCode(code);
        return receive(productId, loc.getId(), quantity);
    }

    public int shipProductByLocationCode(long productId, String code, int quantity) {
        Location loc = requireLocationByCode(code);
        return ship(productId, loc.getId(), quantity);
    }

    public int moveProductByLocationCode(long productId, String fromCode, String toCode, int quantity) {
        Location from = requireLocationByCode(fromCode);
        Location to = requireLocationByCode(toCode);

        if (from.getId() == to.getId()) {
            throw new IllegalArgumentException("Нужно выбрать разные ячейки");
        }

        move(productId, from.getId(), to.getId(), quantity);
        return getQuantity(productId, to.getId());
    }
}