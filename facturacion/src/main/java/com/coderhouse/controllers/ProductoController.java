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

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	@Autowired
	private ProductoService productoService;
	
	//GET ALL PRODUCTOS
	@GetMapping
	public ResponseEntity<List<Producto>> getAllProductos() {
		try {
			List<Producto> productos = productoService.getAllProductos();
			return ResponseEntity.ok(productos); 
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//GET PRODUCTO BY ID
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