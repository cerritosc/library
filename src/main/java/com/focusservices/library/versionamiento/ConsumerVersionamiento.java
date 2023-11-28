package com.focusservices.library.versionamiento;

@FunctionalInterface
public interface ConsumerVersionamiento<T> {
    void consume(RegistroVersionado registroVersionado, RegistroUnico registroUnico, T valorVersionado);
    
    static ConsumerVersionamiento vacio() {
        return (a, b, c) -> {};
    }
}
