/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.focusservices.library.versionamiento;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

/**
 *
 * @author VOlivares
 */
@Value
public class IdTrabajo {
    @Getter(AccessLevel.NONE)
    private Map<Tupla, RegistroUnico> idsCommits;
    private Set<BigDecimal> idsUnicas;

    public IdTrabajo(QueryResult<Set<RegistroUnico>> ultimosIds) {
        // se guardan 2 caches, una de solo ids de commits y otra para obtener el registro unico por id
        // usando otra clase (bigDecimal)
        idsCommits = new HashMap<>();
        idsUnicas = new HashSet<>();
        for (RegistroUnico registro : ultimosIds.getValue()) {
            BigDecimal idCommit = registro.getIdCommit();
            idsCommits.put(new Tupla(registro.getIdLocal().toString(), VersionamientoUtils.bigDecimalIdToBigInteger(idCommit)), registro);
            idsUnicas.add(idCommit);
        }
    }
    
    public static class Tupla {
        private Object idLocal;
        private BigInteger idCommit;

        public Tupla(Object idLocal, BigInteger idCommit) {
            this.idLocal = idLocal;
            this.idCommit = idCommit;
        }

        @Override
        public String toString() {
            return "Tupla{" + "idLocal=" + idLocal + ", idCommit=" + idCommit + '}';
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + Objects.hashCode(this.idLocal);
            hash = 31 * hash + Objects.hashCode(this.idCommit);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Tupla other = (Tupla) obj;
            if (!Objects.equals(this.idLocal, other.idLocal)) {
                return false;
            }
            return Objects.equals(this.idCommit, other.idCommit);
        }
    }

    public Optional<RegistroUnico> getRegistroUnico(Object valor, BigInteger idCommit) {
        return Optional.ofNullable(idsCommits.get(new Tupla(valor.toString(), idCommit)));
    }
    
    public Set<Tupla> toTupla() {
        return idsCommits.keySet();
    }
}
