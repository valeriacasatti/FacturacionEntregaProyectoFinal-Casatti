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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/clientes")
@Tag(name="Gestion de clientes", description="Endpoints para gestionar clientes en el sistema")
public class ClienteController {

	@Autowired
	private ClienteService clienteService;
	
	//GET ALL CLIENTES
	@Operation(summary = "Obtener lista de clientes", description = "Este endpoint devuelve una lista completa de los clientes registrados en el sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de clientes obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"Valeria\",\"apellido\":\"Casatti\",\"email\":\"valeria@gmail.com\"}]"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener los clientes", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
	@GetMapping
	public ResponseEntity<List<Cliente>> getAllClients() {
		try {
			List<Cliente> clientes = clienteService.getAllClients();
			return ResponseEntity.ok(clientes); 
		} catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//GET CLIENTE BY ID
	@Operation(summary = "Obtener un cliente por ID", description = "Devuelve los detalles de un cliente específico según su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cliente obtenido correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Valeria\",\"apellido\":\"Casatti\",\"email\":\"valeria@gmail.com\"}"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener el cliente", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
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
	@Operation(summary = "Crear un cliente", description = "Permite registrar un nuevo cliente en el sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Cliente creado correctamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Valeria\",\"apellido\":\"Casatti\",\"email\":\"valeria@gmail.com\"}"))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
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
	@Operation(summary = "Actualizar un cliente", description = "Permite actualizar un cliente específico según su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Valeria\",\"apellido\":\"Casatti\",\"email\":\"valeria@gmail.com\"}"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener el cliente", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
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
	@Operation(summary = "Eliminar un cliente", description = "Elimina un cliente del sistema según su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Cliente eliminado correctamente.", content = @Content),
			@ApiResponse(responseCode = "404", description = "Error al obtener el cliente", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
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