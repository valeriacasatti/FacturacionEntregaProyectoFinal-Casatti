package com.coderhouse.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coderhouse.models.Cliente;
import com.coderhouse.services.ClienteService;


@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;
	
	//GET ALL CLIENTES
	@GetMapping
	public ResponseEntity<List<Cliente>> getAllClients() {
		try {
			List<Cliente> clientes = clienteService.getAllClients();
			return ResponseEntity.ok(clientes); 
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//GET CLIENTE BY ID
	@GetMapping("/{id}")
	public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
		try {
			Cliente cliente = clienteService.getClienteById(id);
			return ResponseEntity.ok(cliente);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	//CREAR CLIENTE
	@PostMapping
	public ResponseEntity<Cliente> newCliente(@RequestBody Cliente cliente){
		try {
			Cliente clienteNuevo = clienteService.newCliente(cliente);
			return ResponseEntity.status(HttpStatus.CREATED).body(clienteNuevo);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//ACTUALIZAR CLIENTE
	@PutMapping("/{id}")
	public ResponseEntity<Cliente> updateClienteById(@PathVariable Long id, @RequestBody Cliente clienteInfo){
		try {
			Cliente updateCliente = clienteService.updateClienteById(id, clienteInfo);
			return ResponseEntity.ok(updateCliente);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build(); 
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	//ELIMINAR CLIENTE
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteClienteById(@PathVariable Long id){
		try {
			clienteService.deleteClienteById(id);
			return ResponseEntity.noContent().build();
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build(); 
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
}