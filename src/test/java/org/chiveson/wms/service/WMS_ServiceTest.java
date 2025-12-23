package org.chiveson.wms.service;
import org.chiveson.wms.domain.Product;
import org.chiveson.wms.domain.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class WMS_ServiceTest {
    private WMS_Service service;
        @BeforeEach
                void setUp() {
            service = new WMS_Service();
        }

        @Test
                void createProduct_success() {
            Product p = service.createProduct("Milk", 1001);
            assertNotNull(p);
            assertEquals("Milk", p.getName());
            assertEquals(1001, p.getSku());
            assertEquals(Status.ACTIVE, p.getStatus());
            assertEquals(1L, p.getId());
        }

        @Test
                void createProduct_emptyNane_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.createProduct("   ", 1001));
        }

        @Test
                void createProduct_duplicateSku_throws() {
            service.createProduct("Milk", 1001);

            assertThrows(IllegalArgumentException.class,
                    () -> service.createProduct("Water", 1001));
        }

        @Test
                void receive_increasesQuantity() {
            Product p = service.createProduct("Bread", 2001);
            int q1 = service.receive(p.getId(), 1L, 10);
            int q2 = service.receive(p.getId(), 1L, 5);

            assertEquals(10, q1);
            assertEquals(15, q2);
            assertEquals(15, service.getQuantity(p.getId(), 1L));
        }

        @Test
                void ship_decreasesQuantity_andRemovesWhenZero() {
            Product p = service.createProduct("Juice", 3001);
            service.receive(p.getId(), 1L, 10);

            int afterShip = service.ship(p.getId(), 1L, 7);
            assertEquals(3, afterShip);

            int afterShip2 = service.ship(p.getId(), 1L, 3);
            assertEquals(0, afterShip2);

            // после нуля запись удалится, getQuantity должен вернуть 0
            assertEquals(0, service.getQuantity(p.getId(), 1L));
        }

        @Test
                void ship_moreThenAvailable() {
            Product p = service.createProduct("Water", 4001);
            service.receive(p.getId(), 1L, 3);

            assertThrows(IllegalArgumentException.class,
            () -> service.ship(p.getId(), 1L, 4));
        }

        @Test
                void move_movesBetweenLocations() {
            Product p = service.createProduct("Cookie", 5001);
            service.createLocation("A-01-01");
            service.createLocation("A-01-02");

            long fromId = service.getLocationByCode("A-01-01").getId();
            long toId = service.getLocationByCode("A-01-02").getId();

            service.receive(p.getId(), fromId, 20);
            service.move(p.getId(), fromId, toId, 5);

            assertEquals(15, service.getQuantity(p.getId(), fromId));
            assertEquals(5, service.getQuantity(p.getId(), toId));
            assertEquals(20, service.getTotalQuantity(p.getId()));
        }

        @Test
                void receive_archiveProduct_throws() {
            Product p = service.createProduct("Tea", 6001);
            service.archiveProduct(p.getId());

            assertThrows(IllegalArgumentException.class,
                    () -> service.receive(p.getId(), 1L, 1));
        }

        @Test
                void receiveLowerThatZeroQuantity() {
            Product p = service.createProduct("Coffee", 7001);

            assertThrows(IllegalArgumentException.class,
                    () -> service.receive(p.getId(), 1L, 0));

            assertThrows(IllegalArgumentException.class,
                    () -> service.receive(p.getId(), 1L, -1));
        }

        @Test
                void receiveMoreThanAvailable() {
            Product p = service.createProduct("Step", 8001);
            service.createLocation("A-01-01");
            service.createLocation("A-01-02");

            long fromId = service.getLocationByCode("A-01-01").getId();
            long toId = service.getLocationByCode("A-01-02").getId();

            service.receive(p.getId(), fromId, 5);

            assertThrows(IllegalArgumentException.class,
                    () -> service.move(p.getId(), fromId, toId, 6));
        }

}
