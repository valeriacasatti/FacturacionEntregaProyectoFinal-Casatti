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

import com.coderhouse.dtos.ProductoDTO;
import com.coderhouse.dtos.VentaDTO;
import com.coderhouse.services.VentaService;


@RestController
@RequestMapping("/api/ventas")
public class VentaController {

	@Autowired
	private VentaService ventaService;
	
	//GET ALL VENTAS
	@GetMapping
	public ResponseEntity<List<VentaDTO>> getAllVentas() {
		try {
			List<VentaDTO> ventas = ventaService.getAllVentas();
			return ResponseEntity.ok(ventas); 
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//GET VENTA BY ID
	@GetMapping("/{id}")
	public ResponseEntity<VentaDTO> getProductoById(@PathVariable Long id) {
		try {
			VentaDTO venta = ventaService.getVentaById(id);
			return ResponseEntity.ok(venta);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	//CREAR VENTA
	@PostMapping
	public ResponseEntity<VentaDTO> newVenta(@RequestBody VentaDTO dto){
		try {
			if(dto.getClienteId() == null || dto.getProductos() == null || dto.getProductos().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
	        List<Long> productosIds = dto.getProductos().stream()
	                                     .map(ProductoDTO::getId)
	                                     .toList();
	        List<Integer> cantidad = dto.getProductos().stream()
                    					.map(ProductoDTO::getCantidad)
                    					.toList();
			VentaDTO nuevaVenta = ventaService.newVenta(dto.getClienteId(), productosIds, cantidad);
			return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
		}catch(IllegalArgumentException e) {
			System.err.println("Error en argumentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//ACTUALIZAR VENTA
	@PutMapping("/{id}")
	public ResponseEntity<VentaDTO> updateVentaById(@PathVariable Long id, @RequestBody VentaDTO dto){
		try {
			VentaDTO updatedVenta = ventaService.updateVentaById(id, dto);
			return ResponseEntity.ok(updatedVenta);
		}catch(IllegalArgumentException e) {
			System.err.println("Error en argumentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}catch(Exception e) {
			System.err.println("Error inesperado al actualizar la venta: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	//ELIMINAR VENTA
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteVentaById(@PathVariable Long id){
		try {
			ventaService.deleteVentaById(id);
			return ResponseEntity.noContent().build();
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build(); 
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
}