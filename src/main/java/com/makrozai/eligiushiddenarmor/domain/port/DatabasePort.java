package com.makrozai.eligiushiddenarmor.domain.port;

import com.makrozai.eligiushiddenarmor.domain.model.ArmorPiece;

import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;

public interface DatabasePort {
    boolean initialize();
    
    EnumSet<ArmorPiece> getHiddenPieces(UUID uuid);
    
    void setHiddenPieces(UUID uuid, EnumSet<ArmorPiece> hiddenPieces);
    
    Map<UUID, EnumSet<ArmorPiece>> getAllHiddenPieces();
    
    void close();
}
