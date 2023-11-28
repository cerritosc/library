package com.focusservices.library.versionamiento;

import java.util.HashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(of = "registroUnico")
public class RegistroVersionado implements Comparable<RegistroVersionado> {
    private RegistroUnico registroUnico;
    private Map<String, Object> valores = new HashMap<>();

    public RegistroVersionado(RegistroUnico registroUnico) {
        this.registroUnico = registroUnico;
    }
    
    public void addValor(String key, Object value) {
        this.valores.put(key, value);
    }
    
    public Object getValor(String key) {
        return this.valores.get(key);
    }
    
    public Map<String, Object> toMap() {
        return valores;
    }

    @Override
    public int compareTo(RegistroVersionado o) {
        return this.registroUnico.compareTo(o.registroUnico);
    }
}
