package com.makrozai.eligiushiddenarmor.domain.model;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;

public enum ArmorPiece {
    HELMET(5, EquipmentSlot.HELMET),
    CHESTPLATE(6, EquipmentSlot.CHEST_PLATE),
    LEGGINGS(7, EquipmentSlot.LEGGINGS),
    BOOTS(8, EquipmentSlot.BOOTS);

    private final int slotId;
    private final EquipmentSlot equipmentSlot;

    ArmorPiece(int slotId, EquipmentSlot equipmentSlot) {
        this.slotId = slotId;
        this.equipmentSlot = equipmentSlot;
    }

    public int getSlotId() {
        return slotId;
    }

    public EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
    }

    public static ArmorPiece fromSlotId(int slotId) {
        for (ArmorPiece piece : values()) {
            if (piece.getSlotId() == slotId) {
                return piece;
            }
        }
        return null;
    }

    public static ArmorPiece fromEquipmentSlot(EquipmentSlot slot) {
        for (ArmorPiece piece : values()) {
            if (piece.getEquipmentSlot() == slot) {
                return piece;
            }
        }
        return null;
    }

    public static ArmorPiece fromName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
