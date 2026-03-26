package com.cafeteria.service;

import com.cafeteria.model.Cliente;
import com.cafeteria.model.Empresa;
import com.cafeteria.model.Particular;
import com.cafeteria.repository.ClienteRepository;
import com.cafeteria.repository.EmpresaRepository;
import com.cafeteria.repository.ParticularRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;
    private final EmpresaRepository empresaRepo;
    private final ParticularRepository particularRepo;

    public ClienteService(ClienteRepository clienteRepo,
                          EmpresaRepository empresaRepo,
                          ParticularRepository particularRepo) {
        this.clienteRepo = clienteRepo;
        this.empresaRepo = empresaRepo;
        this.particularRepo = particularRepo;
    }

    public Empresa crearEmpresa(Empresa empresa) {
        return empresaRepo.save(empresa);
    }

    public Particular crearParticular(Particular particular) {
        return particularRepo.save(particular);
    }

    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepo.findById(id);
    }

    public List<Cliente> listarActivos() {
        return clienteRepo.findByActivoTrue();
    }

    public List<Cliente> listarTodos() {
        return clienteRepo.findAll();
    }

    public List<Empresa> listarEmpresas() {
        return empresaRepo.findByActivoTrue();
    }

    public List<Particular> listarParticulares() {
        return particularRepo.findByActivoTrue();
    }

    public Cliente actualizar(Cliente cliente) {
        return clienteRepo.save(cliente);
    }

    public void darDeBaja(String id) {
        clienteRepo.findById(id).ifPresent(c -> {
            c.setActivo(false);
            clienteRepo.save(c);
        });
    }
}
