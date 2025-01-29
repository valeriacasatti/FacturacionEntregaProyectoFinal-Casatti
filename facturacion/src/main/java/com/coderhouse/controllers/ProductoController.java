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

import com.coderhouse.models.Producto;
import com.coderhouse.services.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/productos")
@Tag(name="Gestion de productos", description="Endpoints para gestionar productos en el sistema")
public class ProductoController {

	@Autowired
	private ProductoService productoService;
	
	//GET ALL PRODUCTOS
	@Operation(summary = "Obtener lista de productos", description = "Este endpoint devuelve una lista completa de los productos registrados en el sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(value = "[{\"id\":1,\"nombre\":\"Remera\",\"precio\":100,\"stock\":2}]"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener los productos", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
	@GetMapping
	public ResponseEntity<List<Producto>> getAllProductos() {
		try {
			List<Producto> productos = productoService.getAllProductos();
			return ResponseEntity.ok(productos); 
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//GET PRODUCTO BY ID
	@Operation(summary = "Obtener un producto por ID", description = "Devuelve los detalles de un producto específico según su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Producto obtenido correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Remera\",\"precio\":100,\"stock\":2}"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener el producto", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
	@GetMapping("/{id}")
	public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
		try {
			Producto producto = productoService.getProductoById(id);
			return ResponseEntity.ok(producto);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	//CREAR PRODUCTO
	@Operation(summary = "Crear un producto", description = "Permite registrar un nuevo producto en el sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Producto creado correctamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Remera\",\"precio\":100,\"stock\":2}"))),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
	@PostMapping
	public ResponseEntity<Producto> createProducto(@RequestBody Producto producto){
		try {
			Producto productoNuevo = productoService.newProducto(producto);
			return ResponseEntity.status(HttpStatus.CREATED).body(productoNuevo);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//ACTUALIZAR PRODUCTO
	@Operation(summary = "Actualizar un producto", description = "Permite actualizar un producto específico según su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Producto actualizado correctamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Remera\",\"precio\":100,\"stock\":2}"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener el producto", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
	@PutMapping("/{id}")
	public ResponseEntity<Producto> updateProductoById(@PathVariable Long id, @RequestBody Producto productoInfo){
		try {
			Producto updateProducto = productoService.updateProductoById(id, productoInfo);
			return ResponseEntity.ok(updateProducto);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build(); 
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//ELIMINAR PRODUCTO
	@Operation(summary = "Eliminar un producto", description = "Elimina un producto del sistema según su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Producto eliminado correctamente.", content = @Content),
			@ApiResponse(responseCode = "404", description = "Error al obtener el producto", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProductoById(@PathVariable Long id){
		try {
			productoService.deleteProductoById(id);
			return ResponseEntity.noContent().build();
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build(); 
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
}