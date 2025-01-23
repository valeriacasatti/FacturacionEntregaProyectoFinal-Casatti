package com.coderhouse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.coderhouse.models.Cliente;
import com.coderhouse.repositories.ClienteRepository;

import jakarta.transaction.Transactional;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	// GET ALL CLIENTES
	public List<Cliente> getAllClients(){
		return clienteRepository.findAll();
	}
	
	// GET CLIENTE BY ID
	public Cliente getClienteById(Long id) {
		return clienteRepository.findById(id)
				.orElseThrow(()-> new IllegalArgumentException("Cliente no encontrado"));
	}
	
	// CREAR CLIENTE
	@Transactional
	public Cliente newCliente(Cliente clienteInfo) {
		return clienteRepository.save(clienteInfo);
	}
	
	// ACTUALIZAR CLIENTE
	@Transactional
	public Cliente updateClienteById(Long id, Cliente clienteInfo) {
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(()-> new IllegalArgumentException("Cliente no encontrado"));
		
		if(clienteInfo.getNombre() != null && !clienteInfo.getNombre().isEmpty()) {
			cliente.setNombre(clienteInfo.getNombre());
		}
		
		if(clienteInfo.getApellido() != null && !clienteInfo.getApellido().isEmpty()) {
			cliente.setApellido(clienteInfo.getApellido());
		}
		
		if(clienteInfo.getEmail() != null && !clienteInfo.getEmail().isEmpty()) {
			cliente.setEmail(clienteInfo.getEmail());
		}
		
		return clienteRepository.save(cliente);
	}
	
	// ELIMINAR CLIENTE
	public void deleteClienteById(Long id) {
		if(!clienteRepository.existsById(id)) {
			throw new IllegalArgumentException("Cliente no encontrado");
		}
		clienteRepository.deleteById(id);
	}
}