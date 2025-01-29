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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/ventas")
@Tag(name="Gestion de ventas", description="Endpoints para gestionar ventas en el sistema")
public class VentaController {

	@Autowired
	private VentaService ventaService;
	
	//GET ALL VENTAS
	@Operation(summary = "Obtener lista de ventas", description = "Este endpoint devuelve una lista completa de las ventas registradas en el sistema")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Lista de ventas obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VentaDTO.class),
                    		examples = @ExampleObject(value = "[{\"id\":1,"
                            		+ "\"clienteId\":1,"
                            		+ "\"clienteNombre\":\"Valeria\","
                            		+ "\"clienteApellido\":\"Casatti\","
                            		+ "\"fecha\":\"01/23/2025 11:03\","
                            		+ "\"productos\":[{\"id\":10,\"nombre\":\"Remera\",\"precio\":100,\"cantidad\":2}],"
                            		+ "\"total\":200}]"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener las ventas", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
	@GetMapping
	public ResponseEntity<List<VentaDTO>> getAllVentas() {
		try {
			List<VentaDTO> ventas = ventaService.getAllVentas();
			return ResponseEntity.ok(ventas); 
		}catch(IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); 
		}
	}
	
	//GET VENTA BY ID
	@Operation(summary = "Obtener una venta por ID", description = "Devuelve los detalles de una venta específica según su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Venta obtenida correctamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VentaDTO.class),
                            examples = @ExampleObject(value = "{\"id\":1,"
                                    		+ "\"clienteId\":1,"
                                    		+ "\"clienteNombre\":\"Valeria\","
                                    		+ "\"clienteApellido\":\"Casatti\","
                                    		+ "\"fecha\":\"01/23/2025 11:03\","
                                    		+ "\"productos\":[{\"id\":10,\"nombre\":\"Remera\",\"precio\":100,\"cantidad\":2}],"
                                    		+ "\"total\":200}"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener la venta", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
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
	@Operation(summary = "Crear una venta", description = "Permite registrar una nueva venta asociada a un cliente y productos específicos")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "201", description = "Venta creada correctamente.",
	        content = @Content(mediaType = "application/json", 
	                           schema = @Schema(implementation = VentaDTO.class))),
	    @ApiResponse(responseCode = "404", description = "Error al obtener la venta", content = @Content),
	    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
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
	@Operation(summary = "Actualizar una venta", description = "Permite actualizar una venta específica según su ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Venta actualizada correctamente.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = VentaDTO.class),
                            examples = @ExampleObject(name = "Ejemplo de solicitud",
                                    value = "{\"clienteId\":1,\"productos\":[{\"id\":10,\"cantidad\":2}]}"))),
			@ApiResponse(responseCode = "404", description = "Error al obtener la venta", content = @Content),
			@ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
	})
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
	@Operation(summary = "Eliminar una venta", description = "Elimina una venta del sistema según su ID")
	@ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Venta eliminada correctamente.", content = @Content),
            @ApiResponse(responseCode = "404", description = "Error al obtener la venta", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.", content = @Content)
    })
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