package org.jusecase.poe.usecases;

import org.jusecase.inject.Component;
import org.jusecase.poe.entities.Currency;
import org.jusecase.poe.entities.InventorySlot;
import org.jusecase.poe.gateways.CurrencyGateway;
import org.jusecase.poe.gateways.InventorySlotGateway;
import org.jusecase.poe.plugins.ImageHashPlugin;
import org.jusecase.poe.plugins.InputPlugin;

import javax.inject.Inject;

@Component
public class AddCurrencyToStash {

    @Inject
    private InputPlugin inputPlugin;
    @Inject
    private InventorySlotGateway inventorySlotGateway;
    @Inject
    private CurrencyGateway currencyGateway;
    @Inject
    private ImageHashPlugin imageHashPlugin;

    public void execute() {
        for (InventorySlot inventorySlot : inventorySlotGateway.getAll()) {
            for (Currency currency : currencyGateway.getAll()) {
                if (imageHashPlugin.isSimilar(inventorySlot.imageHash, currency.imageHash)) {
                    inputPlugin.clickWithControlPressed(inventorySlot.x, inventorySlot.y);
                    break;
                }
            }
        }
    }
}
