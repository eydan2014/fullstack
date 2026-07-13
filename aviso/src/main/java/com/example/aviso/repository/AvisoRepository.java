package com.example.aviso.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.aviso.model.AvisoModel;

@Repository
public interface AvisoRepository extends JpaRepository<AvisoModel, Long> {
    List<AvisoModel> findByUsuario(String usuario);
}
